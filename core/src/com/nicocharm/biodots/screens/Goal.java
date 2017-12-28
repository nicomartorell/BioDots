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

    private String statement;
    private Label label;
    private Stage stage;
    private Texture background;
    private float alpha;
    private float timer;
    private float textAlpha;
    private float fontScale;

    public PlayScreen getScreen() {
        return screen;
    }

    private PlayScreen screen;

    public Goal(String string){
        statement = string;
        alpha = 1f;
        textAlpha = 0;
        timer = 0;
        fontScale = 3;
    }

    public void setStage(PlayScreen screen){
        this.screen = screen;

        background = new Texture("background-goal.png");

        stage = new Stage(screen.viewport, screen.game.batch);

        Label.LabelStyle style = new Label.LabelStyle();
        BitmapFont font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = new Color(1, 0, 0, textAlpha);

        label = new Label(statement, style);
        label.setFontScale(fontScale);
        label.setAlignment(Align.center);
        GlyphLayout gl = new GlyphLayout(font, statement);
        label.setPosition(screen.game.WIDTH/2 - gl.width/2, screen.game.HEIGHT/2 - gl.height/2);


        stage.addActor(label);
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
        if(stage!=null)stage.dispose();
        alpha = 1f;
        textAlpha = 0;
        timer = 0;
        fontScale = 3;
    }

    public void update(float delta) {
        timer+=delta;
        if(timer < 4) return;

        if(fontScale < 5) fontScale += delta * 1/3f;
        if(textAlpha < 0.9f) textAlpha += delta*0.3f;
        if(alpha > 0.7f) alpha -= delta*0.1f;
        label.setFontScale(fontScale);
        label.getStyle().fontColor = new Color(1, 0, 0, textAlpha);
    }
}
