package com.loovjo.jknut.entity;

import java.awt.image.BufferedImage;
import java.net.StandardSocketOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import com.loovjo.jknut.GameLevel;
import com.loovjo.loo2D.utils.ImageLoader;
import com.loovjo.loo2D.utils.Vector;

public class DDOSKid extends GameEntity {

	private static BufferedImage IMG = ImageLoader.getImage("/Texture/Objects/DDOSKid.png").toBufferedImage();

	public DDOSKid(Vector pos, Optional<GameLevel> level) {
		super(pos, level);
	}

	public void update() {
		super.update();
	}
	
	@Override
	public BufferedImage getImage() {
		return IMG;
	}
	
	@Override
	public DDOSKid clone() {
		DDOSKid clone = new DDOSKid(pos, level);
		clone.moveTo = moveTo;
		clone.direction = direction;
		return clone;
	}
}
