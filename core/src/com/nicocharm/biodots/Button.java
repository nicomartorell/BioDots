package com.nicocharm.biodots;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.nicocharm.biodots.screens.PlayScreen;

public abstract class Button extends Actor{

    private Bounds bounds;

    public Bounds getBounds() {
        return bounds;
    }

    public Label getLabel() {
        return label;
    }

    public Label.LabelStyle getStyle() {
        return style;
    }

    private Label label;
    private Label.LabelStyle style;

    public Button(PlayScreen screen, float x, float y, Texture texture, String text, float scale) {
        super(screen, x, y, false);
        setTexture(texture);
        width = getTexture().getWidth();
        height = getTexture().getHeight();
        setScale(scale);
        this.scale = getScaleX();
        bounds = new Bounds(getX(), getY() - (height*this.scale)/2, width*this.scale, height*this.scale);

        Label.LabelStyle style = new Label.LabelStyle();
        BitmapFont font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = new Color(197/255f, 215/255f, 254/255f, 1);

        label = new Label(text, style);
        label.setFontScale(7*this.scale);
        label.setAlignment(Align.center);
        GlyphLayout gl = new GlyphLayout(style.font, text);
        label.setPosition(getX() - gl.width/2, getY());
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(getTexture(), getX() - (width*scale)/2,
                getY() - (height*scale)/2,
                width*scale,
                height*scale);
    }

    public boolean pressed(float x, float y){
        if(bounds.intersects(x, y)){
            return true;
        }
        return false;
    }

    public void dispose(){
        getTexture().dispose();
    }
}
