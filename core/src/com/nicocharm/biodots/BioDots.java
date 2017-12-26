package com.nicocharm.biodots;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;
import com.nicocharm.biodots.screens.ScreenCreator;

public class BioDots extends Game {
	public SpriteBatch batch;
    public final float WIDTH = 1080;
    public final float HEIGHT = 1920;
    public Player player;

    private Array<PlayScreen> levels;
    private int currentLevel;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		ScreenCreator level1 = new ScreenCreator();
		level1.setInitial_pOfDying(0.8f);
		PlayScreen screen = new PlayScreen(this, level1);

		player = new Player(screen);
		Gdx.input.setInputProcessor(player);
		setScreen(screen);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		levels = new Array<PlayScreen>();
		levels.add(screen);
		currentLevel = 0;
		screen.initialize();
	}

	public void advance(){
		currentLevel++;
		if(currentLevel>=levels.size){
			currentLevel = 0;
		}
		setScreen(levels.get(currentLevel));
		levels.get(currentLevel).initialize();
	}

	public void setLevel(int level){
		currentLevel = level;
		setScreen(levels.get(currentLevel));
	}

	public PlayScreen getLevel(){
		return levels.get(currentLevel);
	}

	@Override
	public void render () {
        super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		super.dispose();
	}

}
