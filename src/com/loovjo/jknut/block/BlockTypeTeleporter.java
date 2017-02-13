package com.loovjo.jknut.block;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.loo2D.utils.Vector;

public class BlockTypeTeleporter extends BlockTypeOnFloor {

	public BlockTypeTeleporter(char blockChr, BufferedImage img) {
		super(blockChr, img);
	}

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(en -> {
			if (!en.level.isPresent())
				return false;
			Vector telepos = en.getPosition().moveInDir(en.getDirection() * 2);
			for (int i = 0; i < 10; i++) {
				telepos = telepos.moveInDir(en.getDirection() * 2);
				if (en.level.get().level.containsKey(telepos)
						&& en.level.get().level.get(telepos) == BlockType.TELEPORTER) {
					break;
				}
			}
			if (en.level.get().level.containsKey(telepos)
					&& en.level.get().level.get(telepos) == BlockType.TELEPORTER) {
				en.setPosition(telepos);
				return true;
			}
			return false;
		}).orElse(false);
	}


}
