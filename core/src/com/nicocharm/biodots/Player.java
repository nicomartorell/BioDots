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
import com.nicocharm.biodots.screens.ScreenCreator;


public class Player implements InputProcessor{

    com.nicocharm.biodots.screens.PlayScreen screen;


    private float x;
    private float y;

    private long firstTouch;
    private long secondTouch;

    public Player(){
        x = 0;
        y = 0;

        firstTouch = 0;
        secondTouch = 0;
    }



    public void applyAntibiotic(float x, float y){
        if(screen.getPowerBar().getActiveButton().isInactive()) return;

        screen.getAntibiotic().init(x, y);

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

    public void setScreen(PlayScreen screen){
        this.screen = screen;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if(screen.showingGoal()) return true;

        //empezá un nuevo juego si ya terminaste!
        if(screen.finished()){
            BioDots game = screen.game;
            game.goToMenu();
            return true;
        }

        //proyectá la coordenada de pantalla a las virtuales
        Vector3 v = new Vector3(screenX, screenY, 0);
        Vector3 v2 = screen.cam.unproject(v);
        x = v2.x;
        y = v2.y;

        //seleccioná un antibiotico si estoy tocando un botón
        for(AntibioticButton b: screen.getPowerBar().getButtons()){
            if(b.pressed(x, y) && b.isAvailable() && b.isUsable()){
                screen.getPowerBar().notifyActivation(b);
            }
        }

        //si no estoy en la arena no hagas nada
        if(!screen.getArena().intersects(x, y)){
            return true;
        }

        secondTouch = System.nanoTime();
        double delta = ((double)(secondTouch - firstTouch))/1000000000.0;

        //dependiendo de cuanto tiempo presioné
        if(delta >= 0.3 && screen.getAntibiotic() != null && !screen.getAntibiotic().isActive()){
            applyAntibiotic(x, y); //aplicá antibiotico
        } else if(delta < 0.3 && screen.getGrid().getActiveBlocks()<3){
            screen.getGrid().activateBlock(x, y); //activá un bloque
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
