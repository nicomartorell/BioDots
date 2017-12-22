package com.nicocharm.biodots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;

import java.util.Random;


public class Bacteria extends Actor {

    public static final short BACTERIA_BLUE = 1;
    public static final short BACTERIA_RED = 2;
    public static final short BACTERIA_ORANGE = 3;
    public static final short BACTERIA_PINK = 4;
    public static final short BACTERIA_GREEN = 5;

    private static int ID_COUNT = 0;
    public int ID;

    private Vector2 target;
    private float targetTimer;
    private float targetTimerLimit;
    private String path;
    private short type;

    private boolean reproducing;
    private int scaleCount;
    private float repScale;

    public boolean isDividing() {
        return dividing;
    }

    private boolean dividing;
    private boolean firstTarget;

    public boolean isDead() {
        return isDead;
    }

    private boolean isDead;

    public float getpOfDying() {
        return pOfDying;
    }

    private float pOfDying;

    public Bacteria (PlayScreen screen, float x, float y, short type, float pOfDying){
        super(screen, x, y);

        ID = Bacteria.ID_COUNT;
        Bacteria.ID_COUNT++;

        Filter filter = new Filter();
        filter.categoryBits = screen.BACTERIA_BIT;
        filter.maskBits = screen.DEFAULT_BIT;
        fixture.setFilterData(filter);
        this.type = type;

        scale = 0.37f;
        width = 350;
        height = 512;
        setPath(type);

        target = getNewTarget();
        body.setLinearVelocity(target.cpy().sub(body.getPosition()));
        angle = body.getLinearVelocity().nor().angle();
        angle -=90;
        setVisuals();

        reproducing = false;
        dividing = false;
        repScale = 1;

        firstTarget = false;

        isDead = false;

        Random r = new Random();
        this.pOfDying = (float)r.nextGaussian()*0.1f + pOfDying;
        if(this.pOfDying > 1){
            this.pOfDying = 1;
        } else if(this.pOfDying < 0){
            this.pOfDying = 0;
        }
    }

    public Bacteria(PlayScreen screen, float x, float y, short type, float pOfDying, Vector2 velocity, Vector2 target){
        this(screen, x, y, type, pOfDying);
        body.setLinearVelocity(velocity);
        this.target = target;
        angle = body.getLinearVelocity().nor().angle();
        angle -=90;
        firstTarget = true;
    };

    @Override
    protected void setVisuals() {
        setTexture(new Texture(path));
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 9; i++){
            frames.add(new TextureRegion(getTexture(), 0, i*(int)height, (int)width, (int)height));
        }
        for(int i = 8; i >= 0; i--){
            frames.add(new TextureRegion(getTexture(), 0, i*(int)height, (int)width, (int)height));
        }

        animation = new Animation<TextureRegion>(1/40f, frames);

    }

    public void update(float delta){
        timer += delta;
        targetTimer += delta;
        setPosition(body.getPosition().x, body.getPosition().y);
        calculateVelocity();
        angle = body.getLinearVelocity().nor().angle();
        angle -=90;

        if(targetTimer> targetTimerLimit) target = getNewTarget();

        handleReproduce();

    }

    public void render(SpriteBatch batch){
            TextureRegion region = animation.getKeyFrame(timer, true);
            batch.draw(region,
                    getX() - (width/2),
                    getY() - (height/2),
                    (width/2),
                    (height/2),
                    width,
                    height,
                    scale*repScale,
                    scale*repScale,
                    angle);
    }

    private Vector2 getNewTarget(){
        Random r = new Random();
        float scale = 0.9f;
        Vector2 t = new Vector2(screen.getNewBacteriaX(r.nextFloat()),
                screen.getNewBacteriaY(r.nextFloat()));
        targetTimer = 0;

        if(firstTarget){
            targetTimerLimit = r.nextFloat()*0.2f + 0.3f;
            firstTarget = false;
        } else {
            targetTimerLimit = r.nextFloat()*6 + 4;
        }

        return t;
    }

    private void calculateVelocity(){
        Vector2 targetSaved = target.cpy();
        if(targetSaved.sub(body.getPosition()).len()< 100){
            target = getNewTarget();
        }

        targetSaved = target.cpy();
        Vector2 desired = targetSaved.sub(getX(), getY()).nor().scl(100);
        body.applyForce((desired.sub(body.getLinearVelocity())).limit(50), body.getWorldCenter(), true);
    }

    private void handleReproduce(){
        Random r = new Random();
        if(r.nextDouble() < 0.0004 && !reproducing) {
            Gdx.app.log("log", "rep!");
            reproducing = true;
            scaleCount = 0;
        } else if (reproducing){
            if(scaleCount < screen.getBacteriaScale().size){
                repScale = screen.getBacteriaScale().get(scaleCount);
                scaleCount++;
            } else {
                divide();
            }
        }
    }

    private void divide() {
        Gdx.app.log("log", "dividing!");
        screen.getBacterias().add(new Bacteria(screen, getX(), getY(), getType(), pOfDying, body.getLinearVelocity(), target));
        screen.getBacterias().add(new Bacteria(screen, getX(), getY(), getType(), pOfDying, body.getLinearVelocity(), target));
        dividing = true;
    }


    public void setPath(short type) {
        switch(type){
            case BACTERIA_BLUE:
                path = "bacteria-blue.png";
                break;
            case BACTERIA_GREEN:
                path = "bacteria-green.png";
                break;
            case BACTERIA_ORANGE:
                path = "bacteria-orange.png";
                break;
            case BACTERIA_PINK:
                path = "bacteria-pink.png";
                break;
            case BACTERIA_RED:
                path = "bacteria-red.png";
                break;
        }
    }

    public short getType() {
        return type;
    }

    public void die() {
        Random r = new Random();
        if(r.nextFloat() < pOfDying){
            isDead = true;
        }
    }
}
