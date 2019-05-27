package io.obadiah.command;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.concurrent.Immutable;
import java.util.stream.Stream;

@Immutable
public enum CommandSource {

    PLAYER(Player.class),
    CONSOLE(ConsoleCommandSender.class),
    COMMAND_BLOCK(BlockCommandSender.class);

    private final Class<?> senderClass;

    CommandSource(Class<?> senderClass) {
        this.senderClass = senderClass;
    }

    private Class<?> getSenderClass() {
        return this.senderClass;
    }

    public static CommandSource fromSender(CommandSender sender) {
        return Stream.of(CommandSource.values())
          .filter(source -> source.getSenderClass().isAssignableFrom(sender.getClass()))
          .findFirst()
          .orElse(null);
    }
}
