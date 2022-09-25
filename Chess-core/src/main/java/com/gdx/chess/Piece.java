package com.gdx.chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;


enum Type{
	
	PAWN,BISHOP,KNIGHT,ROOK,QUEEN,KING;
	
}

enum Colour{
	
	BLACK,WHITE;
	
	private static final Colour[] VALUES = values();
	
	public Colour opposite() {
		return VALUES[this.ordinal()^1];
	}
}

public class Piece extends Actor{
	
	static Texture[][] textures= new Texture[2][6];
	static Texture circle;
	
	Texture currentTexture; 
	Type type = Type.BISHOP;
	
	private final int HEIGHT=50;
	private final int WIDTH=50;
	
	public boolean isTouching = false;
	
	public int row=0;
	public int col=0;
	public Colour color;
	
	public long valid =0;
	public long pseudoLegalMoves =0;
	public long attackedSquares =0;
	public List<Integer> validList = new ArrayList<Integer>();
	
	
	static {
		
		mapTextures();
	}
	
	
	public Piece(int row, int col,Type type,Colour color) {
		
		currentTexture=textures[color.ordinal()][type.ordinal()];
		
		this.type=type;
		this.color=color;
		this.row=row;
		this.col=col;
		
		this.setX(100+WIDTH*col);
		this.setY(20+HEIGHT*row);
		this.setHeight(HEIGHT);
		this.setWidth(WIDTH);
				
		this.addListener(new PieceMovementListener(this));
		
	}

	public void getValidMoves() {
		
		valid=0; // could cache the results too 
//
//		switch (type) {
//			case PAWN:
//				MoveLogic.findPawnMove(this,row,col);
//				break;
//			case BISHOP:
//				 MoveLogic.findDiagonals(this,row,col);
//				break;
//			case KNIGHT:
//				 MoveLogic.findKnightMoves(this,row,col);
//				break;
//			case ROOK:
//				 MoveLogic.findRookMoves(this,row,col);
//				break;
//			case QUEEN:
//				MoveLogic.findRookMoves(this,row,col);
//				MoveLogic.findDiagonals(this,row,col);
//				break;
//			case KING:
//				MoveLogic.findKingMoves(this,row, col);
//				break;
//			default:
//
//		}
		
		attackedSquares = 0;
		
		switch (type) {
			case PAWN:
				attackedSquares = MoveLogic.pawn_moves(row, col, color);
				break;
			case BISHOP:
				attackedSquares = MoveLogic.bishop_moves(row, col);
				break;
			case KNIGHT:
				attackedSquares = MoveLogic.knight_moves(row, col);
				break;
			case ROOK:
				attackedSquares = MoveLogic.rook_moves(row, col);
				break;
			case QUEEN:
				attackedSquares = MoveLogic.rook_moves(row, col);
				attackedSquares |= MoveLogic.bishop_moves(row, col);
				break;
			case KING:
				attackedSquares = MoveLogic.king_moves(row, col);
				break;
		default:

		}
		pseudoLegalMoves = MoveLogic.filterPseudoLegalMoves(attackedSquares, color); 
		valid = pseudoLegalMoves;
		for(long i = 1, num = 0; num < 64; i <<= 1, num++) {
			if((valid & i) != 0) {
				validList.add((int) num);
			}
		}
		//System.out.println(Arrays.toString(validList.toArray()));
	}
	
	
	@Override
	public void draw(Batch batch,float parentAlpha) {
		
		batch.draw(currentTexture,this.getX(),this.getY(), WIDTH, HEIGHT);
		
		if(this.isTouching) {
			drawValidMoves(batch,parentAlpha);
		}
		
	}
	
	public void drawValidMoves(Batch batch, float parentAlpha) {
		
//		for(int i =0 ;i < validList.size();i++) {
//			int num = validList.get(i);
//			batch.draw(circle,115+WIDTH*(num%8),35+HEIGHT*(num/8),20,20); // Least Significant file mapping
//		}
		
		for(long i = 1, num = 0; i <= valid; i <<= 1L, num++) {
			if((valid & i) != 0) {
				batch.draw(circle,115+WIDTH*(num%8),35+HEIGHT*(num/8),20,20); // Least Significant file mapping
			}
		}
		
//		for(int i = 0; i<64;i++) {
//			if((GameState.blackControlled & 1L << i)!=0){
//				batch.draw(circle,115+WIDTH*(i%8),35+HEIGHT*(i/8),10,10); // Least Significant file mapping
//			}
//		}
		
	}
	
	/**
	    * Precondition: called getValidMoves
	    * Tests if position is valid and or 
	    */
	public boolean canMoveTo(int row,int col) {
		
		if((valid & (1L << (row*8 + col)))!=0 ){
			//if(GameState.isOccupied(row, col) && Board.board[row][col].color==this.color) {
				//return false;
			//}
			return true;
		}	
		return false;
	}
	
	// Captures the piece if it exists
	public void capture(Piece toCapture) {
		if(toCapture!=null && toCapture.color!=this.color) {
			boolean b=(toCapture.color==Colour.BLACK) ? Board.blackPieces.remove(toCapture) : Board.whitePieces.remove(toCapture);
			toCapture.remove();
			Board.board[row][col]=null;
		}
	}
	
	public void moveTo(int row, int col) {
		
		this.addAction(Actions.moveTo(100+this.getWidth()*col, 20+this.getHeight()*row, 0.5f)); 
		
		//capture(Board.board[row][col]); // capture the piece if possible
		
		Board.board[row][col] = null;
		GameState.setUnoccupied(row,col);
		
		this.row = row; // update position of the piece
		this.col = col;

		
		Board.board[row][col] = this;
		GameState.setOccupied(row,col);
		
		GameState.blackControlled = 0;
		GameState.whiteControlled = 0;
		
		for (int i = 0; i < 8; i++) {   //recalculate all the valid moves for every piece
			for (int j = 0; j < 8; j++) {
				if (Board.board[i][j] != null) {
					Board.board[i][j].valid=0;
					Board.board[i][j].validList.clear();
					Board.board[i][j].getValidMoves();
				}
			}
		}		
		GameState.playerTurn=GameState.playerTurn.opposite(); // end turn
	}
	
	
	private static void mapTextures() {
		
		circle	= Chess.am.manager.get("circle.png",Texture.class);
		
		for(int i =0;i<textures.length;i++) {
	
			String color = (i==Colour.BLACK.ordinal()) ? "d" : "l";
			
			textures[i][Type.PAWN.ordinal()]	= Chess.am.manager.get(color+"_pawn.png",Texture.class);
			textures[i][Type.BISHOP.ordinal()]	= Chess.am.manager.get(color+"_bishop.png",Texture.class);
			textures[i][Type.KNIGHT.ordinal()]	= Chess.am.manager.get(color+"_knight.png",Texture.class);
			textures[i][Type.ROOK.ordinal()]	= Chess.am.manager.get(color+"_rook.png",Texture.class);
			textures[i][Type.QUEEN.ordinal()]	= Chess.am.manager.get(color+"_queen.png",Texture.class);
			textures[i][Type.KING.ordinal()]	= Chess.am.manager.get(color+"_king.png",Texture.class);
			
		}
	}
	
	

}
