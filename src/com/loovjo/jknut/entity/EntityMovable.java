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

		if (level.isPresent() && canMoveAtAll()) {
			if (level.get().level.get(getPosition()) == BlockType.WATER) {
				
				level.get().level.put(getPosition(), BlockType.SUBMERGED_MOVABLE);
				level.get().entities.removeAll(level.get().getEntitiesAt(getPosition()));
			}
		}
	}

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(entity -> {
			if (moveTo.isPresent())
				return false;
			if (!canMove(entity.direction)) {
				return false;
			}
			
			move(entity.direction);
			
			return true;
		}).orElse(false);
	}

	public BufferedImage getImage() {
		return texture;
	}
	
	@Override
	public EntityMovable clone() {
		EntityMovable clone = new EntityMovable(pos, level);
		clone.moveTo = moveTo;
		clone.direction = direction;
		return clone;
	}

}
