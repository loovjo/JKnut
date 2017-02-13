package com.loovjo.jknut.block;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.entity.GameEntity;

public class BlockTypeWall extends BlockType {

	public BlockTypeWall(char ch, BufferedImage image) {
		super(ch, image);
	}

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return false;
	}

}
