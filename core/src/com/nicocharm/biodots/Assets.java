package com.nicocharm.biodots;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

public class Assets {
    private AssetManager manager;

    public Assets(){
        manager  = new AssetManager();
    }
    public void load(){

        manager.load("title.png", Texture.class);

        //manager.load("small-textures.pack", TextureAtlas.class);
        Gdx.app.log("tag", "loading assets");
        manager.load("menu-button.png", Texture.class);
        manager.load("menu-button-transparent.png", Texture.class);
        manager.load("power-bar-bg.png", Texture.class);
        manager.load("info-bar-blue.png", Texture.class);
        manager.load("box.png", Texture.class);
        manager.load("box-active.png", Texture.class);

        manager.load("bacteria-red.pack", TextureAtlas.class);
        manager.load("bacteria-green.pack", TextureAtlas.class);
        manager.load("bacteria-blue.pack", TextureAtlas.class);
        manager.load("bacteria-pink.pack", TextureAtlas.class);
        manager.load("bacteria-orange.pack", TextureAtlas.class);

        manager.load("background-goal.png", Texture.class);

        manager.load("antibiotic-white.pack", TextureAtlas.class);
        manager.load("antibiotic-red.pack", TextureAtlas.class);
        manager.load("antibiotic-green.pack", TextureAtlas.class);
        manager.load("antibiotic-blue.pack", TextureAtlas.class);
        manager.load("antibiotic-pink.pack", TextureAtlas.class);

        manager.load("antibiotic-box-gray.png", Texture.class);
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
        manager.load("pause-button.png", Texture.class);

        manager.load("background-play.png", Texture.class);
        manager.load("background-lose.png", Texture.class);
        manager.load("background-win.png", Texture.class);
        manager.load("level-border.png", Texture.class);
        manager.load("level-border-locked.png", Texture.class);

        manager.load("config-icon.png", Texture.class);
        manager.load("button-mask.png", Texture.class);

        manager.load("frozen.ogg", Sound.class);
        manager.load("menu-music.ogg", Music.class);
        manager.load("antibiotic.ogg", Sound.class);
        manager.load("wrong-select.ogg", Sound.class);
        manager.load("select.ogg", Sound.class);
        manager.load("kill.ogg", Sound.class);
        manager.load("play-song.ogg", Music.class);
        //manager.load("division.ogg", Sound.class);

        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        FreetypeFontLoader.FreeTypeFontLoaderParameter font1 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        font1.fontFileName = "Roboto-Regular.ttf";
        font1.fontParameters.size = 80;
        manager.load("Roboto-Regular.ttf", BitmapFont.class, font1);

        FreetypeFontLoader.FreeTypeFontLoaderParameter font2 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        font2.fontFileName = "GloriaHallelujah.ttf";
        font2.fontParameters.size = 240;
        manager.load("GloriaHallelujah.ttf", BitmapFont.class, font2);

        FreetypeFontLoader.FreeTypeFontLoaderParameter font3 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        font3.fontFileName = "Roboto-Bold.ttf";
        font3.fontParameters.size = 100;
        manager.load("Roboto-Bold.ttf", BitmapFont.class, font3);

        /*FreetypeFontLoader.FreeTypeFontLoaderParameter font4 = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        font4.fontFileName = "Roboto-Regular.ttf";
        font4.fontParameters.size = 80;
        manager.load("Roboto-Regular.ttf", BitmapFont.class, font4);*/

        Gdx.app.log("tag", "finishing loading assets");
    }
    public void finishLoading(){
        manager.finishLoading();
    }
    public boolean update(){
        return manager.update();
    }

    public Object get(String string, Class<?> c){
        return manager.get(string, c);
    }

    public void dispose(){
        Gdx.app.log("tag", "Disposing assets");
        manager.dispose();
    }

    public void unload(){
        Gdx.app.log("tag", "Unloading assets");
        manager.unload("title.png");
        manager.unload("menu-button.png");
        manager.unload("menu-button-transparent.png");
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
        manager.unload("background-play.png");
        manager.unload("background-lose.png");
        manager.unload("background-win.png");
        manager.unload("level-border.png");
        manager.unload("level-border-locked.png");
        manager.unload("config-icon.png");
        manager.unload("button-mask.png");
        manager.unload("frozen.ogg");
        manager.unload("menu-music.ogg");
        manager.unload("antibiotic.ogg");
        manager.unload("wrong-select.ogg");
        manager.unload("select.ogg");
        manager.unload("kill.ogg");
        manager.unload("play-song.ogg");
        //manager.unload("division.ogg");

        manager.unload("GloriaHallelujah.ttf");
        manager.unload("Roboto-Bold.ttf");
        manager.unload("Roboto-Regular.ttf");
    }

    public void playSound(final Music s, final float delay, final Runnable r){
        s.play();
        Thread t = new Thread(new Runnable(){

            @Override
            public void run() {
                while(!s.isPlaying() || s.getPosition()<delay){
                }
                r.run();
            }
        });
        t.start();
    }

}
