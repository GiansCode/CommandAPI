package io.obadiah.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Command {

    private static final String EMPTY = "";

    private final String name;
    private final Set<Command> subCommands;
    private final Set<String> aliases;

    private String description;
    private String usage;

    private String permission;
    private boolean[] permittedSources;

    private int minArgs;
    private int maxArgs;

    public Command(String name, @Nullable String permission) {
        this.name = name;
        this.subCommands = Sets.newHashSet();
        this.aliases = Sets.newHashSet();

        this.description = EMPTY;
        this.usage = EMPTY;

        this.permission = permission == null ? EMPTY : permission;
        this.permittedSources = new boolean[] {
          true, true, true
        };

        this.minArgs = 0;
        this.maxArgs = 0;
    }

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

    public Command setUsage(String usage) {
        this.description = usage;

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

    public String getDescription() {
        return this.description;
    }

    public String getUsage() {
        return this.usage;
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

    private String createUsage() {
        return "";
    }

    public BukkitCommand asBukkitCommand() {
        return new BukkitCommand(this.name, this.description, this.usage == null ? this.createUsage() : this.usage, Lists.newArrayList(this.aliases)) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                Command.this.fire(sender, args);
                return false;
            }
        };
    }

    private void fire(CommandSender sender, String... args) {
        //
    }
}
