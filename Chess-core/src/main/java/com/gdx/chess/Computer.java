package com.gdx.chess;

import java.util.Random;

public class Computer {

	Colour color;
	Random rand = new Random();
	
	
	public Computer(Colour color) {
		
		this.color=color;
	}
	
	
	public void search() {
		
		if(GameState.playerTurn == this.color) {
			makeRandomMove();
		}
	}
	
	private void makeRandomMove() {
		
//		for(Move m : GameState.moves) {
//			System.out.println(MoveLogic.BBtoSquare.get(m.from) + " - " + MoveLogic.BBtoSquare.get(m.to));
//		}
//		System.out.println("----------------------------------");
		
		Move m = GameState.moves.get(rand.nextInt(GameState.moves.size()));
		int from = MoveLogic.BBtoSquare.get(m.from);
		int to = MoveLogic.BBtoSquare.get(m.to);
		
		Board.board[from].moveTo(to);
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



