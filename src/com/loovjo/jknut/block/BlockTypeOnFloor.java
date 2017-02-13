package com.loovjo.jknut.block;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.loovjo.jknut.GameLevel;

public abstract class BlockTypeOnFloor extends BlockType {
	
	public BlockTypeOnFloor(char blockChr, BufferedImage img) {
		super(blockChr, img);
	}

	@Override
	public void render(Graphics2D g, GameLevel level, int xOnScreen, int yOnScreen, int width, int height) {
		BlockType.FLOOR.render(g, level, xOnScreen, yOnScreen, width, height);
		super.render(g, level, xOnScreen, yOnScreen, width, height);
	}
}
