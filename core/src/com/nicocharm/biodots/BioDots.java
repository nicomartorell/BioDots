package com.nicocharm.biodots;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.AboutScreen;
import com.nicocharm.biodots.screens.ConfigScreen;
import com.nicocharm.biodots.screens.Goal;
import com.nicocharm.biodots.screens.HighScoresScreen;
import com.nicocharm.biodots.screens.LevelScreen;
import com.nicocharm.biodots.screens.LoadingScreen;
import com.nicocharm.biodots.screens.MainMenu;
import com.nicocharm.biodots.screens.PlayScreen;
import com.nicocharm.biodots.screens.ScreenCreator;
import com.nicocharm.biodots.screens.TutorialScreen;

import java.io.File;
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
	private AboutScreen aboutScreen;
	private boolean inAboutScreen;
	private LevelScreen levelScreen;
	private HighScoresScreen highScoresScreen;

	public boolean isInFreeGame() {
		return inFreeGame;
	}

	private boolean inFreeGame = false;


	public Assets manager;

	private LoadingScreen loadingScreen;

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	private boolean completed = false;

	private Array<Float> sineFunction;

	private ConfigScreen configScreen;

	public boolean getGSound() {
		return gSound;
	}

	public boolean getSound() {
		return sound;
	}

	public boolean getMusic() {
		return music;
	}

	private boolean gSound;
	private boolean sound;
	private boolean music;

	@Override
	public void create () {
		Gdx.input.setCatchBackKey(true);

        batch = new SpriteBatch();
        BioDots.fontManager = new FontManager();
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        sineFunction = new Array<Float>();
		double x = 0;
		while(x < 2*Math.PI){
			float y = 3;
			Float f = (float)((1f/(y - 1f))*(y - Math.cos(x)));
			sineFunction.add(f);
			x+=0.05;
		}

		loading = true;
		loadingScreen = new LoadingScreen(this);
		setScreen(loadingScreen);

        manager = new Assets();
        manager.load(); //tengo que chequear si terminó

        levels = new Array<PlayScreen>();
        currentLevel = 0;

        player = new Player();

        gSound = true;
        sound = true;
        music = true;

		Preferences preferences = Gdx.app.getPreferences("BioDots");
		if(!preferences.contains("lastLevel")){
			Gdx.app.log("tag", "it does not contain it");
			preferences.putInteger("lastLevel", 0);
			preferences.flush();
		} else {
			Gdx.app.log("tag", "it contains it");
			//int lastLevel = preferences.getInteger("lastLevel", 0);
		}

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
		if(inAboutScreen){
			inAboutScreen = false;
		}

		setScreen(menu);
		Gdx.input.setInputProcessor(menu);
	}

	public void goToFirstLevel(){
		inFreeGame = false;
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
    
    public void goToAboutScreen(){
    	played = false;
    	inFreeGame = false;
    	inAboutScreen = true;
    	playedLevel = false;
		aboutScreen.setAsInput();
		setScreen(aboutScreen);
	}

	public void goToLevelScreen(){
		played = false;
		inFreeGame = false;
		inAboutScreen = false;
		playedLevel = false;
		Gdx.input.setInputProcessor(levelScreen);
		setScreen(levelScreen);
	}

	public void goToConfigScreen(){
		played = false;
		inFreeGame = false;
		inAboutScreen = false;
		playedLevel = false;
		Gdx.input.setInputProcessor(configScreen);
		setScreen(configScreen);
	}

	public void goToHighScoresScreen(){
		played = false;
		inFreeGame = false;
		inAboutScreen = false;
		playedLevel = false;
		highScoresScreen.setAsInput();
		setScreen(highScoresScreen);
	}

	public void advance(){
    	lastLevel = false;
		playedLevel = true;
		inFreeGame = false;
		getLevel().dispose();

		currentLevel++;
		if(currentLevel==levels.size-1){
			lastLevel = true;
		}

		setScreen(getLevel());
		player.setScreen(getLevel());
		getLevel().initialize();
	}

	public void replay(){
		lastLevel = false;
		playedLevel = true;
		inFreeGame = false;
		getLevel().dispose();

		setScreen(getLevel());
		player.setScreen(getLevel());
		getLevel().initialize();
	}

	public void setLevel(int level){
		playedLevel = true;
		inFreeGame = false;
		currentLevel = level;

		if(currentLevel==levels.size-1){
			lastLevel = true;
		}

		player.setScreen(getLevel());
		Gdx.input.setInputProcessor(player);

		setScreen(getLevel());
		getLevel().initialize();
	}

	public PlayScreen getLevel(){
		return levels.get(currentLevel);
	}

	public void setMusic(boolean b){
		music = b;
	}

	public void setSound(boolean b){
		sound = b;
	}

	public void setGeneralSound(boolean b){
		gSound = b;
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
		
		// ABOUT
		
		aboutScreen = new AboutScreen(this);


        // FREE GAME

        ScreenCreator freeGame = new ScreenCreator();
        freeGame.setInitial_pOfDying(1.0f);
        freeGame.setInitialTime(0);
        freeGame.setnBacterias(10);
        freeGame.setMutationStDev(0.075f);
        freeGame.setFreeGame(true);

        String[] goals = {"Las bacterias no dejan\n" +
				"de aparecer!", "Matalas a todas en\n" +
				"el menor tiempo posible."};
        this.freeGame = new PlayScreen(this, freeGame, new Goal(goals){

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

		/*int nBacterias = 0;
		short[] types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}*/
		level.setBacteriaTypes(new short[0]);

		level.setInitialTime(100f);

		/*goals = new String[3];
		goals[0] = "Matá a las bacterias usando\n" +
				"el antibiótico blanco.";
		goals[1] = "Un toque corto en un\n" +
				"cuadrante pausa a las bacterias\n" +
				"que están sobre él.";
		goals[2] = "Un toque largo aplica el antibiótico.";*/

		TutorialScreen tscreen = new TutorialScreen(this, level, null);/*new Goal(goals){

			@Override
			public boolean met() {
				return getScreen().getBacterias().size < 1;
			}

			@Override
			public boolean failed() {
				return false;
			}
		});*/

		levels.add(tscreen);

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

		int nBacterias = 9;
		short[] types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(60f);

		goals = new String[2];
		goals[0] = "Matá a las bacterias!\nSi no te apurás se dividen!";
		goals[1] = "Cuando hay demasiadas\n" +
				"bacterias perdés!";

		PlayScreen screen = new PlayScreen(this, level, new Goal(goals){

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

		nBacterias = 14;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(60f);

		goals = new String[2];
		goals[0] = "El antibiotico azul es más\n" +
				"potente, pero tarda más en\n" +
				"activarse...";
		goals[1] = "TIP:\n" +
				"En el panel de abajo aparecen\n" +
				"los antibióticos activos (los no\n" +
				"disponibles en gris). Tocá el anti-\n" +
				"biótico para seleccionarlo!";

		screen = new PlayScreen(this, level, new Goal(goals){

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

        // NIVEL 3

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);
		level.setInitialTime(60f);

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

		goals = new String[2];
		goals[0] = "Dejá vivas solo a las\n" +
				"bacterias que sean verdes.";
		goals[1] = "TIP:\n" +
				"Cuando un antibiótico aparece\n" +
				"oscurecido, está desactivado. Luego\n" +
				"de un tiempo volverá a activarse.";

		screen = new PlayScreen(this, level,
				new Goal(goals){

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

		// NIVEL 4

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_WHITE;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_BLUE;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GREEN;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level.setButtonTypes(buttonTypes);

		nBacterias = 16;
		types = new short[nBacterias];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<13){
				type = Bacteria.BACTERIA_RED;
			} else {
				type = Bacteria.BACTERIA_ORANGE;
			}
			types[i] = type;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[3];
		goals[0] = "Dejá vivas a las\n" +
				"bacterias naranjas.";
		goals[1] = "El antibiótico verde es tan\n" +
				"bueno como el azul,\n" +
				"pero dura menos...";
		goals[2] = "TIP:\n" +
				"En la barra superior, el\n" +
				"porcentaje de bacterias que\n" +
				"mueren cambia según el\n" +
				"antibiótico elegido.";

		screen = new PlayScreen(this, level, new Goal(goals){

			@Override
			public boolean met() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() != Bacteria.BACTERIA_ORANGE) return false;
				}
				return true;
			}

			@Override
			public boolean failed() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() == Bacteria.BACTERIA_ORANGE) return false;
				}
				return true;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

		// NIVEL 5

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_WHITE;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_BLUE;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GREEN;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level.setButtonTypes(buttonTypes);

		nBacterias = 17;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(80f);

		goals = new String[1];
		goals[0] = "Quizás empiezan a ser\n" +
				"muchas bacterias...";

		screen = new PlayScreen(this, level, new Goal(goals){

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

		// NIVEL 6

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_WHITE;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_BLUE;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GREEN;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_PINK;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level.setButtonTypes(buttonTypes);

		nBacterias = 20;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(80f);

		goals = new String[1];
		goals[0] = "El antibiótico rosa\n" +
				"es el más efectivo hasta ahora,\n" +
				"pero su duración puede ser...\n" +
				"incómoda.";

		screen = new PlayScreen(this, level, new Goal(goals){

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

		// NIVEL 7

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_WHITE;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_BLUE;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GREEN;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_PINK;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level.setButtonTypes(buttonTypes);

		nBacterias = 15;
		types = new short[nBacterias];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<types.length*(3f/(float)nBacterias)){
				type = Bacteria.BACTERIA_RED;
			} else if(i<types.length*(9f/(float)nBacterias)){
				type = Bacteria.BACTERIA_PINK;
			} else {
				type = Bacteria.BACTERIA_BLUE;
			}
			types[i] = type;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[1];
		goals[0] = "Solo pueden vivir\n" +
				"las bacterias rojas.";

		screen = new PlayScreen(this, level, new Goal(goals){

			@Override
			public boolean met() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() != Bacteria.BACTERIA_RED) return false;
				}
				return true;
			}

			@Override
			public boolean failed() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() == Bacteria.BACTERIA_RED) return false;
				}
				return true;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

		// NIVEL 8

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);

		nBacterias = 24;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(100f);

		goals = new String[1];
		goals[0] = "El antibiótico rojo\n" +
				"es el más efectivo de todos.\n" +
				"Pero hay que esperarlo...";

		screen = new PlayScreen(this, level, new Goal(goals){

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

		// NIVEL 9

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.75f);
		level.setMutationStDev(0.075f);

		nBacterias = 25;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(150f);

		goals = new String[3];
		goals[0] = "Ya tenés todos\n" +
				"los antibióticos,\n" +
				"¿Jugamos en serio?";
		goals[1] = "Desde ahora las bacterias no\n" +
				"se mueren tan fácil.\n";
		goals[2] = "¡Suerte!";

		screen = new PlayScreen(this, level, new Goal(goals){

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


		// LEVEL SCREEN

		levelScreen = new LevelScreen(this);


		// CONFIG SCREEN

		configScreen = new ConfigScreen(this);

		// HIGH SCORES SCREEN

		highScoresScreen = new HighScoresScreen(this);
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
		aboutScreen.dispose();
		levelScreen.dispose();
		configScreen.dispose();
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

	public Array<PlayScreen> getLevels() {
		return levels;
	}

	public LevelScreen getLevelScreen() {
		return levelScreen;
	}

	public void resetProcess(){
		Preferences preferences = Gdx.app.getPreferences("BioDots");
		preferences.putInteger("lastLevel", 0);
		preferences.flush();
		levelScreen.dispose();
		levelScreen = new LevelScreen(this);
		completed = false;
	}

	public void advanceAll(){
		Preferences preferences = Gdx.app.getPreferences("BioDots");
		preferences.putInteger("lastLevel", levels.size-1);
		preferences.flush();
		levelScreen.dispose();
		completed = true;
		levelScreen = new LevelScreen(this);
	}

	public Array<Float> getSineFunction() {
		return sineFunction;
	}

	public int getLevelNumber() {
		return currentLevel;
	}
}
