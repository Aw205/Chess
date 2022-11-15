package com.gdx.chess;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

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
		int square = row * 8 + col;

		if (GameState.playerTurn == piece.color && piece.canMoveTo(square)) {

			long from = MoveLogic.squareToBB.get(piece.squareIndex);
			long to = MoveLogic.squareToBB.get(square);
			Move m = new Move(from, to);
			
			if (Board.board[square] != null) {
				Board.board[square].remove();
			}
			GameState.update(m);
		}
		piece.setPosition(100 + piece.getWidth() * (piece.squareIndex % 8),20 + piece.getHeight() * (piece.squareIndex / 8));
		piece.isTouching = false;

	}
}
