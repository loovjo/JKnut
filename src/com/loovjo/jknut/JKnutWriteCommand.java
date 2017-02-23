package com.loovjo.jknut;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;

public class JKnutWriteCommand implements JKnutCommand {

	@Override
	public Optional<String> run(String args, GameLevel level) {
		String path = args.trim();
		if (path.isEmpty() && level.owner.isPresent()) {
			path = level.owner.get().currentLevel.trim();
		}
		if (path.isEmpty()) {
			return Optional.of("No file name specified. Run as  :write FILE");
		}
		
		try {
			PrintStream ps = new PrintStream(new File(path));
			
			ps.print(level.asLevelString());
			
			ps.close();
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.of("There was an error writing to the file " + path + ". See console for details.");
		}
		
		return Optional.empty();
	}

}
