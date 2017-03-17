package com.loovjo.jknut.block;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.entity.EntityMovable;
import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;
import com.loovjo.jknut.PlayerItem;

public class BlockTypeBomb extends BlockTypeOnFloor {

	public BlockTypeBomb(char blockChr, BufferedImage img) {
		super(blockChr, img);
	}

	@Override
	public boolean step(Optional<GameEntity> oPlayer) {
		return oPlayer.map(entity -> {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				player.die();
				return true;
			}
			return true;
		}).orElse(false);
	}
}
