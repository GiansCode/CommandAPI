package io.obadiah.example;

import io.obadiah.command.Command;
import io.obadiah.command.CommandSource;
import org.bukkit.command.CommandSender;

public class ExampleSingleCommand extends Command {

    /**
     * Represents a command.
     */
    public ExampleSingleCommand() {
        super("example", "myplugin.example");

        this.addAliases(
          "exam",
          "ple"
        );

        this.setDescription("This is an example command built using the Command API.");
        this.removePermittedSources(
          CommandSource.COMMAND_BLOCK
        );

        this.addSubCommands(new ExampleSubCommand());
    }

    @Override
    protected void execute(CommandSender sender, String... args) throws Exception {}

    @Override
    protected String getCommandUsage() {
        return GET_USAGE_FUNCTION.apply(this);
    }
}
