package org.apache.oozie.cli;

import org.apache.commons.cli.CommandLine;

/**
 * Bean that represents a parsed command.
 */
public class Command {
    private String name;
    private CommandLine commandLine;

    public Command(String name, CommandLine commandLine) {
        this.name = name;
        this.commandLine = commandLine;
    }

    /**
     * Return the command name.
     *
     * @return the command name.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the command line.
     *
     * @return the command line.
     */
    public CommandLine getCommandLine() {
        return commandLine;
    }
}
