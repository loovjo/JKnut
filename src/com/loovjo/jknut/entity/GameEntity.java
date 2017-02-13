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
	protected Optional<Vector> moveTo = Optional.empty();
	
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
		Vector delta = moveTo.orElse(pos).sub(pos).div(4);

		if (moveTo.isPresent() && moveTo.get().sub(pos).getLength() < 0.1) {
			pos = moveTo.get();
			moveTo = Optional.empty();
		} else {
			pos = pos.add(delta);
		}
	}

	public boolean canMove() {
		return !moveTo.isPresent();
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
	
	public void move(int direction) {
		this.direction = direction;
		
		if (!canMove())
			return;
		
		moveTo = Optional.of(pos.moveInDir(direction * 2));
		moveTo = Optional.of(new Vector((int) moveTo.get().getX(), (int) moveTo.get().getY()));
		
		if (level.isPresent()) {
			if (!level.get().level.getOrDefault(getPosition(), BlockType.FLOOR).step(Optional.of(this))) {
				moveTo = Optional.empty();
			}
			if (!level.get().getEntitiesAt(getPosition()).stream().allMatch(e -> e == this || e.step(Optional.of(this)))) {
				moveTo = Optional.empty();
			}
		}
	}
	
	public void setPosition(Vector pos) {
		this.pos = pos;
		moveTo = Optional.empty();
	}
	
	public int getDirection() {
		return direction;
	}
}
