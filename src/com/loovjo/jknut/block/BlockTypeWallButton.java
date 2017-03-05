package com.loovjo.jknut.block;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;

public class BlockTypeWallButton extends BlockTypeOnFloor {

	public BlockTypeWallButton(char blockChr, BufferedImage img) {
		super(blockChr, img);
	}

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(entity -> entity.level.map(level -> {

			if (entity instanceof Player) {
				level.level.put(entity.getPosition(), BlockType.WALL);
				return true;
			}

			return false;
		}).orElse(false)).orElse(false);
	}

}
