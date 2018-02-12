package com.nicocharm.biodots;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.nicocharm.biodots.screens.PlayScreen;

public class Button extends Actor{

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
    private String path;

    public int getId() {
        return id;
    }

    private int id;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private boolean active;

    public Button(BioDots game, float x, float y, String path, String text, float scale) {
        this(game, x, y, path, text, scale, "Roboto-Bold.ttf");
    }

    public Button(BioDots game, float x, float y, String path, String text, float scale, String fontS){
        super(null, x, y, false);

        boolean setDims = false;

        if(path !=null){
            this.path = path;
            setTexture((Texture)game.manager.get(path, Texture.class));
            width = getTexture().getWidth();
            height = getTexture().getHeight();
            setDims = true;
        }

        setScale(scale);
        this.scale = scale;

        active = true;

        if(text != null){
            BitmapFont font = (BitmapFont) game.manager.get(fontS, BitmapFont.class);

            style = new Label.LabelStyle();
            font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            style.font = font;
            style.fontColor = new Color(197/255f, 215/255f, 254/255f, 1);

            label = new Label(text, style);
            label.setFontScale(scale);
            label.setAlignment(Align.center);
            GlyphLayout gl = new GlyphLayout(style.font, text);
            label.setPosition(getX() - gl.width/2, getY() - font.getLineHeight()/2);

            if(!setDims){
                width = gl.width;
                height = font.getLineHeight();
            }
        }

        bounds = new Bounds(getX() - (width*this.scale)/2, getY() - (height*this.scale)/2, width*this.scale, height*this.scale);
    }

    public Button(BioDots game, float x, float y, String path, String text, float scale, int id) {
        this(game, x, y, path, text, scale);
        this.id = id;
    }

    public Button(BioDots game, float x, float y, String path, String text, float scale, String font, int id) {
        this(game, x, y, path, text, scale, font);
        this.id = id;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(getTexture(), getX() - (width*scale)/2,
                getY() - (height*scale)/2,
                width*scale,
                height*scale);
    }

    public boolean pressed(float x, float y){
        if(!active) return false;

        if(bounds.intersects(x, y)){
            return true;
        }
        return false;
    }

    public void setButtonPosition(float x, float y){
        setPosition(x, y);
        bounds.setPosition(getX() - (width*this.scale)/2, getY() - (height*this.scale)/2);
        GlyphLayout gl = new GlyphLayout(style.font, label.getText());
        label.setPosition(getX() - gl.width/2, getY() - style.font.getLineHeight()/2);

    }

    public float getScaledWidth(){
        return width * scale;
    }

    public float getScaledHeight() {
        return height*scale;
    }

    public float getLabelWidth(){
        GlyphLayout gl = new GlyphLayout(style.font, label.getText());
        return gl.width*scale;
    }

    public float getLabelHeight(){
        return style.font.getLineHeight();
    }

    public void setText(String text){
        label.setText(text);
    }
}
