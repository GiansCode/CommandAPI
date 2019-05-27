package io.obadiah.command.exception;

import io.obadiah.command.Command;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class IllegalCommandUsageException extends Exception {

    private final String usage;

    public IllegalCommandUsageException(Command command) {
        super("Incorrect command usage for " + command.getName() + "! Correct usage: " + command.getUsage());

        this.usage = command.getUsage();
    }

    public String getUsage() {
        return this.usage;
    }
}
