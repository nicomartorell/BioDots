package com.nicocharm.biodots;


public class Bounds {
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    private float x;
    private float y;
    private float width;
    private float height;

    public Bounds(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public boolean intersects(float xa, float ya){
        if((xa > x && xa < x+width)&&(ya > y && ya < y + height)){
            return true;
        }
        else return false;
    }
}
