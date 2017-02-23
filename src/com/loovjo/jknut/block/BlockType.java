package com.loovjo.jknut.block;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.PlayerItem;
import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;
import com.loovjo.loo2D.utils.ImageLoader;

public abstract class BlockType {

	public final char blockChr;
	public final BufferedImage img;

	public BlockType(char blockChr, BufferedImage img) {
		this.img = img;
		this.blockChr = blockChr;
	}

	public void update(GameLevel level) {
	}

	public void render(Graphics2D g, GameLevel level, int xOnScreen, int yOnScreen, int width, int height) {		
		g.drawImage(img, xOnScreen, yOnScreen, width, height, null);
	}
	

	public boolean step(Optional<GameEntity> optional) {
		return true;
	}
	
	public String toString() {
		return getClass().getSimpleName() + "('" + blockChr + "')";
	}

	public static Optional<BlockType> getBlockTypeFromChar(char ch) {
		for (Field f : BlockType.class.getFields()) {
			try {
				Object obj = f.get(null);
				if (obj instanceof BlockType) {
					BlockType bt = (BlockType) obj;
					if (bt.blockChr == ch) {
						return Optional.of(bt);
					}
				}
			} catch (Exception e) {
			}
		}
		return Optional.empty();
	}

	public static BlockType WALL = new BlockTypeWall('#', ImageLoader.getImage("/Texture/Objects/Block.png").toBufferedImage());
	public static BlockType FLOOR = new BlockTypeFloor(' ', ImageLoader.getImage("/Texture/Objects/Ground.png").toBufferedImage());
	public static BlockType WATER = new BlockTypeWater('w', ImageLoader.getImage("/Texture/Objects/Water.png").toBufferedImage());
	public static BlockType FIRE = new BlockTypeFire('f', ImageLoader.getImage("/Texture/Objects/Fire.png").toBufferedImage());
	public static BlockType ICE = new BlockTypeIce('i', ImageLoader.getImage("/Texture/Objects/Ice.png").toBufferedImage());
	public static BlockType KEX = new BlockTypeKex('k', ImageLoader.getImage("/Texture/Objects/Point.png").toBufferedImage());
	public static BlockType KEX_DOOR = new BlockTypeKexDoor('h', ImageLoader.getImage("/Texture/Objects/KexDoor.png").toBufferedImage());
	public static BlockType PORTAL = new BlockTypePortal('o', ImageLoader.getImage("/Texture/Objects/Portal.png").toBufferedImage());
	public static BlockType SUBMERGED_MOVABLE = new BlockTypeMovableSubmerged('d');
	public static BlockType TF_BLOCK_STATE_1 = new BlockTypeTF('E', ImageLoader.getImage("/Texture/Objects/TFBlock.png").toBufferedImage(), false);
	public static BlockType TF_BLOCK_STATE_2 = new BlockTypeTF('e', ImageLoader.getImage("/Texture/Objects/TFBlock.png").toBufferedImage(), true);
	public static BlockType TF_BUTTON = new BlockTypeTFButton('!', ImageLoader.getImage("/Texture/Objects/TFButton.png").toBufferedImage());
	public static BlockType TELEPORTER = new BlockTypeTeleporter('c', ImageLoader.getImage("/Texture/Objects/Teleporter.png").toBufferedImage());
	public static BlockType THEIF = new BlockTypeTheif('t', ImageLoader.getImage("/Texture/Objects/Thief.png").toBufferedImage());
	public static BlockType GRAVEL = new BlockTypeGravel(':', ImageLoader.getImage("/Texture/Objects/Gravel.png").toBufferedImage());

	public static BlockType WATER_FLIPPERS = new BlockTypePlayerItem('W', PlayerItem.WATER_FLIPPERS);
	public static BlockType ICE_SKATES = new BlockTypePlayerItem('I', PlayerItem.ICE_SKATES);
	public static BlockType FIRE_SHOES = new BlockTypePlayerItem('F', PlayerItem.FIRE_SHOES);
	public static BlockType HOVER_BOOTS = new BlockTypePlayerItem('V', PlayerItem.HOVER_BOOTS);
	
	public static BlockType CONVEYOR_U = new BlockTypeConveyor('^', 'U');
	public static BlockType CONVEYOR_R = new BlockTypeConveyor('>', 'R');
	public static BlockType CONVEYOR_D = new BlockTypeConveyor('v', 'D');
	public static BlockType CONVEYOR_L = new BlockTypeConveyor('<', 'L');
	
	public static BlockType KEY_R = new BlockTypePlayerItem('R', PlayerItem.KEY_R);
	public static BlockType KEY_G = new BlockTypePlayerItem('G', PlayerItem.KEY_G);
	public static BlockType KEY_B = new BlockTypePlayerItem('B', PlayerItem.KEY_B);
	public static BlockType KEY_Y = new BlockTypePlayerItem('Y', PlayerItem.KEY_Y);

	public static BlockType LOCK_R = new BlockTypeLock('r', PlayerItem.KEY_R);
	public static BlockType LOCK_G = new BlockTypeLock('g', PlayerItem.KEY_G);
	public static BlockType LOCK_B = new BlockTypeLock('b', PlayerItem.KEY_B);
	public static BlockType LOCK_Y = new BlockTypeLock('y', PlayerItem.KEY_Y);

	public static BlockType CROSS_HAIR = new BlockTypeCrossHair('\0', ImageLoader.getImage("/Texture/Objects/CrossHair.png").toBufferedImage());
}
