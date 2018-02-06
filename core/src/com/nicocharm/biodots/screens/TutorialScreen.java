package com.nicocharm.biodots.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.nicocharm.biodots.Bacteria;
import com.nicocharm.biodots.BioDots;
import com.nicocharm.biodots.Thumb;

import java.util.HashMap;

public class TutorialScreen extends PlayScreen{

    public final Short STATE_LONGPRESS = 1;
    public final Short STATE_SHORTPRESS = 2;
    public final Short STATE_FINAL = 3;
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

    private Thumb thumb;

    public TutorialScreen(BioDots game, ScreenCreator creator, Goal goal) {
        super(game, creator, goal);

        state = STATE_LONGPRESS;
        map = new HashMap<Short, Goal>();

        String[] statements = {"Bienvenido al juego!\n" +
                "El objetivo es matar bacterias.\n" +
                "Con un toque largo aplicás\n" +
                "el antibiótico.", "Cuidado!\n" +
                "El antibiótico no siempre funciona.\n" +
                "A veces es cuestión de suerte..."};
        map.put(STATE_LONGPRESS, new Goal(statements){
            @Override
            public boolean met() {
                return bacterias.size < 1;
            }

            @Override
            public boolean failed() {
                return false;
            }
        });

        String[] statements2 = {"Bien hecho!\n" +
                "Con un toque corto congelás\n" +
                "a las bacterias del cuadrante\n" +
                "pulsado", "Solo se quedan\n" +
                "congeladas por un tiempo..."};
        map.put(STATE_SHORTPRESS, new Goal(statements2){
            @Override
            public boolean met() {
                return bacterias.size < 1;
            }

            @Override
            public boolean failed() {
                return false;
            }
        });

        String[] statements3 = {"Excelente!\n", "En el panel superior\n" +
                "podés encontrar el tiempo que\n" +
                "queda, los puntos que ganaste\n" +
                "y la probabilidad de que una\n" +
                "bacteria muera con el antibió-\n" +
                "tico seleccionado.",
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

        thumb = new Thumb(this, game.WIDTH/2, game.HEIGHT/2);
    }

    @Override
    protected void update(float delta){
        if(toAdvance){
            if(timer - recordedTime > timeToNextAdvance){
                advance();
            }
        }

        super.update(delta);

        thumb.update(delta);

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
        thumb.render(game.batch);

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
        state++;

        if(state == STATE_FINAL) {
            thumb.setAlive(false);
        }

        if(state == STATE_SHORTPRESS){
            thumb.setTimes(0.1f, 1.9f);
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
}
