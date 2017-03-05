package com.loovjo.jknut.block;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.PlayerItem;
import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;

public class BlockTypeFire extends BlockTypeOnFloor {

	public BlockTypeFire(char blockChr, BufferedImage img) {
		super(blockChr, img);
	}

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(entity -> {
			if (entity instanceof Player) {
				Player player = (Player)entity;
				if (player.hasItem(PlayerItem.FIRE_SHOES)) {
					return true;
				}
				player.die();
			}
			return true;
		}).orElse(false);
	}

}
