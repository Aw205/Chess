package com.gdx.chess;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Board extends Stage {
	
	
	public static final int BOARD_WIDTH=8;
	public static final int BOARD_HEIGHT=8;
	
	static Piece[][] board = new Piece[8][8];
	static Image[][] tiles = new Image[8][8];
	
	static List<Piece> blackPieces = new ArrayList<Piece>();
	static List<Piece> whitePieces = new ArrayList<Piece>();
	
	static Piece king;

	
	public Board() {

		generateBoard();		
		
	}
	
	
	public void generateBoard() {
		
		parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");		
		
		for(int i=0;i<BOARD_WIDTH;i++) {
			for(int j=0;j<BOARD_HEIGHT;j++) {
				if(board[i][j]!=null)
				{
					this.addActor(board[i][j]);
				}
			}
		}
		generateValidMoves();
	}
	
	public static void generateValidMoves() {
		
		if(MoveLogic.isInCheck(king)) {
			System.out.println("here in check!");
			return;
		}
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (Board.board[i][j] != null) {
					Board.board[i][j].getValidMoves();
				}
			}
		}
	}
	
	
	// starting fen: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
	// could redo with regex maybe?
	public void parseFEN(String fen) {
		
		int row=7;
		int col=0;
		
		for(int i=0;i<fen.length();i++) {
			
			char c = fen.charAt(i);
			if(c=='/') {
				row--;
				col=0;
				continue;
			}
			if(Character.isDigit(c)) {
				col+= Character.getNumericValue(c)-1;
				continue;
			}	
			Colour color = (Character.isLowerCase(c)) ? Colour.BLACK : Colour.WHITE;
			List<Piece> list = (color==Colour.BLACK) ? blackPieces : whitePieces;
			
			switch(Character.toLowerCase(c)) {
			
				case 'p':
					board[row][col]= new Piece(row,col,Type.PAWN,color);
					list.add(board[row][col]);
					break;
				case 'b':
					board[row][col]= new Piece(row,col,Type.BISHOP,color);
					list.add(board[row][col]);
					break;
				
				case 'n':
					board[row][col]= new Piece(row,col,Type.KNIGHT,color);
					list.add(board[row][col]);
					break;
				
				case 'r':
					board[row][col]= new Piece(row,col,Type.ROOK,color);
					list.add(board[row][col]);
					break;
				case 'q':
					board[row][col]= new Piece(row,col,Type.QUEEN,color);
					list.add(board[row][col]);
					break;
				case 'k':
					board[row][col]= new Piece(row,col,Type.KING,color);
					list.add(board[row][col]);
					if(color==Colour.WHITE) {
						king=board[row][col];	
					}
					break;
				default :
					System.err.println("Invalid FEN");
				}
				GameState.setOccupied(row, col);
			col++;
		}
		
	}
}
