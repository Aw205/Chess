package com.gdx.chess.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.gdx.chess.Chess;
import com.gdx.chess.GameState;
import com.gdx.chess.Move;

public class PieceMovementListener extends DragListener {
	
	private Piece piece;
	
	public PieceMovementListener(Piece p) {
		this.piece=p;
	}
	
	@Override
	public void enter(InputEvent event, float x, float y, int pointer,Actor fromActor) {
		Gdx.graphics.setSystemCursor(SystemCursor.Hand);
	}
	
	@Override
	public void exit(InputEvent event, float x, float y, int pointer,Actor fromActor) {
		Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
	}
	
	// x,y is relative to bottom left of actor
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		
		piece.moveBy(x - piece.getWidth() / 2, y - piece.getHeight() / 2);
		piece.setZIndex(100);
		piece.isTouching=true;
		
		return true;
	}
	
	@Override
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		piece.moveBy(x - piece.getWidth() / 2, y - piece.getHeight() / 2);
	}
	
	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

		int row = (int) ((event.getStageY() - 20) / 50);
		int col = (int) ((event.getStageX() - 100) / 50);
		int to = row * 8 + col;
		
		if (GameState.playerTurn == piece.color && piece.validList.contains(to)) {

			String key = Integer.toString(piece.square) + Integer.toString(to);
			Move m = GameState.movesMap.get(key);
			
			GameState.update(m);
			TileBoard.update(m,false);
			Chess.am.manager.get("move.ogg",Sound.class).play();
			
		}
		
		piece.setPosition(100 + piece.getWidth() * (piece.square % 8),20 + piece.getHeight() * (piece.square / 8));
		piece.isTouching = false;

	}
	
}
