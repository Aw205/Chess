package com.gdx.chess;

public class MoveLogic {
	
	public static long capture_mask = Long.MAX_VALUE;
	public static long push_mask = Long.MAX_VALUE;
	
	public static long [] diagMasks = new long [16];
	public static long [] antiDiagMasks = new long[16];
	public static long [] rankMasks = new long [8];
	public static long [] fileMasks = new long [8];
	
	public static long [][] pawnAttacks = new long [2][64];
	public static long [] knightAttacks = new long [64];
	public static long [] kingAttacks = new long[64];
	
	static {
		
		for(int row =0;row < 8;row++) {
			
			//rankMasks[row] = ((1L << (8*(row+1))) -1)  - ((1L << (8*(row))) -1);
			rankMasks[row] = 0xFFL << row*8;
			fileMasks[row]= 0x101010101010101L << row;
			
			for(int col = 0; col < 8;col++) {
				
				int diagIndex = (row-col) & 15;
				int antiDiagIndex = (row+col) ^ 7;
				
				diagMasks[diagIndex] |= 1L << (row*8 + col);
				antiDiagMasks[antiDiagIndex] |= 1L << (row*8 + col);
				
				
				knightAttacks[row*8 + col] = knight_moves(row, col);
				kingAttacks[row*8 + col] = king_moves(row,col);
				pawnAttacks[Colour.WHITE.ordinal()][col] = pawn_attacks(row,col,Colour.WHITE);
				pawnAttacks[Colour.BLACK.ordinal()][col] = pawn_attacks(row,col,Colour.BLACK);
				
			}
		}
		//rankMasks[7]-=1;
	}

	private static boolean inBounds(int row, int col) {

		return (row < Board.BOARD_HEIGHT && row > -1 && col < Board.BOARD_WIDTH && col > -1);
	}
	
	
	public static long sliding_moves(int square,long mask) {
		
		long piece_pos = 1L << square;
		long piece_pos_rev = Long.reverse(piece_pos);
		long occupied_rev = Long.reverse(GameState.occupied & mask);
		
		long left = (GameState.occupied & mask) - 2*piece_pos;
		long right = Long.reverse((occupied_rev - 2*piece_pos_rev));
		long slidingAttacks = (left^right) & mask;
		
		return slidingAttacks;
	}
	
	public static long bishop_moves(int square) {
		int row = square/8;
		int col = square%8;
		int diagIndex = (row-col) & 15;
		int antiDiagIndex = (row+col) ^ 7;
		
		return sliding_moves(square,diagMasks[diagIndex]) | sliding_moves(square,antiDiagMasks[antiDiagIndex]);
		
	}

	//hyperbola quintessence
	public static long rook_moves(int square) {
		
		return sliding_moves(square,rankMasks[square/8]) | sliding_moves(square,fileMasks[square%8]);
		
	}
	
	
	private static long knight_moves(int row, int col) {
		
		long knight_moves = 0;

		int[] offset1 = { 1, -1 };
		int[] offset2 = { 2, -2 };

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				int r = row + offset1[i];
				int c = col + offset2[j];
				if (inBounds(r, c)) {
					knight_moves |= 1L << (r * 8 + c);
				}
				r = row + offset2[j];
				c = col + offset1[i];
				if (inBounds(r, c)) {
					knight_moves |= 1L << (r * 8 + c);
				}
			}
		}
		return knight_moves;
	}

	public static long filterLegalMoves(Type type,Colour color,long moves) {
		
		if(type == Type.KING) {
			return moves &= ~GameState.attackedSquares[color.opposite().ordinal()];
		}
			return moves &= (push_mask | capture_mask);
	}
	
	public static long filterPseudoLegalMoves(long moves,Colour pColor) { 
		
		long piecePositions = GameState.colorPositions[pColor.ordinal()];
		moves &= ~piecePositions;
		return moves;

	}
	
	
	public static long pawn_attacks(int row,int col, Colour color) {
		
		long pawn_attacks = 0;
		int incr = (color == Colour.WHITE) ? 1 : -1;
		if (inBounds(row+ incr, col +1)) {
			pawn_attacks |= 1L << ( (row+incr) * 8 + col+1);	
		}
		if (inBounds(row + incr, col + 1)) {
			pawn_attacks |= 1L << ( (row + incr) * 8 + col+1);
		}
		return pawn_attacks;
	}
	
	private static boolean canDoubleMove(Colour color, int row) {

		return color == Colour.WHITE ? (row == 1) : (row == 6); // need to check for occupied
	}
	
	public static long pawn_moves(int row, int col, Colour color) {
		
		long pawn_moves =0;
		int incr = (color == Colour.WHITE) ? 1 : -1;
		if (inBounds(row + incr, col) && !GameState.isOccupied(row + incr, col)) {
			pawn_moves |= 1L << ((row+incr) * 8 + col);
			if (canDoubleMove(color,row)){
				pawn_moves |= 1L << ((row+incr*2) * 8 + col);
			} 
		}
		return pawn_moves;
	}
	
	
	public static long getAttackersToKing(Piece king) {
		
		long rookPos, bishopPos, pawnPos, knightPos;
		
		pawnPos = GameState.getPiecePosition(Type.PAWN, king.color.opposite());
	    knightPos = GameState.getPiecePosition(Type.KNIGHT, king.color.opposite());
		rookPos = bishopPos = GameState.getPiecePosition(Type.QUEEN, king.color.opposite());
		bishopPos |= GameState.getPiecePosition(Type.BISHOP, king.color.opposite());
		rookPos |= GameState.getPiecePosition(Type.ROOK, king.color.opposite());
	
		return (pawnAttacks[king.color.ordinal()][king.squareIndex] & pawnPos) | (knightAttacks[king.squareIndex] & knightPos)
				| (bishop_moves(king.squareIndex) & bishopPos) | (rook_moves(king.squareIndex) & rookPos);

	}
	

	
	public static void updateCheckMasks(Piece king,long attackers) {
		
		if(Long.bitCount(attackers) > 1) {
			push_mask = 0;
			capture_mask = 0;
			return;
		}
		capture_mask = attackers;
		
		int index = (int) (Math.log(attackers)/Math.log(2));
		int kingRow = king.squareIndex/8;
		int kingCol = king.squareIndex%8;
		int row = index/8;
		int col = index%8;
		
		if( (GameState.getPiecePosition(Type.PAWN, king.color.opposite()) & attackers) == 0 ) {	
			if(kingRow - row == 0) {
				push_mask = sliding_moves(king.squareIndex,rankMasks[row]) & sliding_moves(index,rankMasks[row]);
				return;
			}
			else if(kingCol - col == 0) {
				push_mask = sliding_moves(king.squareIndex,fileMasks[col]) & sliding_moves(index,fileMasks[col]);
				return;
			}
			if((kingRow-row) * (kingCol-col) > 0) {
				int diagIndex = (row-col) & 15;
				push_mask = sliding_moves(king.squareIndex,diagMasks[diagIndex]) & sliding_moves(index,diagMasks[diagIndex]);
				return;
			}
			int antiDiagIndex = (row+col) ^ 7;
			push_mask = sliding_moves(king.squareIndex,antiDiagMasks[antiDiagIndex]) & sliding_moves(index,antiDiagMasks[antiDiagIndex]);
		}
		
	}
	
	public static long king_moves(int row, int col) {
		
		long king_moves = 0;
		for(int i=-1;i<2;i++) {
			for(int j=-1;j<2;j++) {
				if(inBounds(row+i,col+j)) {
					king_moves |= 1L << ((row+i) * 8 + col+j);
				}
			}
		}
		return king_moves;
	}

}
