package com.gdx.chess;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Tile extends Image{
	
	
	private static List<Tile> highlightedTiles = new ArrayList<Tile>();
	
	Color currentColor=Color.CLEAR;
	
	public Tile(Texture texture) {
		
		super(texture);
		

		this.addListener(new ClickListener(Buttons.RIGHT) {		
			@Override
			public void clicked(InputEvent event, float x, float y) {			
				toggleHighlight(Color.RED);
			}			
		});	
		
		this.addListener(new ClickListener() {		
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				
				Tile.this.setColor(Color.YELLOW);
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				toggleHighlight(Color.YELLOW);
				
			}
			
			@Override
			public void clicked(InputEvent event, float x, float y) {	
				clearHighlights();
				toggleHighlight(Color.YELLOW);
				
			}			
		});	
	}
	
	private void clearHighlights() {
		
		for(int i=0;i<highlightedTiles.size();i++) {
			highlightedTiles.get(i).setColor(Color.CLEAR);
		}
		highlightedTiles.clear();
	}
	
	public void toggleHighlight(Color color) {
		if(currentColor==color) {
			this.setColor(Color.CLEAR);
			currentColor=Color.CLEAR;
		}
		else {
			highlightedTiles.add(this);
			this.setColor(color);
			currentColor=color;
		}
	}
	

}
