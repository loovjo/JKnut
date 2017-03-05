package com.loovjo.jknut.block;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.entity.GameEntity;

public class BlockTypeIce extends BlockType {

	public BlockTypeIce(char blockChr, BufferedImage img) {
		super(blockChr, img);
	}
	
	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return true;
	}

}
