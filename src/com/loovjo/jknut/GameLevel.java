package com.loovjo.jknut;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.loovjo.jknut.block.BlockType;
import com.loovjo.jknut.block.BlockTypeIce;
import com.loovjo.jknut.block.BlockTypeOnFloor;
import com.loovjo.jknut.command.CommandExecService;
import com.loovjo.jknut.entity.DDOSKid;
import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Player;
import com.loovjo.loo2D.utils.FileLoader;
import com.loovjo.loo2D.utils.ImageLoader;
import com.loovjo.loo2D.utils.Vector;

public class GameLevel {

	public Vector scroll = new Vector(0, 0);

	public int blockSizeX = 50;
	public int blockSizeY = 50;

	public HashMap<Vector, BlockType> level = new HashMap<Vector, BlockType>();

	public CopyOnWriteArrayList<GameEntity> entities = new CopyOnWriteArrayList<GameEntity>();

	public int levelState = 0;
	// 0 = Normal
	// 1 = Won
	// 2 = Restart

	public int tank_direction = 0;

	public boolean tf_on = false;

	private boolean[] keys_down = new boolean[256];

	public Optional<GameScene> owner = Optional.empty();

	public Optional<GameLevelBuilder> testingBuilder = Optional.empty();

	public String command = "";
	public int commandCursorIdx = 0;
	public Optional<String> resp = Optional.empty();

	public int deathTime = 0;

	private static final BufferedImage IMAGE_WASTED = ImageLoader.getImage("/Wasted.png").toBufferedImage();

	public GameLevel(Optional<GameScene> owner) {
		this.owner = owner;
	}

	public Optional<Player> getPlayer() {
		return entities.stream().filter(e -> e instanceof Player).findFirst().map(e -> (Player) e);
	}

	public List<Player> getAllPlayers() {
		return entities.stream().filter(e -> e instanceof Player).map(e -> (Player) e).collect(Collectors.toList());
	}

