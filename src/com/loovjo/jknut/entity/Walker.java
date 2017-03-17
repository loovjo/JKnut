package com.loovjo.jknut.entity;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.block.BlockType;
import com.loovjo.loo2D.utils.ImageLoader;
import com.loovjo.loo2D.utils.Vector;

public class Walker extends GameEntity {

	private static BufferedImage IMG = ImageLoader.getImage("/Texture/Objects/Walker.png").toBufferedImage();

	public Walker(Vector pos, Optional<GameLevel> level) {
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
				int[] dirs = new int[] { 0, 1, 3, 2 };
				if (Math.random() < 0.5)
					dirs = new int[] { 0, 3, 1, 2 };
				
				for (int i : dirs) {
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
	public Walker clone() {
		Walker clone = new Walker(pos, level);
		clone.moveTo = moveTo;
		clone.direction = direction;
		return clone;
	}
}
