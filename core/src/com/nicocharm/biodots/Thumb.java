package com.nicocharm.biodots;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicocharm.biodots.screens.PlayScreen;

public class Thumb extends Actor{
    private Texture up;
    private Texture down;

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    private boolean alive;

    public final short STATE_UP = 0;
    public final short STATE_DOWN = 1;

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
        lastTime = timer;
        if(state == STATE_UP){
            setTexture(up);
        } else if (state == STATE_DOWN){
            setTexture(down);
        }
    }

    private short state;

    private float timer;
    private float lastTime;

    private float downTime;
    private float upTime;

    public Thumb(PlayScreen screen, float x, float y) {
        super(screen, x, y, false);
        setVisuals();
        setState(STATE_UP);

        this.width = getTexture().getWidth();
        this.height = getTexture().getHeight();
        this.scale = 0.8f;

        timer = 0;
        lastTime = 0;

        downTime = 0.4f;
        upTime = 1.6f;

        alive = true;
    }

    @Override
    protected void setVisuals() {
        up = (Texture)screen.game.manager.get("thumb-up.png", Texture.class);
        down = (Texture)screen.game.manager.get("thumb-down.png", Texture.class);
    }

    public void setTimes(float downTime, float upTime){
        this.downTime = downTime;
        this.upTime = upTime;
    }

    @Override
    public void update(float delta) {
        if(!alive) return;
        timer+=delta;

        if(state==STATE_UP && timer - lastTime > upTime){
            setState(STATE_DOWN);
        } else if(state==STATE_DOWN && timer - lastTime > downTime){
            setState(STATE_UP);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if(!alive) return;
        batch.draw(getTexture(), getX(), getY(), width * scale, height * scale);
    }
}
