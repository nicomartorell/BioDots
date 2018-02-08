package com.nicocharm.biodots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;
import com.nicocharm.biodots.screens.TutorialScreen;

public class Grid {
    public Array<Block> getBlocks() {
        return blocks;
    }

    private Array<Block> blocks;
    private PlayScreen screen;
    private float x;

    public float getY() {
        return y;
    }

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

    public String activePath = "box-active.png";
    public String path = "box.png";

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
        for(Block block: blocks){ //20
            block.update(delta);
        }
    }

    public void render(SpriteBatch batch){
        for(Block block: blocks){
            block.render(batch);

        }
    }

    public void activateBlock(float x, float y) {
        for(final Block block: blocks){
            if(!block.isActive() && block.isTouched(x, y)){
                final Music s = (Music) screen.game.manager.get("frozen.wav", Music.class);
                s.play();
                activeBlocks++;

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(!s.isPlaying() || s.getPosition()<0.03f){
                        }
                        block.activate();
                        screen.getInfobar().updatePoints(-20);
                    }
                });
                t.start();
            }
        }

        if(screen.isTutorial()){
            TutorialScreen ts = (TutorialScreen)screen;
            if(ts.getState() == ts.STATE_SHORTPRESS  && !ts.isToAdvance()){
                ts.setToAdvance(3);
            }
        }
    }

    public void reduceActiveBlocks(){
        activeBlocks--;
    }



}
