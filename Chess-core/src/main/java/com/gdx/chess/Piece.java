package com.gdx.chess;

public class Piece{
	
	Type type = Type.BISHOP;

	public int squareIndex = 0;
	public Colour color;
	
	private long pin_mask = -1;
	public long legalMoves = 0;
	public long pseudoLegalMoves =0;
	public long attackedSquares =0;
	
	
	public Piece(int squareIndex,Type type,Colour color) {
		
		this.type = type;
		this.color = color;
		this.squareIndex = squareIndex;

	}
	
	public long generateLegalMoves() {
		
		generateAttackedSquares();

		pseudoLegalMoves = MoveLogic.filterPseudoLegalMoves(attackedSquares, color);

		if (type == Type.PAWN) {
			pseudoLegalMoves &= GameState.occupied;
			pseudoLegalMoves |= MoveLogic.single_pawn_push(squareIndex, color);
			pseudoLegalMoves |= MoveLogic.double_pawn_push(squareIndex, color);
		}

		calcPinMask();
		legalMoves = MoveLogic.filterLegalMoves(type, color, pin_mask, pseudoLegalMoves);
		
		return legalMoves;
		
	}
	
	public void calcPinMask() {
		
		long kingPos = GameState.piecePosition[color.ordinal()][Type.KING.ordinal()];
		int kingIndex = MoveLogic.BBtoSquare.get(kingPos);
		
		pin_mask = -1;
		long from = MoveLogic.squareToBB.get(squareIndex);
		boolean isPinned = (from & MoveLogic.pinned) !=0;
		if(isPinned) {
			pin_mask = MoveLogic.squaresToLine[squareIndex][kingIndex];	
		}
	}
	
	public long generateAttackedSquares(){

		switch (type) {
			case PAWN:
				attackedSquares = MoveLogic.pawnAttacks[color.ordinal()][squareIndex];
				break;
			case BISHOP:
				attackedSquares = MoveLogic.bishop_moves(squareIndex,GameState.occupied);
				break;
			case KNIGHT:
				attackedSquares = MoveLogic.knightAttacks[squareIndex];
				break;
			case ROOK:
				attackedSquares = MoveLogic.rook_moves(squareIndex,GameState.occupied);
				break;
			case QUEEN:
				attackedSquares = MoveLogic.rook_moves(squareIndex,GameState.occupied);
				attackedSquares |= MoveLogic.bishop_moves(squareIndex,GameState.occupied);
				break;
			case KING:
				attackedSquares = MoveLogic.kingAttacks[squareIndex];
				break;
			default:

		}
		return attackedSquares;
	}

}
