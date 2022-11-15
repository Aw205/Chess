package com.gdx.chess;

import java.util.HashMap;
import java.util.Map;

public class MoveLogic {
	
	public static long capture_mask = Long.MAX_VALUE;
	public static long push_mask = Long.MAX_VALUE;
	public static long king_mask = Long.MAX_VALUE;
	public static long pinned = 0;
	
	public static Map<Integer,Long> squareToBB = new HashMap<Integer,Long>();
	public static Map<Long,Integer> BBtoSquare = new HashMap<Long,Integer>();
	
	public static long [] diagMasks = new long [16];
	public static long [] antiDiagMasks = new long[16];
	public static long [] rankMasks = new long [8];
	public static long [] fileMasks = new long [8];
	
	public static long [][] pawnAttacks = new long [2][64];
	public static long [] knightAttacks = new long [64];
	public static long [] kingAttacks = new long[64];
	
	public static long [][] inBetween = new long [64][64];
	public static long [][] squaresToLine = new long [64][64];
	

	public static long xRayRookAttacks(int square, long blockers) {

		long attacks = rook_moves(square,GameState.occupied);
		blockers &= attacks;
		return attacks ^ rook_moves(square,GameState.occupied ^ blockers);

	}

	public static long xRayBishopAttacks(int square, long blockers) {

		long attacks = bishop_moves(square, GameState.occupied);
		blockers &= attacks;
		return attacks ^ bishop_moves(square, GameState.occupied ^ blockers);

	}
	
	public static long sliding_moves(int square,long occupied,long mask) {
		
		long piece_pos = squareToBB.get(square);
		long piece_pos_rev = Long.reverse(piece_pos);
		long occupied_rev = Long.reverse(occupied & mask);
		
		long left = (occupied & mask) - 2*piece_pos;
		long right = Long.reverse((occupied_rev - 2*piece_pos_rev));
		long slidingAttacks = (left^right) & mask;
		
		return slidingAttacks;
	}
	
	public static long bishop_moves(int square,long occupied) {
		int row = square/8;
		int col = square%8;
		int diagIndex = (row-col) & 15;
		int antiDiagIndex = (row+col) ^ 7;
		
		return sliding_moves(square,occupied,diagMasks[diagIndex]) | sliding_moves(square,occupied,antiDiagMasks[antiDiagIndex]);
		
	}

	//hyperbola quintessence
	public static long rook_moves(int square,long occupied) {
		
		return sliding_moves(square,occupied,rankMasks[square/8]) | sliding_moves(square,occupied,fileMasks[square%8]);
		
	}
	

	public static long filterLegalMoves(Type type,Colour color,long moves) {
		
		if(type == Type.KING) {
			 moves &= ~GameState.attackedSquares[color.opposite().ordinal()]; 
			 moves &= king_mask;
			 return moves;
		}
			return moves &= (push_mask | capture_mask);
	}
	
	public static long filterPseudoLegalMoves(long moves,Colour pColor) { 
		
		long piecePositions = GameState.colorPositions[pColor.ordinal()];
		moves &= ~piecePositions;
		return moves;
	}
	
	private static long pawn_attacks(long square, Colour color) {
		
		long east = (square << 1) & ~fileMasks[0];
		long west = (square >> 1) & ~fileMasks[7];
		return (color == Colour.WHITE) ? (east|west) << 8 : (east|west) >> 8;
		
	}
	
	public static long single_pawn_push(int square, Colour color) {
		
		long pawn_move = MoveLogic.squareToBB.get(square);
		return ((pawn_move << 8) >> (color.ordinal() << 4)) & ~(GameState.occupied);
		
	}
	
	public static long double_pawn_push(int square, Colour color) {
		
		long rank = (color == Colour.WHITE) ? rankMasks[3] : rankMasks[4];
		long push = single_pawn_push(square,color);
		return ((push << 8) >> (color.ordinal() << 4)) & ~(GameState.occupied) & rank;
		
	}
	
	
	public static long getAttackersToKing(Colour pColor,int kingIndex) {
		
		long rookPos, bishopPos, pawnPos, knightPos;
		
		pawnPos = GameState.piecePosition[pColor.ordinal()][Type.PAWN.ordinal()];
	    knightPos = GameState.piecePosition[pColor.ordinal()][Type.KNIGHT.ordinal()];
		rookPos = bishopPos = GameState.piecePosition[pColor.ordinal()][Type.QUEEN.ordinal()];
		bishopPos |= GameState.piecePosition[pColor.ordinal()][Type.BISHOP.ordinal()];
		rookPos |= GameState.piecePosition[pColor.ordinal()][Type.ROOK.ordinal()];
	
		return (pawnAttacks[pColor.opposite().ordinal()][kingIndex] & pawnPos) | (knightAttacks[kingIndex] & knightPos)
				| (bishop_moves(kingIndex,GameState.occupied) & bishopPos) | (rook_moves(kingIndex,GameState.occupied) & rookPos);

	}
	
