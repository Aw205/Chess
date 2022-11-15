package com.gdx.chess;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;


public class AssetsManager {
	
	public AssetManager manager = new AssetManager();
	
	public AssetsManager() {
	
		loadTextures();
	}
	
	private void loadTextures() {
		
		manager.load("circle.png",Texture.class);
		manager.load("hollow_circle.png",Texture.class);
			
		manager.load("d_bishop.png", Texture.class);
		manager.load("d_king.png", Texture.class);
		manager.load("d_knight.png", Texture.class);
		manager.load("d_pawn.png", Texture.class);
		manager.load("d_bishop.png", Texture.class);
		manager.load("d_queen.png", Texture.class);
		manager.load("d_rook.png", Texture.class);
		
		manager.load("l_bishop.png", Texture.class);
		manager.load("l_king.png", Texture.class);
		manager.load("l_knight.png", Texture.class);
		manager.load("l_pawn.png", Texture.class);
		manager.load("l_bishop.png", Texture.class);
		manager.load("l_queen.png", Texture.class);
		manager.load("l_rook.png", Texture.class);
		
	}

}
