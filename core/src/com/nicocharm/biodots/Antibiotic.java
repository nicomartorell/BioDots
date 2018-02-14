package com.nicocharm.biodots;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;

import java.util.HashSet;
import java.util.Set;

public class Antibiotic extends Actor{

    public static final short ANTIBIOTIC_RED = 1;
    public static final short ANTIBIOTIC_GRAY = 2;
    public static final short ANTIBIOTIC_BLUE = 4;
    public static final short ANTIBIOTIC_GREEN = 8;
    public static final short ANTIBIOTIC_PINK = 16;
    public static final short ANTIBIOTIC_WHITE = 32;


    private AntibioticButton button;

    private short type;
    private String path;
    private Set<Integer> checkedBacterias;

    private float pOfKilling;
    private float duration;
    private float inactiveTime;
    private float lastTime;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private boolean active;

    public Antibiotic(PlayScreen screen, AntibioticButton button, short type) {
        super(screen, 0, 0, false);
        scale = 2.5f;
        width = 400;
        height = 400;
        setPath(type);
        this.type = type;

        this.button = button;

        setVisuals();
        checkedBacterias = new HashSet<Integer>();

        lastTime = 0;
    }

    @Override
    public void render(SpriteBatch batch) {
        if(!active){
            return;
        }
        TextureRegion region = animation.getKeyFrame(timer, true);
        batch.draw(region, getX() - (region.getRegionWidth()/2)*scale,
                getY() - (region.getRegionHeight()/2)*scale,
                region.getRegionWidth()*scale,
                region.getRegionHeight()*scale);
    }

    private void setPath(short type) {
        switch (type){
            case ANTIBIOTIC_RED:
                path = "antibiotic-red.png";
                pOfKilling = 0.95f;
                duration = 4f;
                inactiveTime = 35;
                break;
            case ANTIBIOTIC_BLUE:
                path = "antibiotic-blue.png";
                pOfKilling = 0.8f;
                duration = 4f;
                inactiveTime = 20;
                break;
            case ANTIBIOTIC_GREEN:
                path = "antibiotic-green.png";
                pOfKilling = 0.75f;
                duration = 2f;
                inactiveTime = 15;
                break;
            case ANTIBIOTIC_PINK:
                path = "antibiotic-pink.png";
                pOfKilling = 0.85f;
                duration = 8f;
                inactiveTime = 10;
                break;
            case ANTIBIOTIC_WHITE:
                path = "antibiotic-white.png";
                pOfKilling = 0.45f;
                duration = 4f;
                inactiveTime = 0;
                break;

        }
    }

    @Override
    protected void setVisuals() {
        if(type == ANTIBIOTIC_GRAY) return;

        setTexture((Texture)screen.game.manager.get(path, Texture.class));
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 11; i++){
            frames.add(new TextureRegion(getTexture(), 0, i*400, 400, 400));
        }
        for(int i = 10; i >= 0; i--){
            frames.add(new TextureRegion(getTexture(), 0, i*400, 400, 400));
        }

        animation = new Animation<TextureRegion>(1/20f, frames);
    }

    public void init(float x, float y){
        if(type == ANTIBIOTIC_GRAY) return;

        active = true;
        setPosition(x, y);
    }

    @Override
    public void update(float delta) {
        if(!active) return;

        if(screen.game.getSound()){
            final Sound s = (Sound) screen.game.manager.get("antibiotic.ogg", Sound.class);

            if(lastTime == 0 || timer - lastTime > 0.5f){
                s.play(0.25f);
                lastTime = timer;
            }
        }


        if(timer > duration){
           reset();
           //screen.setAntibiotic(null);
            if(!screen.getPowerBar().getActiveButton().isInactive()) return;
           screen.getPowerBar().notifyActivation(screen.getPowerBar().getButtons().get(0));
        } else {
            timer += delta;
        }
    }

    public void reset(){
        active = false;
        timer = 0;
        checkedBacterias.clear();
        button.inactivate();
        lastTime = 0;
    }

    public void checkBacteria(Bacteria b){
        if(!active) return;

        if(checkedBacterias.contains(b.ID)) {
            return;
        }

        Vector2 playerPos = new Vector2(getX(), getY());
        Vector2 bPos = new Vector2(b.getX(), b.getY());
        Vector2 difference = playerPos.sub(bPos);
        float distance = difference.len();
        //esto está buenisimo
        TextureRegion region = animation.getKeyFrame(timer, true);
        float i = region.getRegionY() / region.getRegionHeight();

        if(distance < (20 + i*7.5)*scale){
            b.die(pOfKilling);
            checkedBacterias.add(b.ID);
            Gdx.app.log("log", "ADDED!");
        }
    }


    public float getInactiveTime() {
        return inactiveTime;
    }

    public float getPOfKilling() {
        return pOfKilling;
    }
}
