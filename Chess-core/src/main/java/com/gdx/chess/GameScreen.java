package com.gdx.chess;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen implements Screen {
	
	
	public static AssetsManager am;
	private Board board = new Board();
	private TileBoard tileBoard = new TileBoard();
	private Computer computer = new Computer(Colour.BLACK);
	
	private InputMultiplexer multiplexer = new InputMultiplexer();

	public GameScreen(Chess chess, AssetsManager am) {
		//GameScreen.am=am;
		multiplexer.addProcessor(board);
		multiplexer.addProcessor(tileBoard);
		Gdx.input.setInputProcessor(multiplexer);
		
	}

	@Override
	public void dispose() {
		Chess.sb.dispose();
	}

	@Override
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//tileBoard.act();
		board.act();
		
		computer.search();
		
		tileBoard.draw();
		board.draw();
	}
	
	@Override
	public void show() {
		
		
	}

	@Override
	public void resize(int width, int height) {
		
		
	}

	@Override
	public void pause() {
		
		
	}

	@Override
	public void resume() {
		
		
	}

	@Override
	public void hide() {
		
		
	}
}