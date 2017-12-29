package com.nicocharm.biodots;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
    private AssetManager manager;

    public Assets(){
        manager  = new AssetManager();
    }
    public void load(){
        manager.load("menu-button.png", Texture.class);
        manager.load("power-bar-bg.png", Texture.class);
        manager.load("info-bar-blue.png", Texture.class);
        manager.load("box.png", Texture.class);
        manager.load("box-active.png", Texture.class);
        manager.load("bacteria-red.png", Texture.class);
        manager.load("bacteria-green.png", Texture.class);
        manager.load("bacteria-blue.png", Texture.class);
        manager.load("bacteria-pink.png", Texture.class);
        manager.load("bacteria-orange.png", Texture.class);
        manager.load("background-goal.png", Texture.class);
        manager.load("antibiotic-white.png", Texture.class);
        manager.load("antibiotic-red.png", Texture.class);
        manager.load("antibiotic-green.png", Texture.class);
        manager.load("antibiotic-blue.png", Texture.class);
        manager.load("antibiotic-pink.png", Texture.class);
        manager.load("antibiotic-box-blue.png", Texture.class);
        manager.load("antibiotic-box-blue-active.png", Texture.class);
        manager.load("antibiotic-box-blue-inactive.png", Texture.class);
        manager.load("antibiotic-box-red.png", Texture.class);
        manager.load("antibiotic-box-red-active.png", Texture.class);
        manager.load("antibiotic-box-red-inactive.png", Texture.class);
        manager.load("antibiotic-box-pink.png", Texture.class);
        manager.load("antibiotic-box-pink-active.png", Texture.class);
        manager.load("antibiotic-box-pink-inactive.png", Texture.class);
        manager.load("antibiotic-box-green.png", Texture.class);
        manager.load("antibiotic-box-green-active.png", Texture.class);
        manager.load("antibiotic-box-green-inactive.png", Texture.class);
        manager.load("antibiotic-box-white.png", Texture.class);
        manager.load("antibiotic-box-white-active.png", Texture.class);
        manager.load("antibiotic-box-gray.png", Texture.class);
    }
    public void finishLoading(){
        manager.finishLoading();
    }

    public Object get(String string, Class<?> c){
        return manager.get(string, c);
    }

    public void dispose(){
        manager.dispose();
    }

    public void unload(){
        manager.unload("menu-button.png");
        manager.unload("power-bar-bg.png");
        manager.unload("info-bar-blue.png");
        manager.unload("box.png");
        manager.unload("box-active.png");
        manager.unload("bacteria-red.png");
        manager.unload("bacteria-green.png");
        manager.unload("bacteria-blue.png");
        manager.unload("bacteria-pink.png");
        manager.unload("bacteria-orange.png");
        manager.unload("background-goal.png");
        manager.unload("antibiotic-white.png");
        manager.unload("antibiotic-red.png");
        manager.unload("antibiotic-green.png");
        manager.unload("antibiotic-blue.png");
        manager.unload("antibiotic-pink.png");
        manager.unload("antibiotic-box-blue.png");
        manager.unload("antibiotic-box-blue-active.png");
        manager.unload("antibiotic-box-blue-inactive.png");
        manager.unload("antibiotic-box-red.png");
        manager.unload("antibiotic-box-red-active.png");
        manager.unload("antibiotic-box-red-inactive.png");
        manager.unload("antibiotic-box-pink.png");
        manager.unload("antibiotic-box-pink-active.png");
        manager.unload("antibiotic-box-pink-inactive.png");
        manager.unload("antibiotic-box-green.png");
        manager.unload("antibiotic-box-green-active.png");
        manager.unload("antibiotic-box-green-inactive.png");
        manager.unload("antibiotic-box-white.png");
        manager.unload("antibiotic-box-white-active.png");
        manager.unload("antibiotic-box-gray.png");
    }

}
