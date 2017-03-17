package com.loovjo.jknut.entity;

import java.awt.image.BufferedImage;
import java.net.StandardSocketOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import com.loovjo.jknut.GameLevel;
import com.loovjo.loo2D.utils.ImageLoader;
import com.loovjo.loo2D.utils.Vector;

public class Teeth extends GameEntity {

	private static BufferedImage IMG_UP = ImageLoader.getImage("/Texture/Objects/Teeth/U.png").toBufferedImage();
	private static BufferedImage IMG_DOWN = ImageLoader.getImage("/Texture/Objects/Teeth/D.png").toBufferedImage();
	private static BufferedImage IMG_LEFT = ImageLoader.getImage("/Texture/Objects/Teeth/L.png").toBufferedImage();
	private static BufferedImage IMG_RIGHT = ImageLoader.getImage("/Texture/Objects/Teeth/R.png").toBufferedImage();

	public Teeth(Vector pos, Optional<GameLevel> level) {
		super(pos, level);
	}

	@Override
	public void update() {
		super.update();
		if (level.isPresent() && level.get().getPlayer().isPresent()) {
			Vector playerPos = level.get().getPlayer().get().getPosition();
			if (playerPos.equals(getPosition())) {
				// Kill player
				level.get().getPlayer().get().die();
			}
			int dirTo = getPathTo(playerPos, level.get());
			
			
			if (dirTo >= 0) {
				if (getPosition().getLengthToSqrd(playerPos) > getPosition().moveInDir(dirTo * 2)
						.getLengthToSqrd(playerPos)) {
					move(dirTo);
				}
			}
		}
	}

	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(e -> e instanceof Player).orElse(false);
	}

	public int getPathTo(Vector pos, GameLevel level) {
		for (int i : Arrays
				.asList(1, 2, 3, 4)
				.stream().sorted((a, b) -> (int) (
						getPosition().moveInDir(a * 2).getLengthToSqrd(pos) - getPosition().moveInDir(b * 2).getLengthToSqrd(pos)))
				.collect(Collectors.toList())) {
			if (canMove(i)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public BufferedImage getImage() {
		switch (getDirection() % 4) {
		case 0:
			return IMG_UP;
		case 1:
			return IMG_RIGHT;
		case 2:
			return IMG_DOWN;
		case 3:
			return IMG_LEFT;
		}
		return super.getImage();
	}

	@Override
	public Teeth clone() {
		Teeth clone = new Teeth(pos, level);
		clone.moveTo = moveTo;
		clone.direction = direction;
		return clone;
	}
}
