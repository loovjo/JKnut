package com.loovjo.jknut.block;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.entity.GameEntity;

public class BlockTypeTFButton extends BlockType {

	public BlockTypeTFButton(char c, BufferedImage img) {
		super(c, img);
	}

	public boolean step(Optional<GameEntity> oEntity) {
		oEntity.ifPresent(entity -> {
			entity.level.ifPresent(level -> level.tf_on ^= true);
		});
		return true;
	}

	@Override
	public void render(Graphics2D g, GameLevel level, int xOnScreen, int yOnScreen, int width, int height) {
		BlockType.FLOOR.render(g, level, xOnScreen, yOnScreen, width, height);
		super.render(g, level, xOnScreen, yOnScreen, width, height);
	}

}
