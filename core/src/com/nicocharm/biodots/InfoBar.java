package com.nicocharm.biodots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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

    float upScale = 0.8f;
    float downScale = 0.6f;
    float padTop = 80;
    float averageOffset = 45;
    BitmapFont font;

    public float getAverageP() {
        return averageP;
    }

    private float averageP;
    private float pSum;

    private boolean updateBar;

    private String path = "info-bar-blue.png";

    public Button getPauseButton() {
        return pauseButton;
    }

    private Button pauseButton;

    private Label scoreUpdate;
    private float lastUpdate;
    private boolean showingScoreUpdate;

    public InfoBar(PlayScreen screen, float x, float y, float initialTime) {
        super(screen, x, y, false);

        pauseButton = new Button(screen.game, screen.game.WIDTH/2, screen.game.HEIGHT - 80, "pause-button.png", null, 0.6f);

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
        setTexture((Texture)screen.game.manager.get(path, Texture.class));
        setScale(1.1f);
    }

    private void setStage() {
        stage = new Stage(screen.viewport, screen.game.batch);

        averageP = screen.initial_pOfDying;

        Label.LabelStyle style = new Label.LabelStyle();
        font = (BitmapFont) screen.game.manager.get("Roboto-Regular.ttf", BitmapFont.class);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = Color.LIGHT_GRAY;
        style.font.getData().markupEnabled = true;

        /*Table table = new Table();
        table.top();
        table.setFillParent(true);*/
        int printTime = screen.getSettings().isFreeGame() ? (int)(time*-1) : (int)time;
        timeLabel = new Label("Tiempo: " + printTime, style);
        timeLabel.setFontScale(upScale);
        pointsLabel = new Label("Puntos: " + points, style);
        pointsLabel.setFontScale(upScale);

        Label.LabelStyle style2 = new Label.LabelStyle(font, null);
        averageLabel = new Label("",
                style2);
        averageLabel.setFontScale(downScale);


        GlyphLayout gl = new GlyphLayout(font, "Tiempo: " + printTime);
        timeLabel.setPosition((screen.game.WIDTH/2 - pauseButton.getScaledWidth()/2)/2 - gl.width*upScale/2, screen.game.HEIGHT - padTop - gl.height*upScale);

        gl = new GlyphLayout(font, "Puntos: " + points);
        pointsLabel.setPosition( screen.game.WIDTH - (screen.game.WIDTH/2 - pauseButton.getScaledWidth()/2)/2 - gl.width*upScale/2, screen.game.HEIGHT - padTop - gl.height*upScale);

        GlyphLayout gl2 = new GlyphLayout(font, "Preparate...");
        averageLabel.setPosition(screen.game.WIDTH/2 - gl2.width*downScale/2, screen.game.HEIGHT - padTop - gl.height*upScale - averageOffset - gl2.height*downScale);

        stage.addActor(timeLabel);
        stage.addActor(pointsLabel);
        stage.addActor(averageLabel);

        /*table.add(timeLabel).expandX().padTop(30);
        table.add(pointsLabel).expandX().padTop(30);
        table.row();
        table.add(averageLabel).colspan(2).expandX().padTop(40);
        stage.addActor(table);*/

        Label.LabelStyle style3 = new Label.LabelStyle();
        BitmapFont font = (BitmapFont) screen.game.manager.get("Roboto-Bold.ttf", BitmapFont.class);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style3.font = font;
        style3.fontColor = Color.LIGHT_GRAY;
        scoreUpdate = new Label("", style3);
        scoreUpdate.setFontScale(1f);
        scoreUpdate.setAlignment(Align.topRight);
        scoreUpdate.setPosition(screen.game.WIDTH, screen.game.HEIGHT - height*scale - scoreUpdate.getStyle().font.getLineHeight());

        stage.addActor(scoreUpdate);
        showingScoreUpdate = false;

    }

    @Override
    public void update(float delta) {
        if(!updateBar){
            return;
        }

        time-=delta;
        timer+=delta;

        int printTime = screen.getSettings().isFreeGame() ? (int)(time*-1) : (int)time;
        pointsLabel.setText("Puntos: " + points);
        timeLabel.setText("Tiempo: " + printTime);

        GlyphLayout gl = new GlyphLayout(font, "Tiempo: " + printTime);
        timeLabel.setPosition((screen.game.WIDTH/2 - pauseButton.getScaledWidth()/2)/2 - gl.width*upScale/2, screen.game.HEIGHT - padTop - gl.height*upScale);

        gl = new GlyphLayout(font, "Puntos: " + points);
        pointsLabel.setPosition( screen.game.WIDTH - (screen.game.WIDTH/2 - pauseButton.getScaledWidth()/2)/2 - gl.width*upScale/2, screen.game.HEIGHT - padTop - gl.height*upScale);


        calculateAverageP();

        if(!screen.finished()){
            AntibioticButton button = screen.getPowerBar().getActiveButton();
            String tag = button.getColorTag();
            String averageText = tag + String.format("%.1f", averageP*button.getPOfKilling()*100) + "% [LIGHT_GRAY]de las bacterias mueren al ser atacadas.";
            averageLabel.setText(averageText);
            GlyphLayout gl2 = new GlyphLayout(font, averageText);
            averageLabel.setPosition(screen.game.WIDTH/2 - gl2.width*downScale/2, screen.game.HEIGHT - padTop - gl.height*upScale - averageOffset - gl2.height*downScale);
        } else {
            updateBar = false;

            String averageText;
            if(screen.hasWon()){
                averageLabel.getStyle().fontColor = new Color(0, 1, 0, 1);
                averageText = "Ganaste! Tocá para continuar.";
            } else {
                averageLabel.getStyle().fontColor = new Color(1, 0, 0, 1);
                averageText = "Perdiste :( Tocá para continuar.";
            }

            averageLabel.setText(averageText);
            GlyphLayout gl2 = new GlyphLayout(font, averageText);
            averageLabel.setPosition(screen.game.WIDTH/2 - gl2.width*downScale/2, screen.game.HEIGHT - padTop - gl.height*upScale - averageOffset - gl2.height*downScale);
        }

        if(showingScoreUpdate && timer - lastUpdate > 1f){
            scoreUpdate.setText("");
            showingScoreUpdate = false;
        }

    }

    public void sumP(Bacteria bacteria){
        pSum += bacteria.getpOfDying();
    }

    private void calculateAverageP() {
        if(screen.getBacterias().size == 0){
            averageP = 0;
            pSum = 0;
            return;
        }

        averageP = pSum / (float)screen.getBacterias().size;
        pSum = 0;
    }

    public void render(SpriteBatch batch){
        batch.draw(getTexture(), getX(),
                getY(),
                width*scale,
                height*scale);
        pauseButton.render(batch);
    }

    public void dispose() {
        stage.dispose();
    }

    public void updatePoints(int newPoints){
        points += newPoints;

        int text = newPoints;

        if(showingScoreUpdate){
            text += Integer.parseInt(scoreUpdate.getText().toString());
        }

        if(text >= 0){
            scoreUpdate.getStyle().fontColor = new Color(0, 1, 0, 1);
            scoreUpdate.setText("+" + text);
        } else {
            scoreUpdate.getStyle().fontColor = new Color(1, 0, 0, 1);
            scoreUpdate.setText("" + text);
        }

        GlyphLayout gl = scoreUpdate.getGlyphLayout();
        Gdx.app.log("tag", "gl.width: " + gl.width);
        scoreUpdate.setPosition(screen.game.WIDTH - 40, screen.game.HEIGHT - height*scale - 15);

        showingScoreUpdate = true;
        lastUpdate = timer;

    }

    public int getPoints() {
        return points;
    }
}
