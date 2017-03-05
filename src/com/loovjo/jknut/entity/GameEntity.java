package com.loovjo.jknut.entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.block.BlockType;
import com.loovjo.loo2D.utils.Vector;

public abstract class GameEntity {

	public Optional<GameLevel> level;

	protected Vector pos = new Vector(0, 0);
	public Optional<Vector> moveTo = Optional.empty();

	protected int direction;

	public GameEntity(Vector pos, Optional<GameLevel> level) {
		this.pos = pos;
		this.level = level;
	}

	public void render(Graphics2D g, int xOnScreen, int yOnScreen, int playerWidth, int playerHeight, int width,
			int height) {
		g.drawImage(getImage(), xOnScreen, yOnScreen, playerWidth, playerHeight, null);
	}

	public void update() {
		// Vector delta = moveTo.orElse(pos).sub(pos).div(4);

		Vector delta = moveTo.orElse(pos).sub(pos);
		if (delta.getLength() > 0.1)
			delta.setLength(0.1f);
		if (moveTo.isPresent() && moveTo.get().sub(pos).getLength() < 0.1) {
			pos = moveTo.get();
			moveTo = Optional.empty();
		} else {
			pos = pos.add(delta);
		}
	}

	public Vector getPosition() {
		return moveTo.orElse(pos);
	}

	public Vector getPositionOnScreen() {
		return pos;
	}

	public BufferedImage getImage() {
		BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);

		Graphics g = img.getGraphics();
		g.setColor(new Color(0xFF00FF));
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, img.getWidth() / 2, img.getHeight() / 2);
		g.fillRect(img.getWidth() / 2, img.getHeight() / 2, img.getWidth() / 2, img.getHeight() / 2);

		return img;
	}

	public boolean step(Optional<GameEntity> oEntity) {
		return true;
	}

	public boolean canMoveAtAll() {
		return !moveTo.isPresent();
	}

	public boolean canMove(int direction) {
		Vector nextPos = getPosition().moveInDir(direction * 2);
		
		if (level.isPresent()) {
			GameEntity clone = clone();
			if (level.isPresent())
				clone.level = Optional.of(level.get().clone());
			
			clone.pos = nextPos;
			clone.moveTo = Optional.of(nextPos);
			clone.direction = direction;
			
			if (!level.get().clone().level.getOrDefault(nextPos, BlockType.FLOOR).step(Optional.of(clone))) {
				return false;
			}
			if (!level.get().clone().getEntitiesAt(nextPos).stream()
					.allMatch(e -> e == this || e.step(Optional.of(clone)))) {
				return false;
			}
		}
		return true;
	}

	public boolean move(int direction) {
		this.direction = direction;
		if (!canMoveAtAll())
			return false;
		if (!canMove(direction))
			return false;

		moveTo = Optional.of(pos.moveInDir(direction * 2));
		moveTo = Optional.of(new Vector((int) moveTo.get().getX(), (int) moveTo.get().getY()));

		if (level.isPresent()) {
			if (!level.get().level.getOrDefault(getPosition(), BlockType.FLOOR).step(Optional.of(this))) {
				moveTo = Optional.empty();
				return false;
			}
			if (!level.get().getEntitiesAt(getPosition()).stream()
					.allMatch(e -> e == this || e.step(Optional.of(this)))) {
				moveTo = Optional.empty();
				return false;
			}
		}
		return true;
	}

	public void setPosition(Vector pos) {
		this.pos = pos;
		moveTo = Optional.empty();
	}

	public int getDirection() {
		return direction;
	}
	
	public String toString() {
		return getClass().getSimpleName() + "(pos=" + pos + ",dir=" + direction + ")";
	}
	
	@Override
	public GameEntity clone() {
		GameEntity clone = new GameEntity(pos, level) {
		};
		clone.moveTo = moveTo;
		clone.direction = direction;
		return clone;
	}

	public char getChar() {
		if (this instanceof Player)
			return 'x';
		if (this instanceof EntityMovable)
			return 'm';
		if (this instanceof Teeth)
			return 'z';
		if (this instanceof Tank)
			return 'T';
		if (this instanceof Bug)
			return 'u';
		if (this instanceof Glider)
			return 'A';
		if (this instanceof BounceBall)
			return '*';
		return '?';
	}
}
