package io.obadiah.command;

import com.google.common.reflect.ClassPath;
import io.obadiah.command.annotation.NoAutoRegister;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This is only a plugin to ensure Commodore works with it.
 */
@ThreadSafe
public class CommandAPI extends JavaPlugin {

    private static final CommandMap COMMAND_MAP;
    private static Commodore COMMODORE = null;

    static {
        try {
            Bukkit.getLogger().info("Obtaining command map for CommandAPI..");
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            COMMAND_MAP = (CommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (CommodoreProvider.isSupported()) {
            COMMODORE = CommodoreProvider.getCommodore(CommandAPI.get());
        }
    }

    /**
     * Registers all commands found in a specific package unless annotated with @NoAutoRegister
     *
     * @param packageName The name of the package to traverse.
     */
    public static void registerCommands(String packageName) {
        try {
            ClassPath.from(Bukkit.class.getClassLoader()).getTopLevelClasses(packageName).stream()
              .map(ClassPath.ClassInfo::load)
              .filter(c -> Command.class.isAssignableFrom(c) && c != Command.class)
              .forEach(c -> {
                  if (c.isAnnotationPresent(NoAutoRegister.class)) {
                      return;
                  }

                  try {
                      Bukkit.getLogger().info("Attempting to register the command, " + c.getSimpleName() + "..");
                      Command command = (Command) c.getConstructor().newInstance();

                      registerCommand(command);
                  } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                      Bukkit.getLogger().severe("Could not register the command, " + c.getSimpleName() + "!!");
                      e.printStackTrace();
                  }
              });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers an array of commands
     *
     * @param commands Commands to register.
     */
    public static void registerCommands(Command... commands) {
        Stream.of(commands).forEach(CommandAPI::registerCommand);
    }

    /**
     * Registers a list of commands
     *
     * @param commands Commands to register.
     */
    public static void registerCommands(List<Command> commands) {
        commands.forEach(CommandAPI::registerCommand);
    }

    /**
     * Registers a set of commands
     *
     * @param commands Commands to register.
     */
    public static void registerCommands(Set<Command> commands) {
        commands.forEach(CommandAPI::registerCommand);
    }

    /**
     * Registers a command.
     *
     * @param command Command to register.
     */
    public static void registerCommand(Command command) {
        BukkitCommand bCommand = command.asBukkitCommand();
        COMMAND_MAP.register("CommandAPI", bCommand);

        if (COMMODORE != null && command instanceof CompletableCommand) {
            COMMODORE.register(bCommand, ((CompletableCommand) command).getCompletions());
        }

        Bukkit.getLogger().info("Registered the command, " + command.getClass().getSimpleName() + ", successfully!");
    }

    /**
     * Checks whether a specific command has already been added to the command map.
     *
     * @param command Command to check
     *
     * @return {@code true} If the command has already been added
     */
    public static boolean commandExists(Command command) {
        return commandExists(command.getName());
    }

    /**
     * Checks whether a specific command has already been added to the command map.
     *
     * @param command Command to check
     *
     * @return {@code true} If the command has already been added
     */
    public static boolean commandExists(String command) {
        return COMMAND_MAP.getCommand(command) != null;
    }

    /**
     * @return The CommandAPI instance in use by Bukkit.
     */
    private static CommandAPI get() {
        return JavaPlugin.getPlugin(CommandAPI.class);
    }
}
