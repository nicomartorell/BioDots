package com.nicocharm.biodots;

import com.badlogic.gdx.Gdx;
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

    private String id;
    private LevelScreen screen;
    private Label label;

    private float x;
    private float y;
    private float scale;
    private int lines;

    private GlyphLayout gl;

    private Bounds bounds;

    public void setPreferedX(float preferedX) {
        this.preferedX = preferedX;
    }

    private float preferedX;

    private boolean locked;

    public Level(LevelScreen screen, String id, int lines, float x, float y, boolean locked){
        this.screen = screen;
        this.id = id;
        this.x = x;
        this.y = y;
        this.lines = lines;

        preferedX = x;

        scale = 0.88f;
        Texture t;

        this.locked = locked;

        if(!locked){
            BitmapFont font = (BitmapFont) screen.getGame().manager.get("Roboto-Bold.ttf", BitmapFont.class);

            Label.LabelStyle style = new Label.LabelStyle();
            font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            style.font = font;
            style.fontColor = new Color(197/255f, 215/255f, 254/255f, 1);

            label = new Label(id, style);
            label.setFontScale(scale);
            label.setAlignment(Align.center);
            gl = new GlyphLayout(style.font, id);
            Gdx.app.log("tag", "gl.width*scale /2: " + (gl.width*scale)/2 + " | font.getLineHeight*scale /2: " + (font.getLineHeight()*scale)/2);
            label.setPosition(x - (gl.width/**scale*/)/2, y - (label.getStyle().font.getLineHeight()/**scale*/*lines)/2);

            t = screen.getBorder();
        } else {
            t = screen.getLockedBorder();
        }


        bounds = new Bounds( x - (t.getWidth()*this.scale)/2f, y - (t.getHeight()*this.scale)/2f, t.getWidth()*this.scale, t.getHeight()*this.scale);
    }

    public void render(SpriteBatch batch){
        Texture t;
        if(locked){
            t = screen.getLockedBorder();
        } else {
            t = screen.getBorder();
        }
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
        if(locked) return;
        label.setPosition(label.getX() + deltax, label.getY());    }

    public Label getLabel() {
        return label;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y += y;
        Texture t = screen.getBorder();
        bounds.setPosition(x - (t.getWidth()*scale)/2, y - t.getHeight()*scale);
        if(locked) return;
        label.setPosition(x - (gl.width/**scale*/)/2, y - (label.getStyle().font.getLineHeight()/**scale*/*lines)/2);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        Texture t = screen.getBorder();
        bounds.setPosition(x  - (t.getWidth()*scale)/2, bounds.getY());
        if(locked) return;
        label.setPosition(x - (gl.width/**scale*/)/2, label.getY());
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
