package io.obadiah.example;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.obadiah.command.Command;
import io.obadiah.command.CommandSource;
import io.obadiah.command.CompletableCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ExampleBrigadierCommand extends Command implements CompletableCommand {

    /**
     * Represents a command.
     */
    public ExampleBrigadierCommand() {
        super("brigadier", "myplugin.brigadier");

        this.addAliases(
          "brig"
        );

        this.setDescription("This is an example command which makes use of Mojang's Brigadier library.");

        // Console only.
        this.removePermittedSources(
          CommandSource.PLAYER,
          CommandSource.COMMAND_BLOCK
        );

        this.setMinArgs(1);
        this.setMaxArgs(1);
    }

    @Override
    protected void execute(CommandSender sender, String... args) throws Exception {
        sender.sendMessage(ChatColor.BLUE + "Your word was: " + args[0]);
    }

    @Override
    protected String getCommandUsage() {
        return GET_USAGE_FUNCTION.apply(this);
    }

    @Override
    public LiteralCommandNode<?> getCompletions() {
        return LiteralArgumentBuilder.literal("brigadier")
          .then(RequiredArgumentBuilder.argument("word", StringArgumentType.word()))
          .build();
    }
}
