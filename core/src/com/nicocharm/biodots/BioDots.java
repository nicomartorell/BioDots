package com.nicocharm.biodots;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.Goal;
import com.nicocharm.biodots.screens.LoadingScreen;
import com.nicocharm.biodots.screens.MainMenu;
import com.nicocharm.biodots.screens.PlayScreen;
import com.nicocharm.biodots.screens.ScreenCreator;

import java.awt.Menu;
import java.util.Random;

public class BioDots extends Game {
    public static FontManager fontManager;

	public SpriteBatch batch;
    public final float WIDTH = 1080;
    public final float HEIGHT = 1920;
    public Player player;

    private PlayScreen freeGame;

    private Array<PlayScreen> levels;
    private int currentLevel;

    private MainMenu menu;

    public boolean lastLevel = false;
    public boolean toMenu = false;
    private boolean played = false;
    private boolean playedLevel = false;
    private boolean loading = false;

	public boolean isInFreeGame() {
		return inFreeGame;
	}

	private boolean inFreeGame = false;


	public Assets manager;

	private LoadingScreen loadingScreen;

	@Override
	public void create () {
        batch = new SpriteBatch();
        BioDots.fontManager = new FontManager();
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

		loading = true;
		loadingScreen = new LoadingScreen(this);
		setScreen(loadingScreen);

        manager = new Assets();
        manager.load(); //tengo que chequear si terminó

        levels = new Array<PlayScreen>();
        currentLevel = 0;


        player = new Player();




	}

	public void goToMenu(){
		lastLevel = false;
		toMenu = false;

	    if(playedLevel){
			Gdx.app.log("tag", "Played Level disposed!");
	        getLevel().dispose();
	        playedLevel = false;
        }
        if(played){
			Gdx.app.log("tag", "Free Game disposed!");
	    	freeGame.dispose();
	    	played = false;
		}
		if(loading){
			Gdx.app.log("tag", "Loading screen disposed!");
			loadingScreen.dispose();
			loading = false;
		}
		setScreen(menu);
		Gdx.input.setInputProcessor(menu);
	}

	public void goToFirstLevel(){
		lastLevel = false;
		currentLevel = 0;
	    playedLevel = true;
        player.setScreen(getLevel());
        Gdx.input.setInputProcessor(player);

        setScreen(getLevel());
        getLevel().initialize();
    }

    public void goToFreeGame(){
        played = true;
        inFreeGame = true;
        player.setScreen(freeGame);
        Gdx.input.setInputProcessor(player);

        setScreen(freeGame);
        freeGame.initialize();
    }

	public void advance(){
    	lastLevel = false;
		playedLevel = true;
		getLevel().dispose();

		currentLevel++;
		if(currentLevel==levels.size-1){
			lastLevel = true;
		}

		setScreen(getLevel());
		player.setScreen(getLevel());
		getLevel().initialize();
	}

	public void setLevel(int level){
        playedLevel = true;

        getLevel().dispose();
		currentLevel = level;

		if(currentLevel==levels.size-1){
			lastLevel = true;
		}

		setScreen(getLevel());
		player.setScreen(getLevel());
	}

	public PlayScreen getLevel(){
		return levels.get(currentLevel);
	}

	private void createLevels(){ // acá está la lógica que define cada nivel

		Random random = new Random();

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

        // FREE GAME

        ScreenCreator freeGame = new ScreenCreator();
        freeGame.setInitial_pOfDying(0.7f);
        this.freeGame = new PlayScreen(this, freeGame, new Goal("Matá a todas las bacterias\n" +
                "antes de que se acabe el tiempo!"){

            @Override
            public boolean met() {
                return getScreen().getBacterias().size < 1;
            }

            @Override
            public boolean failed() {
                return false;
            }
        });

		///////////////////////////////////////////////////////////

		// NIVEL 0

		ScreenCreator level = new ScreenCreator();
		level.setInitial_pOfDying(1f);

		short[] buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_WHITE;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level.setButtonTypes(buttonTypes);

		int nBacterias = 3;
		short[] types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(50f);

		PlayScreen screen = new PlayScreen(this, level, new Goal("Matá a la bacteria usando\nel antibiótico blanco.\n\n" +
				"Un toque corto en un\ncuadrante pausa a las bacterias\nque están sobre él.\n\n" +
				"Un toque largo aplica el antibiótico."){

			@Override
			public boolean met() {
				return getScreen().getBacterias().size < 1;
			}

			@Override
			public boolean failed() {
				return false;
			}
		});

		levels.add(screen);

        ///////////////////////////////////////////////////////////

		// NIVEL 1

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_WHITE;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level.setButtonTypes(buttonTypes);

		nBacterias = 6;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(50f);

		screen = new PlayScreen(this, level, new Goal("Matá a las bacterias!\nSi no te apurás se dividen!"){

			@Override
			public boolean met() {
				return getScreen().getBacterias().size < 1;
			}

			@Override
			public boolean failed() {
				return false;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

		// NIVEL 2

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_WHITE;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_BLUE;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level.setButtonTypes(buttonTypes);

		nBacterias = 9;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(60f);

		screen = new PlayScreen(this, level, new Goal("El antibiotico azul es más\n" +
				"potente, pero tarda más en\n" +
				"activarse..."){

			@Override
			public boolean met() {
				return getScreen().getBacterias().size < 1;
			}

			@Override
			public boolean failed() {
				return false;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

        // NIVEL 10

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_WHITE;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_BLUE;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level.setButtonTypes(buttonTypes);

		types = new short[6];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<types.length*(4/6f)){
				type = Bacteria.BACTERIA_BLUE;
			} else {
				type = Bacteria.BACTERIA_GREEN;
			}
			types[i] = type;
		}
		level.setBacteriaTypes(types);

		screen = new PlayScreen(this, level,
				new Goal("Dejá vivas solo a las\n" +
						"bacterias que sean verdes."){

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
		levels.add(screen);

        ///////////////////////////////////////////////////////////



	}

	@Override
	public void render () {
        if(loading && manager.update()){
            Gdx.app.log("tag", "running my thread");
            createLevels();
            loading = false;
            toMenu = true;
            Gdx.app.log("tag", "Finishing thread. To menu: " + toMenu);
        }

        if(toMenu){
        	goToMenu();
        	return;
		}
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		menu.dispose();
		if(playedLevel){
			getLevel().dispose();
		}
		if(played){
			freeGame.dispose();
		}

		manager.unload();
		manager.dispose();
		fontManager = null;
	}

    public void end() {
	    Gdx.app.exit();
    }

	public void setToMenu(boolean toMenu) {
		this.toMenu = toMenu;
	}
}
