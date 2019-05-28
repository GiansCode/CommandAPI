package io.obadiah.command;

import javax.annotation.concurrent.Immutable;
import java.util.stream.Collectors;

/**
 * Tiny utility class to quickly generate command usage.
 */
@Immutable
public final class UsageBuilder {

    private final Command command;

    private UsageBuilder(Command command) {
        this.command = command;
    }

    /**
     * @return The final string containing all the command / subcommands.
     */
    public String get() {
        StringBuilder builder = new StringBuilder();

        builder
          .append("/")
          .append(command.getName())
          .append(" ");

        Command c = this.command;
        while (c.getSubCommands().size() > 0) {
            StringBuilder subBuilder = new StringBuilder();
            String cmds = c.getSubCommands().stream().map(cmd -> cmd.getName() + "/").collect(Collectors.joining());

            subBuilder.append("<").append(cmds, 0, cmds.length() - 1).append("> ");
            builder.append(subBuilder.toString());
        }

        String usage = builder.toString();
        return usage.substring(0, usage.length() - 1);
    }

    /**
     * Obtains a UsageBuilder instance from a Command. Exists primarily for cleanliness.
     *
     * @param command Command to build a usage builder from.
     *
     * @return A UsageBuilder instance.
     */
    public static UsageBuilder from(Command command) {
        return new UsageBuilder(command);
    }
}
