package com.loovjo.jknut.block;

import java.awt.image.BufferedImage;
import java.util.Optional;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import com.loovjo.jknut.entity.EntityMovable;
import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;

public class BlockTypeGravel extends BlockType {

	public BlockTypeGravel(char blockChr, BufferedImage img) {
		super(blockChr, img);
	}
	
	public boolean step(Optional<GameEntity> optional) {
		return optional.map(e -> e instanceof Player || e instanceof EntityMovable).orElse(false);
	}

}
