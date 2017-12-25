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

    private Array<AntibioticButton> buttons;
    private int nButtons;

    public PowerBar(PlayScreen screen, float x, float maxy) {
        this.screen = screen;

        setVisuals();
        offset = maxy;

        buttons = new Array<AntibioticButton>();
        nButtons = 5;
        float width = AntibioticButton.WIDTH;
        float xoffset = (screen.game.WIDTH - nButtons * width)/2f;

        short[] types = new short[5];
        types[0] = Antibiotic.ANTIBIOTIC_RED;
        types[1] = Antibiotic.ANTIBIOTIC_BLUE;
        types[2] = Antibiotic.ANTIBIOTIC_GREEN;
        types[3] = Antibiotic.ANTIBIOTIC_PINK;
        types[4] = Antibiotic.ANTIBIOTIC_GRAY;

        for(int i = 0; i < nButtons; i++){
            buttons.add(new AntibioticButton(screen, xoffset + i*width, offset/2, types[i]));
        }

    }


    public void setVisuals(){
        background = new Texture("power-bar-bg.png");
    }


    public void render(SpriteBatch batch){
        float y = offset - background.getHeight();

        batch.draw(background, 0, y);

        for(AntibioticButton button: buttons){
            button.render(batch);
        }
    }

    public void dispose(){
        background.dispose();
        for(AntibioticButton button: buttons){
            button.dispose();
        }
    }

    public Array<AntibioticButton> getButtons() {
        return buttons;
    }
}
