package com.nicocharm.biodots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;

public class Grid {
    public Array<Block> getBlocks() {
        return blocks;
    }

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

    public int getActiveBlocks() {
        return activeBlocks;
    }

    private int activeBlocks;

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

        activeBlocks = 0;
    }

    public void update(float delta){
        for(Block block: blocks){
            block.update(delta);
        }
    }

    public void render(SpriteBatch batch){
        for(Block block: blocks){
            block.render(batch);

        }
    }

    public void activateBlock(int screenX, int screenY) {
        Vector3 v = new Vector3(screenX, screenY, 0);
        Vector3 v2 = screen.cam.unproject(v);
        x = v2.x;
        y = v2.y;

        for(Block block: blocks){
            if(!block.isActive() && block.isTouched(x, y)){
                activeBlocks++;
                block.activate();
            }
        }
    }

    public void reduceActiveBlocks(){
        activeBlocks--;
    }

    public void dispose(){
        for(Block block: blocks){
            block.dispose();
        }
    }

}