	public void render(Graphics2D g, int width, int height) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);

		Vector scroll = this.scroll;
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

		BufferedImage resultingImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (Entry<Vector, BlockType> e : levelWithSurroundings.entrySet()) {
			Vector pos = e.getKey();
			BlockType bt = e.getValue();
			if (pos.getX() > firstBlockOnScreenX - 2 && pos.getX() < lastBlockOnScreenX + 1
					&& pos.getY() > firstBlockOnScreenY - 2 && pos.getY() < lastBlockOnScreenY + 1) {

				int x = (int) ((pos.getX() - scroll.getX()) * blockSizeX) + width / 2;
				int y = (int) ((pos.getY() - scroll.getY()) * blockSizeY) + height / 2;
				if (bt.overrideRender) {
					bt.render((Graphics2D) resultingImage.getGraphics(), this, x, y, blockSizeX, blockSizeY);
				} else {
					if (bt instanceof BlockTypeOnFloor) {
						drawImage(resultingImage, x, y, BlockType.FLOOR.img);
					}
					BufferedImage img = bt.img;
					drawImage(resultingImage, x, y, img);
				}
			}
		}

		ArrayList<GameEntity> entities = new ArrayList<GameEntity>(this.entities);

		if (deathTime == 0) {
			g.drawImage(resultingImage, 0, 0, null);
			entities.forEach(
					e -> e.render(g, (int) ((e.getPositionOnScreen().getX() - scroll.getX()) * blockSizeX) + width / 2,
							(int) ((e.getPositionOnScreen().getY() - scroll.getY()) * blockSizeY) + height / 2,
							blockSizeX, blockSizeY, width, height));
		} else {
			g.translate(width / 2, height / 2);
			g.rotate(deathTime / 600.0);
			g.scale(deathTime / 700.0 + 1.1, deathTime / 700.0 + 1.1);
			g.translate(-width / 2, -height / 2);
			g.drawImage(resultingImage, 0, 0, null);

			if (getPlayer().isPresent()) {
				getPlayer().get().hud_height = 0;
			}
			resp = Optional.empty();
			command = "";
			commandCursorIdx = 0;
			entities.forEach(
					e -> e.render(g, (int) ((e.getPositionOnScreen().getX() - scroll.getX()) * blockSizeX) + width / 2,
							(int) ((e.getPositionOnScreen().getY() - scroll.getY()) * blockSizeY) + height / 2,
							blockSizeX, blockSizeY, width, height));

			try {
				g.transform(g.getTransform().createInverse());
			} catch (NoninvertibleTransformException e1) {
				e1.printStackTrace();
			}

			g.setComposite(AlphaComposite.SrcOver.derive(Math.min(1, deathTime / 60.0f)));

			g.translate(width / 2, height / 2);
			g.rotate(-deathTime / 2000.0 + Math.PI / 16);
			g.translate(-width / 2, -height / 2);

			int wastedWidth = (int) Math.max(200, width / 2.5);
			int wastedHeight = wastedWidth * IMAGE_WASTED.getHeight() / IMAGE_WASTED.getWidth();

			g.drawImage(IMAGE_WASTED, (3 * width / 2 - wastedWidth) / 2, (3 * height / 2 - wastedHeight) / 2,
					wastedWidth, wastedHeight, null);
		}

		g.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
		g.setColor(Color.BLACK);
		if (!(this instanceof GameLevelBuilder) && deathTime == 0) {
			getPlayer().ifPresent(player -> {
				g.drawString("Kex: " + player.kex, 0, 32);
			});
		}

		g.setColor(Color.WHITE);

		g.setFont(new Font("", Font.PLAIN, 20));
		if (command.length() > 0) {
			g.fillRect(0, height - g.getFont().getSize(), width, g.getFont().getSize());
			g.setColor(Color.black);
			g.drawString(command, 0, height - 2);
			int size = g.getFontMetrics().stringWidth(command.substring(0, commandCursorIdx));
			g.drawLine(size, height - g.getFont().getSize(), size, height);
		}
		if (resp.isPresent()) {
			g.setColor(Color.blue);
			g.fillRect(0, height - g.getFont().getSize() * 2, width, g.getFont().getSize());
			g.setColor(Color.white);
			g.drawString(resp.get(), 0, height - 2 - g.getFont().getSize());
		}
	}

	private void drawImage(BufferedImage resultingImage, int xPos, int yPos, BufferedImage img) {
		Random rand = new Random();
		int moveX = 0;
		int moveY = 0;
		int colorXor = 0;
		int lineSpace = 0;

		boolean ddos = entities.stream().anyMatch(e -> e instanceof DDOSKid);

		OptionalDouble o_ddosDist = getPlayerDistToClosestDDOSKid();

		double prob = 0;
		int greenAdd = 0;

		if (o_ddosDist.isPresent() && !(this instanceof GameLevelBuilder)) {
			double ddosDist = o_ddosDist.getAsDouble() + 1;

			prob = 0.1 / (ddosDist * ddosDist * ddosDist);

			greenAdd = (int) Math.max(0, Math.min(255, 256 / (ddosDist * ddosDist)));

			if (rand.nextFloat() < prob) {
				moveX = rand.nextInt(40) - 20;
			}
			if (rand.nextFloat() < prob) {
				moveY = rand.nextInt(40) - 20;
			}
			if (rand.nextFloat() < prob) {
				colorXor = rand.nextInt(0xFFFFFF);
			}
			if (rand.nextFloat() < prob) {
				lineSpace = rand.nextInt(20) + 1;
			}
		}

		int[] surfaceData = ((DataBufferInt) resultingImage.getRaster().getDataBuffer()).getData();
		int[] imgData = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				int idx = (x + xPos) + (y + yPos) * resultingImage.getWidth();

				if (x + xPos >= 0 && x + xPos < resultingImage.getWidth() && y + yPos >= 0
						&& y + yPos < resultingImage.getHeight()) {
					Color col = new Color(imgData[x + y * img.getWidth()], true);

					if (col.getAlpha() != 255) {
						float fAlpha = col.getAlpha() / 256f;
						Color lastCol = new Color(surfaceData[idx], true);

						col = new Color((int) (col.getRed() * fAlpha + (1 - fAlpha) * lastCol.getRed()),
								(int) (col.getGreen() * fAlpha + (1 - fAlpha) * lastCol.getGreen()),
								(int) (col.getBlue() * fAlpha + (1 - fAlpha) * lastCol.getBlue()), 255);

					}

					if (deathTime > 0) {

						int x1 = xPos - resultingImage.getWidth();
						int y1 = yPos - resultingImage.getHeight();
						float dist = (x1 * x1 + y1 * y1)
								/ (float) (Math.sqrt(resultingImage.getWidth() + resultingImage.getHeight()) / 2);

						int avg = (int) Math.min(1,
								Math.max(0, (col.getRed() + col.getGreen() + col.getBlue() - dist) / 3));
						float timeMul = Math.min(1, (deathTime + 30) / 400f);

						if (deathTime < 40) {
							avg = 255;
							timeMul = (float) Math.sin(deathTime / 40.0 * Math.PI);
						}

						col = new Color((int) (col.getRed() * (1 - timeMul) + timeMul * avg),
								(int) (col.getGreen() * (1 - timeMul) + timeMul * avg),
								(int) (col.getBlue() * (1 - timeMul) + timeMul * avg), 255);
					}
					if (ddos) {
						col = new Color(Math.max(0, col.getRed() - greenAdd), Math.min(255, col.getGreen() + greenAdd),
								Math.max(0, col.getBlue() - greenAdd), col.getAlpha());

						if (lineSpace > 0 && x % lineSpace == 0)
							col = Color.WHITE;
						surfaceData[Math.max(0,
								Math.min(surfaceData.length - 1, idx + moveX + moveY * img.getWidth()))] = col.getRGB()
										^ colorXor;
					} else {
						surfaceData[idx] = col.getRGB();
					}
				}
			}
		}

	}

	private OptionalDouble getPlayerDistToClosestDDOSKid() {
		if (!getPlayer().isPresent())
			return OptionalDouble.empty();

		Player pl = getPlayer().get();

		return entities.stream().filter(e -> e instanceof DDOSKid)
				.mapToDouble(e -> e.getPosition().getLengthTo(pl.getPosition())).min();
	}

	public List<GameEntity> getEntitiesAt(Vector pos) {
		return entities.stream().filter(e -> e.getPosition().equals(pos)).collect(Collectors.toList());
	}

	public void update() {
		getPlayer().ifPresent(player -> {
			scroll = scroll.sub(player.getPosition()).div(1.1f).add(player.getPosition());
		});
		if (deathTime % 5 == 0)
			entities.forEach(GameEntity::update);

		Optional<Integer> moveDir = Optional.empty();
		for (int i = KeyEvent.VK_LEFT; i <= KeyEvent.VK_DOWN; i++) {
			if (keys_down[i])
				moveDir = Optional.of((i - 2) % 4);
		}

		if (moveDir.isPresent()) {
			if (getPlayer().isPresent() && !getPlayer().get().isDead()) {
				if (!(level.get(getPlayer().get().getPosition()) instanceof BlockTypeIce)
						|| getPlayer().get().hasItem(PlayerItem.ICE_SKATES)) {

					getPlayer().get().walkDirection = moveDir.get();
				}
			}
		}

		if (levelState == 1 && owner.isPresent() && testingBuilder.isPresent()) {
			owner.get().level = testingBuilder.get();
		}

		if (levelState == 2 && owner.isPresent() && testingBuilder.isPresent()) {
			testingBuilder.get().testLevel();
		}

		if (getPlayer().isPresent() && getPlayer().get().isDead())
			deathTime++;

		OptionalDouble o_ddosDist = getPlayerDistToClosestDDOSKid();
		o_ddosDist.ifPresent(ddosDist -> {
			if (ddosDist == 0 && getPlayer().get().canMoveAtAll()) {
				if (testingBuilder.isPresent() && owner.isPresent()) {
					owner.get().level = testingBuilder.get();
				} else {
					System.exit(1);
				}
			}
		});
		if (deathTime > 300) {
			levelState = 2;
		}
		if (getAllPlayers().stream().anyMatch(pl -> pl.isDead())) {
			List<Player> notDead = getAllPlayers().stream().filter(pl -> !pl.isDead()).collect(Collectors.toList());
			entities.removeAll(notDead);
			entities.addAll(notDead);
			getAllPlayers().stream().forEach(p -> p.die());
		}
	}

	public GameLevel clone() {
		GameLevel clone = new GameLevel(owner);
		clone.blockSizeX = blockSizeX;
		clone.blockSizeY = blockSizeY;

		clone.command = command;
		clone.commandCursorIdx = commandCursorIdx;
		clone.resp = resp;

		clone.deathTime = deathTime;
		clone.entities = new CopyOnWriteArrayList<GameEntity>(entities.stream().map(e -> {
			GameEntity eClone = e.clone();
			eClone.level = Optional.of(clone);
			return eClone;
		}).collect(Collectors.toList()));

		clone.level = new HashMap<Vector, BlockType>(level);
		clone.levelState = levelState;

		clone.keys_down = keys_down;
		clone.scroll = scroll;

		clone.tank_direction = tank_direction;
		clone.tf_on = tf_on;

		clone.testingBuilder = testingBuilder;

		return clone;
	}

	public static Optional<GameLevel> LOAD_LEVEL(Optional<GameScene> owner, String levelName) {
		String[] lines;
		try {
			lines = FileLoader.readFile("/" + levelName).split("\n");
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

				for (Entry<Character, Class<? extends GameEntity>> e : GameEntity.ENTITIES.entrySet()) {
					if (e.getKey() == ch) {
						try {
							level.entities.add(e.getValue().getDeclaredConstructor(Vector.class, Optional.class)
									.newInstance(new Vector(x, y), Optional.of(level)));
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException | NoSuchMethodException | SecurityException e1) {
							e1.printStackTrace();
						}
					}
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
			if (getPlayer().isPresent() && getPlayer().get().isDead()) {
				getPlayer().get().die();
				levelState = 2;
			}
			if (!testingBuilder.isPresent())
				levelState = 2;
		}

		if (keyCode == KeyEvent.VK_E && testingBuilder.isPresent() && owner.isPresent()) {
			owner.get().level = testingBuilder.get();
		}

		if (keyCode == KeyEvent.VK_SHIFT && getPlayer().isPresent()) {
			getPlayer().get().isRunning = true;
		}

		if (keyCode == KeyEvent.VK_SPACE && getPlayer().isPresent()) {
			System.out.println(getPlayer().get().getPosition());
		}

		if (keyCode == KeyEvent.VK_Q && getPlayer().isPresent()) {
			Player player = getPlayer().get();

			// Put at end
			System.out.println(entities + ", " + player);
			entities.remove(player);
			entities.add(player);
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
						Class<? extends GameEntity> en = e.get(0).getClass();
						Optional<Entry<Character, Class<? extends GameEntity>>> entity = GameEntity.ENTITIES.entrySet()
								.stream().filter(entry -> entry.getValue().equals(en)).findAny();
						if (entity.isPresent()) {
							ch = entity.get().getKey();
						}
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

	public void win() {
		levelState = 1;
		if (owner.isPresent() && !testingBuilder.isPresent())
			owner.get().stats.inc("wins");
	}
}
