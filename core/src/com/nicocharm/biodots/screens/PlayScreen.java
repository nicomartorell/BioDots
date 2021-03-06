package com.nicocharm.biodots.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nicocharm.biodots.Antibiotic;
import com.nicocharm.biodots.AntibioticButton;
import com.nicocharm.biodots.Bacteria;
import com.nicocharm.biodots.BioDots;
import com.nicocharm.biodots.Block;
import com.nicocharm.biodots.Bounds;
import com.nicocharm.biodots.Grid;
import com.nicocharm.biodots.InfoBar;
import com.nicocharm.biodots.PauseMenu;
import com.nicocharm.biodots.PowerBar;

import java.util.Random;


public class PlayScreen implements Screen {

    private float interval;

    public ScreenCreator getSettings() {
        return settings;
    }

    private ScreenCreator settings;

    //private Color backgroundColor;
    private Texture backgroundPlay;
    private Texture backgroundWin;
    private Texture backgroundLose;

    protected Texture background;

    protected float timer;

    public boolean isShowingGoal() {return showingGoal;}
    public void setShowingGoal(boolean showingGoal) {this.showingGoal = showingGoal;}
    //private final float firstTime = 15;
    protected boolean showingGoal = true;

    public Random random;

    // constantes que definen el filtro para distintos elementos del juego
    // definen con qué puede chocar un cuerpo de box2d
    public final short BACTERIA_BIT = 1;
    public final short DEFAULT_BIT = 2;

    // elementos necesarios y generales
    public BioDots game;
    public OrthographicCamera cam;
    public Viewport viewport;

    //acá guardo mis bacterias vivas
    protected Array<Bacteria> bacterias;
    public Array<Bacteria> getBacterias(){
        return bacterias;
    }

    //timer que marca el tiempo pasado desde la última bacteria nueva
    //private float bacteriaTimer;

    //probabilidad inicial de morir por antibiotico para nueva bacteria
    public float initial_pOfDying;

    //array que guarda valores de la función seno para la reproducción
    //optimizo para no calcularlo en cada reproducción
    public Array<Float> getBacteriaScale() {
        return bacteriaScale;
    }
    private Array<Float> bacteriaScale;

    public Antibiotic getAntibiotic() {
        return currentAntibiotic;
    }

    //guardo mi antibiótico actual y el proximo
    protected Antibiotic currentAntibiotic;
    private Antibiotic nextAntibiotic;



    //instancio mis objetos de box2D, no uso en principio el dr
    private World world;
    public World getWorld(){
        return world;
    }


    //instancio mi powerbar y defino cuánto se separa de y=0
    public PowerBar getPowerBar() {return powerBar;}
    protected PowerBar powerBar;
    private float totalLift = 300; //altura del tope de la barra

    public Bounds getArena() {
        return arena;
    }

    //defino valores de límite para la arena (donde las bacterias se mueven)
    //esto lo uso para calcular donde aparecen y sus targets
    private Bounds arena;

    public Grid getGrid() {
        return grid;
    }

    //la grid de blocks
    protected Grid grid;

    public InfoBar getInfobar() {
        return infobar;
    }

    //la barra de información de arriba
    protected InfoBar infobar;


    // llevo la cuenta de si terminó el nivel y si gané o perdí

    public boolean finished() {
        return ended;
    }
    private boolean ended;

    public boolean hasWon() {
        return won;
    }
    private boolean won;

    public boolean isClickable() {
        if(ended){
            return timer > endTime + 1f;
        }
        return true;
    }
    private float endTime;

    protected boolean paused;

    protected Goal goal;

    protected PauseMenu pauseMenu;

    private float lastBacteria = 0;

    private int maxBlocks;

    public boolean isTutorial() {
        return isTutorial;
    }

    protected boolean isTutorial;

    public double getpOfRep() {
        return pOfRep;
    }

    private double pOfRep;

