package com.gdx.chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class GameState {
	
	public static Colour playerTurn = Colour.BLACK;
	
	public static long occupied = 0;
	public static long [] colorPositions = new long[2];
	public static long [] attackedSquares = new long[2];
	public static long [][] piecePosition = new long [2][6];
	public static int [][] castlingRights = {{1,1},{1,1}};
	
	public static List<Move> moves = new ArrayList<Move>();
	public static Map<String,Move> movesMap = new HashMap<String,Move>();
	
	@SuppressWarnings("unchecked")
	public static List<Piece> [] pieceArr = new List[2];
	public static List<Piece> blackPieces = new ArrayList<Piece>();
	public static List<Piece> whitePieces = new ArrayList<Piece>();
	
	public static Piece[] board = new Piece[64];
	
	
	public GameState() {

		
	}
	
	public static void init() {
		
		for(Piece p: board) {
			if(p!=null) {
				long pos = MoveLogic.squareToBB.get(p.squareIndex);
				occupied |= pos;
				piecePosition[p.color.ordinal()][p.type.ordinal()] |= pos;
				colorPositions[p.color.ordinal()] |= pos;
			}
		}
		
		pieceArr[Colour.WHITE.ordinal()] = whitePieces;
		pieceArr[Colour.BLACK.ordinal()]= blackPieces;
		
		generateMoves();
		playerTurn = playerTurn.opposite();
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
		Type type = board[fromIndex].type;
		Type promoType = null;
		
		updateCastlingRights(type);
		
		switch(move.type) {
		
			case QUIET:
				
				piecePosition[playerTurn.ordinal()][type.ordinal()] ^= fromTo;
				occupied ^= fromTo;
				break;
				
			case CAPTURE:
				
				Type capturedType = board[toIndex].type;
				pieceArr[playerTurn.opposite().ordinal()].remove(board[toIndex]);
				colorPositions[playerTurn.opposite().ordinal()] ^= move.to;
				
				piecePosition[playerTurn.opposite().ordinal()][capturedType.ordinal()] ^= move.to;
				piecePosition[playerTurn.ordinal()][type.ordinal()] ^= fromTo;
				
				occupied ^= move.from;
				
				break;
			
			case CASTLE:
				
				Move kingMove = new Move(move.from,move.to,mType.QUIET);
				updatePositions(kingMove);
				Move rookMove = (move.to > move.from) ? new Move(move.from << 3,move.from << 1,mType.QUIET) : new Move(move.from >> 4,move.from >> 1,mType.QUIET);
				updatePositions(rookMove);
				return;
			
			case QUEEN_PROMO:
				
				if(promoType == null) promoType = Type.QUEEN;
				
			case KNIGHT_PROMO:
				
				if(promoType == null) promoType = Type.KNIGHT;
				
			case BISHOP_PROMO:
				
				if(promoType == null) promoType = Type.BISHOP;
				
			case ROOK_PROMO:
				
				if(promoType == null) promoType = Type.ROOK;
				
				piecePosition[playerTurn.ordinal()][type.ordinal()] ^= move.from;
				piecePosition[playerTurn.ordinal()][promoType.ordinal()] ^= move.to;
				
				board[fromIndex].type = Type.QUEEN;
				
				occupied ^= fromTo;
				break;
				
			case QUEEN_PROMO_CAP:
				
				piecePosition[playerTurn.ordinal()][type.ordinal()] ^= move.from;
				piecePosition[playerTurn.ordinal()][Type.QUEEN.ordinal()] ^= move.to;
				
				board[fromIndex].type = Type.QUEEN;
				
				Type capType = board[toIndex].type;
				pieceArr[playerTurn.opposite().ordinal()].remove(board[toIndex]);
				colorPositions[playerTurn.opposite().ordinal()] ^= move.to;
				
				piecePosition[playerTurn.opposite().ordinal()][capType.ordinal()] ^= move.to;
				
				occupied ^= move.from;
				
				break;
				
			default:
				System.out.println("Invalid move");
				break;
		
		}
		
		colorPositions[playerTurn.ordinal()] ^= fromTo;
		board[fromIndex].squareIndex = toIndex;
		board[toIndex] = board[fromIndex];
		board[fromIndex] = null;
		
	}
	
	
	private static void generateMoves() {
		
		moves.clear();
		movesMap.clear();
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
			addMoves(p.squareIndex,p.legalMoves,p.type);
			
			attackedSquares[p.color.ordinal()] |= p.attackedSquares;
		}
		
		generateCastleMoves(kingPos,kingIndex);
		
		//reset check mask
		MoveLogic.capture_mask= -1;
		MoveLogic.push_mask= -1;
		MoveLogic.king_mask = -1;
	}
	
	
	private static void updateCastlingRights(Type type) {
		
		if(type == Type.KING) {
			castlingRights[playerTurn.ordinal()][0] = 0;
			castlingRights[playerTurn.ordinal()][1] = 0;
		}
		
		for(int i = 0; i < 2;i++) {
			int idx = MoveLogic.initialRookSquares[playerTurn.ordinal()][i];
			if(board[idx] == null || board[idx].type!= Type.ROOK) {
				castlingRights[playerTurn.ordinal()][i] = 0;
			}
		}
		
	}

	private static void generateCastleMoves(long kingPos, int kingIndex) {
		
		for (int i = 0; i < 2; i++) {
			if (castlingRights[playerTurn.opposite().ordinal()][i] == 1) {
				if ((MoveLogic.castleSquares[playerTurn.opposite().ordinal()][i] & (occupied | attackedSquares[playerTurn.ordinal()])) == 0) {
					
					int to = MoveLogic.castleTargetSquares[playerTurn.opposite().ordinal()][i];
					Move castle = new Move(kingPos,MoveLogic.squareToBB.get(to),mType.CASTLE);
					
					moves.add(castle);
					String s= Integer.toString(kingIndex) + Integer.toString(to);
					movesMap.put(s, castle);
					
					board[kingIndex].legalMoves |= MoveLogic.squareToBB.get(to);

				}
			}
		}
		
	}
	
	private static void addMoves(int fromIndex,long move,Type type) {
		
		long from =  MoveLogic.squareToBB.get(fromIndex);
		
		while(move!=0) {
			int squareIndex = Long.numberOfTrailingZeros(move);	
			long to = MoveLogic.squareToBB.get(squareIndex);
			
			Move curr = new Move(from,to,mType.QUIET);
			if(type == Type.PAWN && (squareIndex > 55 || squareIndex < 8)) {
				curr.type = mType.QUEEN_PROMO;
				if((occupied & to) != 0) {
					curr.type = mType.QUEEN_PROMO_CAP;
				}
			}
			else if((occupied & to) !=0) { // capture
				curr.type = mType.CAPTURE;
			}
			
			moves.add(curr);
			
			String s = Integer.toString(fromIndex) + Integer.toString(squareIndex);
			movesMap.put(s, curr);
			
			move &= (move -1);
		}
	}
	
}
