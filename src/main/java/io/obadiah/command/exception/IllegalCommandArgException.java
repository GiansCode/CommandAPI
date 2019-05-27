package io.obadiah.command.exception;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class IllegalCommandArgException extends Exception {

    private final String argumentName;
    private final Class<?> requiredType;

    public IllegalCommandArgException(String argumentName, Class<?> requiredType) {
        super("Incorrect argument type for, " + argumentName + ". Required type: " + requiredType.getSimpleName());

        this.argumentName = argumentName;
        this.requiredType = requiredType;
    }

    public String getArgumentName() {
        return this.argumentName;
    }

    public Class<?> getRequiredType() {
        return this.requiredType;
    }
}
