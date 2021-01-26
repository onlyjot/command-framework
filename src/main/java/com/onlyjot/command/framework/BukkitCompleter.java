package com.onlyjot.command.framework;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BukkitCompleter implements TabCompleter {

	private final Map<String, Entry<Method, Object>> completers = new HashMap<>();

	public void addCompleter(String label, Method m, Object obj) {
		this.completers.put(label, new AbstractMap.SimpleEntry<>(m, obj));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		for (int i = args.length; i >= 0; i--) {
			StringBuilder builder = new StringBuilder();
			builder.append(label.toLowerCase());

			for (int x = 0; x < i; x++) {
				if (!args[x].equals("") && !args[x].equals(" "))
					builder.append(".").append(args[x].toLowerCase());
			}

			String cmdLabel = builder.toString();
			if (this.completers.containsKey(cmdLabel)) {
				Entry<Method, Object> entry = this.completers.get(cmdLabel);
				try {
					return (List<String>) entry.getKey().invoke(entry.getValue(),
							new CommandArgs(sender, command, label, args, cmdLabel.split("\\.").length - 1));
				} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}