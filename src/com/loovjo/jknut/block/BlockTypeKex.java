package com.loovjo.jknut.block;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;

public class BlockTypeKex extends BlockTypeOnFloor {

	public BlockTypeKex(char blockChr, BufferedImage img) {
		super(blockChr, img);
	}
	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		oEntity.ifPresent(entity -> {
			if (entity instanceof Player) {
				Player player = (Player)entity;
				player.kex++;
				if (player.level.isPresent()) {
					player.level.get().level.remove(player.getPosition());
					player.level.get().level.put(player.getPosition(), BlockType.FLOOR);
				}
			}
		});
		return true;
	}
	

}
