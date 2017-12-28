package com.nicocharm.biodots;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FontManager {

    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private Map<String, BitmapFont> fonts;

    public FontManager(){
        FreeTypeFontGenerator.setMaxTextureSize(FreeTypeFontGenerator.NO_MAXIMUM);
        fonts = new HashMap<String, BitmapFont>();
    }

    public BitmapFont get(String path, int size){
        String key = path + "-" + Integer.valueOf(size).toString();
        if(fonts.containsKey(key)) return fonts.get(key);

        generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);
        fonts.put(key, font);
        generator.dispose();

        return font;
    }
}
