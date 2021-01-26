[udemy-course]: https://www.udemy.com/course/develop-minecraft-plugins-java-programming/?couponCode=BIGSALE
[course-discord]: https://discord.gg/uZacPdD

# CommandFramework
A useful and efficient command framework for Spigot / Bukkit.
There are several benefits to using this over the default `CommandExecutor`. For one, when registering commands you do not have to include them in your plugin.yml, as they're registered into Bukkit's command handler when registered by the `CommandFramework`. You can also easily declare sub commands (commands followed by specific argumments e.g. `/party create`) by separating arguments with a period.

## How to setup:
Download the source into your project, declare it in a class (probably your main) like so:
```java
private CommandFramework commandFramework;
```
Assign a new instance of the class to the variable:
```java
this.commandFramework = new CommandFramework(this);
```


## How to create a command:
Create a class for your command(s), and create a `public void` with the `@Command` annotation:
```java
@Command()
public void onCommand() {
    // code here
}
```
### Declaring attributes for your command:
name:
The name of the command, e.g. `name = "party"`.
If it is a sub command (e.g. `/party create`) you can separate the arguments via periods, for instance `name = "party.create"`

permission:
The permission required to execute the command, e.g. `permission = "party.default"`

noPerm:
The message displayed to the user if they do not have permission to execute the command, e.g. `noPerm = "&cYou do not have permission to do this."`

aliases:
Other commands able to be entered to execute this command, e.g. `aliases = {"p", "partyalias"}`

description:
The description that will appear in /help for the command, e.g. `description = "Main party command"`

usage:
The usage message that will appear in /help <command>, e.g. `usage = "Usage: /party invite <player>"`

inGameOnly:
Whether or not that command is able to be executed in console as well as in game, e.g. `inGameOnly = true`

Final annotation:
```java
@Command(name = "party.invite", permission = "party.default", noPerm = "&cYou do not have permission to do this.", aliases = {"p.invite", "p.inv"}, usage = "Usage: /party invite <player>", inGameOnly = true)
```
This is an example with every attribute, however it is rare you will need to use all of them. For example, you can declare a defaults in `Command.java`

### Accessing command information:
So you have your command method:
```java
@Command(name = "party.invite", aliases = {"p.invite"}, inGameOnly = true)
public void onCommand() {
    // code here
}
```
In this method, you have no way of easily accessing information about said command when ran, for example, the player that executed the command, potential following arguemnts, etc. To do so, parse `CommandArgs` into your public method, e.g. `public void onCommand(CommandArgs cmd)`

Final example:
```java
@Command(name = "party.invite", aliases = {"p.invite"}, inGameOnly = true)
public void onCommand(CommandArgs cmd) {
    Player player = cmd.getPlayer();
    String[] args = cmd.getArgs();
    // code here
}
```

### CommandArgs attributes:
CommandArgs#getSender - Return the `CommandSender` object<br>
CommandArgs#getCommand - Return the original command object<br>
CommandArgs#getLabel - Return the label, including sub command labels<br>
CommandArgs#getArgs - Return the arguments after the commands label, does not include sub commands specified in the `name`<br>
CommandArgs#length - Returns the length of the command arguments<br>
CommandArgs#getPlayer - Returns the `Player` object (if the sender is not a player, it will return null)<br>


## Registering a command
Using the variable you used to instantiate `CommandFramework` you're able to either register a specific command, via the annotation and method, or you can register all commands within a class (easier & recommended).

### Registering all commands in a class:
Example:
```java
private CommandFramework commandFramework;

@Override
public void onEnable() {
    this.commandFramework = new CommandFramework(this);
    this.commandFramework.registerCommands(new PartyInviteCommand());
}
```

If you have lots of commands, for readability instead of repeating that line, you could use a loop, for example:
```java
for (Object command : Arrays.asList(
    new PartyCommand(),
    new PartyCreateCommand(),
    new PartyInviteCommand(),
    new PartyJoinCommand(),
    new PartyDisbandCommand
)) {
    this.commandFramework.registerCommands(command);
}
```

## Looking to advance your knowledge in Java?
If you're just starting out as a Developer, or want to reinforce your already discovered knowledge, be sure to check out [this][udemy-course] course on Udemy. Not sure if you want to invest in the course? Join the Discord [here][course-discord] and get to know the people behind it.
