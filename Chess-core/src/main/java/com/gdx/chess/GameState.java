package com.gdx.chess;

public final class GameState {
	
	public static Colour playerTurn= Colour.WHITE;
	public boolean hasCastled = false;
	
	public static long occupied = 0;
	
	public static long whiteControlled = 0;
	public static long blackControlled =0;
	
	public static long [] piecePosition = new long [10]; //array of bitboards
	public static long whitePositions =0 ;
	public static long blackPositions =0;
	
	public GameState() {

	}
	
	public static void update() {
		
		
		
		//update game state by 
		
	}
	
	public static void calcControlledSquares() {
		
		for (Piece[] arr : Board.board) {
			for (Piece p : arr) { // this does work because the move logic includes the piece being attacked
				if (p.color == Colour.WHITE) {
					whiteControlled |= p.pseudoLegalMoves;
				}
				blackControlled |= p.pseudoLegalMoves;
			}
		}

	}
	

	/** 
	 * @param type - pawn,knight,bishop,rook,queen,king
	 * @param color - white or black
	 * @return bitboard of requested piece positions
	 */
	public static long getPiecePosition(Type type,Colour color) {
		
		return piecePosition[type.ordinal() * (color.ordinal()+1)];
	}
	
	public static void setPiecePosition(Piece p) {
		
		piecePosition[p.type.ordinal() * (p.color.ordinal()+1)] |= 1L << (p.row*8+p.col);
		
	}
	
	// to clear least significant bit use x&(x-1)
	
	public static boolean isOccupied(int row,int col) {
		return (occupied & 1L << (row*8+col))!=0;
	}

	public static void setOccupied(int row,int col) {
		occupied |= 1L << (row*8+col);
	}
	
	public static void setUnoccupied(int row,int col) {
		occupied &= ~(1L << (row*8+col));
	}
	
	public static void setControlled(Colour color,int row,int col) {
		if(color==Colour.BLACK) {
			blackControlled |= 1L << (row*8+col);
			return;
		}
		whiteControlled |= 1L << (row*8+col);
	}
	
	public void resetState() {
		playerTurn=Colour.WHITE;
		hasCastled = false;
		occupied = 0;
	}
	
}
