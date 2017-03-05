package com.loovjo.jknut.block;

import java.awt.Graphics2D;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.PlayerItem;
import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;
import com.loovjo.loo2D.utils.ImageLoader;

public class BlockTypeLock extends BlockType {

	public final PlayerItem key;

	public BlockTypeLock(char blockChr, PlayerItem key) {
		super(blockChr, ImageLoader.getImage("/Texture/Objects/Lock.png").toBufferedImage());
		PlayerItem.tint(img, key.col);
		this.key = key;
		
		overrideRender = true;
	}

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(entity -> {
			if (entity instanceof Player) {
				Player player = (Player)entity;
				if (key == PlayerItem.KEY_G && player.hasItem(key)) {
					player.level.ifPresent(level -> level.level.remove(player.getPosition()));
					return true;
				}
				if (player.takeItem(key)) {
					player.level.ifPresent(level -> level.level.remove(player.getPosition()));
					return true;
				}
			}
			return false;
		}).orElse(false);
	}
	
	@Override
	public void render(Graphics2D g, GameLevel level, int xOnScreen, int yOnScreen, int width, int height) {
		BlockType.WALL.render(g, level, xOnScreen, yOnScreen, width, height);
		super.render(g, level, xOnScreen, yOnScreen, width, height);
	}

}
