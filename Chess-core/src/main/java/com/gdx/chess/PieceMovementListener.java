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
		
		//Board.tiles[piece.row][piece.col].setColor(0.9922f,0.9922f,0.5882f, 1f);
		
		return true;
		
	}
	
	@Override
	public void touchDragged(InputEvent event, float x, float y, int pointer) {
		
		piece.moveBy(x - piece.getWidth() / 2, y - piece.getHeight() / 2);
		
	}
	
	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer,int button) {
		
		int row = (int) ((event.getStageY() - 20) / 50);
		int col = (int) ((event.getStageX() - 100) / 50);

		if (GameState.playerTurn==piece.color && piece.canMoveTo(row, col)) {

			//Board.tiles[row][col].setColor(1,1,0.5f, 0.75f);

			//piece.capture(Board.board[row][col]);

			Board.board[piece.row][piece.col] = null;
			GameState.setUnoccupied(piece.row, piece.col);

			piece.row = row;
			piece.col = col;

			Board.board[piece.row][piece.col] = piece;
			GameState.setOccupied(piece.row, piece.col);

			GameState.blackControlled = 0;
			GameState.whiteControlled = 0;
			
			Board.generateValidMoves();
			
			
			GameState.playerTurn=GameState.playerTurn.opposite();
		}
		piece.setPosition(100 + piece.getWidth() * piece.col, 20 + piece.getHeight() * piece.row);
		
		piece.isTouching=false;

	}
}
