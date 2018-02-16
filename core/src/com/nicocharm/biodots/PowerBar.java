package com.nicocharm.biodots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;

import java.util.Locale;

public class PowerBar{

    private PlayScreen screen;

    private float offset;
    private Texture background;
    private String path = "power-bar-bg.png";

    private Array<AntibioticButton> buttons;

    private int nButtons;

    private int activeButton;

    public PowerBar(PlayScreen screen, float x, float maxy, short[] types) {
        this.screen = screen;

        setVisuals();
        offset = maxy;

        buttons = new Array<AntibioticButton>();
        nButtons = types.length;

        float width = AntibioticButton.WIDTH;
        float xoffset = (screen.game.WIDTH - nButtons * width)/2f;

        for(int i = 0; i < nButtons; i++){
            buttons.add(new AntibioticButton(screen, xoffset + i*width, offset/2, types[i]));
        }

        activeButton = 0;
    }

    public void setVisuals(){
        background = (Texture)screen.game.manager.get("power-bar-bg.png", Texture.class);
    }

    public void render(SpriteBatch batch){
        float y = offset - background.getHeight();

        batch.draw(background, 0, y);

        for(AntibioticButton button: buttons){
            button.render(batch);
        }
    }

    public void update(float delta){
        for(AntibioticButton button: buttons){ //5
            button.update(delta);
        }
    }


    public Array<AntibioticButton> getButtons() {
        return buttons;
    }

    public void notifyActivation(AntibioticButton antibioticButton) {
        AntibioticButton current = buttons.get(activeButton);
        if(!current.isInactive()) current.setAvailable();
        activeButton = buttons.indexOf(antibioticButton, true);
        buttons.get(activeButton).selectAntibiotic();
    }

    public AntibioticButton getActiveButton() {
        return buttons.get(activeButton);
    }

    public void setActiveButton(int activeButton) {
        this.activeButton = activeButton;
    }
}
