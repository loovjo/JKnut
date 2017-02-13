package com.loovjo.jknut.block;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;
import com.loovjo.jknut.PlayerItem;

public class BlockTypeWater extends BlockType {

	public BlockTypeWater(char blockChr, BufferedImage img) {
		super(blockChr, img);
	}

	@Override
	public boolean step(Optional<GameEntity> oPlayer) {
		return oPlayer.map(entity -> {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				if (player.hasItem(PlayerItem.WATER_FLIPPERS)) {
					return true;
				}
				player.dead = true;
			}
			return false;
		}).orElse(false);
	}
}
