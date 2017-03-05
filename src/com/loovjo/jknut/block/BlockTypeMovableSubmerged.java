package com.loovjo.jknut.block;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;

public class BlockTypeMovableSubmerged extends BlockType {

	public BlockTypeMovableSubmerged(char blockChr) {
		super(blockChr, new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB));

		Graphics g = img.getGraphics();
		g.setColor(new Color(128, 64, 0));
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
	}

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(entity -> entity.level.map(level -> {
			if (entity instanceof Player)
				level.level.put(entity.getPosition(), BlockType.FLOOR);
			return entity instanceof Player;
		}).orElse(false)).orElse(false);
	}

}
