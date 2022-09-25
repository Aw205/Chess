package com.gdx.chess;

import java.util.List;
import java.util.Random;

public class Computer {

	Colour color;
	List<Piece> myPieces;
	List<Piece> opponentPieces;
	Random rand = new Random();
	
	
	public Computer(Colour color) {
		
		this.color=color;
		
		myPieces = (color==Colour.BLACK) ? Board.blackPieces : Board.whitePieces;
		opponentPieces = (color==Colour.WHITE) ? Board.blackPieces : Board.whitePieces;
	}
	
	
	public void search() {
		
		if(GameState.playerTurn!=this.color) {
			return;
		}
		
		if(false && MoveLogic.isInCheck(null)) {
			for(Piece p : Board.whitePieces) {
				p.getValidMoves(); // calculates opponent controlled squares
			}
			MoveLogic.findKingMoves(null, 0, 0);
		}
		else {
			makeRandomMove();
		}
	}
	
	private void makeRandomMove() {
		
		int numMoves = 0;
		Piece piece = null;
		
		while(numMoves==0) {
			piece = myPieces.get(rand.nextInt(myPieces.size())); 
			numMoves = piece.validList.size();
		}
		
		int pos = piece.validList.get(rand.nextInt(piece.validList.size()));
		piece.moveTo(pos/8, pos%8);	
	}
	
}



