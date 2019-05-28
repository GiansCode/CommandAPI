package io.obadiah.example;

import io.obadiah.command.Command;
import io.obadiah.command.annotation.NoAutoRegister;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@NoAutoRegister // Used when you are registering entire packages with the CommandAPI. You do not want to register subcommands.
public class ExampleSubCommand extends Command {

    /**
     * Represents a command.
     */
    public ExampleSubCommand() {
        super("sub", "myplugin.example.sub");

        this.setDescription("An example sub command.");

        this.setMinArgs(0);
        this.setMaxArgs(1);
    }

    @Override
    protected void execute(CommandSender sender, String... args) throws Exception {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Try adding your name at the end.");
            return;
        }

        sender.sendMessage(ChatColor.BLUE + "Hello, " + args[0]);
    }

    @Override
    protected String getCommandUsage() {
        return "/example sub <yourName>";
    }
}
