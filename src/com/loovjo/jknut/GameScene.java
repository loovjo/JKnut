package com.loovjo.jknut;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.loovjo.jknut.block.BlockTypeIce;
import com.loovjo.loo2D.scene.Scene;
import com.loovjo.loo2D.utils.FastImage;
import com.loovjo.loo2D.utils.ImageLoader;
import com.loovjo.loo2D.utils.Vector;

public class GameScene implements Scene {

	public GameLevel level;

	public String currentLevel = "";

	private boolean[] keys_down = new boolean[256];

	public static FastImage TEXTURE_WON = ImageLoader.getImage("/Texture/SideBar/WinScreen.png");
	public static FastImage TEXTURE_OUTLINE = ImageLoader.getImage("/Texture/SideBar/OUTLINE.png");

	public static List<String> LEVELS = Arrays
			.asList(new String[] { "Lesson 1", "Lesson 2", "Lesson 3", "Lesson 4", "Lesson 5", "Lesson 6", "Lesson 7",
					"Lesson 8", "Bugging", "back n forth", "From dirt to floor", "A moveable level", "Tricked",
					"Trinity", "NutsAndBolt", "Brushfire", "Elementary", "Cellblocked", "Nice day", "Digger",
					"Castle moat", "Hunt", "Forced entry", "Oorto geld", "Blobnet", "Go with the flow", "Ping pong",
					"Pier seven", "Mishmesh", "Seeing stars", "Spooks", "Corridor", "Digdirt", "Morton", "Steam" });

	public GameScene() {
		loadNextLevel(1);
	}

	private void loadNextLevel(int jump) {
		int idx = LEVELS.indexOf(currentLevel) + jump;
		currentLevel = LEVELS.get(idx);
		level = GameLevel.LOAD_LEVEL(currentLevel).get();
	}

	@Override
	public void update() {
		level.update();

		Optional<Integer> moveDir = Optional.empty();
		for (int i = KeyEvent.VK_LEFT; i <= KeyEvent.VK_DOWN; i++) {
			if (keys_down[i])
				moveDir = Optional.of((i - 2) % 4);
		}

		if (moveDir.isPresent()) {

			if (!(level.level.get(level.getPlayer().getPosition()) instanceof BlockTypeIce)
					|| level.getPlayer().hasItem(PlayerItem.ICE_SKATES)) {

				level.getPlayer().direction = moveDir.get();
				level.getPlayer().moveForward();
			}
		}
	}

	@Override
	public void render(Graphics g, int width, int height) {
		level.render((Graphics2D) g, width, height);

		if (level.won) {
			int x = (width - TEXTURE_WON.getWidth()) / 2;
			int y = (height - TEXTURE_WON.getHeight()) / 2;
			g.fillRoundRect(x - 30, y - 30, TEXTURE_WON.getWidth() + 60, TEXTURE_WON.getHeight() + 60, 30, 30);
			g.drawImage(TEXTURE_WON.toBufferedImage(), x, y, null);
		}
	}

	@Override
	public void mousePressed(Vector pos, int button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(Vector pos, int button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(Vector pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(int keyCode) {
		if (!keys_down[keyCode])
			_keyPressed(keyCode);
		keys_down[keyCode] = true;
	}

	private void _keyPressed(int keyCode) {
		if (level.won) {
			loadNextLevel(1);
		} else {
			/*
			 * if (keyCode >= KeyEvent.VK_LEFT && keyCode <= KeyEvent.VK_DOWN) {
			 * if (level.player.canMove()) { moveDir = (keyCode - 2) % 4; } }
			 */
			if (keyCode == KeyEvent.VK_R) {
				loadNextLevel(0);
			}
			if (keyCode == KeyEvent.VK_N) {
				loadNextLevel(1);
			}
			if (keyCode == KeyEvent.VK_SHIFT) {
				level.getPlayer().isRunning = true;
			}
		}
	}

	@Override
	public void keyReleased(int keyCode) {
		keys_down[keyCode] = false;

		if (keyCode == KeyEvent.VK_SHIFT) {
			level.getPlayer().isRunning = false;
		}
	}

	@Override
	public void keyTyped(char key) {

	}

	@Override
	public void mouseWheal(MouseWheelEvent e) {
		// TODO Auto-generated method stub

	}

}
