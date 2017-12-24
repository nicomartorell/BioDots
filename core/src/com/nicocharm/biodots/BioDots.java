package com.nicocharm.biodots;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicocharm.biodots.screens.PlayScreen;

public class BioDots extends Game {
	public SpriteBatch batch;
    public final float WIDTH = 1080;
    public final float HEIGHT = 1920;
    public Player player;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		// sería una buena idea separar la construcción de playscreen
		// de su init, para que no se inicialice toodo 2 segundos antes
		// de que empiece el juego.
		PlayScreen screen = new PlayScreen(this);
		player = new Player(screen);
		Gdx.input.setInputProcessor(player);
		setScreen(screen);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	@Override
	public void render () {
        super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

}
