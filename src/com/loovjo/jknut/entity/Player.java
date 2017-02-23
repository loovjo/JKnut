package com.loovjo.jknut.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.PlayerItem;
import com.loovjo.jknut.block.BlockType;
import com.loovjo.jknut.block.BlockTypeConveyor;
import com.loovjo.loo2D.utils.ImageLoader;
import com.loovjo.loo2D.utils.Vector;

public class Player extends GameEntity {

	private static BufferedImage IMG_UP = ImageLoader.getImage("/Texture/Character/PlayerU.png").toBufferedImage();
	private static BufferedImage IMG_RIGHT = ImageLoader.getImage("/Texture/Character/PlayerR.png").toBufferedImage();
	private static BufferedImage IMG_DOWN = ImageLoader.getImage("/Texture/Character/PlayerD.png").toBufferedImage();
	private static BufferedImage IMG_LEFT = ImageLoader.getImage("/Texture/Character/PlayerL.png").toBufferedImage();
	private static BufferedImage IMG_W_UP = ImageLoader.getImage("/Texture/Character/Player_WU.png").toBufferedImage();
	private static BufferedImage IMG_W_RIGHT = ImageLoader.getImage("/Texture/Character/Player_WR.png")
			.toBufferedImage();
	private static BufferedImage IMG_W_DOWN = ImageLoader.getImage("/Texture/Character/Player_WD.png")
			.toBufferedImage();
	private static BufferedImage IMG_W_LEFT = ImageLoader.getImage("/Texture/Character/Player_WL.png")
			.toBufferedImage();

	public int direction; // 0 = up, 1 = right, 2 = down, 3 = right
	public boolean isRunning = false;

	public boolean dead = false;

	public int kex;

	public int max_items_X = 4;
	public int max_items_Y = 2;
	private ArrayList<PlayerItem> items = new ArrayList<PlayerItem>();
	public float hud_height = 0;

	public Player(Vector pos, Optional<GameLevel> level) {
		super(pos, level);

	}

	public void update() {
		Vector delta;
		if (isSliding() || isRunning) {
			delta = getPosition().sub(pos);
			if (delta.getLength() > 0.2)
				delta.setLength(0.2f);
		} else {
			delta = moveTo.orElse(pos).sub(pos).div(4);
		}

		if (moveTo.isPresent() && moveTo.get().sub(pos).getLength() < 0.1) {
			pos = moveTo.get();
			moveTo = Optional.empty();

			if (isSliding()) {
				Vector moveToOld = moveTo.orElse(null);
				moveForward();
				if (moveTo.orElse(null) == moveToOld) {
					direction = direction ^ 2;
					moveForward();
				}
			}
			if (!hasItem(PlayerItem.HOVER_BOOTS)
					&& level.map(level -> level.level.get(getPosition()) instanceof BlockTypeConveyor).orElse(false)) {
				int dir = level.map(level -> ((BlockTypeConveyor) level.level.get(getPosition())).getDirection()).get();
				direction = dir;
				moveForward();
			}
		} else {
			pos = pos.add(delta);
		}

		if (items.size() > 0) {
			if (hud_height == 0)
				hud_height = 0.01f;
			if (hud_height < 1)
				hud_height *= 1.1;
			else
				hud_height = 1;
		}
	}

	private boolean isSliding() {
		return !hasItem(PlayerItem.ICE_SKATES)
				&& level.map(level -> level.level.get(getPosition()) == BlockType.ICE).orElse(false);
	}

	public void moveForward() {
		move(direction);
	}

	public void render(Graphics2D g, int xOnScreen, int yOnScreen, int playerWidth, int playerHeight, int width,
			int height) {
		super.render(g, xOnScreen, yOnScreen, playerWidth, playerHeight, width, height);

		float hud_height = this.hud_height * (max_items_Y + 1) - 1;

		int hud_width = playerWidth * max_items_X;
		int hud_x = width - hud_width - 20;
		g.setColor(Color.black);
		g.fillRoundRect(hud_x - 10, -30, hud_width + 20, (int) (hud_height * playerHeight) + 40, 30, 30);
		g.setColor(Color.gray);
		g.fillRoundRect(hud_x - 5, -35, hud_width + 10, (int) (hud_height * playerHeight) + 40, 20, 20);
		for (int hud_cell_y = 0; hud_cell_y < max_items_Y; hud_cell_y++) {
			for (int hud_cell_x = 0; hud_cell_x < max_items_X; hud_cell_x++) {
				int idx = hud_cell_x + hud_cell_y * max_items_X;

				g.drawImage(BlockType.FLOOR.img, hud_x + hud_cell_x * playerWidth,
						(int) ((hud_height - 1) * playerHeight) - hud_cell_y * playerHeight, playerWidth, playerHeight,
						null);

				if (idx < items.size()) {
					g.drawImage(items.get(idx).img, hud_x + hud_cell_x * playerWidth,
							(int) ((hud_height - 1) * playerHeight) - hud_cell_y * playerHeight, playerWidth,
							playerHeight, null);
				}

			}
		}
	}

	@Override
	public BufferedImage getImage() {
		if (level.map(level -> level.level.get(getPosition()) == BlockType.WATER).orElse(false)) {
			switch (direction) {
			case 0:
				return IMG_W_UP;
			case 1:
				return IMG_W_RIGHT;
			case 2:
				return IMG_W_DOWN;
			case 3:
				return IMG_W_LEFT;
			}
		} else {
			switch (direction) {
			case 0:
				return IMG_UP;
			case 1:
				return IMG_RIGHT;
			case 2:
				return IMG_DOWN;
			case 3:
				return IMG_LEFT;
			}
		}
		return IMG_UP;
	}

	public boolean recieveItem(PlayerItem item) {
		if (hasItem(item) || items.size() >= max_items_X * max_items_X) {
			return false;
		}
		items.add(item);
		return true;
	}

	public boolean takeItem(PlayerItem item) {
		if (hasItem(item)) {
			items.remove(item);
			return true;
		}
		return false;
	}

	public boolean hasItem(PlayerItem item) {
		return items.contains(item);
	}

	public boolean removeAllItems() {
		boolean empty = items.isEmpty();
		items.clear();
		return !empty;
	}

	@Override
	public String toString() {
		return "Player(pos=" + pos + ",dir=" + direction + ")";
	}

	@Override
	public Player clone() {
		Player clone = new Player(pos, level);
		clone.moveTo = moveTo;
		clone.direction = direction;
		return clone;
	}
}
