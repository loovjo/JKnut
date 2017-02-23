package com.loovjo.jknut;

import java.util.HashMap;
import java.util.Optional;

public class CommandExecService {

	public static HashMap<String, JKnutCommand> commands = new HashMap<String, JKnutCommand>();

	static {
		commands.put("write", new JKnutWriteCommand());
		commands.put("edit", new JKnutEditCommand());
	}

	public static Optional<String> run(String command, GameLevel level) {

		for (String commandText : commands.keySet()) {
			if (command.startsWith(":" + commandText.charAt(0))) {
				return commands.get(commandText).run(command.substring(command.indexOf(' ')), level);
			}
		}
		if (command == ":")
			return Optional.empty();

		return Optional.of("Command not found");
	}

}
