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

public class PowerBar extends Actor {


    public float offset;

    private Texture background;

    private Array<AntibioticButton> buttons;

    public PowerBar(PlayScreen screen, float x, float y) {
        super(screen, x, y, false);
        setVisuals();
        width = getTexture().getWidth();
        height = getTexture().getHeight();
        scale = getScaleX();
        offset = y;

        buttons = new Array<AntibioticButton>();
        int n = 5;
        float width = 200;
        float xoffset = (screen.game.WIDTH - n * width)/2f;

        short type = Antibiotic.ANTIBIOTIC_RED;
        for(int i = 0; i < 5; i++){
            if(i>0) type = Antibiotic.ANTIBIOTIC_GRAY;
            buttons.add(new AntibioticButton(screen, xoffset + i*width, offset, type));
        }

    }


    public void setVisuals(){
        Texture t = new Texture("bar-alone.png");
        setTexture(t);
        setScale(1f);

        background = new Texture("power-bar-bg.png");
    }

    @Override
    public void update(float delta) {

    }


    public void render(SpriteBatch batch){

        float y = offset + getTexture().getHeight()/2 - background.getHeight();

        batch.draw(background, 0, y);
        /*batch.draw(getTexture(), getX() - (width/2)*scale,
                getY() - (height/2)*scale,
                width*scale,
                height*scale);*/

        for(AntibioticButton button: buttons){
            button.render(batch);
        }
    }

    public void dispose(){
        getTexture().dispose();
        background.dispose();
        for(AntibioticButton button: buttons){
            button.dispose();
        }
    }
}
