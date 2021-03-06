package com.nicocharm.biodots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;

public class AntibioticButton extends Actor {

    private short type;
    private Bounds bounds;

    public Antibiotic getAntibiotic() {
        return antibiotic;
    }

    private Antibiotic antibiotic;

    private Texture texture;
    private Texture activeTexture;
    private Texture inactiveTexture;
    private String path;
    private String activePath;
    private String inactivePath;
    private String colorTag;

    private int state;

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

        inactivate();
    }

    @Override
    protected void setVisuals() {
        texture = (Texture)screen.game.manager.get(path, Texture.class);
        Gdx.app.log("tag", "Path: " + path);
        setTexture(texture);
        if(type == Antibiotic.ANTIBIOTIC_GRAY) return;
        activeTexture = (Texture)screen.game.manager.get(activePath, Texture.class);
        if(type == Antibiotic.ANTIBIOTIC_WHITE) return;
        inactiveTexture = (Texture)screen.game.manager.get(inactivePath, Texture.class);
    }

    private void setType(short type) {
        switch (type){
            case Antibiotic.ANTIBIOTIC_RED:
                path = "antibiotic-box-red.png";
                activePath = "antibiotic-box-red-active.png";
                inactivePath = "antibiotic-box-red-inactive.png";
                colorTag = "[RED]";
                break;
            case Antibiotic.ANTIBIOTIC_GRAY:
                path = "antibiotic-box-gray.png";
                colorTag = "[LIGHT_GRAY]";
                break;
            case Antibiotic.ANTIBIOTIC_BLUE:
                path = "antibiotic-box-blue.png";
                activePath = "antibiotic-box-blue-active.png";
                inactivePath = "antibiotic-box-blue-inactive.png";
                colorTag = "[CYAN]";
                break;
            case Antibiotic.ANTIBIOTIC_GREEN:
                path = "antibiotic-box-green.png";
                activePath = "antibiotic-box-green-active.png";
                inactivePath = "antibiotic-box-green-inactive.png";
                colorTag = "[GREEN]";
                break;
            case Antibiotic.ANTIBIOTIC_PINK:
                path = "antibiotic-box-pink.png";
                activePath = "antibiotic-box-pink-active.png";
                inactivePath = "antibiotic-box-pink-inactive.png";
                colorTag = "[PINK]";
                break;
            case Antibiotic.ANTIBIOTIC_WHITE:
                path = "antibiotic-box-white.png";
                activePath = "antibiotic-box-white-active.png";
                colorTag = "[WHITE]";
                break;

        }
    }

    @Override
    public void update(float delta) {
        if(type == Antibiotic.ANTIBIOTIC_GRAY) return;

        if(state == STATE_INACTIVE){
            if(timer > antibiotic.getInactiveTime()){
                if(screen.getPowerBar().getActiveButton().equals(this)){
                    selectAntibiotic();
                } else {
                    setTexture(texture);
                    state = STATE_DEFAULT;
                }
                timer = 0;
            } else {
                timer += delta;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if(type == Antibiotic.ANTIBIOTIC_GRAY && getTexture()==null) setTexture((Texture) screen.game.manager.get(path, Texture.class));
        batch.draw(getTexture(), getX(),
                getY() - (width*scale)/2,
                width*scale,
                height*scale);
    }

    public boolean pressed(float x, float y){
        if(type == Antibiotic.ANTIBIOTIC_GRAY) return false;
        if(bounds.intersects(x, y)){
            return true;
        }
        return false;
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
        if(type == Antibiotic.ANTIBIOTIC_GRAY) return;
        if(type==Antibiotic.ANTIBIOTIC_WHITE){
            return;
        }

        state = STATE_INACTIVE;
        setTexture(inactiveTexture);
    }

    public void setAvailable(){
        state = STATE_DEFAULT;
        setTexture(texture);
    }

    public void setActive(){
        if(type == Antibiotic.ANTIBIOTIC_GRAY) return;
        state = STATE_ACTIVE;
        setTexture(activeTexture);
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

    public String getColorTag() {
        return colorTag;
    }

    public float getPOfKilling() {
        if(type == Antibiotic.ANTIBIOTIC_GRAY) return 0;
        return antibiotic.getPOfKilling();
    }
}
