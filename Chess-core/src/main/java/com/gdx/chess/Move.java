package com.gdx.chess;



public class Move {
	
	public long from = 0;
	public long to = 0;
	public mType type;
	
	public Move(long from, long to) {

		this.from = from;
		this.to = to;
		
	}
	
	public Move(long from, long to, mType type) {
		
		this.from = from;
		this.to = to;
		this.type = type;
		
	}

}
