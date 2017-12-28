package com.nicocharm.biodots;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.nicocharm.biodots.screens.PlayScreen;



public class InfoBar extends Actor {

    public Stage stage;
    private int points;

    public float getTime() {
        return time;
    }

    private float time;

    private Label pointsLabel;
    private Label timeLabel;
    private Label averageLabel;

    public float getAverageP() {
        return averageP;
    }

    private float averageP;
    private float pSum;

    private boolean updateBar;

    public InfoBar(PlayScreen screen, float x, float y, float initialTime) {
        super(screen, x, y, false);

        setVisuals();
        width = getTexture().getWidth();
        height = getTexture().getHeight();
        scale = getScaleX();
        points = 0;
        time = initialTime;
        setStage();

        pSum = 0;

        updateBar = true;
    }

    @Override
    protected void setVisuals() {
        Texture t = new Texture("info-bar-blue.png");
        setTexture(t);
        setScale(1.1f);
    }

    private void setStage() {
        stage = new Stage(screen.viewport, screen.game.batch);

        averageP = screen.initial_pOfDying;

        Label.LabelStyle style = new Label.LabelStyle();
        BitmapFont font = BioDots.fontManager.get("Roboto-Regular.ttf", 80);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = Color.LIGHT_GRAY;
        style.font.getData().markupEnabled = true;

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        timeLabel = new Label("Time: " + time, style);
        timeLabel.setFontScale(0.8f);
        pointsLabel = new Label("Points: " + points, style);
        pointsLabel.setFontScale(0.8f);

        Label.LabelStyle style2 = new Label.LabelStyle(font, null);
        averageLabel = new Label("",
                style2);
        averageLabel.setFontScale(0.6f);


        table.add(timeLabel).expandX().padTop(30);
        table.add(pointsLabel).expandX().padTop(30);
        table.row();
        table.add(averageLabel).colspan(2).expandX().padTop(15);

        stage.addActor(table);
    }

    @Override
    public void update(float delta) {
        if(!updateBar){
            return;
        }

        time-=delta;
        pointsLabel.setText("Points: " + points);
        timeLabel.setText("Time: " + (int)time);

        calculateAverageP();

        if(!screen.finished()){
            AntibioticButton button = screen.getPowerBar().getActiveButton();
            String tag = button.getColorTag();
            averageLabel.setText(tag + String.format("%.1f", averageP*button.getPOfKilling()*100) + "% [LIGHT_GRAY]de las bacterias mueren al ser atacadas.");
        } else {
            updateBar = false;

            if(screen.hasWon()){
                averageLabel.getStyle().fontColor = new Color(0, 1, 0, 1);
                averageLabel.setText("Ganaste! Felicitaciones");
            } else {
                averageLabel.getStyle().fontColor = new Color(1, 0, 0, 1);
                averageLabel.setText("Las bacterias te ganaron :(");
            }

            Label endLabel = new Label("Tocá para empezar de nuevo.", averageLabel.getStyle());
            endLabel.setFontScale(0.6f);

            Table table = (Table) stage.getActors().get(0);
            table.row();
            table.add(endLabel).colspan(2).expandX().padTop(5);
        }

    }

    public void sumP(Bacteria bacteria){
        pSum += bacteria.getpOfDying();
    }

    private void calculateAverageP() {
        averageP = pSum / (float)screen.getBacterias().size;
        pSum = 0;
    }

    public void render(SpriteBatch batch){
        batch.draw(getTexture(), getX(),
                getY(),
                width*scale,
                height*scale);
    }

    public void dispose() {
        getTexture().dispose();
        stage.dispose();
    }

    public void updatePoints(int newPoints){
        points += newPoints;
    }
}
