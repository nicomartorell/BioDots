package com.nicocharm.biodots;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;

public class Actor extends Sprite {

    protected PlayScreen screen;
    protected Array<TextureRegion> frames;
    protected Animation<TextureRegion> animation;
    protected float timer;
    protected float scale;
    protected float width;
    protected float height;
    protected float angle;

    protected Body body;
    protected Fixture fixture;
    
    protected boolean toDispose;

    public Actor (PlayScreen screen, float x, float y){
        this.screen = screen;
        this.setPosition(x, y);
        timer = 0;
        scale = 0.37f;
        width = 350;
        height = 512;
        defineBox2D(10);
        angle = 0;
        
        toDispose = false;
    }

    public Actor (PlayScreen screen, float x, float y, boolean bodyNeeded){
        this.screen = screen;
        this.setPosition(x, y);
        timer = 0;
        scale = 0.37f;
        width = 350;
        height = 512;
        if(bodyNeeded) defineBox2D(10);
        angle = 0;

        toDispose = false;
    }

    protected void defineBox2D(float radius){
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(new Vector2(getX(), getY()));
        body = screen.getWorld().createBody(bd);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void update(float delta){

    }

    public void render(SpriteBatch batch){
        super.draw(batch);
    }

    //children have to override this method
    protected void setVisuals(){

    }

    public Body getBody() {
        return body;
    }
}
