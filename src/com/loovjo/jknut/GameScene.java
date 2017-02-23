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

	public static FastImage TEXTURE_WON = ImageLoader.getImage("/Texture/SideBar/WinScreen.png");
	public static FastImage TEXTURE_OUTLINE = ImageLoader.getImage("/Texture/SideBar/OUTLINE.png");

	public static List<String> LEVELS = Arrays
			.asList(new String[] { "Lesson 1", "Lesson 2", "Lesson 3", "Lesson 4", "Lesson 5", "Lesson 6", "Lesson 7",
					"Lesson 8", "Bugging", "back n forth", "From dirt to floor", "A moveable level", "Tricked",
					"Trinity", "NutsAndBolt", "Brushfire", "Elementary", "Cellblocked", "Nice day", "Digger",
					"Castle moat", "Hunt", "Forced entry", "Oorto geld", "Blobnet", "Go with the flow", "Ping pong",
					"Pier seven", "Mishmesh", "Seeing stars", "Spooks", "Corridor", "Digdirt", "Morton", "Steam" });

	public GameScene() {
		level = GameLevelBuilder.LEVEL_EMPTY(Optional.of(this)).get();
	}

	private void loadNextLevel(int jump) {
		int idx = LEVELS.indexOf(currentLevel) + jump;
		currentLevel = LEVELS.get(idx);
		level = GameLevel.LOAD_LEVEL(Optional.of(this), "/Levels/" + currentLevel).get();
	}

	@Override
	public void update() {
		level.update();

		if (level.levelState == 1) {
			loadNextLevel(1);
		}
		if (level.levelState == 2) {
			loadNextLevel(0);
		}
	}

	@Override
	public void render(Graphics g, int width, int height) {
		level.render((Graphics2D) g, width, height);

		if (level.levelState == 1) {
			int x = (width - TEXTURE_WON.getWidth()) / 2;
			int y = (height - TEXTURE_WON.getHeight()) / 2;
			g.fillRoundRect(x - 30, y - 30, TEXTURE_WON.getWidth() + 60, TEXTURE_WON.getHeight() + 60, 30, 30);
			g.drawImage(TEXTURE_WON.toBufferedImage(), x, y, null);
		}
	}

	@Override
	public void mousePressed(Vector pos, int button) {
		level.mousePressed(pos, button);
	}

	@Override
	public void mouseReleased(Vector pos, int button) {
		level.mouseReleased(pos, button);
	}

	@Override
	public void mouseMoved(Vector pos) {
		level.mouseMoved(pos);
	}

	@Override
	public void keyPressed(int keyCode) {
		if (level.command.length() == 0)
			level.keyPressed(keyCode);
	}

	@Override
	public void keyReleased(int keyCode) {
		level.keyReleased(keyCode);
	}

	@Override
	public void keyTyped(char key) {
		if (level.command.length() > 0) {
			if (key == '\b') {
				level.command = level.command.substring(0, level.command.length() - 1);
			} else if (key == '\n') {
				if (level.command == ":") {
					level.command = "";
					level.resp = Optional.empty();
				} else {
					level.execCommand();
				}
			} else {
				level.command += key;
			}
		} else {
			if (key == ':') {
				level.command += ':';
			}
		}
	}

	@Override
	public void mouseWheal(MouseWheelEvent e) {
	}

}
