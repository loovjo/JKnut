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

	public static final float PLAYER_SPEED_FAST = 0.2f;
	public static final float PLAYER_SPEED_SLOW = 0.1f;

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

	private boolean dead = false;

	public int kex;

	public int max_items_X = 4;
	public int max_items_Y = 2;
	private ArrayList<PlayerItem> items = new ArrayList<PlayerItem>();
	public float hud_height = 0;

	public int walkDirection = -1;

	public Player(Vector pos, Optional<GameLevel> level) {
		super(pos, level);
	}
	
	public boolean step(Optional<GameEntity> oEntity) {
		return oEntity.map(e ->! (e instanceof Player)).orElse(true);
	}

	public void update() {
		Vector delta;

		if (!moveTo.isPresent()) {
			if (walkDirection != -1) {
				direction = walkDirection;
				walkDirection = -1;
				moveForward();
				if (level.isPresent() && level.get().owner.isPresent())
					level.get().owner.get().stats.inc("steps");
			}
		} else if (moveTo.get().sub(pos).getLength() >= 0.1) {
			walkDirection = -1;
		}

		delta = getPosition().sub(pos);
		if (isSliding() || isRunning) {
			if (delta.getLength() > PLAYER_SPEED_FAST)
				delta.setLength(PLAYER_SPEED_FAST);

		} else {
			if (delta.getLength() > PLAYER_SPEED_SLOW)
				delta.setLength(PLAYER_SPEED_SLOW);
		}

		if (moveTo.isPresent() && moveTo.get().sub(pos).getLength() < 0.1) {
			pos = moveTo.get();
			moveTo = Optional.empty();

			if (!hasItem(PlayerItem.HOVER_BOOTS)
					&& level.map(level -> level.level.get(getPosition()) instanceof BlockTypeConveyor).orElse(false)) {
				int dir = level.map(level -> ((BlockTypeConveyor) level.level.get(getPosition())).getDirection()).get();

				if (walkDirection == -1 || walkDirection == dir || walkDirection == (dir + 2) % 4) {
					direction = dir;
					moveForward();
				} else {
					direction = walkDirection;
					moveForward();
					if (!moveTo.isPresent())
						walkDirection = direction = dir;
				}

			}
			if (walkDirection != -1) {
				direction = walkDirection;
				walkDirection = -1;
				moveForward();
			}

			if (isSliding()) {
				if (!canMove(direction)) {
					direction = direction ^ 2;
				}
				moveForward();
			}
		} else {
			pos = pos.add(delta);
		}

		if (items.size() > 0) {
			if (hud_height == 0)
				hud_height = 0.1f;
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

	public boolean moveForward() {
		return move(direction);
	}

	@Override
	public boolean canMoveAtAll() {
		return super.canMoveAtAll() && !dead;
	}

	public boolean move(int direction) {
		if (dead) {
			moveTo = Optional.of(pos.moveInDir(direction * 2));
			moveTo = Optional.of(new Vector((int) moveTo.get().getX(), (int) moveTo.get().getY()));
			return true;
		} else {
			return super.move(direction);
		}
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
	
	public void die() {
		dead = true;
		if (level.isPresent() && level.get().owner.isPresent())
			level.get().owner.get().stats.inc("deaths");
	}
	
	public boolean isDead() {
		return dead;
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
		clone.kex = kex;
		clone.direction = direction;
		clone.isRunning = isRunning;
		clone.items = new ArrayList<PlayerItem>(items);
		return clone;
	}
}
