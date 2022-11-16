package com.gdx.chess;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;


enum Type{
	
	PAWN,BISHOP,KNIGHT,ROOK,QUEEN,KING;
}

enum Colour{
	
	WHITE,BLACK;
	
	private static final Colour[] VALUES = values();
	
	public Colour opposite() {
		return VALUES[this.ordinal()^1];
	}
}

public class Piece extends Actor{
	
	private static Texture[][] textures= new Texture[2][6];
	private static Texture circle;
	private static Texture hollowCircle;
	private Texture currentTexture; 
	private final int HEIGHT=50;
	private final int WIDTH=50;
	
	
	Type type = Type.BISHOP;

	public boolean isTouching = false;
	
	public int squareIndex = 0;
	public Colour color;
	
	public long legalMoves = 0;
	public long pseudoLegalMoves =0;
	public long attackedSquares =0;
	public List<Integer> validList = new ArrayList<Integer>();
	
	
	static {
		
		mapTextures();
	}
	
	
	public Piece(int squareIndex,Type type,Colour color) {
		
		currentTexture = textures[color.ordinal()][type.ordinal()];

		this.type = type;
		this.color = color;
		this.squareIndex = squareIndex;

		this.setX(100 + WIDTH * (squareIndex % 8));
		this.setY(20 + HEIGHT * (squareIndex / 8));
		this.setHeight(HEIGHT);
		this.setWidth(WIDTH);
		this.addListener(new PieceMovementListener(this));

	}
	
	public long generateLegalMoves() {
		
		generateAttackedSquares();

		pseudoLegalMoves = MoveLogic.filterPseudoLegalMoves(attackedSquares, color);

		if (type == Type.PAWN) {
			pseudoLegalMoves &= GameState.occupied;
			pseudoLegalMoves |= MoveLogic.single_pawn_push(squareIndex, color);
			pseudoLegalMoves |= MoveLogic.double_pawn_push(squareIndex, color);
		}

		legalMoves = MoveLogic.filterLegalMoves(type, color, pseudoLegalMoves);
		
		return legalMoves;
		
	}
	
	public long generateAttackedSquares(){

		switch (type) {
			case PAWN:
				attackedSquares = MoveLogic.pawnAttacks[color.ordinal()][squareIndex];
				break;
			case BISHOP:
				attackedSquares = MoveLogic.bishop_moves(squareIndex,GameState.occupied);
				break;
			case KNIGHT:
				attackedSquares = MoveLogic.knightAttacks[squareIndex];
				break;
			case ROOK:
				attackedSquares = MoveLogic.rook_moves(squareIndex,GameState.occupied);
				break;
			case QUEEN:
				attackedSquares = MoveLogic.rook_moves(squareIndex,GameState.occupied);
				attackedSquares |= MoveLogic.bishop_moves(squareIndex,GameState.occupied);
				break;
			case KING:
				attackedSquares = MoveLogic.kingAttacks[squareIndex];
				break;
			default:

		}
		return attackedSquares;
	}
	
	@Override
	public void draw(Batch batch,float parentAlpha) {
		
		batch.draw(currentTexture,this.getX(),this.getY(), WIDTH, HEIGHT);
		if(this.isTouching) {
			drawLegalMoves(batch,parentAlpha);
		}
		
	}
	
	public void drawLegalMoves(Batch batch, float parentAlpha) {
		
		for(int i =0 ;i < validList.size();i++) {
			int num = validList.get(i);
			if(Board.board[num]!= null) {
				batch.draw(hollowCircle,100+WIDTH*(num%8),20+HEIGHT*(num/8),50,50);
				continue;
			}
			batch.draw(circle,115+WIDTH*(num%8),35+HEIGHT*(num/8),20,20);
		}
	}
	
	
	public boolean canMoveTo(int square) {
		
		return (legalMoves & MoveLogic.squareToBB.get(square))!=0;
	}
	
	public void moveTo(int square) {

		int row = square / 8;
		int col = square % 8;
		this.addAction(Actions.moveTo(100 + this.getWidth() * col, 20 + this.getHeight() * row, 0.5f));
		if (Board.board[square] != null) {
			Board.board[square].remove();
		}
	}
	
	private static void mapTextures() {
		
		circle	= Chess.am.manager.get("circle.png",Texture.class);
		hollowCircle = Chess.am.manager.get("hollow_circle.png",Texture.class);
		
		for(int i =0;i<textures.length;i++) {
	
			String color = (i==Colour.BLACK.ordinal()) ? "d" : "l";
			
			textures[i][Type.PAWN.ordinal()]	= Chess.am.manager.get(color+"_pawn.png",Texture.class);
			textures[i][Type.BISHOP.ordinal()]	= Chess.am.manager.get(color+"_bishop.png",Texture.class);
			textures[i][Type.KNIGHT.ordinal()]	= Chess.am.manager.get(color+"_knight.png",Texture.class);
			textures[i][Type.ROOK.ordinal()]	= Chess.am.manager.get(color+"_rook.png",Texture.class);
			textures[i][Type.QUEEN.ordinal()]	= Chess.am.manager.get(color+"_queen.png",Texture.class);
			textures[i][Type.KING.ordinal()]	= Chess.am.manager.get(color+"_king.png",Texture.class);
			
		}
	}
}
