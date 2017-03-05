package com.loovjo.jknut;

import com.loovjo.loo2D.MainWindow;
import com.loovjo.loo2D.utils.FileLoader;
import com.loovjo.loo2D.utils.Vector;

import com.loovjo.jknut.GameScene;

public class Main extends MainWindow {
	
	private static final long serialVersionUID = 3180111975953913914L;

	public Main() {
		super("J-Knut", new GameScene(), new Vector(900, 650), true);
	}
	
	public static void main(String[] args) {  
		FileLoader.setLoaderClass(Main.class);
		new Main();
	}
	
}
