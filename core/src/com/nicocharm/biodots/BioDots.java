package com.nicocharm.biodots;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.Goal;
import com.nicocharm.biodots.screens.MainMenu;
import com.nicocharm.biodots.screens.PlayScreen;
import com.nicocharm.biodots.screens.ScreenCreator;

import java.awt.Menu;

public class BioDots extends Game {
	public SpriteBatch batch;
    public final float WIDTH = 1080;
    public final float HEIGHT = 1920;
    public Player player;

    private Array<PlayScreen> levels;
    private int currentLevel;

    private MainMenu menu;

	@Override
	public void create () {
		batch = new SpriteBatch();
		levels = new Array<PlayScreen>();
		currentLevel = 0;

		createLevels();

		player = new Player();
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		goToMenu();

	}

	public void goToMenu(){
		setScreen(menu);
		Gdx.input.setInputProcessor(menu);
	}

	public void goToFirstLevel(){
        player.setScreen(getLevel());
        Gdx.input.setInputProcessor(player);

        setScreen(getLevel());
        getLevel().initialize();
    }

	public void advance(){
		getLevel().dispose();

		currentLevel++;
		if(currentLevel>=levels.size){
			currentLevel = 0;
		}

		setScreen(getLevel());
		player.setScreen(getLevel());
		getLevel().initialize();
	}

	public void setLevel(int level){
		currentLevel = level;
		setScreen(getLevel());
		player.setScreen(getLevel());
	}

	public PlayScreen getLevel(){
		return levels.get(currentLevel);
	}

	private void createLevels(){ // acá está la lógica que define cada nivel

		// PASOS PARA DEFINIR UN NIVEL:
		//	CREO UN SCREEN CREATOR.
		//	LE SETEO LAS COSAS QUE QUIERO:
		//		DEFAULTS: pOfDying 1, 150s, 20 bacterias aleatorias,
		//					todos los botones
		//	CREO UNA SCREEN, PASANDO ESTA CLASE, EL SC Y UN GOAL
		//		INNER CLASS DE GOAL: NECESITA DESCRIPCIÓN DE GOAL
		//		NECESITA IMPLEMENTAR MÉTODOS met() Y failed().
		// 	AGREGO MI SCREEN A LEVELS.
		// 	LISTO!

		///////////////////////////////////////////////////////////

		// MENU

		menu = new MainMenu(this);




		// NIVEL 1

		ScreenCreator level1 = new ScreenCreator();
		level1.setInitial_pOfDying(0.8f);

		short[] types = new short[10];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<types.length*0.8f){
				type = Bacteria.BACTERIA_BLUE;
			} else {
				type = Bacteria.BACTERIA_GREEN;
			}
			types[i] = type;
		}
		level1.setBacteriaTypes(types);

		PlayScreen screen1 = new PlayScreen(this, level1,
				new Goal("Dejá vivas solo a las\n" +
						"bacterias verdes!"){

			@Override
			public boolean met() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() != Bacteria.BACTERIA_GREEN) return false;
				}
				return true;
			}

			@Override
			public boolean failed() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() == Bacteria.BACTERIA_GREEN) return false;
				}
				return true;
			}
		});
		levels.add(screen1);

		///////////////////////////////////////////////////////



	}

	@Override
	public void render () {
        super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

		for(Screen level: levels){
			level.dispose();
		}
	}

}
