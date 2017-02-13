package com.loovjo.jknut;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.loovjo.jknut.block.BlockType;
import com.loovjo.jknut.entity.EntityMovable;
import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;
import com.loovjo.jknut.entity.Teeth;
import com.loovjo.loo2D.utils.FileLoader;
import com.loovjo.loo2D.utils.Vector;

public class GameLevel {

	public Vector scroll = new Vector(0, 0);

	public int blockSizeX = 50;
	public int blockSizeY = 50;

	public HashMap<Vector, BlockType> level = new HashMap<Vector, BlockType>();

	public ArrayList<GameEntity> entities = new ArrayList<GameEntity>();

	public boolean won = false;

	public boolean tf_on = false;

	public GameLevel() {

	}

	public Player getPlayer() {
		return (Player) entities.stream().filter(e -> e instanceof Player).findAny().get();
	}

	public void render(Graphics2D g, int width, int height) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);

		Vector scroll = this.scroll;
		if (!getPlayer().dead) {
			HashMap<Vector, BlockType> levelWithSurroundings = new HashMap<Vector, BlockType>(level);

			int firstBlockOnScreenX = (int) Math.round(scroll.getX() - width / blockSizeX / 2 - 1);
			int lastBlockOnScreenX = (int) Math.round(scroll.getX() + width / blockSizeX / 2 + 1);
			int firstBlockOnScreenY = (int) Math.round(scroll.getY() - height / blockSizeY / 2 - 1);
			int lastBlockOnScreenY = (int) Math.round(scroll.getY() + height / blockSizeY / 2 + 1);

			for (int x = firstBlockOnScreenX; x < lastBlockOnScreenX; x++) {
				for (int y = firstBlockOnScreenY; y < lastBlockOnScreenY; y++) {
					if (!levelWithSurroundings.containsKey(new Vector(x, y))) {
						levelWithSurroundings.put(new Vector(x, y), BlockType.FLOOR);
					}
				}
			}

			for (Entry<Vector, BlockType> e : levelWithSurroundings.entrySet()) {
				Vector pos = e.getKey();
				BlockType bt = e.getValue();
				if (pos.getX() + 1 > firstBlockOnScreenX && pos.getX() < lastBlockOnScreenX
						&& pos.getY() + 1 > firstBlockOnScreenY && pos.getY() < lastBlockOnScreenY) {
					bt.render(g, this, (int) ((pos.getX() - scroll.getX()) * blockSizeX) + width / 2,
							(int) ((pos.getY() - scroll.getY()) * blockSizeY) + height / 2, blockSizeX, blockSizeY);
				}
			}

			entities.forEach(
					e -> e.render(g, (int) ((e.getPositionOnScreen().getX() - scroll.getX()) * blockSizeX) + width / 2,
							(int) ((e.getPositionOnScreen().getY() - scroll.getY()) * blockSizeY) + height / 2,
							blockSizeX, blockSizeY, width, height));

			g.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
			g.setColor(Color.WHITE);
			g.drawString("Kex: " + getPlayer().kex, 0, 32);
		} else {
			g.setColor(Color.WHITE);
			g.drawString("sry u ded kiddo", 0, 32);
		}
	}

	public List<GameEntity> getEntitiesAt(Vector pos) {
		return entities.stream().filter(e -> e.getPosition().equals(pos)).collect(Collectors.toList());
	}

	public void update() {
		scroll = scroll.sub(getPlayer().getPosition()).div(1.1f).add(getPlayer().getPosition());

		entities.forEach(GameEntity::update);
	}

	public static Optional<GameLevel> LOAD_LEVEL(String levelName) {
		String[] lines;
		try {
			lines = FileLoader.readFile("/Levels/" + levelName).split("\n");
		} catch (NullPointerException e) {
			e.printStackTrace();
			return Optional.empty();
		}
		GameLevel level = new GameLevel();

		for (int y = 0; y < lines.length; y++) {
			for (int x = 0; x < lines[y].length(); x++) {
				char ch = lines[y].charAt(x);
				level.level.put(new Vector(x, y), BlockType.getBlockTypeFromChar(ch).orElse(BlockType.FLOOR));
				if (ch == 'x') {
					level.entities.add(new Player(new Vector(x, y), Optional.of(level)));
					level.scroll = level.getPlayer().getPosition();
				}
				if (ch == 'm') {
					level.entities.add(new EntityMovable(new Vector(x, y), Optional.of(level)));
				}
				if (ch == 'z') {
					level.entities.add(new Teeth(new Vector(x, y), Optional.of(level)));
				}
			}
		}

		return Optional.of(level);

	}
}
