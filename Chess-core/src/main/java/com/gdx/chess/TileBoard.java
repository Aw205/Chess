package com.gdx.chess;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class TileBoard extends Stage {
	
	private final int TILE_LENGTH=50;
	static Image[][] tiles = new Image[8][8];

	public TileBoard() {
		
		createTileBackground();

	}

	private void createTileBackground() {

		Group group = new Group();
		Pixmap pm = new Pixmap(TILE_LENGTH, TILE_LENGTH, Format.RGB888);

		pm.setColor(0.96f, 0.87f, 0.70f, 1f);
		pm.fillRectangle(0, 0, TILE_LENGTH, TILE_LENGTH);
		Texture tan = new Texture(pm);

		pm.setColor(0.76f, 0.60f, 0.42f, 1f);
		pm.fillRectangle(0, 0, TILE_LENGTH, TILE_LENGTH);
		Texture brown = new Texture(pm);

		pm.dispose();

		for (int i = 0; i < Board.BOARD_WIDTH; i++) {
			for (int j = 0; j < Board.BOARD_HEIGHT; j++) {

				Tile img = ((i + j) % 2 == 0) ? new Tile(brown) : new Tile(tan);
				img.setPosition(100 + TILE_LENGTH * j, 20 + TILE_LENGTH * i);
				group.addActor(img);
				tiles[i][j] = img;
			}
		}
		this.addActor(group);
	}

}
