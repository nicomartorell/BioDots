package com.nicocharm.biodots;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicocharm.biodots.screens.PlayScreen;

public class AntibioticButton extends Actor {

    private short type;
    private String path;
    private Bounds bounds;
    private Antibiotic antibiotic;

    public static final float WIDTH = 200;

    public AntibioticButton(PlayScreen screen, float x, float y, short type) {
        super(screen, x, y, false);

        this.type = type;
        setType(type);

        setVisuals();
        width = getTexture().getWidth();
        height = getTexture().getHeight();
        scale = getScaleX();

        if(type != Antibiotic.ANTIBIOTIC_GRAY){
            antibiotic = new Antibiotic(screen, type);
        } else{
            antibiotic = null;
        }


        //la y está centrada pero la x no
        // esto me hace la vida más fácil nomás
        bounds = new Bounds(getX(), getY() - (height*scale)/2, width*scale, height*scale);
    }

    @Override
    protected void setVisuals() {
        setTexture(new Texture(path));
    }

    private void setType(short type) {
        switch (type){
            case Antibiotic.ANTIBIOTIC_RED:
                path = "antibiotic-box-red.png";
                break;
            case Antibiotic.ANTIBIOTIC_GRAY:
                path = "antibiotic-box-gray.png";
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(getTexture(), getX(),
                getY() - (width*scale)/2,
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
        if(antibiotic != null) antibiotic.dispose();
    }

    public void selectAntibiotic() {
        if(type == Antibiotic.ANTIBIOTIC_GRAY) return;
        screen.setAntibiotic(antibiotic);
    }
}