	public static void findAbsolutePins() {
		
		long occRQ = GameState.piecePosition[GameState.playerTurn.ordinal()][Type.ROOK.ordinal()];
		long occBQ = GameState.piecePosition[GameState.playerTurn.ordinal()][Type.BISHOP.ordinal()];
		
		long kingBB = GameState.piecePosition[GameState.playerTurn.opposite().ordinal()][Type.KING.ordinal()];
		int kingPos = BBtoSquare.get(kingBB);
		
		long pinner = xRayRookAttacks(kingPos,GameState.colorPositions[GameState.playerTurn.opposite().ordinal()]) & occRQ;
		pinned = 0;
		
		while(pinner!=0) {
			int square = Long.numberOfTrailingZeros(pinner);
			pinned |= inBetween[square][kingPos] & GameState.colorPositions[GameState.playerTurn.opposite().ordinal()];
			pinner &= pinner-1;
		}
		
	    pinner = xRayBishopAttacks(kingPos,GameState.colorPositions[GameState.playerTurn.opposite().ordinal()]) & occBQ;
		
		while(pinner!=0) {
			int square = Long.numberOfTrailingZeros(pinner);
			pinned |= inBetween[square][kingPos] & GameState.colorPositions[GameState.playerTurn.opposite().ordinal()];
			pinner &= pinner-1;
		}
	}
	
	public static void updateCheckMasks(int kingIndex,long attackers) {
		
		if(Long.bitCount(attackers) > 1) { // need to set king mask still
			push_mask = 0;
			capture_mask = 0;
			return;
		}
		
		capture_mask = attackers;
		push_mask = 0;
		
		int attackerIndex = MoveLogic.BBtoSquare.get(attackers);
		Type t = Board.board[attackerIndex].type;
		
		if(t== Type.BISHOP || t== Type.ROOK || t == Type.QUEEN){
			
			push_mask = MoveLogic.inBetween[kingIndex][attackerIndex];
			king_mask = ~squaresToLine[kingIndex][attackerIndex];
		}

	}

	private static long knight_moves(long pos) {

		long east, west, attacks = 0;

		east = (pos << 1) & ~fileMasks[0];
		west = (pos >> 1) & ~fileMasks[7];
		attacks = (east | west) << 16;
		attacks |= (east | west) >> 16;
		east = (east << 1) & ~fileMasks[0];
		west = (west >> 1) & ~fileMasks[7];
		attacks |= (east | west) << 8;
		attacks |= (east | west) >> 8;

		return attacks;

	}
	
	private static long king_moves(long square) {
		
		long east = (square << 1) & ~fileMasks[0];
		long west = (square >> 1) & ~fileMasks[7];
		long attack = (east | west);
		attack |= (attack << 8 | attack >> 8);
		
		return (attack | (square >> 8)| (square << 8));
	
	}
	
	public static void precompute() {
		
		for (int row = 0; row < 8; row++) {

			rankMasks[row] = 0xFFL << row * 8;
			fileMasks[row] = 0x101010101010101L << row;
		}
		
		for(int i =0; i< 64;i++) {
			
			int row = i/8, col = i%8;
			long BB = 1L << i;
			squareToBB.put(i, BB);
			BBtoSquare.put(BB, i);
			
			int diagIndex = (row - col) & 15;
			int antiDiagIndex = (row + col) ^ 7;

			diagMasks[diagIndex] |= BB;
			antiDiagMasks[antiDiagIndex] |= BB;

			knightAttacks[i] = knight_moves(BB);
			kingAttacks[i] = king_moves(BB);
			
			pawnAttacks[Colour.WHITE.ordinal()][i] = pawn_attacks(BB, Colour.WHITE);
			pawnAttacks[Colour.BLACK.ordinal()][i] = pawn_attacks(BB, Colour.BLACK);
			
		}
		
		for (int from = 0; from < 64; from++) {
			for (int to = from + 1; to < 64; to++) {

				int fromRow = from / 8, fromCol = from % 8, toRow = to / 8, toCol = to % 8;
				long occupied = squareToBB.get(from) | squareToBB.get(to);
				long currentMask = 0;

				if (fromRow == toRow) {
					currentMask = rankMasks[fromRow];

				} else if (fromCol == toCol) {
					currentMask = fileMasks[fromCol];

				} else if ((fromRow - toRow) * (fromCol - toCol) > 0) {
					int diagIndex = (toRow - toCol) & 15;
					currentMask = diagMasks[diagIndex];

				} else {
					int antiDiagIndex = (toRow + toCol) ^ 7;
					currentMask = antiDiagMasks[antiDiagIndex];
				}
				
				long inBetweenSquares = sliding_moves(from, occupied, currentMask) & sliding_moves(to, occupied, currentMask);
				
				inBetween[from][to] = inBetween[to][from] = inBetweenSquares;
				squaresToLine[from][to] = squaresToLine[to][from] = currentMask;

			}
		}
	}
}
