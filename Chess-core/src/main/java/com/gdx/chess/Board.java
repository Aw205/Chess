package com.gdx.chess;

import java.util.List;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Board extends Stage {
	
	
	public static final int BOARD_WIDTH=8;
	public static final int BOARD_HEIGHT=8;
	
	static Piece[] board = new Piece[64];

	
	public Board() {

		generateBoard();		
	}
	
	
	public void generateBoard() {
		
		//parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
		parseFEN("rnbqk2r/pppp1ppp/5n2/4p3/1b2P3/2NP4/PPP3PP/R1BQKBNR"); // test pins
		
		for(int i = 0; i< 64;i++) {
			if(board[i]!= null) {
				this.addActor(board[i]);
			}
		}
		MoveLogic.precompute();
		GameState.init();
		
	}
	
	// starting fen: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
	// could redo with regex maybe?
	public void parseFEN(String fen) {
		
		int idx = 56;
		
		for(int i=0;i<fen.length();i++) {
			char c = fen.charAt(i);
			if(c=='/') {
				idx-=16;
				continue;
			}
			if(Character.isDigit(c)) {
				idx+= Character.getNumericValue(c);
				continue;
			}	
			Colour color = (Character.isLowerCase(c)) ? Colour.BLACK : Colour.WHITE;
			List<Piece> list = (color==Colour.BLACK) ? GameState.blackPieces : GameState.whitePieces;
			Piece p = null;
			
			switch(Character.toLowerCase(c)) {
				case 'p':
					 p = new Piece(idx,Type.PAWN,color);
					break;
				case 'b':
					p = new Piece(idx,Type.BISHOP,color);
					break;
				case 'n':
					p = new Piece(idx,Type.KNIGHT,color);
					break;		
				case 'r':
					p = new Piece(idx,Type.ROOK,color);
					break;
				case 'q':
					p = new Piece(idx,Type.QUEEN,color);
					break;
				case 'k':
					p = new Piece(idx,Type.KING,color);
					break;
				default :
					System.err.println("Invalid FEN");
				}
			board[idx]= p;
			list.add(p);
			idx++;
		}
	}
}
