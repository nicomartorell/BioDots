package com.nicocharm.biodots;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nicocharm.biodots.screens.PlayScreen;


public class MenuButton extends Button {
    public static void resetID() {
        MenuButton.ID = 0;
    }

    private static int ID = 0;
    public int id;

    public MenuButton(BioDots game, float x, float y, String text, float scale) {
        super(game, x, y, "menu-button-transparent.png", text, scale);
        id = MenuButton.ID;
        MenuButton.ID++;
    }

}
