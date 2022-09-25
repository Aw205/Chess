package com.gdx.chess;

public class MoveLogic {

	private static boolean inBounds(int row, int col) {

		return (row < Board.BOARD_HEIGHT && row > -1 && col < Board.BOARD_WIDTH && col > -1);
	}

	
	private static boolean isControlled(Colour color,int row, int col) {
		
		if(color==Colour.BLACK) {
			return (GameState.blackControlled & 1L << (row * 8 + col)) != 0;
		}
		return (GameState.whiteControlled & 1L << (row * 8 + col)) != 0;
	}

	
	private static boolean canCapture(Colour color,Piece toCapture) {
		
		if(toCapture!=null) {
			return color!=toCapture.color;
		}
		return false;
	}

	private static void addValidMove(Piece p, int row, int col) {

		p.valid |= 1L << (row * 8 + col);
		p.validList.add(row * 8 + col);

	}

	public static void findDiagonals(Piece p,int row, int col) {

		int[] offsets = { -1, 1 };
	
		for (int i : offsets) {
			for (int j : offsets) {
				int r = row + i;
				int c = col + j;
				while (inBounds(r, c) && !GameState.isOccupied(r,c)){				
					addValidMove(p,r, c);
					GameState.setControlled(p.color, r,c);
					r += i;
					c += j;
				}
				if(inBounds(r,c)) {
					if(Board.board[r][c].color==p.color) {
						GameState.setControlled(p.color,r, c);
					}
					else {
						addValidMove(p,r,c);
					}
				}
			}
		}
	}

	public static void findRookMoves(Piece p,int row, int col) {
		
		for(int i=0;i<4;i++) {
			int offsetX=(i-1)%2;    // -1 0 1 0
			int offsetY= (3-i-1)%2; // 0 1 0 -1
			int r = row + offsetY;
			int c = col + offsetX;
			while (inBounds(r, c) && !GameState.isOccupied(r,c)) {
				addValidMove(p,r, c);
				r += offsetY;
				c += offsetX;
			}
			if(inBounds(r,c)) {
				if(Board.board[r][c].color==p.color) {
					GameState.setControlled(p.color,r, c);
				}
				else {
					addValidMove(p,r,c);
				}
			}	
		}
	}

