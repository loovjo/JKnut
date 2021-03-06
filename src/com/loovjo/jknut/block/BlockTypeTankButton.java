package com.loovjo.jknut.block;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;

public class BlockTypeTankButton extends BlockTypeOnFloor {

	public BlockTypeTankButton(char c, BufferedImage img) {
		super(c, img);
	}

	public boolean step(Optional<GameEntity> oEntity) {
		oEntity.ifPresent(entity -> {
			if (entity instanceof Player)
				entity.level.ifPresent(level -> level.tank_direction ^= 2);
		});
		return true;
	}

}
