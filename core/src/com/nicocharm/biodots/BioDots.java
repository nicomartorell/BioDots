package com.nicocharm.biodots;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.Goal;
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
		levels = new Array<PlayScreen>();
		currentLevel = 0;

		createLevels();

		player = new Player(getLevel());
		Gdx.input.setInputProcessor(player);

		setScreen(getLevel());
		Gdx.app.setLogLevel(Application.LOG_DEBUG);



		getLevel().initialize();
	}

	public void advance(){
		getLevel().dispose();

		currentLevel++;
		if(currentLevel>=levels.size){
			currentLevel = 0;
		}

		setScreen(getLevel());
		getLevel().initialize();
	}

	public void setLevel(int level){
		currentLevel = level;
		setScreen(levels.get(currentLevel));
	}

	public PlayScreen getLevel(){
		return levels.get(currentLevel);
	}

	private void createLevels(){
		ScreenCreator level1 = new ScreenCreator();
		level1.setInitial_pOfDying(0.8f);
		PlayScreen screen1 = new PlayScreen(this, level1, new Goal("Matá a todas las bacterias\nantes de que se acabe el tiempo!"){

			//trying out the abstract class goal

			@Override
			public boolean met(Array<Bacteria> bacteria) {
				for(Bacteria b: bacteria){
					if(b.getType() != Bacteria.BACTERIA_GREEN) return false;
				}
				return true;
			}
		});
		levels.add(screen1);
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
