package com.loovjo.jknut;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.loovjo.jknut.block.BlockType;
import com.loovjo.jknut.block.BlockTypeIce;
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

	public int levelState = 0;
	// 0 = Normal
	// 1 = Won
	// 2 = Restart

	public boolean tf_on = false;

	private boolean[] keys_down = new boolean[256];

	public Optional<GameScene> owner = Optional.empty();

	public Optional<GameLevelBuilder> testingBuilder = Optional.empty();

	public String command = "";
	public Optional<String> resp = Optional.empty();

	public GameLevel(Optional<GameScene> owner) {
		this.owner = owner;
	}

	public Optional<Player> getPlayer() {
		return entities.stream().filter(e -> e instanceof Player).findAny().map(e -> (Player) e);
	}

	public void render(Graphics2D g, int width, int height) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);

		Vector scroll = this.scroll;
		if (!getPlayer().isPresent() || !getPlayer().get().dead) {
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
			if (!(this instanceof GameLevelBuilder)) {
				getPlayer().ifPresent(player -> {
					g.drawString("Kex: " + player.kex, 0, 32);
				});
			}
		} else {
			g.setColor(Color.WHITE);
			g.drawString("sry u ded kiddo", 0, 32);
		}
		g.setFont(new Font("Monospaced", Font.PLAIN, 20));
		if (command.length() > 0) {
			g.fillRect(0, height - g.getFont().getSize(), width, g.getFont().getSize());
			g.setColor(Color.black);
			g.drawString(command, 0, height - 2);
			int size = g.getFontMetrics().stringWidth(command);
			g.drawLine(size, height - g.getFont().getSize(), size, height);
		}
		if (resp.isPresent()) {
			g.setColor(Color.blue);
			g.fillRect(0, height - g.getFont().getSize() * 2, width, g.getFont().getSize());
			g.setColor(Color.white);
			g.drawString(resp.get(), 0, height - 2 - g.getFont().getSize());
		}
	}

	public List<GameEntity> getEntitiesAt(Vector pos) {
		return entities.stream().filter(e -> e.getPosition().equals(pos)).collect(Collectors.toList());
	}

	public void update() {
		getPlayer().ifPresent(player -> {
			scroll = scroll.sub(player.getPosition()).div(1.1f).add(player.getPosition());
		});

		entities.forEach(GameEntity::update);

		Optional<Integer> moveDir = Optional.empty();
		for (int i = KeyEvent.VK_LEFT; i <= KeyEvent.VK_DOWN; i++) {
			if (keys_down[i])
				moveDir = Optional.of((i - 2) % 4);
		}

		if (moveDir.isPresent()) {
			if (getPlayer().isPresent()) {
				if (!(level.get(getPlayer().get().getPosition()) instanceof BlockTypeIce)
						|| getPlayer().get().hasItem(PlayerItem.ICE_SKATES)) {

					getPlayer().get().direction = moveDir.get();
					getPlayer().get().moveForward();
				}
			}
		}

		if (levelState == 1 && owner.isPresent() && testingBuilder.isPresent()) {
			owner.get().level = testingBuilder.get();
		}
		if (levelState == 2 && owner.isPresent() && testingBuilder.isPresent()) {
			testingBuilder.get().testLevel();
		}
	}

	public static Optional<GameLevel> LOAD_LEVEL(Optional<GameScene> owner, String levelName) {
		String[] lines;
		try {
			lines = FileLoader.readFile("/Levels/" + levelName).split("\n");
		} catch (NullPointerException e) {
			e.printStackTrace();
			return Optional.empty();
		}
		return LOAD_LEVEL(owner, lines);
	}

	public static Optional<GameLevel> LOAD_LEVEL(Optional<GameScene> owner, String[] lines) {
		GameLevel level = new GameLevel(owner);

		for (int y = 0; y < lines.length; y++) {
			for (int x = 0; x < lines[y].length(); x++) {
				char ch = lines[y].charAt(x);
				level.level.put(new Vector(x, y), BlockType.getBlockTypeFromChar(ch).orElse(BlockType.FLOOR));
				if (ch == 'x') {
					level.entities.add(new Player(new Vector(x, y), Optional.of(level)));
					level.scroll = level.getPlayer().get().getPosition();
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

	public void keyPressed(int keyCode) {
		if (!keys_down[keyCode])
			_keyPressed(keyCode);
		keys_down[keyCode] = true;
	}

	private void _keyPressed(int keyCode) {

		/*
		 * if (keyCode >= KeyEvent.VK_LEFT && keyCode <= KeyEvent.VK_DOWN) { if
		 * (level.player.canMove()) { moveDir = (keyCode - 2) % 4; } }
		 */
		if (keyCode == KeyEvent.VK_R) {
			if (getPlayer().isPresent() && getPlayer().get().dead) {
				getPlayer().get().dead = false;
				levelState = 2;
			}
			if (!testingBuilder.isPresent())
				levelState = 2;
		}
		if (keyCode == KeyEvent.VK_N && !testingBuilder.isPresent()) {
			levelState = 1;
		}
		if (keyCode == KeyEvent.VK_E && testingBuilder.isPresent()) {
			levelState = 1;
		}
		if (keyCode == KeyEvent.VK_SHIFT && getPlayer().isPresent()) {
			getPlayer().get().isRunning = true;
		}

	}

	public void keyReleased(int keyCode) {
		keys_down[keyCode] = false;

		if (keyCode == KeyEvent.VK_SHIFT && getPlayer().isPresent()) {
			getPlayer().get().isRunning = false;
		}

	}

	public void mousePressed(Vector pos, int button) {
	}

	public void mouseReleased(Vector pos, int button) {
	}

	public void mouseMoved(Vector pos) {
	}

	public void execCommand() {
		resp = CommandExecService.run(command, this);

	}

	public String asLevelString() {
		int startX = (int) level.keySet().stream().sorted((a, b) -> (int) Math.signum(a.getX() - b.getX())).findFirst()
				.get().getX();
		int endX = (int) level.keySet().stream().sorted((a, b) -> (int) Math.signum(b.getX() - a.getX())).findFirst()
				.get().getX();
		int startY = (int) level.keySet().stream().sorted((a, b) -> (int) Math.signum(a.getY() - b.getY())).findFirst()
				.get().getY();
		int endY = (int) level.keySet().stream().sorted((a, b) -> (int) Math.signum(b.getY() - a.getY())).findFirst()
				.get().getY();

		String out = "";
		System.out.println(startX + ", " + endX + ", " + startY + ", " + endY);
		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				char ch = ' ';
				BlockType b = level.getOrDefault(new Vector(x, y), BlockType.FLOOR);
				if (b.equals(BlockType.FLOOR)) {
					List<GameEntity> e = getEntitiesAt(new Vector(x, y));
					if (e.size() > 0) {
						ch = e.get(0).getChar();
					}
				} else {
					ch = b.blockChr;
				}
				out += ch;
			}
			out += "\n";
		}

		return out;
	}
}
