package com.nicocharm.biodots;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicocharm.biodots.screens.PlayScreen;

public class Block extends Actor{

    private Bounds bounds;
    private boolean active;

    public Block(PlayScreen screen, float x, float y, float width) {
        super(screen, x, y, false);
        setPosition(x, y);
        bounds = new Bounds(x, y, width, width);
        setVisuals();
        this.width = getTexture().getWidth();
        this.height = getTexture().getHeight();
        scale = width / getTexture().getWidth();
    }

    @Override
    protected void setVisuals() {
        setTexture(new Texture("box.png"));
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(getTexture(), getX(), getY(), width * scale, height * scale);
    }
}
