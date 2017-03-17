package com.loovjo.jknut.entity;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.block.BlockType;
import com.loovjo.loo2D.utils.ImageLoader;
import com.loovjo.loo2D.utils.Vector;

public class BounceBall extends GameEntity {

	private static BufferedImage IMG = ImageLoader.getImage("/Texture/Objects/BounceBall.png").toBufferedImage();

	public BounceBall(Vector pos, Optional<GameLevel> level) {
		super(pos, level);
		direction = 1;
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

				for (int i : new int[] { 0, 2 }) {
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
		return IMG;
	}

	@Override
	public BounceBall clone() {
		BounceBall clone = new BounceBall(pos, level);
		clone.moveTo = moveTo;
		clone.direction = direction;
		return clone;
	}
}
