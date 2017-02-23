package com.loovjo.jknut;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class JKnutEditCommand implements JKnutCommand {

	@Override
	public Optional<String> run(String args, GameLevel level) {
		
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
