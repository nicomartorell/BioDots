package com.nicocharm.biodots.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.Bacteria;
import com.nicocharm.biodots.BioDots;


public abstract class Goal {

    private String[] statements;
    private Label label;
    private Stage stage;
    private Texture background;
    private float alpha;
    private float timer;
    private float textAlpha;
    private float fontScale;

    private int counter;

    public PlayScreen getScreen() {
        return screen;
    }

    private PlayScreen screen;

    private String path = "background-goal.png";

    public Goal(String[] strings){
        statements = strings;
        alpha = 1f;
        textAlpha = 0;
        timer = 0;
        fontScale = 0.5f;
        counter = 0;
    }

    public void setStage(PlayScreen screen){
        this.screen = screen;

        background = (Texture)screen.game.manager.get(path, Texture.class);

        stage = new Stage(screen.viewport, screen.game.batch);

        BitmapFont font = (BitmapFont) screen.game.manager.get("Roboto-Regular.ttf", BitmapFont.class);
        Label.LabelStyle style = new Label.LabelStyle();

        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = new Color(197/255f, 215/255f, 254/255f, textAlpha);

        label = new Label(statements[0], style);
        label.setFontScale(fontScale*0.75f);
        label.setAlignment(Align.center);
        GlyphLayout gl = new GlyphLayout(font, statements[0]);
        label.setPosition(screen.game.WIDTH/2 - gl.width/2, screen.game.HEIGHT/2 - gl.height/2 + 50);


        stage.addActor(label);

        Label label2 = new Label("Tocá para continuar", style);
        label2.setFontScale(0.75f);
        label2.setAlignment(Align.center);
        GlyphLayout gl2 = new GlyphLayout(font, "Tocá para continuar");
        label2.setPosition(screen.game.WIDTH/2 - gl2.width/2, 100);
        stage.addActor(label2);
    }

    // puedo tener otros métodos con otros argumentos si hace falta
    public abstract boolean met();
    public abstract boolean failed();

    public void render(SpriteBatch batch){
        batch.begin();
        Color color = batch.getColor();
        batch.setColor(new Color(0, 0, 0, alpha));
        batch.draw(background,0,0);
        batch.setColor(color);
        batch.end();
        stage.draw();
    }

    public void dispose(){
        Gdx.app.log("tag", "DISPOSED GOAL");
        if(stage!=null)stage.dispose();
        stage = null;
        reset();
    }

    public void update(float delta) {
        timer+=delta;
        if(timer < 0.8f || timer > 10f) return;

        if(fontScale < 1) fontScale += delta * 1/10f;
        if(textAlpha < 0.9f) textAlpha += delta*0.3f;
        if(alpha > 0.7f) alpha -= delta*0.1f;
        label.setFontScale(fontScale*0.75f);
        label.getStyle().fontColor = new Color(197/255f, 215/255f, 254/255f, textAlpha);
    }

    public void pressed(){
        if(counter == 0 && fontScale < 1){
            timer = 10f;
            fontScale = 1;
            textAlpha = 0.9f;
            alpha = 0.7f;
            label.setFontScale(fontScale*0.75f);
            label.getStyle().fontColor = new Color(197/255f, 215/255f, 254/255f, textAlpha);
            return;
        }

        if(counter + 1 < statements.length){
            label.setText(statements[counter + 1]);
        } else {
            screen.setShowingGoal(false);
        }

        counter++;
    }

    public void setFinalParams(){
        timer = 10f;
        fontScale = 1;
        textAlpha = 0.9f;
        alpha = 0.7f;
        label.setFontScale(fontScale*0.75f);
        label.getStyle().fontColor = new Color(197/255f, 215/255f, 254/255f, textAlpha);
    }

    public void reset() {
        alpha = 1f;
        textAlpha = 0;
        timer = 0;
        fontScale = 0.5f;
        counter = 0;
    }
}
