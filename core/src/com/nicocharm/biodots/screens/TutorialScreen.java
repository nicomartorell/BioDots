package com.nicocharm.biodots.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.nicocharm.biodots.Bacteria;
import com.nicocharm.biodots.BioDots;
import com.nicocharm.biodots.Thumb;

import java.util.HashMap;

public class TutorialScreen extends PlayScreen{

    public final Short STATE_SHORTPRESS = 1;
    public final Short STATE_LONGPRESS = 2;
    public final Short STATE_BAD_LONGPRESS = 3;
    public final Short STATE_FINAL = 4;
    private HashMap<Short, Goal> map;

    public boolean isToAdvance() {
        return toAdvance;
    }

    private boolean toAdvance;
    private float recordedTime = 0;
    private float timeToNextAdvance = 0;

    public short getState() {
        return state;
    }

    private short state;

    public void setTimePressed(float timePressed) {
        this.timePressed = timePressed;
    }

    private float timePressed;
    //private Thumb thumb;

    public TutorialScreen(BioDots game, ScreenCreator creator, Goal goal) {
        super(game, creator, goal);

        state = STATE_SHORTPRESS;
        map = new HashMap<Short, Goal>();

        String[] statements = {"Tocá la pantalla para\n" +
                "congelar un cuadrante.\n"};
        map.put(STATE_SHORTPRESS, new Goal(statements){
            @Override
            public boolean met() {
                return false;
            }

            @Override
            public boolean failed() {
                return false;
            }
        });

        String[] statements2 = {"Bien hecho!\n" +
                "Ahora un toque más largo\n" +
                "aplica un antibiótico."};
        map.put(STATE_LONGPRESS, new Goal(statements2){
            @Override
            public boolean met() {
                return false;
            }

            @Override
            public boolean failed() {
                return false;
            }
        });

        String[] statements4 = {"Demasiado corto,\n" +
                "probá de nuevo."};

        map.put(STATE_BAD_LONGPRESS, new Goal(statements4){
            @Override
            public boolean met() {
                return false;
            }

            @Override
            public boolean failed() {
                return false;
            }
        });

        String[] statements3 = {"¡Excelente!",
                "Matá a todas las bacterias\n" +
                "antes de que se acabe el tiempo.\n"};
        map.put(STATE_FINAL, new Goal(statements3){
            @Override
            public boolean met() {
                return getBacterias().size < 1;
            }

            @Override
            public boolean failed() {
                return false;
            }
        });

        this.goal = map.get(state);

        isTutorial = true;
        toAdvance = false;

        //thumb = new Thumb(this, game.WIDTH/2, game.HEIGHT/2);
        timePressed = 0;
    }

    @Override
    protected void update(float delta){
        if(toAdvance){
            if(timer - recordedTime > timeToNextAdvance){
                advance();
            }
        }

        super.update(delta);

        //thumb.update(delta);

    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(cam.combined); //dibujo según la cam

        update(delta); // sin esto no pasa nada

        game.batch.begin();

        game.batch.draw(background, 0, 0);

        for(Bacteria bacteria: bacterias){ // dibujo todas las bacterias
            bacteria.render(game.batch); // 60
        }

        //dr.render(world, cam.combined);

        if(currentAntibiotic != null) currentAntibiotic.render(game.batch);

        grid.render(game.batch);
        powerBar.render(game.batch);
        infobar.render(game.batch);
        //thumb.render(game.batch);

        game.batch.end();

        infobar.stage.draw();

        if(showingGoal){
            goal.render(game.batch);
        }

        if(paused){
            pauseMenu.render(game.batch);
        }

    }

    public void advance(){

        switch(state){
            case 1: //SHORTPRESS --> LONGPRESS
                state = STATE_LONGPRESS;
                break;
            case 2: //LONGPRESS --> BAD LONGPRESS OR FINAL
                if(timePressed < 0.3f){
                state = STATE_BAD_LONGPRESS;
                } else {
                    for(int i = 0; i<4; i++){
                        addBacteria();
                    }
                    state = STATE_FINAL;
                }
                break;
            case 3: //BAD LONGPRESS --> FINAL
                for(int i = 0; i<4; i++){
                    addBacteria();
                }
                state = STATE_FINAL;
                break;
            case 4: // FINAL
                break;
        }

        Goal goal = map.get(state);
        goal.setStage(this);
        goal.setFinalParams();
        this.goal = goal;
        setShowingGoal(true);
        toAdvance = false;
    }

    public void setToAdvance(float time) {
        toAdvance = true;
        recordedTime = timer;
        timeToNextAdvance = time;
    }

    @Override
    public void hide() {
        state = STATE_SHORTPRESS;
        Goal goal = map.get(state);
        goal.setStage(this);
        this.goal = goal;
        super.hide();
    }
}
