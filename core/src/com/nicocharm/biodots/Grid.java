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
                final long time0 = System.nanoTime();

                //final Music s = (Music) screen.game.manager.get("frozen.ogg", Music.class);
                Sound s = (Sound) screen.game.manager.get("frozen.ogg", Sound.class);
                s.play(0.3f);

                activeBlocks++;

                block.activate();
                screen.getInfobar().updatePoints(-20);
                /*long time1 = System.nanoTime();

                s.play();

                long time2 = System.nanoTime();*/


                /*screen.game.manager.playSound(s, 0.002f, new Runnable(){

                    @Override
                    public void run() {
                        block.activate();
                        screen.getInfobar().updatePoints(-20);
                    }
                });*/

                /*Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long time3 = 0;
                        boolean entered = false;
                        while(!s.isPlaying() || s.getPosition()<0.0002f){
                            if(!entered && s.getPosition()>0.009f){
                                time3 = System.nanoTime();
                                entered = true;
                            }
                        }
                        long time4 = System.nanoTime();
                        block.activate();
                        screen.getInfobar().updatePoints(-20);

                        long delta1 = (time3 - time0)/1000000;
                        long delta2 = (time4 - time0)/1000000;

                        Gdx.app.log("tag", "Been playing for 0.1ms: " + delta1);
                        Gdx.app.log("tag", "Out of loop, played 30ms: " + delta2);
                    }
                });
                t.start();

                long delta1 = (time1 - time0)/1000000;
                long delta2 = (time2 - time0)/1000000;

                Gdx.app.log("tag", "Got the file: " + delta1);
                Gdx.app.log("tag", "Set to play: " + delta2);*/
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
