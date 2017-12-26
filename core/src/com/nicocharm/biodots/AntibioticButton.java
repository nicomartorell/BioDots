package com.nicocharm.biodots;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;

public class AntibioticButton extends Actor {

    private short type;
    private Bounds bounds;
    private Antibiotic antibiotic;

    private Texture texture;
    private Texture activeTexture;
    private Texture inactiveTexture;
    private String path;
    private String activePath;
    private String inactivePath;

    private int state;

    private float inactiveTime;

    public static final int STATE_ACTIVE = 1;
    public static final int STATE_INACTIVE = 2;
    public static final int STATE_DEFAULT = 4;

    public static final float WIDTH = 200;

    public AntibioticButton(PlayScreen screen, float x, float y, short type) {
        super(screen, x, y, false);

        state = STATE_DEFAULT;
        this.type = type;

        setType(type);
        setVisuals();

        width = getTexture().getWidth();
        height = getTexture().getHeight();
        scale = getScaleX();

        if(type != Antibiotic.ANTIBIOTIC_GRAY){
            antibiotic = new Antibiotic(screen, this, type);
        } else{
            antibiotic = null;
        }


        //la y está centrada pero la x no
        // esto me hace la vida más fácil nomás
        bounds = new Bounds(getX(), getY() - (height*scale)/2, width*scale, height*scale);
    }

    @Override
    protected void setVisuals() {
        texture = new Texture(path);
        setTexture(texture);
        if(type == Antibiotic.ANTIBIOTIC_GRAY) return;
        activeTexture = new Texture(activePath);
        if(type == Antibiotic.ANTIBIOTIC_WHITE) return;
        inactiveTexture = new Texture(inactivePath);
    }

    private void setType(short type) {
        switch (type){
            case Antibiotic.ANTIBIOTIC_RED:
                path = "antibiotic-box-red.png";
                activePath = "antibiotic-box-red-active.png";
                inactivePath = "antibiotic-box-red-inactive.png";

                inactiveTime = 3;
                break;
            case Antibiotic.ANTIBIOTIC_GRAY:
                path = "antibiotic-box-gray.png";
                break;
            case Antibiotic.ANTIBIOTIC_BLUE:
                path = "antibiotic-box-blue.png";
                activePath = "antibiotic-box-blue-active.png";
                inactivePath = "antibiotic-box-blue-inactive.png";

                inactiveTime = 8;
                break;
            case Antibiotic.ANTIBIOTIC_GREEN:
                path = "antibiotic-box-green.png";
                activePath = "antibiotic-box-green-active.png";
                inactivePath = "antibiotic-box-green-inactive.png";

                inactiveTime = 6;
                break;
            case Antibiotic.ANTIBIOTIC_PINK:
                path = "antibiotic-box-pink.png";
                activePath = "antibiotic-box-pink-active.png";
                inactivePath = "antibiotic-box-pink-inactive.png";

                inactiveTime = 3;
                break;
            case Antibiotic.ANTIBIOTIC_WHITE:
                path = "antibiotic-box-white.png";
                activePath = "antibiotic-box-white-active.png";
                break;

        }
    }

    @Override
    public void update(float delta) {
        if(state == STATE_INACTIVE){
            if(timer > inactiveTime){
                setTexture(texture);
                state = STATE_DEFAULT;
                timer = 0;
            } else {
                timer += delta;
            }
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

        setTexture(activeTexture);
        state = STATE_ACTIVE;

        if(screen.getAntibiotic() == null){
            screen.setAntibiotic(antibiotic);
        } else {
            screen.setNextAntibiotic(antibiotic);
        }
    }

    public void inactivate(){
        if(type==Antibiotic.ANTIBIOTIC_WHITE){
            setAvailable();
            return;
        }

        state = STATE_INACTIVE;
        setTexture(inactiveTexture);
    }

    public void setAvailable(){
        state = STATE_DEFAULT;
        setTexture(texture);
    }

    public int getState() {
        return state;
    }

    public boolean isAvailable() {
        return state == STATE_DEFAULT;
    }

    public boolean isInactive() {
        return state == STATE_INACTIVE;
    }

    public boolean isUsable(){
        return type != Antibiotic.ANTIBIOTIC_GRAY;
    }
}
