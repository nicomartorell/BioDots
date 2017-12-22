package com.nicocharm.biodots;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;

public class Grid {
    private Array<Block> blocks;
    private PlayScreen screen;
    private float x;
    private float y;

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    private float width;
    private float height;
    private int columns;
    private int rows;

    public Grid(PlayScreen screen, float x, float y, float width, int columns, int rows){
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.width = width;
        this.columns = columns;
        this.rows = rows;

        blocks = new Array<Block>();

        float blockWidth = width / (float)columns;

        height = (float)rows * blockWidth;

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                blocks.add(new Block(screen, this, x + j * blockWidth, y + i * blockWidth, blockWidth));
            }
        }

    }

    public void render(SpriteBatch batch){
        for(Block block: blocks){
            block.render(batch);

        }
    }
}
