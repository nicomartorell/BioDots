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

		Preferences preferences = Gdx.app.getPreferences("BioDots");
		if(!preferences.contains("lastLevel")){
			Gdx.app.log("tag", "it does not contain it");
			preferences.putInteger("lastLevel", 0);
			preferences.flush();
		} else {
			Gdx.app.log("tag", "it contains it");
			//int lastLevel = preferences.getInteger("lastLevel", 0);
		}

		if(preferences.contains("sound")){
			sound = preferences.getBoolean("sound", true);
		} else {
			preferences.putBoolean("sound", true);
			sound = true;
		}

		if(preferences.contains("music")){
			music = preferences.getBoolean("music", true);
		} else {
			preferences.putBoolean("music", true);
			music = true;
		}
		preferences.flush();
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
		if(currentLevel==levels.size-1){
			lastLevel = true;
		}

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
        freeGame.setnBacterias(15);
        freeGame.setMutationStDev(0.033f);
        freeGame.setpOfRep(0.0005);
        freeGame.setFreeGame(true);
        freeGame.setInterval(2f);

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
        level.setpOfRep(0.0008);

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

		level.setInitialTime(120f);

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
		level.setpOfRep(0.0007);

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

		level.setInitialTime(120f);

		goals = new String[2];
		goals[0] = "El antibiotico azul es más\n" +
				"potente, pero tarda más en\n" +
				"activarse...";
		goals[1] = "[RED]TIP:[]\n" +
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
		level.setInitialTime(120f);
        level.setpOfRep(0.0010);

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
		goals[1] = "[RED]TIP:[]\n" +
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
        level.setpOfRep(0.0008);

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
		goals[2] = "[RED]TIP:[]\n" +
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
        level.setpOfRep(0.0008);

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

		goals = new String[2];
		goals[0] = "Quizás empiezan a ser\n" +
				"muchas bacterias...";
		goals[1] = "[RED]TIP:[]\n" +
				"Las bacterias se adaptan\n" +
				"un poco a los antibióticos\n" +
				"cada vez que se reproducen.";

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

		///////////////////////////////////////////////////////////

		// NIVEL 10

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.75f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(3);
		level.setpOfRep(0.003);

		nBacterias = 25;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(150f);

		goals = new String[2];
		goals[0] = "Ahora congelás hasta\n" +
				"tres cuadrantes...\n";
		goals[1] = "...pero las bacterias\n" +
				"están con más ganas\n" +
				"de dividirse.";

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

		// NIVEL 11

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.9f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(3);
		level.setpOfRep(0.005);

		nBacterias = 20;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(90f);

		goals = new String[1];
		goals[0] = "Rápido... se\n" +
				"reproducen como locas.\n";

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

		// NIVEL 12

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(3);
		level.setpOfRep(0.0018);

		nBacterias = 55;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(180f);

		goals = new String[2];
		goals[0] = "¿Y si empezás a punto\n" +
				"de perder?\n";
		goals[1] = "Matalas antes de que\n" +
				"se reproduzcan!\n";

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

		// NIVEL 13

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(3);
		level.setpOfRep(0.002);

		nBacterias = 30;
		types = new short[nBacterias];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<22){
				type = Bacteria.BACTERIA_GREEN;
			} else if(i<29){
				type = Bacteria.BACTERIA_RED;
			} else {
				type = Bacteria.BACTERIA_BLUE;
			}
			types[i] = type;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[2];
		goals[0] = "¿Podrás dejar vivas\n" +
				"solo a las celestes?";
		goals[1] = "Acordate de\n" +
				"no perder :)";

		screen = new PlayScreen(this, level, new Goal(goals){

			@Override
			public boolean met() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() != Bacteria.BACTERIA_BLUE) return false;
				}
				return true;
			}

			@Override
			public boolean failed() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() == Bacteria.BACTERIA_BLUE) return false;
				}
				return true;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

		// NIVEL 14

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.2f);
		level.setMaxBlocks(3);
		level.setpOfRep(0.002);

		nBacterias = 20;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[2];
		goals[0] = "¿Qué hacés cuando las\n" +
				"bacterias se adaptan\n" +
				"muy rápido?";
		goals[1] = "Te apurás...\n" +
				"o perdés.";

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

		// NIVEL 15

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(3);
		level.setpOfRep(0.004);

		nBacterias = 1;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(90f);

		goals = new String[2];
		goals[0] = "Quizás querés\n" +
				"menos bacterias...";
		goals[1] = "¿Podrás ganar habiendo\n" +
				"hecho al menos 3000 puntos?";

		screen = new PlayScreen(this, level, new Goal(goals){

			@Override
			public boolean met() {
				return getScreen().getBacterias().size < 1 && getScreen().getInfobar().getPoints() >= 3000;
			}

			@Override
			public boolean failed() {
				return getScreen().getBacterias().size < 1 && getScreen().getInfobar().getPoints() < 3000;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

		// NIVEL 16

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(3);
		level.setpOfRep(0.0002);

		nBacterias = 50;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_WHITE;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level.setButtonTypes(buttonTypes);

		level.setInitialTime(90f);

		goals = new String[1];
		goals[0] = "Estas casi no se reproducen.\n" +
				"¿Te arreglás con un\n" +
				"solo antibiótico?";

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

		// NIVEL 17

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(3);
		level.setpOfRep(0.0005);

		nBacterias = 15;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_BLUE;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level.setButtonTypes(buttonTypes);

		level.setInitialTime(90f);

		goals = new String[1];
		goals[0] = "¿Y si el único anti-\n" +
				"biótico que tenés\n" +
				"tarda en cargarse?";

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

		// NIVEL 18

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(3);
		level.setpOfRep(0.0005);

		nBacterias = 25;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_RED;
		level.setButtonTypes(buttonTypes);

		level.setInitialTime(150f);

		goals = new String[1];
		goals[0] = "¿Y si tarda un\n" +
				"montón en cargarse?";

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

		// NIVEL 19

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(3);
		level.setpOfRep(0.0005);

		nBacterias = 15;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_PINK;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level.setButtonTypes(buttonTypes);

		level.setInitialTime(100f);

		goals = new String[1];
		goals[0] = "Este nivel puede ser\n" +
				"un poco molesto...";

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

		// NIVEL 20

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(0);
		level.setpOfRep(0.001);

		nBacterias = 15;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(90f);

		goals = new String[1];
		goals[0] = "Ahora que estamos entrenados,\n" +
				"¿Podés ganar sin\n" +
				"congelar cuadrantes?";

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

		// NIVEL 21

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(0);
		level.setpOfRep(0.001);

		nBacterias = 25;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(90f);

		goals = new String[1];
		goals[0] = "Se va poniendo\n" +
				"difícil, no?";

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

		// NIVEL 22

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(0);
		level.setpOfRep(0.002);

		nBacterias = 25;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(60f);

		goals = new String[2];
		goals[0] = "Para ganar y no perder,\n" +
				"10,000 puntos hay que hacer.";
		goals[1] = "Qué se yo, yo hago juegos,\n" +
				"no soy poeta. Es lo que hay.";

		screen = new PlayScreen(this, level, new Goal(goals){

			@Override
			public boolean met() {
				return getScreen().getInfobar().getPoints() >= 10000;
			}

			@Override
			public boolean failed() {
				return false;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

		// NIVEL 23

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(0);
		level.setpOfRep(0.0007);

		nBacterias = 30;
		types = new short[nBacterias];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<4){
				type = Bacteria.BACTERIA_PINK;
			} else if(i<20){
				type = Bacteria.BACTERIA_GREEN;
			} else {
				type = Bacteria.BACTERIA_BLUE;
			}
			types[i] = type;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(90f);

		goals = new String[1];
		goals[0] = "¿Podés dejar vivas\n" +
				"sólo a las rosas?";

		screen = new PlayScreen(this, level, new Goal(goals){

			@Override
			public boolean met() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() != Bacteria.BACTERIA_PINK) return false;
				}
				return true;
			}

			@Override
			public boolean failed() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() == Bacteria.BACTERIA_PINK) return false;
				}
				return true;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

		// NIVEL 24

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.0011);

		nBacterias = 10;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(20f);

		goals = new String[2];
		goals[0] = "Bueno, tranqui, podés\n" +
				"congelar cuadrantes de nuevo.";
		goals[1] = "Espero que también\n" +
				"puedas jugar rápido.";

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

		// NIVEL 25

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.0005);

		nBacterias = 16;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(20f);

		goals = new String[1];
		goals[0] = "¡Rápido, rápido!";


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

		// NIVEL 26

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.005);

		nBacterias = 6;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(30f);

		goals = new String[1];
		goals[0] = "Pestaneás y se\n" +
				"reproducen...";


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

		// NIVEL 27

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.0011);

		nBacterias = 12;
		types = new short[nBacterias];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<2){
				type = Bacteria.BACTERIA_RED;
			} else if(i<10){
				type = Bacteria.BACTERIA_ORANGE;
			} else {
				type = Bacteria.BACTERIA_PINK;
			}
			types[i] = type;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(30f);

		goals = new String[1];
		goals[0] = "¡Qué sólo vivan\n" +
				"las bacterias rojas!";

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

		// NIVEL 28

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0);

		nBacterias = 25;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(30f);

		goals = new String[1];
		goals[0] = "Esto está complicado...";


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

		// NIVEL 29

		level = new ScreenCreator();
		level.setInitial_pOfDying(1f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.0003);

		nBacterias = 30;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(30f);

		goals = new String[1];
		goals[0] = "Muy complicado.";


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

		// NIVEL 30

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.002);

		nBacterias = 45;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = Bacteria.BACTERIA_BLUE;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(90f);

		goals = new String[1];
		goals[0] = "Jugá este nivel para\n" +
				"relajarte un poco :)";


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

		// NIVEL 31

		level = new ScreenCreator();
		level.setFreeGame(true);
		level.setInterval(4f);
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.0011);

		nBacterias = 23;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[2];
		goals[0] = "¿Ya estás relajado?\n" +
				"Probemos algo distinto.";
		goals[1] = "Las bacterias no dejan de\n" +
				"aparecer. ¿Podés matarlas a\n" +
				"todas antes de que se\n" +
				"acabe el tiempo?";


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

		// NIVEL 32

		level = new ScreenCreator();
		level.setFreeGame(true);
		level.setInterval(3f);
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.0011);

		nBacterias = 25;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[1];
		goals[0] = "¿Podés llegar a 15 mil\n" +
				"puntos antes de que se\n" +
				"acabe el tiempo?";


		screen = new PlayScreen(this, level, new Goal(goals){

			@Override
			public boolean met() {
				return getScreen().getInfobar().getPoints() >= 15000;
			}

			@Override
			public boolean failed() {
				return false;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

		// NIVEL 33

		level = new ScreenCreator();
		level.setFreeGame(true);
		level.setInterval(4f);
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.0011);

		nBacterias = 20;
		types = new short[nBacterias];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<5){
				type = Bacteria.BACTERIA_ORANGE;
			} else {
				type = (short)(random.nextInt(5) + 1);
			}
			types[i] = type;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[1];
		goals[0] = "Que sólo queden vivas\n" +
				"bacterias naranjas.";


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

		// NIVEL 34

		level = new ScreenCreator();
		level.setFreeGame(true);
		level.setInterval(2f);
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.05f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.0011);

		nBacterias = 15;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[1];
		goals[0] = "¿Y si empiezan a aparecer\n" +
				"un poco más rápido?";



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

		// NIVEL 35

		level = new ScreenCreator();
		level.setFreeGame(true);
		level.setInterval(2f);
		level.setInitial_pOfDying(1f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.003);

		nBacterias = 15;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[2];
		goals[0] = "...y además se\n" +
				"reproducen más?";
		goals[1] = "Tratá de llegar a\n" +
				"20 mil puntos.";

		screen = new PlayScreen(this, level, new Goal(goals){

			@Override
			public boolean met() {
				return getScreen().getInfobar().getPoints() >= 20000;
			}

			@Override
			public boolean failed() {
				return false;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

		// NIVEL 36

		level = new ScreenCreator();
		level.setFreeGame(true);
		level.setInterval(2f);
		level.setInitial_pOfDying(1f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.003);

		nBacterias = 15;
		types = new short[nBacterias];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<5){
				type = Bacteria.BACTERIA_GREEN;
			} else {
				type = (short)(random.nextInt(5) + 1);
			}
			types[i] = type;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(50f);

		goals = new String[1];
		goals[0] = "...y si ahora encima tenés que\n" +
				"dejar vivas sólo a las verdes?";

		screen = new PlayScreen(this, level, new Goal(goals){

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

		// NIVEL 37

		level = new ScreenCreator();
		level.setFreeGame(true);
		level.setInterval(2f);
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(3);
		level.setpOfRep(0.003);

		nBacterias = 20;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(200f);

		goals = new String[1];
		goals[0] = "No te voy a mentir,\n" +
				"este nivel es difícil.\n" +
				"Hacé lo que puedas.";

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

		// NIVEL 38

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.002);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_GREEN;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_GREEN;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GREEN;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_GREEN;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_GREEN;
		level.setButtonTypes(buttonTypes);

		nBacterias = 20;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = Bacteria.BACTERIA_GREEN;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(50f);

		goals = new String[1];
		goals[0] = "Se me están acabando las\n" +
				"ideas, así que esto puede\n" +
				"parecerte un poco raro.";

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

		// NIVEL 39

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.65f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.002);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_BLUE;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_BLUE;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_BLUE;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_BLUE;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_BLUE;
		level.setButtonTypes(buttonTypes);

		nBacterias = 20;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = Bacteria.BACTERIA_BLUE;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[1];
		goals[0] = "Al menos no podés decir\n" +
				"que no soy original, no?";

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

		// NIVEL 40

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.002);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_PINK;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_PINK;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_PINK;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_PINK;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_PINK;
		level.setButtonTypes(buttonTypes);

		nBacterias = 25;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = Bacteria.BACTERIA_PINK;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(70f);

		goals = new String[2];
		goals[0] = "Frustrante, quizás.\n" +
				"Pero ¿divertido?";
		goals[1] = "No, no mucho.";

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

		// NIVEL 41

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.001);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_RED;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_RED;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_RED;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_RED;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_RED;
		level.setButtonTypes(buttonTypes);

		nBacterias = 15;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = Bacteria.BACTERIA_RED;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(40f);

		goals = new String[1];
		goals[0] = "¿Tenías ganas de esperar?";

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

		// NIVEL 42

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.05f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.002);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_WHITE;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_RED;
		level.setButtonTypes(buttonTypes);

		nBacterias = 25;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = (short)(random.nextInt(5) + 1);
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(125f);

		goals = new String[1];
		goals[0] = "¿Hacemos un nivel difícil\n" +
				"con recursos mínimos?";

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

		// NIVEL 43

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.05f);
		level.setMaxBlocks(1);
		level.setpOfRep(0.002);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_BLUE;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_PINK;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_BLUE;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_PINK;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_BLUE;
		level.setButtonTypes(buttonTypes);

		nBacterias = 25;
		types = new short[nBacterias];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<15){
				type = Bacteria.BACTERIA_BLUE;
			} else {
				type = Bacteria.BACTERIA_PINK;
			}
			types[i] = type;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[1];
		goals[0] = "Una combinación un poco\n" +
				"rara, debo admitir.";

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

		// NIVEL 44

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(10);
		level.setpOfRep(0.01);

		nBacterias = 55;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = Bacteria.BACTERIA_ORANGE;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[1];
		goals[0] = "¿Hace frío?";

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

		// NIVEL 45

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(3);
		level.setpOfRep(0.003);

		nBacterias = 30;
		types = new short[nBacterias];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<2){
				type = Bacteria.BACTERIA_PINK;
			} else {
				type = (short)(random.nextInt(5) + 1);
			}
			types[i] = type;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[2];
		goals[0] = "Fijate que ya a esta altura\n" +
				"ni siquiera tengo ganas de\n" +
				"decirte qué tenés que hacer.";
		goals[1] = "No creo que haga falta, ¿no?";

		screen = new PlayScreen(this, level, new Goal(goals){

			@Override
			public boolean met() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() != Bacteria.BACTERIA_PINK) return false;
				}
				return true;
			}

			@Override
			public boolean failed() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() == Bacteria.BACTERIA_PINK) return false;
				}
				return true;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

		// NIVEL 46

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(3);
		level.setpOfRep(0.002);

		nBacterias = 30;
		types = new short[nBacterias];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<10){
				type = Bacteria.BACTERIA_BLUE;
			} else if(i<20) {
				type = Bacteria.BACTERIA_ORANGE;
			} else  {
				type = Bacteria.BACTERIA_RED;
			}
			types[i] = type;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(60f);

		goals = new String[1];
		goals[0] = "¿Todo bien?";

		screen = new PlayScreen(this, level, new Goal(goals){

			@Override
			public boolean met() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() != Bacteria.BACTERIA_BLUE) return false;
				}
				return true;
			}

			@Override
			public boolean failed() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() == Bacteria.BACTERIA_BLUE) return false;
				}
				return true;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

		// NIVEL 47

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.05f);
		level.setMaxBlocks(5);
		level.setpOfRep(0.003);

		nBacterias = 30;
		types = new short[nBacterias];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<15){
				type = Bacteria.BACTERIA_PINK;
			} else  {
				type = Bacteria.BACTERIA_GREEN;
			}
			types[i] = type;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[1];
		goals[0] = "Lindo día, ¿no?";

		screen = new PlayScreen(this, level, new Goal(goals){

			@Override
			public boolean met() {
				return getScreen().getInfobar().getPoints() >= 20000;
			}

			@Override
			public boolean failed() {
				return false;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

		// NIVEL 48

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.8f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(5);
		level.setpOfRep(0.0015);

		nBacterias = 50;
		types = new short[nBacterias];
		for(int i = 0; i < types.length; i++){
			short type;
			if(i<10){
				type = Bacteria.BACTERIA_BLUE;
			} else if(i<20) {
				type = Bacteria.BACTERIA_ORANGE;
			} else if(i<30) {
				type = Bacteria.BACTERIA_RED;
			} else if(i<40) {
				type = Bacteria.BACTERIA_PINK;
			} else {
				type = Bacteria.BACTERIA_GREEN;
			}
			types[i] = type;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		goals = new String[3];
		goals[0] = "Tengo que admitir que\n" +
				"esto se está volviendo\n" +
				"un poco repetitivo.";
		goals[1] = "No estás aburrido?\n" +
				"Podríamos dejarlo acá...";
		goals[2] = "Sí, creo que sí.\n" +
				"Suerte con este, buena\n" +
				"vida y feliz navidad.";

		screen = new PlayScreen(this, level, new Goal(goals){

			@Override
			public boolean met() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() != Bacteria.BACTERIA_GREEN && b.getType() != Bacteria.BACTERIA_RED) return false;
				}
				return true;
			}

			@Override
			public boolean failed() {
				for(Bacteria b: getScreen().getBacterias()){
					if(b.getType() == Bacteria.BACTERIA_GREEN || b.getType() == Bacteria.BACTERIA_RED) return false;
				}
				return true;
			}
		});

		levels.add(screen);

		///////////////////////////////////////////////////////////

		// NIVEL 49

		level = new ScreenCreator();
		level.setInitial_pOfDying(0.2f);
		level.setMutationStDev(0.1f);
		level.setMaxBlocks(0);
		level.setpOfRep(0f);

		nBacterias = 1;
		types = new short[nBacterias];
		for(int i = 0; i < nBacterias; i++){
			types[i] = Bacteria.BACTERIA_BLUE;
		}
		level.setBacteriaTypes(types);

		level.setInitialTime(120f);

		buttonTypes = new short[5];
		buttonTypes[0] = Antibiotic.ANTIBIOTIC_WHITE;
		buttonTypes[1] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[2] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[3] = Antibiotic.ANTIBIOTIC_GRAY;
		buttonTypes[4] = Antibiotic.ANTIBIOTIC_GRAY;
		level.setButtonTypes(buttonTypes);

		goals = new String[2];
		goals[0] = "¡Bueno muy bien!\n" +
				"Espero que te hayas\n" +
				"divertido.";
		goals[1] = "Te regalo una bacteria\n" +
				"de la fortuna. ¡Qué la\n" +
				"disfrutes!";

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

    public void addLevel(PlayScreen screen){
		levels.add(screen);
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
		levelScreen.setLevelCreated(false);
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
		levelScreen.setLevelCreated(true);
	}

	public Array<Float> getSineFunction() {
		return sineFunction;
	}

	public int getLevelNumber() {
		return currentLevel;
	}
}
