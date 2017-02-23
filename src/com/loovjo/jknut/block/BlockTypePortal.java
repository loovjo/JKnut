package com.loovjo.jknut.block;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;

public class BlockTypePortal extends BlockType {

	public BlockTypePortal(char blockChr, BufferedImage img) {
		super(blockChr, img);
	}

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		oEntity.ifPresent(player -> player.level.get().levelState = 1);
		return false;
	}

}
