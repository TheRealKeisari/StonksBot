package Core.Commands;

import Core.YahooAPI.YahooConnectorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains all the executable commands and handles execution of those commands on demand
 * @author etsubu
 * @version 26 Jul 2018
 *
 */
@Component
public class CommandHandler {
    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);
    private final Map<String, Command> commandMap;
    private final YahooConnectorImpl api;
    
    /**
     * Defines the prefix which a command must begin with
     */
    public static final String COMMAND_PREFIX = "!";
    
    /**
     * Initializes CommandHandler
     * @param api AlphaVantageConnector for searching asset prices
     */
    public CommandHandler(YahooConnectorImpl api, List<Command> commandList) {
        this.commandMap = new HashMap<>();
        this.api = api;
        commandList.forEach(x -> commandMap.put(x.getName(), x));
        log.info("Initialized command handler");
    }

    /**
     *
     * @param name Name of the command to retrieve
     * @return Command object that has the given name
     */
    public Command getCommand(String name) {
        return this.commandMap.get(name);
    }
    /**
     * 
     * @param cmd Message typed
     * @return True if the message starts with the command prefix, false if not
     */
    public boolean isCommand(String cmd) {
        return cmd.startsWith(COMMAND_PREFIX);
    }
    
    /**
     * Parses the command name and executes the corresponding command object
     * @param command Command the user typed
     * @return CommandResult of the command object
     */
    public CommandResult execute(String command) {
        if(!command.startsWith(COMMAND_PREFIX) || command.length() == COMMAND_PREFIX.length()) {
            return new CommandResult("Command did not start with prefix '" + COMMAND_PREFIX + "'", false);
        }
        int index = command.indexOf(" ");
        index = (index == -1) ? command.length() : index;
        String parsed = command.substring(COMMAND_PREFIX.length(), index).toLowerCase();
        Command cmd = this.commandMap.get(parsed);
        if(cmd == null) {
            log.info("Failed to find command for user input: " + command.replaceAll("\n", ""));
            return new CommandResult("Unknown command!", false);
        }
        if(index < command.length() - 1)
            return cmd.execute(command.substring(index + 1));
        return cmd.execute("");
    }
}