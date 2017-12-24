package com.nicocharm.biodots;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

    public InfoBar(PlayScreen screen, float x, float y, float initialTime) {
        super(screen, x, y, false);

        setVisuals();
        width = getTexture().getWidth();
        height = getTexture().getHeight();
        scale = getScaleX();
        points = 0;
        time = initialTime;
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

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        timeLabel = new Label("Time: " + time, style);
        timeLabel.setFontScale(4);
        pointsLabel = new Label("Points: " + points, style);
        pointsLabel.setFontScale(4);

        table.add(timeLabel).expandX().padTop(30);
        table.add(pointsLabel).expandX().padTop(30);

        stage.addActor(table);
    }

    @Override
    public void update(float delta) {
        if(time > 0){
            time-=delta;
        }
        pointsLabel.setText("Points: " + points);
        timeLabel.setText("Time: " + (int)time);
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
