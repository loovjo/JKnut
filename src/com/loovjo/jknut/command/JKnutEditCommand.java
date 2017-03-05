package com.loovjo.jknut.command;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.GameLevelBuilder;

public class JKnutEditCommand implements JKnutCommand {
	
	public boolean clear;
	
	public JKnutEditCommand(boolean clear) {
		this.clear = clear;
	}
	
	@Override
	public Optional<String> run(String args, GameLevel level) {
		if (args.trim().length() == 0) {
			if (clear) {
				level.owner.get().level = new GameLevelBuilder(level.owner);
				return Optional.empty();
			}
			level.owner.get().level = new GameLevelBuilder(level);
		}
		String pathName = args.trim();

		File file = new File(pathName);
		if (!file.exists())
			return Optional.of("Can't find " + file.getPath());
		if (!file.canRead())
			return Optional.of("Can't read " + file.getPath());
		try {
			Path path = file.toPath();
			String[] lines = (String[]) Files.readAllLines(path).toArray(new String[Files.readAllLines(path).size()]);
			
			level.owner.get().level = new GameLevelBuilder(GameLevel.LOAD_LEVEL(level.owner, lines).get());
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.of("There was an error opening the file " + pathName + ". See console for details.");
		}

		return Optional.empty();
	}

}
