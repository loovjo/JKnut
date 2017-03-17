package com.loovjo.jknut.entity;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import com.loovjo.jknut.GameLevel;
import com.loovjo.loo2D.utils.ImageLoader;
import com.loovjo.loo2D.utils.Vector;

public class Tank extends GameEntity {

	private static BufferedImage IMG_UP = ImageLoader.getImage("/Texture/Objects/Tank/U.png").toBufferedImage();
	private static BufferedImage IMG_DOWN = ImageLoader.getImage("/Texture/Objects/Tank/D.png").toBufferedImage();
	private static BufferedImage IMG_LEFT = ImageLoader.getImage("/Texture/Objects/Tank/L.png").toBufferedImage();
	private static BufferedImage IMG_RIGHT = ImageLoader.getImage("/Texture/Objects/Tank/R.png").toBufferedImage();

	public Tank(Vector pos, Optional<GameLevel> level) {
		super(pos, level);
	}

	@Override
	public boolean step(Optional<GameEntity> e) {
		return false;
	}

	public void update() {
		super.update();

		move(getDirection());

		if (level.isPresent() && level.get().getEntitiesAt(getPosition()).stream().anyMatch(e -> e instanceof Player)) {
			level.get().getEntitiesAt(getPosition())
					.stream()
					.filter(e -> e instanceof Player)
					.forEach(e -> ((Player) e).die());
		}
	}

	public int getPathTo(Vector pos, GameLevel level) {
		for (int i : Arrays
				.asList(1, 2, 3,
						4)
				.stream().sorted((a, b) -> (int) (getPosition().moveInDir(a * 2).getLengthToSqrd(pos)
						- getPosition().moveInDir(b * 2).getLengthToSqrd(pos)))
				.collect(Collectors.toList())) {
			Vector nextPos = getPosition().moveInDir(i * 2);
			if (!level.level.containsKey(nextPos) || level.level.get(nextPos).step(Optional.empty())) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getDirection() {
		if (level.isPresent())
			return level.get().tank_direction;
		return super.getDirection();
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
	public Tank clone() {
		Tank clone = new Tank(pos, level);
		clone.moveTo = moveTo;
		clone.direction = direction;
		return clone;
	}
}
