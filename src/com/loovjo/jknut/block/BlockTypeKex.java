package com.loovjo.jknut.block;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;
import com.loovjo.jknut.entity.Teeth;

public class BlockTypeKex extends BlockTypeOnFloor {

	public BlockTypeKex(char blockChr, BufferedImage img) {
		super(blockChr, img);
	}
	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(entity -> {
			
			if (entity instanceof Player) {
				Player player = (Player)entity;
				player.kex++;
				if (player.level.isPresent() && player.level.get().owner.isPresent())
					player.level.get().owner.get().stats.inc("kex");
				
				if (player.level.isPresent()) {
					player.level.get().level.remove(player.getPosition());
					player.level.get().level.put(player.getPosition(), BlockType.FLOOR);
				}
				return true;
			}
			return entity instanceof Teeth;
			
		}).orElse(false);
	}
	

}
