package com.gdx.chess;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class Chess extends Game {

	public static SpriteBatch sb;
	static ShapeRenderer sr;
	public static AssetsManager am;

	@Override
	public void create() {
		sb = new SpriteBatch();
		sr = new ShapeRenderer();
		sr.setAutoShapeType(true);

		am = new AssetsManager();	
		am.manager.finishLoading();
		this.setScreen(new GameScreen(this,am));
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		sb.dispose();
		am.manager.dispose();
	}
}
