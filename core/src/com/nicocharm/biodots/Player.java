package com.nicocharm.biodots;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;
import com.nicocharm.biodots.screens.ScreenCreator;
import com.nicocharm.biodots.screens.TutorialScreen;


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

        //si es un tutorial, tengo que mostrar como aplicar este antibiotico
        if(screen.isTutorial()){
            TutorialScreen ts = (TutorialScreen)screen;
            if((ts.getState() == ts.STATE_LONGPRESS || ts.getState() == ts.STATE_BAD_LONGPRESS) && !ts.isToAdvance()){
                ts.setToAdvance(5);
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            if(screen.isShowingGoal() || screen.isPaused()){
                screen.game.setToMenu(true);
            } else{
                screen.pause();
            }
        }
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

        if(screen.isShowingGoal()){
            screen.getGoal().pressed();
            return true;
        }

        if(!screen.isClickable()){
            return true;
        }

        //proyectá la coordenada de pantalla a las virtuales
        Vector3 v = new Vector3(screenX, screenY, 0);
        Vector3 v2 = screen.cam.unproject(v);
        x = v2.x;
        y = v2.y;

        if(screen.isPaused()){
            screen.getPauseMenu().clicked(x, y);
            return true;
        }

        if(screen.getInfobar().getPauseButton().pressed(x, y)){
            //screen.game.setToMenu(true);
            if(screen.game.getSound()){
                final Sound s = (Sound) screen.game.manager.get("select.ogg", Sound.class);
                s.play(0.3f);
            }
            screen.pause();
            return true;
        }

        //empezá un nuevo juego si ya terminaste!
        if(screen.finished()){
            BioDots game = screen.game;
            if(game.isInFreeGame()){
                game.setToMenu(true);
            } else if(screen.hasWon()){
                if(!game.lastLevel){
                    game.advance();
                } else game.setToMenu(true);
            } else {
                game.replay();
            }
            return false;
        }


        //for debugging only

        if(x < 110 && y > screen.game.HEIGHT - 110){
            BioDots game = screen.game;
            if(game.isInFreeGame()){
                Gdx.app.log("tag", "In free game");
                game.setToMenu(true);
            } else if(!game.lastLevel){
                Gdx.app.log("tag", "In a level");
                screen.win();
            } else{
                Gdx.app.log("tag", "In last level");
                game.setToMenu(true);
            }
            return false;
        }

        //seleccioná un antibiotico si estoy tocando un botón
        for(AntibioticButton b: screen.getPowerBar().getButtons()){
            if(b.pressed(x, y)){
                if(b.isAvailable() && b.isUsable()){
                    screen.getPowerBar().notifyActivation(b);
                    if(screen.game.getSound()){
                        final Sound s = (Sound) screen.game.manager.get("select.ogg", Sound.class);
                        s.play(0.3f);
                    }
                    return true;
                } else if(b.isInactive()){
                    if(screen.game.getSound()){
                        final Sound s = (Sound) screen.game.manager.get("wrong-select.ogg", Sound.class);
                        s.play(0.3f);
                    }
                }
            }
        }

        //si no estoy en la arena no hagas nada
        if(!screen.getArena().intersects(x, y)){
            return true;
        }

        secondTouch = System.nanoTime();
        double delta = ((double)(secondTouch - firstTouch))/1000000000.0;

        if(screen.getClass() == TutorialScreen.class && (((TutorialScreen)screen).getState()) == ((TutorialScreen)screen).STATE_LONGPRESS){
            ((TutorialScreen)screen).setTimePressed((float)delta);
            if(delta < 0.3){
                ((TutorialScreen)screen).setToAdvance(1f);
            }
        }

        //dependiendo de cuanto tiempo presioné
        if(delta >= 0.3 && screen.getAntibiotic() != null && !screen.getAntibiotic().isActive()){
            applyAntibiotic(x, y); //aplicá antibiotico
        } else if(delta < 0.3 && screen.getGrid().getActiveBlocks()<screen.getMaxBlocks()){
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
