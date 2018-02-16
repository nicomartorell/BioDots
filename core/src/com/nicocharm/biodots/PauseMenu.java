package com.nicocharm.biodots;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;

public class PauseMenu {

    PlayScreen screen;

    private Stage stage;
    private Array<Button> buttons;
    private Texture background;

    private final String CONTINUAR = "CONTINUAR";
    private final String MENU = "MENU";
    private String[] texts = {MENU, CONTINUAR};

    private Button showGoals;

    public PauseMenu(PlayScreen screen){
        this.screen = screen;

        background = (Texture)screen.game.manager.get("background-goal.png", Texture.class);

        buttons = new Array<Button>();
        stage = new Stage(screen.viewport, screen.game.batch);

        float scale = 0.75f;
        float height = ((Texture)screen.game.manager.get("menu-button.png", Texture.class)).getHeight() * scale;

        Button button = new Button(screen.game, screen.game.WIDTH/2, screen.game.HEIGHT/2 + 50 + height/2, "menu-button.png", CONTINUAR, scale, 0);
        buttons.add(button);
        stage.addActor(button.getLabel());

        Button button2 = new Button(screen.game, screen.game.WIDTH/2, screen.game.HEIGHT/2 - 50 - height/2, "menu-button.png", MENU, scale, 1);
        buttons.add(button2);
        stage.addActor(button2.getLabel());

        /*for(int i = 0; i < texts.length; i++){
            Button button = new Button(screen.game, screen.game.WIDTH/2, screen.game.HEIGHT/2 - height - 50 + i*(height + 100), "menu-button.png", texts[i], scale);
            buttons.add(button);
            stage.addActor(button.getLabel());
        }*/

        showGoals = new Button(screen.game, screen.game.WIDTH/2, 190, null, "Mostrar objetivo", 0.6f);
        stage.addActor(showGoals.getLabel());

    }

    public void clicked(float x, float y){
        for(Button button: buttons){
            if(button.pressed(x, y)){
                if(button.getId() == 0){
                    screen.resume();
                    return;
                } else if(button.getId() == 1){
                    screen.game.setToMenu(true);
                    return;
                }
            }
        }

        if(showGoals.pressed(x, y)){
            screen.getGoal().reset();
            screen.getGoal().setFinalParams();
            screen.setShowingGoal(true);

        }
    }

    public void render(SpriteBatch batch){
        batch.begin();
        Color color = batch.getColor();
        batch.setColor(new Color(0, 0, 0, 0.5f));
        batch.draw(background,0,0);
        batch.setColor(color);
        for(Button button: buttons) button.render(batch);
        batch.end();

        stage.draw();
    }
}
