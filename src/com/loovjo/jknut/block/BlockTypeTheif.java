package com.loovjo.jknut.block;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;

public class BlockTypeTheif extends BlockTypeOnFloor {

	public BlockTypeTheif(char blockChr, BufferedImage img) {
		super(blockChr, img);
	}
	

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		oEntity.ifPresent(entity -> {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				player.removeAllItems();
			}
		});
		return true;
	}

}
