package io.obadiah.command;

import com.google.common.reflect.ClassPath;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

@ThreadSafe
public final class CommandRegistry {

    private static final CommandMap COMMAND_MAP;

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
    }

    public static void registerCommands(String packageName) {
        try {
            ClassPath.from(Bukkit.class.getClassLoader()).getTopLevelClasses(packageName).stream()
              .map(ClassPath.ClassInfo::load)
              .filter(c -> Command.class.isAssignableFrom(c) && c != Command.class)
              .forEach(c -> {
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

    public static void registerCommands(Command... commands) {
        Stream.of(commands).forEach(CommandRegistry::registerCommand);
    }

    public static void registerCommand(Command command) {
        COMMAND_MAP.register("CommandAPI", command.asBukkitCommand());
        Bukkit.getLogger().info("Registered the command, " + command.getClass().getSimpleName() + ", successfully!");
    }

    public static boolean commandExists(Command command) {
        return commandExists(command.getName());
    }

    public static boolean commandExists(String command) {
        return COMMAND_MAP.getCommand(command) != null;
    }
}
