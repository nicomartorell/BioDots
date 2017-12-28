package com.nicocharm.biodots;

import com.badlogic.gdx.graphics.Texture;
import com.nicocharm.biodots.screens.PlayScreen;

public class MenuButton extends Button {

    public MenuButton(PlayScreen screen, float x, float y, String text, float scale) {
        super(screen, x, y, new Texture("menu-button.png"), text, scale);
    }

}
