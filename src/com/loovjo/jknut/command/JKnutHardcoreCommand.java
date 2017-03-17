package com.loovjo.jknut.command;

import java.util.Arrays;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.GameScene;

public class JKnutHardcoreCommand implements JKnutCommand {

	@Override
	public Optional<String> run(String a, GameLevel level) {
		if (level.owner.isPresent()) {
			System.out.println("Hej");
			GameScene.levels_dir = "Levels/Hardcore/";
			GameScene.LEVELS = Arrays.asList(new String[] { "Stage1", "Stage2", "Stage3" });
			if (level.owner.get().loadNextLevel(1))
				return Optional.empty();
		}
		return Optional.of("Failed to load hardcore");
	}

}
