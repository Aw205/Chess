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
		int square = row*8 + col;

		if (GameState.playerTurn == piece.color && piece.canMoveTo(square)) {
			//Board.tiles[row][col].setColor(1,1,0.5f, 0.75f);
			
			//updating game state
			long from = 1L << (piece.squareIndex);
			long to = 1L << (square);
			Move m  = new Move(from,to);
			
			if(Board.board[square]!=null) { 
				Board.board[square].remove();
			}
			
			GameState.update(m);
			
//			piece.validList.clear();
//			for(long i = 1, num = 0; num < 64; i <<= 1, num++) {
//				if((piece.legalMoves & i) != 0) {
//					piece.validList.add((int) num);
//				}
//			}
			
		}
		piece.setPosition(100 + piece.getWidth() * (piece.squareIndex%8), 20 + piece.getHeight() * (piece.squareIndex/8));
		piece.isTouching=false;

	}
}
