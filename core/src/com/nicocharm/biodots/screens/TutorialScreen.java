package com.nicocharm.biodots.screens;


import com.nicocharm.biodots.BioDots;

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
                return false;
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
                return false;
            }

            @Override
            public boolean failed() {
                return false;
            }
        });

        String[] statements3 = {"Excelente!\n", "En el panel superior\n" +
                "podés encontrar el tiempo que\n" +
                "queda, los puntos que ganaste\n" +
                "(o perdiste!) y la probabilidad\n" +
                "de que una bacteria muera con\n" +
                "el antibiótico seleccionado.",
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
    }

    @Override
    protected void update(float delta){
        if(toAdvance){
            if(timer - recordedTime > timeToNextAdvance){
                advance();
            }
        }

        super.update(delta);
    }

    @Override
    public void render(float delta){
        super.render(delta);
    }

    public void advance(){
        if(state == STATE_FINAL) return;

        state++;
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
