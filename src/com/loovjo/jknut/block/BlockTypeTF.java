package com.loovjo.jknut.block;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.entity.GameEntity;

public class BlockTypeTF extends BlockType {
	
	public boolean isState1;
	
	public BlockTypeTF(char c, BufferedImage img, boolean b) {
		super(c, img);
		isState1 = b;
		
		overrideRender = true;
	}
	
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(en -> {
			return en.level.map(level -> level.tf_on ^ isState1).orElse(false);
		}).orElse(false);
	}

	public void render(Graphics2D g, GameLevel level, int xOnScreen, int yOnScreen, int width, int height) {
		if (isState1 ^ (level == null ? false : level.tf_on)) {
			BlockType.FLOOR.render(g, level, xOnScreen, yOnScreen, width, height);
		}
		else {
			BlockType.WALL.render(g, level, xOnScreen, yOnScreen, width, height);
		}
		g.drawImage(img, xOnScreen, yOnScreen, width, height, null);
	}
	
}
