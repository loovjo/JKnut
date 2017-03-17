package com.loovjo.jknut;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.loovjo.jknut.block.BlockType;
import com.loovjo.jknut.entity.Apocalypse;
import com.loovjo.jknut.entity.BounceBall;
import com.loovjo.jknut.entity.Bug;
import com.loovjo.jknut.entity.DDOSKid;
import com.loovjo.jknut.entity.EntityMovable;
import com.loovjo.jknut.entity.GameEntity;
import com.loovjo.jknut.entity.Glider;
import com.loovjo.jknut.entity.Player;
import com.loovjo.jknut.entity.Tank;
import com.loovjo.jknut.entity.Teeth;
import com.loovjo.jknut.entity.Walker;
import com.loovjo.loo2D.utils.Vector;

public class GameLevelBuilder extends GameLevel {

	public GameLevelBuilder(Optional<GameScene> owner) {
		super(owner);
	}

	public GameLevelBuilder(GameLevel gameLevel) {
		super(gameLevel.owner);
		level = gameLevel.level;
		entities = gameLevel.entities;
		if (gameLevel.getPlayer().isPresent()) {
			scroll = crossHairPos = gameLevel.getPlayer().get().getPosition();
		}
	}

	private int TAB_WIDTH = 200;

	public Vector crossHairPos = new Vector(0, 0);

	private boolean[] keys_down = new boolean[256];

	public int last_width = 0;
	public int last_y = 0;
	public int mouse_traveled = 0;
	public boolean is_pressing = false;

	public int tab_scroll = 0;

	public Optional<Object> selectedBlock = Optional.empty();

	public static ArrayList<Object> BLOCKS = new ArrayList<Object>();

	static {
		for (Field f : BlockType.class.getFields()) {
			try {
				Object obj = f.get(null);
				if (obj instanceof BlockType) {
					BlockType bt = (BlockType) obj;
					BLOCKS.add(bt);
				}
			} catch (Exception e) {
			}
		}
		for (Class<? extends GameEntity> e : GameEntity.ENTITIES.values()) {
			System.out.println(e);
			try {
				BLOCKS.add(e.getDeclaredConstructor(Vector.class, Optional.class).newInstance(new Vector(0, 0), Optional.empty()));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void update() {
		scroll = scroll.sub(crossHairPos).div(1.3f).add(crossHairPos);

		if (scroll.getLengthTo(crossHairPos) < 0.1) {
			scroll = crossHairPos;

			for (int i = KeyEvent.VK_LEFT; i <= KeyEvent.VK_DOWN; i++) {
				if (keys_down[i]) {
					crossHairPos = crossHairPos.moveInDir(((i - 2) % 4) * 2);
				}
			}
		}
		if (keys_down[KeyEvent.VK_SPACE] && selectedBlock.isPresent()) {
			put(crossHairPos, selectedBlock.get());
		}
		if (keys_down[KeyEvent.VK_X]) {
			level.remove(crossHairPos.clone());
			entities.removeAll(getEntitiesAt(crossHairPos));
		}
	}

	private void put(Vector crossHairPos2, Object object) {
		if (object instanceof BlockType) {
			level.put(crossHairPos2, (BlockType) object);
		} else if (object instanceof GameEntity) {
			if (getEntitiesAt(crossHairPos2).isEmpty()) {
				System.out.println(object);

				GameEntity e = (GameEntity) object;
				e = e.clone();
				e.setPosition(crossHairPos2);
				entities.add((GameEntity) e);
			}
		}
	}

	public void render(Graphics2D g, int width, int height) {
		super.render(g, width - TAB_WIDTH, height);

		if (selectedBlock.isPresent()) {
			Composite last = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER).derive(0.5f));
			g.drawImage(getImg(selectedBlock.get()), (width - TAB_WIDTH) / 2, (height) / 2, blockSizeX, blockSizeY,
					null);
			g.setComposite(last);
		}

		g.setColor(Color.BLACK);
		g.fillRect(width - TAB_WIDTH, 0, TAB_WIDTH, height);

		g.setFont(new Font("Helvetica", Font.PLAIN, 12));

		int items_per_row = 4;

		for (int i = 0; i < BLOCKS.size(); i++) {
			int x = i % items_per_row;
			int y = i / items_per_row;

			Object block = BLOCKS.get(i);

			g.drawImage(BlockType.FLOOR.img, x * blockSizeX + width - TAB_WIDTH, y * blockSizeY, blockSizeX, blockSizeY,
					null);
			if (block instanceof BlockType) {
				((BlockType) block).render(g, this, x * blockSizeX + width - TAB_WIDTH, y * blockSizeY, blockSizeX,
						blockSizeY);
			} else if (block instanceof GameEntity) {
				((GameEntity) block).render(g, x * blockSizeX + width - TAB_WIDTH, y * blockSizeY, blockSizeX,
						blockSizeY, blockSizeX, blockSizeY);
			}
		}

		last_width = width;
	}

	private BufferedImage getImg(Object object) {
		if (object instanceof BlockType) {
			return ((BlockType) object).img;
		} else if (object instanceof GameEntity) {
			return ((GameEntity) object).getImage();
		}
		return null;
	}

	public static Optional<GameLevel> LEVEL_EMPTY(Optional<GameScene> owner) {
		return Optional.of(new GameLevelBuilder(owner));
	}

	@Override
	public void keyPressed(int keyCode) {
		keys_down[keyCode] = true;

		if (keyCode == KeyEvent.VK_1) {
			System.out.println(entities);
		}
		if (keyCode == KeyEvent.VK_P && owner.isPresent()) {
			testLevel();

		}
	}

	public void testLevel() {
		owner.get().level = new GameLevel(owner);
		owner.get().level.level = new HashMap<Vector, BlockType>(level);
		owner.get().level.entities = new CopyOnWriteArrayList<GameEntity>(entities.stream().map(e -> {
			GameEntity e1 = e.clone();
			e1.level = Optional.of(owner.get().level);
			return e1;
		}).collect(Collectors.toList()));

		owner.get().level.testingBuilder = Optional.of(this);

	}

	@Override
	public void keyReleased(int keyCode) {
		keys_down[keyCode] = false;
	}

	@Override
	public void mousePressed(Vector pos, int button) {
		if (pos.getX() > last_width - TAB_WIDTH) {
			int x = (int) ((pos.getX() - last_width + TAB_WIDTH) / blockSizeX);
			int y = (int) (pos.getY() / blockSizeX);

			int i = y * (TAB_WIDTH / blockSizeX) + x;

			selectedBlock = Optional.of(BLOCKS.get(i));
		}
	}

	@Override
	public void mouseReleased(Vector pos, int button) {

	}

	@Override
	public void mouseMoved(Vector pos) {
	}

}
