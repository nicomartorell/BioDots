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

    public Stage stage;
    private Label average_pOfDying;

    public float getAverageP() {
        return averageP;
    }

    private float averageP;
    private float pSum;
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
        setStage();

        pSum = 0;

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

    private void setStage() {
        stage = new Stage(screen.viewport, screen.game.batch);
        averageP = screen.initial_pOfDying;

        Label.LabelStyle style = new Label.LabelStyle();
        BitmapFont font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = Color.RED;

        //float formattedAverage = (Math.round(averageP * 1000))/1000f;
        average_pOfDying = new Label("",
                style);
        average_pOfDying.setFontScale(4f);
        average_pOfDying.setSize(screen.game.WIDTH, font.getLineHeight());
        average_pOfDying.setPosition(0, offset);
        average_pOfDying.setAlignment(Align.center, Align.center);
        stage.addActor(average_pOfDying);
    }

    public void setVisuals(){
        Texture t = new Texture("bar-alone.png");
        setTexture(t);
        setScale(1f);

        background = new Texture("power-bar-bg.png");
    }

    @Override
    public void update(float delta) {
        calculateAverageP();
        if(!screen.finished()){
            average_pOfDying.setText("Matá a todas las bacterias!\n" + String.format("%.1f", averageP*100) + "% de las bacterias\nmueren al ser atacadas.");
        } else if(screen.hasWon()){
            average_pOfDying.getStyle().fontColor = new Color(0, 1, 0, 1);
            average_pOfDying.setText("Ganaste! Felicitaciones\nTocá para empezar de nuevo.");
        } else {
            average_pOfDying.setText("Las bacterias te ganaron :(\nTocá para empezar de nuevo.");
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
        stage.dispose();
        getTexture().dispose();
        background.dispose();
        for(AntibioticButton button: buttons){
            button.dispose();
        }
    }
}
