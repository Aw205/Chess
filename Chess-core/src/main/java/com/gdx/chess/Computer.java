package com.gdx.chess;

import java.util.Random;

public class Computer {

	Colour color;
	Random rand = new Random();
	
	
	public Computer(Colour color) {
		
		this.color=color;
	}
	
	
	public void search() {
		
		if(GameState.playerTurn!=this.color) {
			return;
		}
			makeRandomMove();
	}
	
	private void makeRandomMove() {
		
		Move m = GameState.moves.get(rand.nextInt(GameState.moves.size()));
		int fromIndex = Long.numberOfTrailingZeros(m.from);
		int toIndex = Long.numberOfTrailingZeros(m.to);
		//System.out.println("from: " + fromIndex + " to: " + toIndex);
		Board.board[fromIndex].moveTo(toIndex);
		
		GameState.update(m);
		
		for(Piece p: GameState.whitePieces) {
		    long move = p.legalMoves;
		    p.validList.clear();
			while(move!=0) {
				int squareIndex = Long.numberOfTrailingZeros(move);
				p.validList.add(squareIndex);
				move &= (move -1);
			}
		}
	}
	
}



