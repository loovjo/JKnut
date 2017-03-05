package com.loovjo.jknut.block;

import java.util.Optional;

import com.loovjo.jknut.PlayerItem;
import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;

public class BlockTypePlayerItem extends BlockTypeOnFloor {

	public final PlayerItem item;

	public BlockTypePlayerItem(char blockChr, PlayerItem item) {
		super(blockChr, item.img);
		this.item = item;
	}

	@Override
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(entity -> {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				boolean success = player.recieveItem(item);

				if (success) {
					if (player.level.isPresent() && player.level.get().owner.isPresent())
						player.level.get().owner.get().stats.inc("itemsCollected");
					player.level.ifPresent(level -> level.level.remove(player.getPosition()));
				}
				return true;
			}
			return false;
		}).orElse(true);
	}

}
