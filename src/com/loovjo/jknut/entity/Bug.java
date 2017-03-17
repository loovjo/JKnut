package com.loovjo.jknut.entity;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.block.BlockType;
import com.loovjo.loo2D.utils.ImageLoader;
import com.loovjo.loo2D.utils.Vector;

public class Bug extends GameEntity {

	private static BufferedImage IMG_UP = ImageLoader.getImage("/Texture/Objects/Bug/U.png").toBufferedImage();
	private static BufferedImage IMG_DOWN = ImageLoader.getImage("/Texture/Objects/Bug/D.png").toBufferedImage();
	private static BufferedImage IMG_LEFT = ImageLoader.getImage("/Texture/Objects/Bug/L.png").toBufferedImage();
	private static BufferedImage IMG_RIGHT = ImageLoader.getImage("/Texture/Objects/Bug/R.png").toBufferedImage();


	public Bug(Vector pos, Optional<GameLevel> level) {
		super(pos, level);
	}

	@Override
	public boolean step(Optional<GameEntity> e) {
		return false;
	}

	public void update() {
		super.update();

		if (level.isPresent() && level.get().getPlayer().isPresent()) {
			Vector playerPos = level.get().getPlayer().get().getPosition();
			if (playerPos.equals(getPosition())) {
				// Kill player
				level.get().getPlayer().get().die();
			}
			if (!moveTo.isPresent()) {

				for (int i : new int[] { 3, 0, 1, 2 }) {
					int dir = (direction + i) % 4;
					if (canMove(dir)) {
						move(dir);
						break;

					}
				}
			}

		}
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
	public Bug clone() {
		Bug clone = new Bug(pos, level);
		clone.moveTo = moveTo;
		clone.direction = direction;
		return clone;
	}
}
