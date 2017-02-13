package com.loovjo.jknut.block;

import com.loovjo.loo2D.utils.ImageLoader;

public class BlockTypeConveyor extends BlockType {

	public BlockTypeConveyor(char c, char dir) {
		super(c, ImageLoader.getImage("/Texture/Objects/Conveyor" + dir + ".png").toBufferedImage());
	}
	
	public int getDirection() {
		return "^>v<".indexOf(blockChr);
	}
	
}
