package com.loovjo.jknut.entity;

import java.awt.image.BufferedImage;
import java.util.Optional;

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

	public void update() {
		super.update();

		if (level.isPresent()) {
			Vector playerPos = level.get().getPlayer().getPosition();
			if (playerPos.equals(getPosition())) {
				// Kill player
				level.get().getPlayer().dead = true;
			}
			int dirTo = getPathTo(playerPos, level.get());
			move(dirTo);
		}
	}

	public int getPathTo(Vector pos, GameLevel level) {
		double dir = pos.sub(getPosition()).mul(new Vector(1, -1)).getRotation() / 360.0;
		return (int) (dir * 4) + 3;
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

}
