package com.onlyjot.command.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

	/**
	 * The name of the command. If it is a sub command then its values would be
	 * separated by periods. ie. a command that would be a subcommand of test
	 * would be 'test.subcommandname'
	 * 
	 * @return
	 */
	String name();

	/**
	 * Gets the required permission of the command
	 * 
	 * @return
	 */
	String permission() default "";

	/**
	 * The message sent to the player when they do not have permission to
	 * execute it
	 * 
	 * @return
	 */
	String noPerm() default "&cYou do not have permission to do this.";

	/**
	 * A list of alternate names that the command is executed under. See
	 * name() for details on how names work
	 * 
	 * @return
	 */
	String[] aliases() default {};

	/**
	 * The description that will appear in /help of the command
	 * 
	 * @return
	 */
	String description() default "";

	/**
	 * The usage that will appear in /help (commandname)
	 * 
	 * @return
	 */
	String usage() default "";
	
	/**
	 * Whether or not the command is available to players only
	 * 
	 * @return
	 */
	boolean inGameOnly() default false;
}
