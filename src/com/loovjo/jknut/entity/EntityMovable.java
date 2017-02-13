package com.loovjo.jknut.entity;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.block.BlockType;
import com.loovjo.loo2D.utils.ImageLoader;
import com.loovjo.loo2D.utils.Vector;

public class EntityMovable extends GameEntity {

	public BufferedImage texture = ImageLoader.getImage("/Texture/Objects/Moveable.png").toBufferedImage();

	public EntityMovable(Vector pos, Optional<GameLevel> level) {
		super(pos, level);
	}

	public void update() {
		super.update();
	}

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(entity -> {
			move(entity.direction);

			if (canMove() && level.isPresent()) {

				Vector nextPos = getPosition().clone().moveInDir(entity.direction * 2);
				
				if (level.get().level.get(nextPos) == BlockType.WATER) {
					
					level.get().level.put(nextPos, BlockType.SUBMERGED_MOVABLE);
					level.get().entities.remove(this);
				}
			}
			return false;
		}).orElse(false);
	}

	public BufferedImage getImage() {
		return texture;
	}

}
