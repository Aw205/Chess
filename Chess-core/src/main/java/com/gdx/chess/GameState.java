package com.gdx.chess;

import java.util.ArrayList;
import java.util.List;

public final class GameState {
	
	public static Colour playerTurn= Colour.WHITE;
	public boolean hasCastled = false;
	
	public static long occupied = 0;
	public static long [] colorPositions = new long[2];
	public static long [] attackedSquares = new long[2];
	public static long [] piecePosition = new long [12];
	
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
				long pos = 1L << (p.squareIndex);
				occupied |= pos;
				piecePosition[p.type.ordinal() * (p.color.ordinal()+1)] |= pos;
				colorPositions[p.color.ordinal()] |= pos;
			}
		}
		for(Piece p: Board.board) {
				if(p!=null) {
					if(p.color == Colour.BLACK) {
						blackPieces.add(p);
						p.generateAttackedSquares();
						attackedSquares[p.color.ordinal()] |= p.attackedSquares;
					}
				}
		}
		
		for(Piece p: Board.board) {
			if(p!=null && p.color==Colour.WHITE) {
				
				whitePieces.add(p);
				p.generateLegalMoves();
				long move = p.legalMoves;
				
				while(move!=0) {
					int squareIndex = Long.numberOfTrailingZeros(move);
					p.validList.add(squareIndex);
					move &= (move -1);
				}
				
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
		
		int fromIndex = Long.numberOfTrailingZeros(move.from);
		int toIndex = Long.numberOfTrailingZeros(move.to);
		
		long fromTo = move.from ^ move.to;
		Type type = Board.board[fromIndex].type;
		
		colorPositions[playerTurn.ordinal()] ^= fromTo;
		piecePosition[type.ordinal() * (playerTurn.ordinal()+1)]^= fromTo;
		Board.board[fromIndex].squareIndex = toIndex;
		Board.board[toIndex] = Board.board[fromIndex];
		Board.board[fromIndex] = null;
		
		//if a capture
		if((occupied & move.to) != 0) {
			Type capturedType = Board.board[toIndex].type;
			pieceArr[playerTurn.opposite().ordinal()].remove(Board.board[toIndex]);
			
			colorPositions[playerTurn.opposite().ordinal()] ^= move.to;
			piecePosition[capturedType.ordinal() * (playerTurn.opposite().ordinal()+1)]^= move.to;
			occupied ^= move.from;
			return;
		}
			occupied ^= fromTo;
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
		Piece king = (playerTurn == Colour.WHITE) ? Board.bKing : Board.wKing;
		long checkers = MoveLogic.getAttackersToKing(king);
		if (checkers != 0) {
			System.out.println("king in check"); //check is still buggy
			MoveLogic.updateCheckMasks(king, checkers);
		}

		// generate legal moves to add to move list
		// also update the attacked squares in the process
		for (Piece p : GameState.pieceArr[playerTurn.opposite().ordinal()]) {
			p.generateLegalMoves();
			attackedSquares[p.color.ordinal()] |= p.attackedSquares;
			addMove(p.squareIndex,p.legalMoves);
		}
		
		//reset check mask
		MoveLogic.capture_mask= Long.MAX_VALUE;
		MoveLogic.push_mask= Long.MAX_VALUE;

	}
	
	
	private static void addMove(int fromSquare,long move) {
		
		while(move!=0) {
			int squareIndex = Long.numberOfTrailingZeros(move);
			long from = 1L << fromSquare;
			long to = 1L << squareIndex;
			moves.add(new Move(from,to));
			move &= (move -1);
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
		piecePosition[p.type.ordinal() * (p.color.ordinal()+1)] |= 1L << (p.squareIndex);
	}

	public static boolean isOccupied(int row,int col) {
		return (occupied & 1L << (row*8+col))!=0;
	}
	
	public void resetState() {
		playerTurn=Colour.WHITE;
		hasCastled = false;
		occupied = 0;
	}
	
}
