package com.loovjo.jknut.command;

import java.util.Optional;

import com.loovjo.jknut.GameLevel;

public interface JKnutCommand {
	
	public Optional<String> run(String args, GameLevel level);
}