    public PlayScreen(BioDots game, ScreenCreator creator, Goal goal){
        settings = creator;
        this.goal = goal;
        this.isTutorial = false;

        random = new Random();
        paused = true;
        this.game = game;
        cam = new OrthographicCamera();
        // creo un viewport con mi cámara y la llevo al centro
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, cam);
        cam.translate(game.WIDTH / 2, game.HEIGHT / 2);
    }

    public void initialize() {
        timer = 0;
        backgroundPlay = (Texture) game.manager.get("background-play.png", Texture.class);
        backgroundWin = (Texture) game.manager.get("background-win.png", Texture.class);
        backgroundLose = (Texture) game.manager.get("background-lose.png", Texture.class);

        background = backgroundPlay;

        pOfRep = settings.getpOfRep();

        bacterias = new Array<Bacteria>();
        //bacteriaTimer = 0;

        paused = false;

        world = new World(new Vector2(0, 0), true); // sin gravedad

        // 50% de morir en un principio
        initial_pOfDying = settings.getInitial_pOfDying();

        powerBar = new PowerBar(this, game.WIDTH / 2, totalLift, settings.getButtonTypes()); //centrada en x

        // calculo los puntos que necesito de la función seno para rep
        bacteriaScale = game.getSineFunction();

        //creo mi grid
        grid = new Grid(this, 0, totalLift, game.WIDTH, 4, 5);

        //la arena tiene w y h de grid, ademas de su altura
        arena = new Bounds(0, grid.getY(), grid.getWidth(), grid.getHeight());

        powerBar.getButtons().get(0).selectAntibiotic();

        for(int i = 0; i < settings.getBacteriaTypes().length; i++){
            bacterias.add(new Bacteria(this, getNewBacteriaX(random.nextFloat(), arena), getNewBacteriaY(random.nextFloat(), arena), settings.getBacteriaTypes()[i], initial_pOfDying));
        }

        infobar = new InfoBar(this, 0, totalLift + arena.getHeight(), settings.getInitialTime());

        ended = false;
        won = false;

        goal.setStage(this);

        pauseMenu = new PauseMenu(this);

        maxBlocks = settings.getMaxBlocks();

        interval = settings.getInterval();
        lastBacteria = timer;

        final Music s = (Music) game.manager.get("menu-music.ogg", Music.class);
        s.stop();

        if((game.getMusic()) && (game.getScreen().getClass() == PlayScreen.class || game.getScreen().getClass() == TutorialScreen.class)){
            final Music s1 = (Music) game.manager.get("play-song.ogg", Music.class);
            s1.setLooping(true);
            s1.setVolume(0.8f);
            s1.play();
        }
    }

    //retorno valores X e Y para una nueva bacteria, según un random pasado
    public float getNewBacteriaX(float r, Bounds bounds){
        return r*bounds.getWidth()*0.84f + bounds.getX() + bounds.getWidth()*0.08f;
    }
    public float getNewBacteriaY(float r, Bounds bounds){
        return (r*bounds.getHeight()*0.9f + bounds.getY() + bounds.getHeight()*0.05f);
    }

    protected void update(float delta){

        if(showingGoal){
            goal.update(delta);
            return;
        }

        if(paused) return;

        timer+=delta;

        if(!ended){
            if(bacterias.size >= 60 || goal.failed()){
                Gdx.app.log("tag4", "bacterias.size >= 60 || goal.failed()");
                lose();
            } else if(!game.isInFreeGame() && infobar.getTime() < 1){
                Gdx.app.log("tag4", "!game.isInFreeGame() && infobar.getTime() < 1");
                if(goal.met()){
                    win();
                } else lose();
            }else if (bacterias.size < 1){
                Gdx.app.log("tag4", "bacterias.size < 1");
                if(goal.met()){
                    win();
                } else if(this.getClass() == TutorialScreen.class && ((TutorialScreen)this).getState()!=((TutorialScreen)this).STATE_FINAL){
                } else {
                    lose();
                }
            } else if(goal.met()){
                Gdx.app.log("tag4", "goal.met()");
                win();
            }
        }

        //Gdx.app.log("tag2", "IS THE TIME FROM LAST GREATER THAN INTERVAL? " + (timer - lastBacteria >= interval));

        Gdx.app.log("tag2", "Timer - lastBacteria - interval: " + (timer - lastBacteria - interval) + "; timer: " + timer + "; lastBacteria: " + lastBacteria + "; Ended? " + ended + "; Free Game? " + settings.isFreeGame());

        if(settings.isFreeGame() && !ended && timer - lastBacteria - interval > 0){
            Gdx.app.log("tag3", "Bacteria added!");
            addBacteria();
        }

        
        // cambio de antibiotico si tengo que hacerlo
        if(nextAntibiotic != null && currentAntibiotic != null && !currentAntibiotic.isActive()){
            currentAntibiotic = nextAntibiotic;
        } /*else if(!currentAntibiotic.isActive()){
            powerBar.getButtons().get(0).selectAntibiotic();
        }*/


        // paso por todas las bacterias!
        for(int i = 0; i < bacterias.size; i++){ // n = 60
            Bacteria b = bacterias.get(i);
            b.update(delta, true);

            //sumo la probabilidad de que esta bacteria muera a la total
            infobar.sumP(b);

            //si se está dividiendo la elimino
            if(b.isDividing()){
                bacterias.removeIndex(i);
                world.destroyBody(b.getBody());
                if(bacterias.size > 0){ //si es 0 ya no hay bacterias y da error
                    i--;    // si no hago esto me salteo una bacteria
                }
                continue;
            }

            //para cada bacteria, chequeo todos los antibioticos
            if(currentAntibiotic != null){
                currentAntibiotic.checkBacteria(b); //está cerca?

                if(b.isDead()) { // si la maté, la elimino
                    bacterias.removeIndex(i);
                    world.destroyBody(b.getBody());
                    infobar.updatePoints(100); // sumo puntos!

                    if(bacterias.size > 0){ //lo mismo que antes
                        i--;
                    } else {
                        break;
                    }
                }

            }
        }

        if(currentAntibiotic != null){
            currentAntibiotic.update(delta);
        }


        infobar.update(delta);

        //para cada bloque de grid
        grid.update(delta);

        powerBar.update(delta);

        world.step(delta,6,2); //avanza box2d

    }

    protected void addBacteria() {
        if(infobar.getAverageP()==0){
            bacterias.add(new Bacteria(this, getNewBacteriaX(random.nextFloat(), arena), getNewBacteriaY(random.nextFloat(), arena), (short)(random.nextInt(5) + 1), initial_pOfDying));
            lastBacteria = timer;
            return;
        }
        bacterias.add(new Bacteria(this, getNewBacteriaX(random.nextFloat(), arena), getNewBacteriaY(random.nextFloat(), arena), (short)(random.nextInt(5) + 1), infobar.getAverageP()));
        lastBacteria = timer;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(cam.combined); //dibujo según la cam

        update(delta); // sin esto no pasa nada

        game.batch.begin();

        game.batch.draw(background, 0, 0);

        for(Bacteria bacteria: bacterias){ // dibujo todas las bacterias
            bacteria.render(game.batch); // 60
        }

        //dr.render(world, cam.combined);

        if(currentAntibiotic != null) currentAntibiotic.render(game.batch);

        grid.render(game.batch);
        powerBar.render(game.batch);
        infobar.render(game.batch);
        game.batch.end();

        infobar.stage.draw();

        if(showingGoal){
            goal.render(game.batch);
        }

        if(paused && !showingGoal){
            pauseMenu.render(game.batch);
        }
    }


    private void saveScore(Preferences preferences){
        int score = infobar.getPoints();

        if(game.isInFreeGame()){
            String s;
            String[] values;
            if(preferences.contains("scoreFree")){
                s = preferences.getString("scoreFree", "Free Game&" + 0);
                values = s.split("&");
            } else {
                values = new String[2];
                values[0] = "Free Game";
                values[1] = "" + -100000;
            }

            int pastScore = Integer.parseInt(values[1]);

            if(score>pastScore){
                preferences.putString("scoreFree", "Free Game&" + score);
                preferences.flush();
            }

        } else {
            String s;
            String[] values;

            Gdx.app.log("tag", "Saving score");

            if(preferences.contains("score" + game.getLevelNumber())){
                s = preferences.getString("score" + game.getLevelNumber(), game.getLevelNumber() + "&" + 0);
                values = s.split("&");
                Gdx.app.log("tag", "There was a value");
            } else {
                values = new String[2];
                values[0] = "score" + game.getLevelNumber();
                values[1] = "" + -100000;
                Gdx.app.log("tag", "There was not a value");
            }

            Gdx.app.log("tag", "The value is now " + values[1]);

            int pastScore = Integer.parseInt(values[1]);

            Gdx.app.log("tag", "Score is " + score + " and the past score is " + pastScore);

            Gdx.app.log("tag", "The level number is " + game.getLevelNumber());

            if(score>pastScore){
                preferences.putString("score" + game.getLevelNumber(), game.getLevelNumber() + "&" + score);
                preferences.flush();
            }
        }
    }

    private void lose(){
        Gdx.app.log("tag4", "omg LOSING");
        endTime = timer;
        ended = true;
        won = false;
        background = backgroundLose;

        Preferences preferences = Gdx.app.getPreferences("BioDots");
        saveScore(preferences);
    }

    public void win(){
        Gdx.app.log("tag4", "omg WINNING");
        endTime = timer;
        ended = true;
        won = true;
        background = backgroundWin;

        infobar.endPoints();

        Preferences preferences = Gdx.app.getPreferences("BioDots");

        saveScore(preferences);

        int current = preferences.getInteger("lastLevel");
        int thisOne = game.getLevels().indexOf(this, true) + 1;
        if(thisOne > current && thisOne < game.getLevels().size - 1){
            game.getLevelScreen().addLevel();
            preferences.putInteger("lastLevel", thisOne);
            preferences.flush();
        }

    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height); //viewport cambia al cambiar la pantalla
    }

    @Override
    public void pause() {
        paused = true;
        final Music s = (Music) game.manager.get("play-song.ogg", Music.class);
        s.pause();
    }

    @Override
    public void resume() {
        paused = false;

        if(game.getMusic()){
            final Music s1 = (Music) game.manager.get("play-song.ogg", Music.class);
            s1.play();
        }
    }

    @Override
    public void hide() {
        final Music s1 = (Music) game.manager.get("play-song.ogg", Music.class);
        s1.stop();
        this.goal.reset();
    }

    @Override
    public void dispose() { //todos los disposables aca
        paused = false;
        if(world != null)world.dispose();
        if(infobar != null)infobar.dispose();
        if(goal != null)goal.dispose();
        currentAntibiotic = null;
        nextAntibiotic = null;
        Bacteria.ID_COUNT = 0;
        showingGoal = true;
    }

    public void setAntibiotic(Antibiotic antibiotic) {
        this.currentAntibiotic = antibiotic;
    }

    public void setNextAntibiotic(Antibiotic nextAntibiotic) {
        this.nextAntibiotic = nextAntibiotic;
    }


    public boolean isPaused() {
        return paused;
    }

    public PauseMenu getPauseMenu() {
        return pauseMenu;
    }

    public Goal getGoal() {
        return goal;
    }

    public int getMaxBlocks() {
        return maxBlocks;
    }
}
