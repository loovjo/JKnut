package com.loovjo.jknut;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.loovjo.loo2D.utils.ImageLoader;

public enum PlayerItem {
	
	KEY_R("Key.png", Color.RED), KEY_B("Key.png", Color.BLUE), KEY_G("Key.png", Color.GREEN), KEY_Y("Key.png", Color.YELLOW),
		WATER_FLIPPERS("Flipper.png", null),
		ICE_SKATES("IceSkates.png", null),
		FIRE_SHOES("FireShoes.png", null),
		HOVER_BOOTS("HoverBoots.png", null)
	;
	
	public BufferedImage img;
	public Color col;
	
	private PlayerItem(String imagePath, Color col) {
		img = ImageLoader.getImage("/Texture/Objects/" + imagePath).toBufferedImage();
		if (col != null)
			tint(img, col);
		this.col = col;
	}
	
	public static void tint(BufferedImage image, Color color) {
	    for (int x = 0; x < image.getWidth(); x++) {
	        for (int y = 0; y < image.getHeight(); y++) {
				Color pixelColor = new Color(image.getRGB(x, y), true);
				int r = Math.min(pixelColor.getRed(), color.getRed());
				int g = Math.min(pixelColor.getGreen(), color.getGreen());
				int b = Math.min(pixelColor.getBlue(), color.getBlue());
				int a = pixelColor.getAlpha();
				int rgba = (a << 24) | (r << 16) | (g << 8) | b;
				image.setRGB(x, y, rgba);
			}
		}
	}
}
