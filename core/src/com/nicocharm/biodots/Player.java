package com.nicocharm.biodots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;


public class Player implements InputProcessor{

    com.nicocharm.biodots.screens.PlayScreen screen;


    private float x;
    private float y;

    private long firstTouch;
    private long secondTouch;

    public Player(com.nicocharm.biodots.screens.PlayScreen screen){
        this.screen = screen;
        x = 0;
        y = 0;

        firstTouch = 0;
        secondTouch = 0;
    }

    public void applyAntibiotic(int screenX, int screenY){
        Vector3 v = new Vector3(screenX, screenY, 0);
        Vector3 v2 = screen.cam.unproject(v);
        x = v2.x;
        y = v2.y;
        Antibiotic antibiotic = new Antibiotic(screen, x, y, Antibiotic.ANTIBIOTIC_RED);
        screen.getAntibiotics().add(antibiotic);

        //usar antibiotico cuesta puntos!
        screen.getInfobar().updatePoints(-30);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        firstTouch = System.nanoTime();
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        //empezá un nuevo juego si ya terminaste!
        if(screen.finished()){
            PlayScreen newScreen = new PlayScreen(screen.game);
            screen.game.setScreen(newScreen);
            screen.dispose();
            return true;
        }

        secondTouch = System.nanoTime();

        double delta = ((double)(secondTouch - firstTouch))/1000000000.0;

        if(delta >= 0.3){
            if(screen.getAntibiotics().size<1){
                applyAntibiotic(screenX, screenY);
            }
        } else if(screen.getGrid().getActiveBlocks()<3){
            screen.getGrid().activateBlock(screenX, screenY);
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
