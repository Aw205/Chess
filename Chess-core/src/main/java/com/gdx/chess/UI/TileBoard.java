package com.gdx.chess.UI;

import java.util.List;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.gdx.chess.Chess;
import com.gdx.chess.Colour;
import com.gdx.chess.GameState;
import com.gdx.chess.Move;
import com.gdx.chess.MoveLogic;
import com.gdx.chess.Piece;
import com.gdx.chess.Type;
import com.gdx.chess.mType;

public class TileBoard extends Stage {
	
	private final int TILE_LENGTH=50;
	static Image[][] tiles = new Image[8][8];
    static com.gdx.chess.UI.Piece[] board = new com.gdx.chess.UI.Piece[64];

	public TileBoard() {
		
		createTiles();
		generateBoard();

	}
	
	
	public static void update(Move m,boolean isComputerMove) {
		
		int from = MoveLogic.BBtoSquare.get(m.from);
		int to = MoveLogic.BBtoSquare.get(m.to);
		
		if(m.type == mType.CASTLE) {

			Move kingMove = new Move(m.from,m.to,mType.QUIET);
			update(kingMove,isComputerMove);
			Move rookMove = (m.to > m.from) ? new Move(m.from << 3,m.from << 1,mType.QUIET) : new Move(m.from >> 4,m.from >> 1,mType.QUIET);
			update(rookMove,true);
			return;
		}
		else if(m.type == mType.QUEEN_PROMO || m.type == mType.QUEEN_PROMO_CAP) {
			board[from].setTexture(Type.QUEEN);
		}
		
		if(isComputerMove) {
			board[from].moveTo(to);
			//sound.play();
		}
		
		if (board[to] != null) {
			TileBoard.board[to].remove();
			Chess.am.manager.get("capture.ogg",Sound.class).play();
		}
		board[from].square = to;
		board[to] = board[from];
		board[from] = null;
		
		if(isComputerMove) {
			updateLegalMoves();
		}
	}
	
	private static void updateLegalMoves() {
		
		for(com.gdx.chess.UI.Piece p : board) {
			if(p!=null) {
				p.validList.clear();
			}
		}
		
		for(Move m : GameState.moves) {
			
			int from = MoveLogic.BBtoSquare.get(m.from);
			int to = MoveLogic.BBtoSquare.get(m.to);
			
			TileBoard.board[from].validList.add(to);
		}
		
	}

	private void createTiles() {

		Group group = new Group();
		Pixmap pm = new Pixmap(TILE_LENGTH, TILE_LENGTH, Format.RGB888);

		pm.setColor(0.96f, 0.87f, 0.70f, 1f);
		pm.fillRectangle(0, 0, TILE_LENGTH, TILE_LENGTH);
		Texture tan = new Texture(pm);

		pm.setColor(0.76f, 0.60f, 0.42f, 1f);
		pm.fillRectangle(0, 0, TILE_LENGTH, TILE_LENGTH);
		Texture brown = new Texture(pm);

		pm.dispose();

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Tile img = ((i + j) % 2 == 0) ? new Tile(brown) : new Tile(tan);
				img.setPosition(100 + TILE_LENGTH * j, 20 + TILE_LENGTH * i);
				group.addActor(img);
				tiles[i][j] = img;
			}
		}
		this.addActor(group);
	}

	public void generateBoard() {

		  parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR KQkq");
		//parseFEN("rnbqk2r/pppp1ppp/5n2/4p3/1b2P3/2NP4/PPP3PP/R1BQKBNR KQkq"); // test pins
		
		for (int i = 0; i < 64; i++) {
			if (board[i] != null) {
				this.addActor(board[i]);
			}
		}
		MoveLogic.precompute();
		GameState.init();
		updateLegalMoves();

	}

	// starting fen: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
	// could redo with regex maybe?
	public void parseFEN(String fen) {

		int idx = 56;
		int i = 0;

		for (i = 0; i < fen.length() && fen.charAt(i) != ' '; i++) {
			char c = fen.charAt(i);
			if (c == '/') {
				idx -= 16;
				continue;
			}
			if (Character.isDigit(c)) {
				idx += Character.getNumericValue(c);
				continue;
			}
			Colour color = (Character.isLowerCase(c)) ? Colour.BLACK : Colour.WHITE;
			List<Piece> list = (color == Colour.BLACK) ? GameState.blackPieces : GameState.whitePieces;
			Type type = null;
			Piece p = null;

			switch (Character.toLowerCase(c)) {
			case 'p':
				type = Type.PAWN;
				p = new Piece(idx, Type.PAWN, color);
				break;
			case 'b':
				type = Type.BISHOP;
				p = new Piece(idx, Type.BISHOP, color);
				break;
			case 'n':
				type = Type.KNIGHT;
				p = new Piece(idx, Type.KNIGHT, color);
				break;
			case 'r':
				type = Type.ROOK;
				p = new Piece(idx, Type.ROOK, color);
				break;
			case 'q':
				type = Type.QUEEN;
				p = new Piece(idx, Type.QUEEN, color);
				break;
			case 'k':
				type = Type.KING;
				p = new Piece(idx, Type.KING, color);
				break;
			default:
				System.err.println("Invalid FEN");
			}
			com.gdx.chess.UI.Piece piece_ui = new com.gdx.chess.UI.Piece(idx, color,type);
			board[idx] = piece_ui;
			GameState.board[idx] = p;
			list.add(p);
			idx++;
		}
		i++;
		if (fen.charAt(i) != '-') {
			while (i < fen.length() && fen.charAt(i) != ' ') {
				char c = fen.charAt(i);
				if (c == 'K') {
					GameState.castlingRights[Colour.WHITE.ordinal()][0] = 1;
				} else if (c == 'k') {
					GameState.castlingRights[Colour.BLACK.ordinal()][0] = 1;
				} else if (c == 'Q') {
					GameState.castlingRights[Colour.WHITE.ordinal()][1] = 1;
				} else if (c == 'q') {
					GameState.castlingRights[Colour.BLACK.ordinal()][1] = 1;
				}
				i++;

			}
		}

	}

}
