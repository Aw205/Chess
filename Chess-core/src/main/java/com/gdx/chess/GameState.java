package com.gdx.chess;

import java.util.ArrayList;
import java.util.List;

public final class GameState {
	
	public static Colour playerTurn = Colour.WHITE;
	public boolean hasCastled = false;
	
	public static long occupied = 0;
	public static long [] colorPositions = new long[2];
	public static long [] attackedSquares = new long[2];
	public static long [][] piecePosition = new long [2][6];
	
	public static List<Move> moves = new ArrayList<Move>();
	
	@SuppressWarnings("unchecked")
	public static List<Piece> [] pieceArr = new List[2];
	public static List<Piece> blackPieces = new ArrayList<Piece>();
	public static List<Piece> whitePieces = new ArrayList<Piece>();
	
	
	public GameState() {

		
	}
	
	public static void init() {
		
		for(Piece p: Board.board) {
			if(p!=null) {
				long pos = MoveLogic.squareToBB.get(p.squareIndex);
				occupied |= pos;
				piecePosition[p.color.ordinal()][p.type.ordinal()] |= pos;
				colorPositions[p.color.ordinal()] |= pos;
			}
		}
		for(Piece p: Board.board) {
				if(p!=null) {
					if(p.color == Colour.BLACK) {
						p.generateAttackedSquares();
						attackedSquares[p.color.ordinal()] |= p.attackedSquares;
					}
				}
		}
		
		for(Piece p: Board.board) {
			if(p!=null && p.color==Colour.WHITE) {
				p.generateLegalMoves();
				addMoves(p.squareIndex,p.legalMoves);		
				attackedSquares[p.color.ordinal()] |= p.attackedSquares;
			}
		}
		
		pieceArr[Colour.WHITE.ordinal()] = whitePieces;
		pieceArr[Colour.BLACK.ordinal()]= blackPieces;
	}
	
	
	public static void update(Move move) {
		
		updatePositions(move);
		generateMoves();
		
		playerTurn = playerTurn.opposite();
		
	}
	
	private static void updatePositions(Move move) {

		int fromIndex = MoveLogic.BBtoSquare.get(move.from);
		int toIndex = MoveLogic.BBtoSquare.get(move.to);

		long fromTo = move.from ^ move.to;
		Type type = Board.board[fromIndex].type;

		colorPositions[playerTurn.ordinal()] ^= fromTo;
		piecePosition[playerTurn.ordinal()][type.ordinal()] ^= fromTo;

		Board.board[fromIndex].squareIndex = toIndex;

		// if a capture
		if ((occupied & move.to) != 0) {
			Type capturedType = Board.board[toIndex].type;
			pieceArr[playerTurn.opposite().ordinal()].remove(Board.board[toIndex]);

			colorPositions[playerTurn.opposite().ordinal()] ^= move.to;
			piecePosition[playerTurn.opposite().ordinal()][capturedType.ordinal()] ^= move.to;
			occupied ^= move.from;
		} else {
			occupied ^= fromTo;
		}

		Board.board[toIndex] = Board.board[fromIndex];
		Board.board[fromIndex] = null;
	}
	
	
	public static void generateMoves() {
		
		moves.clear();
		attackedSquares[0] = 0;
		attackedSquares[1] = 0;

		// recalculate attacked squares for opposite side
		for (Piece p : GameState.pieceArr[playerTurn.ordinal()]) {
			attackedSquares[playerTurn.ordinal()] |= p.generateAttackedSquares();
		}

		// determine check masks so can generate right legal moves
		
		long kingPos = GameState.piecePosition[playerTurn.opposite().ordinal()][Type.KING.ordinal()];
		int kingIndex = MoveLogic.BBtoSquare.get(kingPos);
		long checkers = MoveLogic.getAttackersToKing(playerTurn,kingIndex);
		
		if (checkers != 0) {
			MoveLogic.updateCheckMasks(kingIndex, checkers);
		}
		
		//find pins
		MoveLogic.findAbsolutePins();

		// generate legal moves to add to move list
		// also update the attacked squares in the process
		for (Piece p : GameState.pieceArr[playerTurn.opposite().ordinal()]) {
			p.generateLegalMoves();
			
			long from =  MoveLogic.squareToBB.get(p.squareIndex);
			boolean isPinned = (from & MoveLogic.pinned) !=0;
			if(isPinned) {
				p.legalMoves &= MoveLogic.squaresToLine[p.squareIndex][kingIndex];	
			}
			
			attackedSquares[p.color.ordinal()] |= p.attackedSquares;
			addMoves(p.squareIndex,p.legalMoves);
		}
		
		//reset check mask
		MoveLogic.capture_mask= Long.MAX_VALUE;
		MoveLogic.push_mask= Long.MAX_VALUE;
		MoveLogic.king_mask = Long.MAX_VALUE;
	}
	
	
	private static void addMoves(int fromIndex,long move) {
		
		long from =  MoveLogic.squareToBB.get(fromIndex);
		while(move!=0) {
			int squareIndex = Long.numberOfTrailingZeros(move);	
			long to = MoveLogic.squareToBB.get(squareIndex);
			moves.add(new Move(from,to));
			move &= (move -1);
		}
	}
	
}
