package com.loovjo.jknut.block;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.entity.GameEntity;

public class BlockTypeKexDoor extends BlockType {

	public BufferedImage kexLeftImg;
	public BufferedImage noKexLeftImg;
	
	public BlockTypeKexDoor(char blockChr, BufferedImage kexLeftImg, BufferedImage noKexLeftImg) {
		super(blockChr, null);
		overrideRender = true;
		this.kexLeftImg = kexLeftImg;
		this.noKexLeftImg = noKexLeftImg;
	}

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(entity -> {
			if (!entity.level.get().level.values().stream().anyMatch(b -> b instanceof BlockTypeKex)) {
				if (entity.level.isPresent()) {
					entity.level.get().level.remove(entity.getPosition());
					entity.level.get().level.put(entity.getPosition(), BlockType.FLOOR);
					return true;
				}
				return false;
			}
			return false;
		}).orElse(false);
	}

	@Override
	public void render(Graphics2D g, GameLevel level, int xOnScreen, int yOnScreen, int width, int height) {
		BlockType.FLOOR.render(g, level, xOnScreen, yOnScreen, width, height);
		if (level.level.values().stream().anyMatch(b -> b instanceof BlockTypeKex)) { // Kex left
			g.drawImage(kexLeftImg, xOnScreen, yOnScreen, width, height, null);
		}
		else {
			g.drawImage(noKexLeftImg, xOnScreen, yOnScreen, width, height, null);
		}
		
	}
	
}
