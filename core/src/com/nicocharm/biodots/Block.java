package com.nicocharm.biodots;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicocharm.biodots.screens.PlayScreen;

public class Block extends Actor{

    public Bounds getBounds() {
        return bounds;
    }

    private Bounds bounds;
    private boolean active;
    private Grid grid;

    private Texture activeTexture;
    private Texture inactiveTexture;
    private float activationTime;

    public Block(PlayScreen screen, Grid grid, float x, float y, float width) {
        super(screen, x, y, false);
        this.grid = grid;

        setPosition(x, y);
        bounds = new Bounds(x, y, width, width);
        setVisuals();
        this.width = getTexture().getWidth();
        this.height = getTexture().getHeight();
        scale = width / getTexture().getWidth();
    }

    @Override
    protected void setVisuals() {
        activeTexture = new Texture("box-active.png");
        inactiveTexture = new Texture("box.png");
        setTexture(inactiveTexture);
    }

    @Override
    public void update(float delta) {
        timer += delta;

        if(active && timer - activationTime > 5){
            active = false;
            grid.reduceActiveBlocks();
            setTexture(inactiveTexture);
        }
    }

    public void activate(){
        active = true;
        setTexture(activeTexture);
        activationTime = timer;
    }

    public boolean isTouched(float x, float y){
        if(bounds.intersects(x, y)){
            return true;
        }
        return false;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(getTexture(), getX(), getY(), width * scale, height * scale);
    }

    public void dispose(){
        activeTexture.dispose();
        inactiveTexture.dispose();
    }
}
