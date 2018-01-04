package com.nicocharm.biodots;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.nicocharm.biodots.screens.LevelScreen;

/**
 * Created by Nicolas on 03/01/2018.
 */

public class Level {

    private int id;
    private LevelScreen screen;
    private Label label;

    private float x;
    private float y;
    private float scale;

    private GlyphLayout gl;

    private Bounds bounds;

    public void setPreferedX(float preferedX) {
        this.preferedX = preferedX;
    }

    private float preferedX;

    public Level(LevelScreen screen, int id, float x, float y){
        this.screen = screen;
        this.id = id;
        this.x = x;
        this.y = y;

        preferedX = x;

        scale = 0.88f;

        BitmapFont font = (BitmapFont) screen.getGame().manager.get("Roboto-Bold.ttf", BitmapFont.class);

        Label.LabelStyle style = new Label.LabelStyle();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = new Color(197/255f, 215/255f, 254/255f, 1);

        label = new Label("Nivel " + Integer.toString(id), style);
        label.setFontScale(scale);
        label.setAlignment(Align.center);
        gl = new GlyphLayout(style.font, "Nivel " + Integer.toString(id));
        label.setPosition(x - (gl.width*scale)/2, y - (font.getLineHeight()*scale)/2);

        Texture t = screen.getBorder();
        bounds = new Bounds( x - (t.getWidth()*this.scale)/2, y - (t.getHeight()*this.scale)/2, t.getWidth()*this.scale, t.getHeight()*this.scale);
    }

    public void render(SpriteBatch batch){
        Texture t = screen.getBorder();
        batch.draw(t, x - (t.getWidth()*scale)/2, y - (t.getHeight()*scale)/2, t.getWidth()*scale, t.getHeight()*scale);
    }

    public void update(float delta){
        float velocity = preferedX - x;
        
        if(Math.abs(velocity)<10){
            velocity = 0;
            setX(preferedX);
        }

        float deltax = velocity*0.2f;
        if(deltax > 60) deltax = 60;
        
        translate(deltax, 0);
    }

    public void translate(float deltax, float deltay){
        x += deltax;
        y += deltay;
        bounds.translate(deltax, deltay);
        label.setPosition(x - (gl.width*scale)/2, y - (label.getStyle().font.getLineHeight()*scale)/2);
    }

    public Label getLabel() {
        return label;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y += y;
        bounds.setPosition(x, y);
        label.setPosition(x - (gl.width*scale)/2, y - (label.getStyle().font.getLineHeight()*scale)/2);

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        label.setPosition(x - (gl.width*scale)/2, label.getY());
        bounds.setPosition(x, bounds.getY());
    }

    public boolean pressed(float x, float y){
        if(bounds.intersects(x, y)){
            return true;
        }
        return false;
    }

    public float getPreferedX() {
        return preferedX;
    }
}
