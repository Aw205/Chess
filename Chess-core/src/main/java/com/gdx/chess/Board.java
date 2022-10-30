package com.gdx.chess;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Board extends Stage {
	
	
	public static final int BOARD_WIDTH=8;
	public static final int BOARD_HEIGHT=8;
	
	static Piece[] board = new Piece[64];
	static Image[][] tiles = new Image[8][8];
	
	static List<Piece> blackPieces = new ArrayList<Piece>();
	static List<Piece> whitePieces = new ArrayList<Piece>();
	
	static Piece wKing;
	static Piece bKing;

	
	public Board() {

		generateBoard();		
	}
	
	
	public void generateBoard() {
		
		parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
		
		for(int i = 0; i< 64;i++) {
			if(board[i]!= null) {
				this.addActor(board[i]);
			}
		}
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
			List<Piece> list = (color==Colour.BLACK) ? blackPieces : whitePieces;
			
			switch(Character.toLowerCase(c)) {
				case 'p':
					board[idx]= new Piece(idx,Type.PAWN,color);
					list.add(board[idx]);
					break;
				case 'b':
					board[idx]= new Piece(idx,Type.BISHOP,color);
					list.add(board[idx]);
					break;
				
				case 'n':
					board[idx]= new Piece(idx,Type.KNIGHT,color);
					list.add(board[idx]);
					break;
				
				case 'r':
					board[idx]= new Piece(idx,Type.ROOK,color);
					list.add(board[idx]);
					break;
				case 'q':
					board[idx]= new Piece(idx,Type.QUEEN,color);
					list.add(board[idx]);
					break;
				case 'k':
					board[idx]= new Piece(idx,Type.KING,color);
					list.add(board[idx]);
					if(color==Colour.WHITE) {
						wKing=board[idx];	
					}
					else {
						bKing=board[idx];	
					}
					break;
				default :
					System.err.println("Invalid FEN");
				}
			
			idx++;
		}
	}
}
