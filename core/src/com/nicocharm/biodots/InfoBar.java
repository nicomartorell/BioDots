package com.nicocharm.biodots;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.nicocharm.biodots.screens.PlayScreen;



public class InfoBar extends Actor {

    public Stage stage;

    public InfoBar(PlayScreen screen, float x, float y) {
        super(screen, x, y, false);

        setVisuals();
        width = getTexture().getWidth();
        height = getTexture().getHeight();
        scale = getScaleX();
        setStage();
    }

    @Override
    protected void setVisuals() {
        Texture t = new Texture("info-bar-blue.png");
        setTexture(t);
        setScale(1f);
    }

    private void setStage() {
        stage = new Stage(screen.viewport, screen.game.batch);

        Label.LabelStyle style = new Label.LabelStyle();
        BitmapFont font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = Color.RED;

    }

    public void render(SpriteBatch batch){
        batch.draw(getTexture(), getX(),
                getY(),
                width*scale,
                height*scale);
    }

    public void dispose() {
        getTexture().dispose();
    }
}
