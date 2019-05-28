# CommandAPI
A sane and fluid method of adding multiple commands to Bukkit.

### Creating a command example
![Example Command](https://github.com/ObadiahCrowe/CommandAPI/blob/master/docs/example.png)

### Checking if a command exists
```java
CommandRegistry.commandExists("commandName");
```

### Registering a command (or multiple)
```java
// Registering all commands found in a package.
CommandRegistry.registerCommands("your.package.name");

// Registering multiple command instances.
CommandRegistry.registerCommands(new ACommand(), new AnotherCommand());

// Registering a single command instance.
CommandRegistry.registerCommand(new Command1());
```