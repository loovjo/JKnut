package com.loovjo.jknut.command;

import java.util.HashMap;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;

public class CommandExecService {

	public static HashMap<String, JKnutCommand> commands = new HashMap<String, JKnutCommand>();

	static {
		commands.put("write", new JKnutWriteCommand());
		commands.put("next", new JKnutCommand() {

			@Override
			public Optional<String> run(String args, GameLevel level) {
				int skip = 1;
				if (args.length() > 0) {
					try {
						skip = Integer.parseInt(args);
					} catch (NumberFormatException e) {
						return Optional.of("Number not understood '" + args + "'");
					}
				}
				if (!level.owner.isPresent()) {
					return Optional.of("GameScene not present. This is bad");
				}
				level.owner.get().loadNextLevel(skip);
				return Optional.empty();
			}
		});
		commands.put("load", new JKnutCommand() {

			@Override
			public Optional<String> run(String args, GameLevel level) {
				int levelNum = 1;
				if (args.length() > 0) {
					try {
						levelNum = Integer.parseInt(args);
					} catch (NumberFormatException e) {
						return Optional.of("Number not understood '" + args + "'");
					}
				}
				if (!level.owner.isPresent()) {
					return Optional.of("GameScene not present. This is bad");
				}
				level.owner.get().currentLevel = "";
				boolean success = level.owner.get().loadNextLevel(levelNum);
				if (success)
					return Optional.empty();
				return Optional.of("Could not load level " + levelNum);
			}
		});
		commands.put("edit", new JKnutEditCommand(false));
		commands.put("clear", new JKnutEditCommand(true));
		commands.put("stats", new JKnutStatisticsCommand());
	}

	public static Optional<String> run(String command, GameLevel level) {

		for (String commandText : commands.keySet()) {
			if (command.startsWith(":" + commandText.charAt(0))) {
				if (command.indexOf(' ') == -1)
					return commands.get(commandText).run("", level);
				return commands.get(commandText).run(command.substring(command.indexOf(' ')).trim(), level);
			}
		}
		if (command.trim().equals(":"))
			return Optional.empty();

		return Optional.of("Command not found");
	}

}
