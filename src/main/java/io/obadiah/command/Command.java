package io.obadiah.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.obadiah.command.exception.IllegalCommandArgException;
import io.obadiah.command.exception.IllegalCommandUsageException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Command {

    /**
     * A handy method for automatically converting a command instance into a usage string.
     */
    protected static final Function<Command, String> GET_USAGE_FUNCTION = command -> UsageBuilder.from(command).get();
    private static final String EMPTY = "";

    private final String name;
    private final Set<Command> subCommands;
    private final Set<String> aliases;
    private final String usage;

    private String description;

    private String permission;
    private boolean[] permittedSources;

    private int minArgs;
    private int maxArgs;

    /**
     * Represents a command.
     *
     * @param name Name of the command.
     * @param permission The optional permission node that is required to execute this command.
     */
    public Command(String name, @Nullable String permission) {
        this.name = name;
        this.subCommands = Sets.newHashSet();
        this.aliases = Sets.newHashSet();
        this.usage = this.getCommandUsage();

        this.description = EMPTY;

        this.permission = permission == null ? EMPTY : permission;
        this.permittedSources = new boolean[] {
          true, true, true
        };

        this.minArgs = 0;
        this.maxArgs = 0;
    }

    /**
     * Adds an array of subcommands to this command.
     *
     * @param commands Commands to register as subcommands.
     *
     * @return This command instance.
     */
    public Command addSubCommands(Command... commands) {
        this.subCommands.addAll(Stream.of(commands).collect(Collectors.toList()));

        return this;
    }

    public Command addAliases(String... aliases) {
        this.aliases.addAll(Stream.of(aliases).collect(Collectors.toList()));

        return this;
    }

    public Command setDescription(String description) {
        this.description = description;

        return this;
    }

    public Command setPermission(String permission) {
        this.permission = permission;

        return this;
    }

    public Command addPermittedSources(CommandSource... sources) {
        Stream.of(sources).forEach(src -> this.permittedSources[src.ordinal()] = true);

        return this;
    }

    public Command removePermittedSources(CommandSource... sources) {
        Stream.of(sources).forEach(src -> this.permittedSources[src.ordinal()] = false);

        return this;
    }

    public Command setMinArgs(int minArgs) {
        if (minArgs < 0) {
            throw new IllegalArgumentException("Minimum arguments cannot be set below 0!");
        }

        this.minArgs = minArgs;

        return this;
    }

    public Command setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;

        return this;
    }

    public String getName() {
        return this.name;
    }

    public Set<Command> getSubCommands() {
        return Collections.unmodifiableSet(this.subCommands);
    }

    public Set<String> getAliases() {
        return Collections.unmodifiableSet(this.aliases);
    }

    public String getUsage() {
        return this.usage;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPermission() {
        return this.permission;
    }

    public CommandSource[] getPermittedSources() {
        CommandSource[] sources = new CommandSource[3];
        for (int i = 0; i < this.permittedSources.length; i++) {
            if (this.permittedSources[i]) {
                sources[i] = CommandSource.values()[i];
            }
        }

        return sources;
    }

    public boolean isPlayerPermitted() {
        return this.permittedSources[0];
    }

    public boolean isConsolePermitted() {
        return this.permittedSources[1];
    }

    public boolean isCommandBlockPermitted() {
        return this.permittedSources[2];
    }

    public int getMinArgs() {
        return this.minArgs;
    }

    public int getMaxArgs() {
        return this.maxArgs;
    }

    public BukkitCommand asBukkitCommand() {
        return new BukkitCommand(this.name, this.description, this.usage, Lists.newArrayList(this.aliases)) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                Command.this.fire(sender, args);
                return false;
            }
        };
    }

    private void fire(CommandSender sender, String... args) {
        if (args.length == 0) {
            this.runChecks(sender, args);
            return;
        }

        Command command = this.subCommands.stream()
          .filter(c -> c.getName().equalsIgnoreCase(args[0]))
          .findFirst()
          .orElse(null);

        if (command == null) {
            this.runChecks(sender, args);
            return;
        }

        command.fire(sender, Stream.of(args).skip(1).toArray(String[]::new));
    }

    private void runChecks(CommandSender sender, String... args) {
        CommandSource source = CommandSource.fromSender(sender);

        if (!this.permittedSources[source.ordinal()]) {
            sender.sendMessage(ChatColor.RED + "This command cannot be executed by your account type");
            return;
        }

        if (source == CommandSource.PLAYER && !sender.hasPermission(this.permission)) {
            sender.sendMessage(ChatColor.RED + "You have insufficient permissions to execute this command!");
            return;
        }

        if (args.length > this.maxArgs) {
            return;
        }

        if (args.length < this.minArgs) {
            return;
        }

        try {
            this.execute(sender, args);
        } catch (Exception e) {
            if (e instanceof IllegalCommandArgException) {
                e.printStackTrace();
                IllegalCommandArgException ex = (IllegalCommandArgException) e;

                sender.sendMessage(ChatColor.RED + "You have specified the wrong argument type for, " + ex.getArgumentName() +
                  ", expected a " + ex.getRequiredType().getSimpleName() + "!");
                return;
            }

            if (e instanceof IllegalCommandUsageException) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + "Incorrect command usage, the correct usage is:\n" + this.getUsage());
                return;
            }

            e.printStackTrace();
        }
    }

    protected abstract void execute(CommandSender sender, String... args) throws Exception;

    protected abstract String getCommandUsage();
}
