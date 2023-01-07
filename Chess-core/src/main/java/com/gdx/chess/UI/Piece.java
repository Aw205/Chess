package com.gdx.chess.UI;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.gdx.chess.Chess;
import com.gdx.chess.Colour;
import com.gdx.chess.Type;

public class Piece extends Actor {
	
	private static Texture[][] textures= new Texture[2][6];
	private static Texture circle;
	private static Texture hollowCircle;
	private Texture currentTexture; 
	private final int HEIGHT=50;
	private final int WIDTH=50;
	public boolean isTouching = false;
	public int square = 0;
	public Type type = Type.BISHOP;
	public Colour color;
	
	public List<Integer> validList = new ArrayList<Integer>();
	
	static {
		
		mapTextures();
	}
	
	
	public Piece(int square,Colour color,Type type) {
		
		
		currentTexture = textures[color.ordinal()][type.ordinal()];

		this.type = type;
		this.color = color;
		this.square = square;

		this.setX(100 + WIDTH * (square % 8));
		this.setY(20 + HEIGHT * (square / 8));
		this.setHeight(HEIGHT);
		this.setWidth(WIDTH);
		this.addListener(new PieceMovementListener(this));
		
	}
	
	@Override
	public void draw(Batch batch,float parentAlpha) {
		
		batch.draw(currentTexture,this.getX(),this.getY(), WIDTH, HEIGHT);
		if(this.isTouching) {
			drawLegalMoves(batch,parentAlpha);
		}
		
	}
	
	private void drawLegalMoves(Batch batch, float parentAlpha) {
		
		for(int num: validList) {
			if(TileBoard.board[num]!= null) {
				batch.draw(hollowCircle,100+WIDTH*(num%8),20+HEIGHT*(num/8),50,50);
				continue;
			}
			batch.draw(circle,115+WIDTH*(num%8),35+HEIGHT*(num/8),20,20);
		}
	}
	
	public void moveTo(int square) {

		int row = square / 8;
		int col = square % 8;
		this.addAction(Actions.moveTo(100 + this.getWidth() * col, 20 + this.getHeight() * row, 0.5f));
	}

	private static void mapTextures() {

		circle = Chess.am.manager.get("circle.png", Texture.class);
		hollowCircle = Chess.am.manager.get("hollow_circle.png", Texture.class);

		for (int i = 0; i < textures.length; i++) {

			String color = (i == Colour.BLACK.ordinal()) ? "d" : "l";

			textures[i][Type.PAWN.ordinal()] = Chess.am.manager.get(color + "_pawn.png", Texture.class);
			textures[i][Type.BISHOP.ordinal()] = Chess.am.manager.get(color + "_bishop.png", Texture.class);
			textures[i][Type.KNIGHT.ordinal()] = Chess.am.manager.get(color + "_knight.png", Texture.class);
			textures[i][Type.ROOK.ordinal()] = Chess.am.manager.get(color + "_rook.png", Texture.class);
			textures[i][Type.QUEEN.ordinal()] = Chess.am.manager.get(color + "_queen.png", Texture.class);
			textures[i][Type.KING.ordinal()] = Chess.am.manager.get(color + "_king.png", Texture.class);

		}
	}
	
	public void setTexture(Type t) {
		this.currentTexture = textures[color.ordinal()][t.ordinal()];
	}

}
