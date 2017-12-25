package com.nicocharm.biodots;


import com.badlogic.gdx.Gdx;
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

    private String path;
    public boolean toDestroy;
    private Set<Integer> checkedBacterias;


    public Antibiotic(PlayScreen screen, float x, float y, short type) {
        super(screen, x, y, false);
        scale = 2.5f;
        width = 400;
        height = 400;
        toDestroy = false;
        setPath(type);
        setVisuals();
        checkedBacterias = new HashSet<Integer>();
    }

    @Override
    public void render(SpriteBatch batch) {
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
                break;
        }
    }

    @Override
    protected void setVisuals() {
        setTexture(new Texture(path));
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 11; i++){
            frames.add(new TextureRegion(getTexture(), 0, i*400, 400, 400));
        }
        for(int i = 10; i >= 0; i--){
            frames.add(new TextureRegion(getTexture(), 0, i*400, 400, 400));
        }

        animation = new Animation<TextureRegion>(1/20f, frames);
    }

    @Override
    public void update(float delta) {
        if(timer > 4){
           toDestroy = true;
        } else {
            timer += delta;
        }
    }

    public void checkBacteria(Bacteria b){
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
            b.die();
            checkedBacterias.add(b.ID);
            Gdx.app.log("log", "ADDED!");
        }
    }

}
