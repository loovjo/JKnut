package com.loovjo.jknut.block;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;

public class BlockTypeFakeWall extends BlockType {

	public boolean disappaer;

	public BlockTypeFakeWall(char blockChr, BufferedImage img, boolean disappear) {
		super(blockChr, img);
		this.disappaer = disappear;
	}

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(entity -> entity.level.map(level -> {
			
			if (entity instanceof Player) {
				if (disappaer) {
					level.level.put(entity.getPosition(), BlockType.FLOOR);
					return true;
				}
				else {
					level.level.put(entity.getPosition(), BlockType.WALL);
					return false;
				}
			}

			return false;
		}).orElse(false)).orElse(false);
	}

}
