package com.loovjo.jknut;

import java.util.Optional;

public interface JKnutCommand {
	
	public Optional<String> run(String args, GameLevel level);
}
