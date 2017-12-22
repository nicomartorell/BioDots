package com.nicocharm.biodots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.nicocharm.biodots.screens.PlayScreen;

import java.util.Locale;

public class PowerBar extends Actor {

    public Stage stage;
    private Label average_pOfDying;
    private float averageP;
    private float pSum;
    public float offset;

    public PowerBar(PlayScreen screen, float x, float y) {
        super(screen, x, y, false);
        setVisuals();
        width = getTexture().getWidth();
        height = getTexture().getHeight();
        scale = getScaleX();
        offset = y;
        setStage();

        pSum = 0;

    }

    private void setStage() {
        stage = new Stage(screen.viewport, screen.game.batch);
        averageP = screen.initial_pOfDying;

        Label.LabelStyle style = new Label.LabelStyle();
        BitmapFont font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = Color.RED;

        //float formattedAverage = (Math.round(averageP * 1000))/1000f;
        average_pOfDying = new Label("AVERAGE p",
                style);
        average_pOfDying.setFontScale(4);
        average_pOfDying.setSize(screen.game.WIDTH, font.getLineHeight());
        average_pOfDying.setPosition(0, offset);
        average_pOfDying.setAlignment(Align.center, Align.center);
        stage.addActor(average_pOfDying);
    }

    public void setVisuals(){
        Texture t = new Texture("bar-alone.png");
        setTexture(t);
        setScale(1f);
    }

    @Override
    public void update(float delta) {
        calculateAverageP();
        if(screen.getBacterias().size>0){
            average_pOfDying.setText("AVERAGE p: " + String.format("%.3f", averageP));
        }
        stage.act(delta);
    }

    public void sumP(Bacteria bacteria){
        pSum += bacteria.getpOfDying();
    }

    private void calculateAverageP() {
        averageP = pSum / (float)screen.getBacterias().size;
        pSum = 0;
    }

    public void render(SpriteBatch batch){

        batch.draw(getTexture(), getX() - (width/2)*scale,
                getY() - (height/2)*scale,
                width*scale,
                height*scale);
    }

    public void dispose(){
        stage.dispose();
    }
}
