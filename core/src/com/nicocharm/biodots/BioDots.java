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
	        getLevel().dispose();
	        playedLevel = false;
        }
        if(played){
	    	freeGame.dispose();
	    	played = false;
		}
		if(loading){
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

		ScreenCreator level0 = new ScreenCreator();
		level0.setInitial_pOfDying(1f);

		short[] buttonTypes0 = new short[5];
		buttonTypes0[0] = Antibiotic.ANTIBIOTIC_WHITE;
		buttonTypes0[1] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes0[2] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes0[3] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes0[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level0.setButtonTypes(buttonTypes0);

		int nBacterias0 = 3;
		short[] types0 = new short[nBacterias0];
		Random random = new Random();
		for(int i = 0; i < nBacterias0; i++){
			types0[i] = (short)(random.nextInt(5) + 1);
		}
		level0.setBacteriaTypes(types0);

		level0.setInitialTime(50f);

		PlayScreen screen0 = new PlayScreen(this, level0, new Goal("Matá a la bacteria usando\nel antibiótico blanco.\n\n" +
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

		levels.add(screen0);

        ///////////////////////////////////////////////////////////

		// NIVEL 1

		ScreenCreator level1 = new ScreenCreator();
		level1.setInitial_pOfDying(1f);

		short[] buttonTypes1 = new short[5];
		buttonTypes1[0] = Antibiotic.ANTIBIOTIC_WHITE;
		buttonTypes1[1] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes1[2] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes1[3] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes1[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level1.setButtonTypes(buttonTypes1);

		int nBacterias1 = 6;
		short[] types1 = new short[nBacterias1];
		for(int i = 0; i < nBacterias1; i++){
			types1[i] = (short)(random.nextInt(5) + 1);
		}
		level1.setBacteriaTypes(types1);

		level1.setInitialTime(50f);

		PlayScreen screen1 = new PlayScreen(this, level1, new Goal("Matá a las bacterias!\nSi no te apurás se dividen!"){

			@Override
			public boolean met() {
				return getScreen().getBacterias().size < 1;
			}

			@Override
			public boolean failed() {
				return false;
			}
		});

		levels.add(screen1);

		///////////////////////////////////////////////////////////

        // NIVEL 10

		ScreenCreator level10 = new ScreenCreator();
		level10.setInitial_pOfDying(0.8f);

		short[] types10 = new short[10];
		for(int i = 0; i < types10.length; i++){
			short type;
			if(i<types10.length*0.8f){
				type = Bacteria.BACTERIA_BLUE;
			} else {
				type = Bacteria.BACTERIA_GREEN;
			}
			types10[i] = type;
		}
		level10.setBacteriaTypes(types10);

		PlayScreen screen10 = new PlayScreen(this, level10,
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
		levels.add(screen10);

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