	public static void findKnightMoves(Piece p,int row, int col) {

		int[] offset1 = { 1, -1 }; 
		int[] offset2 = { 2, -2 };

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				int r = row + offset1[i];
				int c = col + offset2[j];
				if (inBounds(r, c) && (!GameState.isOccupied(r,c) || canCapture(p.color,Board.board[r][c]))) {
					addValidMove(p,r,c);
				}
				r = row + offset2[j];
				c = col + offset1[i];
				if (inBounds(r, c) && (!GameState.isOccupied(r,c) || canCapture(p.color,Board.board[r][c]))) {
					addValidMove(p,r, c);
				}
			}
		}
	}
	
	public static long getKnightMoves(int row, int col,Colour color) {
		
		long validMoves=0;

		int[] offset1 = { 1, -1 }; 
		int[] offset2 = { 2, -2 };

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				int r = row + offset1[i];
				int c = col + offset2[j];
				if (inBounds(r, c) && (!GameState.isOccupied(r,c) || canCapture(color,Board.board[r][c]))) {
					validMoves |= 1L << (row * 8 + col);
				}
				r = row + offset2[j];
				c = col + offset1[i];
				if (inBounds(r, c) && (!GameState.isOccupied(r,c) || canCapture(color,Board.board[r][c]))) {
					validMoves |= 1L << (row * 8 + col);
				}
			}
		}
		
		return validMoves;
	}
	
	/**
	 *  Returns squares that a bishop can move to, including the piece that obstructs it. 
	 * @param p
	 * @param row
	 * @param col
	 * @return
	 */
	public static long bishop_moves(int row, int col) {
		
		long bishop_moves = 0;

		int[] offsets = { -1, 1 };
		for (int i : offsets) {
			for (int j : offsets) {
				int r = row + i;
				int c = col + j;
				while (inBounds(r, c) && !GameState.isOccupied(r,c)){				
					bishop_moves |= 1L << (r * 8 + c);
					r += i;
					c += j;
				}
				if(inBounds(r,c)) {		
						bishop_moves |= 1L << (r * 8 + c);
				}
			}
		}
		
		return bishop_moves;
	}

	// Returns squares that a rook can move to, including the piece that obstructs it. 
	public static long rook_moves(int row, int col) {
		
		long rook_moves = 0;

		for (int i = 0; i < 4; i++) {
			int offsetX = (i - 1) % 2; // -1 0 1 0
			int offsetY = (3 - i - 1) % 2; // 0 1 0 -1
			int r = row + offsetY;
			int c = col + offsetX;
			while (inBounds(r, c) /* && !GameState.isOccupied(r, c) */) {
				rook_moves |= 1L << (r * 8 + c);
				r += offsetY;
				c += offsetX;
			}
			if (inBounds(r, c)) {	
				rook_moves |= 1L << (r * 8 + c);
			}
		}
		return rook_moves;
	}
	
	
	// Returns squares that are a knight's distance from given position
	public static long knight_moves(int row, int col) {
		
		long knight_moves=0;

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
	/**
	 * 
	 * @param moves
	 * @param color - color of the piece moving
	 * @return
	 */
	public static long filterLegalMoves(long moves,Colour color) { // filter moves to find the legal moves for a piece 
		
		long piecePositions = color == Colour.BLACK ? GameState.whitePositions : GameState.blackPositions;
		return moves &= ~piecePositions;

	}
	
	/**
	 * 
	 * @param moves
	 * @param color - color of the piece moving
	 * @return
	 */
	public static long filterPseudoLegalMoves(long moves,Colour color) { // filter moves to find the legal moves for a piece 
		
		long piecePositions = color == Colour.BLACK ? GameState.whitePositions : GameState.blackPositions;
		return moves &= ~piecePositions;

	}
	
	// White and black pawns can only double move on the 2nd and 7th rank
	// respectively.
	private static boolean canDoubleMove(Colour color, int row) {

		return color == Colour.WHITE ? (row == 1) : (row == 6);
	}

	public static void findPawnMove(Piece p,int row, int col) {
		
		int incr=1;
		if(p.color==Colour.BLACK) {
			incr*=-1;
		}
		
		if(inBounds(row+incr,col+1)) {
			GameState.setControlled(p.color,row+incr, col+1);
			if(GameState.isOccupied(row+incr, col+1) && Board.board[row+incr][col+1].color!=p.color) {
				addValidMove(p,row+incr,col+1);
				
			}
		}
		if(inBounds(row+incr,col-1)) {
			GameState.setControlled(p.color,row+incr, col-1);
			if(GameState.isOccupied(row+incr, col-1) && Board.board[row+incr][col-1].color!=p.color) {
				addValidMove(p,row+incr,col-1);
				
			}
		}
		if (inBounds(row + incr, col) && !GameState.isOccupied(row + incr, col)) {
			addValidMove(p,row + incr, col);
			if (canDoubleMove(p.color,row) && !GameState.isOccupied(row + incr*2, col)) {
				addValidMove(p,row + incr*2, col);
				
			} 
		}
	}
	
	public static long pawn_moves(int row, int col, Colour color) {
		
		long pawn_moves =0;
		int incr = (color == Colour.WHITE) ? 1 : -1;
		
		if(inBounds(row+incr,col+1)) {
				pawn_moves |= 1L << ((row+incr) * 8 + col+1);
		}
		if(inBounds(row+incr,col-1)) {
				pawn_moves |= 1L << ((row+incr) * 8 + col-1);
		}
		if (inBounds(row + incr, col) && !GameState.isOccupied(row + incr, col)) {
			pawn_moves |= 1L << ((row+incr) * 8 + col);
			if (canDoubleMove(color,row)){
				pawn_moves |= 1L << ((row+incr*2) * 8 + col);
			} 
		}
		
		return pawn_moves;
	}
	
	
	public static boolean isInCheck(Piece king) {
		
		return isControlled(king.color.opposite(),king.row,king.col);
	}
	
	
	// Evade check by either capturing attacking piece
	// blocking the attacker if possible
	// moving king to safety
	public static void evadeCheck(Piece king) {
		
		long attackers = 0;
		
		attackers |= MoveLogic.knight_moves(0, 0) & GameState.getPiecePosition(Type.KNIGHT, Colour.BLACK);
		
		if(attackers > 0) { //king is in check
			
			findKingMoves(king,king.row,king.col); // move king out of check
			
			
		}
		
		// first get list of squares to check, ex. knight/bishop distance away squares
		
		
		// calculate all the controlled squares of opponent
		//find kings moves
		
		//attackers is bitboard of the pieces that can be captured
		//get rays to see which squares can be blocked. 
		
		
		
	}
	
	private static void getRaysToKing(Piece checker,Piece king) {
		
		long ray =0;
		
		if(checker.type==Type.QUEEN || checker.type== Type.ROOK) {
				
			int deltaCol = king.col-checker.col;
			int deltaRow = king.row-checker.row;
		
			for(int i=checker.row,j=checker.col;i!=king.row && j!=king.col;i+=deltaCol/deltaCol,j+=deltaRow/deltaRow) {
				
				ray |= 1L << (i*8+j);
			}
			
		}
	}
	
	private static boolean detectChecks(Piece king) {
		
		long attackers = 0;
		Type[] types = Type.values();
		for(int i=0;i<types.length;i++) {
			attackers |= GameState.getPiecePosition(types[i], king.color.opposite()) & getKnightMoves(king.row,king.col,king.color);
		}
		
		return attackers!=0;
		
	}
	
	
	// Assumes controlled squares
	public static void findKingMoves(Piece p,int row, int col) {
	
		
		for(int i=-1;i<2;i++) {
			for(int j=-1;j<2;j++) {
				if(inBounds(row+i,col+j) && !GameState.isOccupied(row+i,col+j) && !isControlled(p.color.opposite(),row+i,col+j)) {
					addValidMove(p,row+i,col+j);
					
				}
			}
		}

	}
	
	public static long king_moves(int row, int col) {
		
		long king_moves = 0;
		
		for(int i=-1;i<2;i++) {
			for(int j=-1;j<2;j++) {
				if(inBounds(row+i,col+j) && !GameState.isOccupied(row+i,col+j)) {
					
					king_moves |= 1L << ((row+i) * 8 + col+j);
				}
			}
		}
		
		return king_moves;
	}

}
