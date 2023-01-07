package com.gdx.chess;

import java.util.Random;

import com.gdx.chess.UI.TileBoard;

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
		GameState.update(m);
		TileBoard.update(m,true);
		
	}
	
}



