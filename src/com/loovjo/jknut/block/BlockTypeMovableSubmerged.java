package com.loovjo.jknut.block;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;

public class BlockTypeMovableSubmerged extends BlockType {

	public BlockTypeMovableSubmerged(char blockChr) {
		super(blockChr, null);
	}

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(entity -> entity.level.map(level -> {
			if (entity instanceof Player)
				level.level.put(entity.getPosition(), BlockType.FLOOR);
			return entity instanceof Player;
		}).orElse(false)).orElse(false);
	}

	@Override
	public void render(Graphics2D g, GameLevel level, int xOnScreen, int yOnScreen, int width, int height) {
		g.setColor(new Color(128, 64, 0));
		g.fillRect(xOnScreen, yOnScreen, width, height);
	}

}
