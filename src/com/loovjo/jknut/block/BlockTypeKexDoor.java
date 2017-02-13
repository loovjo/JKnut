package com.loovjo.jknut.block;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.entity.GameEntity;

public class BlockTypeKexDoor extends BlockTypeOnFloor {

	public BlockTypeKexDoor(char blockChr, BufferedImage img) {
		super(blockChr, img);
	}
	
	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		oEntity.ifPresent(entity -> {
			if (!entity.level.get().level.values().stream().anyMatch(b -> b instanceof BlockTypeKex)) {
				if (entity.level.isPresent()) {
					entity.level.get().level.remove(entity.getPosition());
					entity.level.get().level.put(entity.getPosition(), BlockType.FLOOR);
				}
			}
			
		});
		return false;
	}
	

}
