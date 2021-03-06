package com.loovjo.jknut.block;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.entity.GameEntity;

public class BlockTypeTFButton extends BlockTypeOnFloor {

	public BlockTypeTFButton(char c, BufferedImage img) {
		super(c, img);
	}

	public boolean step(Optional<GameEntity> oEntity) {
		oEntity.ifPresent(entity -> {
			entity.level.ifPresent(level -> {
				level.tf_on ^= true;
			});
		});
		return true;
	}


}
