package com.onlyjot.command.framework;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CommandFramework implements CommandExecutor {

	private final Map<String, Entry<Method, Object>> commandMap = new HashMap<>();
	private CommandMap map;
	private final Plugin plugin;

	/**
	 * Initializes the command framework and sets up the command maps
	 */
	public CommandFramework(Plugin plugin) {
		this.plugin = plugin;
		if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
			SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();

			try {
				Field field = SimplePluginManager.class.getDeclaredField("commandMap");
				field.setAccessible(true);
				this.map = (CommandMap) field.get(manager);
			} catch (IllegalArgumentException | SecurityException | IllegalAccessException | NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		return handleCommand(sender, cmd, label, args);
	}
	
	/**
	 * Handles commands. Used in the onCommand method in your JavaPlugin class
	 * 
	 * @param sender The {@link CommandSender} parsed from
	 *            onCommand
	 * @param cmd The {@link org.bukkit.command.Command} parsed from onCommand
	 * @param label The label parsed from onCommand
	 * @param args The arguments parsed from onCommand
	 * @return Always returns true for simplicity's sake in onCommand
	 */
	public boolean handleCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		for (int i = args.length; i >= 0; i--) {
			StringBuilder builder = new StringBuilder();
			builder.append(label.toLowerCase());

			for (int x = 0; x < i; x++)
				builder.append(".").append(args[x].toLowerCase());

			String cmdLabel = builder.toString();

			if (this.commandMap.containsKey(cmdLabel)) {
				Method method = commandMap.get(cmdLabel).getKey();
				Object methodObject = commandMap.get(cmdLabel).getValue();
				Command command = method.getAnnotation(Command.class);

				if (!command.permission().equals("") && !sender.hasPermission(command.permission())) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', command.noPerm()));
					return true;
				}

				if (command.inGameOnly() && !(sender instanceof Player)) {
					sender.sendMessage("This command is only performable in game");
					return true;
				}

				try {
					method.invoke(methodObject, new CommandArgs(sender, cmd, label, args,
							cmdLabel.split("\\.").length - 1));
				} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
				return true;
			}
		}

		defaultCommand(new CommandArgs(sender, cmd, label, args, 0));
		return true;
	}

	/**
	 * Registers all command and completer methods inside of the object. Similar
	 * to Bukkit's registerEvents method.
	 * 
	 * @param obj The object to register the commands of
	 */
	public void registerCommands(Object obj) {
		for (Method m : obj.getClass().getMethods()) {
			if (m.getAnnotation(Command.class) != null) {
				Command command = m.getAnnotation(Command.class);

				if (m.getParameterTypes().length > 1 || m.getParameterTypes()[0] != CommandArgs.class) {
					System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
					continue;
				}

				registerCommand(command, command.name(), m, obj);

				for (String alias : command.aliases())
					registerCommand(command, alias, m, obj);

			} else if (m.getAnnotation(Completer.class) != null) {
				Completer comp = m.getAnnotation(Completer.class);
				if (m.getParameterTypes().length != 1 || m.getParameterTypes()[0] != CommandArgs.class) {
					System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
					continue;
				}

				if (m.getReturnType() != List.class) {
					System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
					continue;
				}

				registerCompleter(comp.name(), m, obj);

				for (String alias : comp.aliases())
					registerCompleter(alias, m, obj);
			}
		}
	}

	public void registerCommand(Command command, String label, Method m, Object obj) {
		this.commandMap.put(label.toLowerCase(), new AbstractMap.SimpleEntry<>(m, obj));
		this.commandMap.put(this.plugin.getName() + ':' + label.toLowerCase(), new AbstractMap.SimpleEntry<>(m, obj));

		String cmdLabel = label.split("\\.")[0].toLowerCase();

		if (this.map.getCommand(cmdLabel) == null) {
			org.bukkit.command.Command cmd = new BukkitCommand(cmdLabel, this, plugin);
			this.map.register(this.plugin.getName(), cmd);
		}

		if (!command.description().equalsIgnoreCase("") && cmdLabel.equals(label))
			this.map.getCommand(cmdLabel).setDescription(command.description());

		if (!command.usage().equalsIgnoreCase("") && cmdLabel.equals(label))
			this.map.getCommand(cmdLabel).setUsage(command.usage());

	}

	public void registerCompleter(String label, Method m, Object obj) {
		String cmdLabel = label.split("\\.")[0].toLowerCase();

		if (this.map.getCommand(cmdLabel) == null) {
			org.bukkit.command.Command command = new BukkitCommand(cmdLabel, this, this.plugin);
			this.map.register(plugin.getName(), command);
		}

		if (this.map.getCommand(cmdLabel) instanceof BukkitCommand) {
			BukkitCommand command = (BukkitCommand) this.map.getCommand(cmdLabel);

			if (command.completer == null)
				command.completer = new BukkitCompleter();


			command.completer.addCompleter(label, m, obj);

		} else if (this.map.getCommand(cmdLabel) instanceof PluginCommand) {
			try {
				Object command = this.map.getCommand(cmdLabel);
				Field field = command.getClass().getDeclaredField("completer");
				field.setAccessible(true);

				if (field.get(command) == null) {
					BukkitCompleter completer = new BukkitCompleter();
					completer.addCompleter(label, m, obj);
					field.set(command, completer);

				} else if (field.get(command) instanceof BukkitCompleter) {
					BukkitCompleter completer = (BukkitCompleter) field.get(command);
					completer.addCompleter(label, m, obj);
				} else {
					System.out.println("Unable to register tab completer " + m.getName() + ". A tab completer is already registered for that command!");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void defaultCommand(CommandArgs args) {
		args.getSender().sendMessage(ChatColor.RED + args.getLabel() + " isn't handled!");
	}
	
}
