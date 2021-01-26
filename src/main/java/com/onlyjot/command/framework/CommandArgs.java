package com.onlyjot.command.framework;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandArgs {

	private final CommandSender sender;
	private final org.bukkit.command.Command command;
	private final String label;
	private final String[] args;

	protected CommandArgs(CommandSender sender, org.bukkit.command.Command command, String label, String[] args,
                          int subCommand) {
		String[] modArgs = new String[args.length - subCommand];
		if (args.length - subCommand >= 0) System.arraycopy(args, subCommand, modArgs, 0, args.length - subCommand);

		StringBuilder builder = new StringBuilder();
		builder.append(label);

		for (int x = 0; x < subCommand; x++)
			builder.append(".").append(args[x]);


		String cmdLabel = builder.toString();
		this.sender = sender;
		this.command = command;
		this.label = cmdLabel;
		this.args = modArgs;
	}

	/**
	 * Gets the command sender
	 * 
	 * @return
	 */
	public CommandSender getSender() {
		return sender;
	}

	/**
	 * Gets the original command object
	 * 
	 * @return
	 */
	public org.bukkit.command.Command getCommand() {
		return command;
	}

	/**
	 * Gets the label including sub command labels of this command
	 * 
	 * @return Something like 'test.subcommand'
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Gets all the arguments after the command's label. ie. if the command
	 * label was test.subcommand and the arguments were subcommand foo foo, it
	 * would only return 'foo foo' because 'subcommand' is part of the command
	 * 
	 * @return
	 */
	public String[] getArgs() {
		return args;
	}
	
	/**
	 * Gets the argument at the specified index
	 * @param index The index to get
	 * @return The string at the specified index
	 */
	public String getArgs(int index) {
		return args[index];
	}

	/**
	 * Returns the length of the command arguments
	 * @return int length of args
	 */
	public int length() {
		return args.length;
	}
	
	public boolean isPlayer() {
		return sender instanceof Player;
	}

	public Player getPlayer() {
		if (sender instanceof Player)
			return (Player) sender;

		return null;
	}

}
