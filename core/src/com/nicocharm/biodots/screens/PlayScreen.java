package com.nicocharm.biodots.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nicocharm.biodots.Antibiotic;
import com.nicocharm.biodots.Bacteria;
import com.nicocharm.biodots.BioDots;
import com.nicocharm.biodots.Block;
import com.nicocharm.biodots.Bounds;
import com.nicocharm.biodots.Grid;
import com.nicocharm.biodots.InfoBar;
import com.nicocharm.biodots.PowerBar;

import java.util.Random;


public class PlayScreen implements Screen {

    private Color backgroundColor;

    // constantes que definen el filtro para distintos elementos del juego
    // definen con qué puede chocar un cuerpo de box2d
    public final short BACTERIA_BIT = 1;
    public final short DEFAULT_BIT = 2;

    // elementos necesarios y generales
    public BioDots game;
    public OrthographicCamera cam;
    public Viewport viewport;

    //acá guardo mis bacterias vivas
    private Array<Bacteria> bacterias;
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

    //guardo todos mis antibióticos activos
    public Array<Antibiotic> getAntibiotics() {
        return antibiotics;
    }
    private Array<Antibiotic> antibiotics;

    //instancio mis objetos de box2D, no uso en principio el dr
    private World world;
    public World getWorld(){
        return world;
    }

    private Box2DDebugRenderer dr;

    //instancio mi powerbar y defino cuánto se separa de y=0
    private PowerBar bar;
    private float barLift = 165; //altura del centro de la barra
    private float totalLift; //altura del tope de la barra

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
    private Grid grid;

    public InfoBar getInfobar() {
        return infobar;
    }

    //la barra de información de arriba
    private InfoBar infobar;


    // llevo la cuenta de si terminó el nivel y si gané o perdí

    public boolean finished() {
        return ended;
    }
    private boolean ended;

    public boolean hasWon() {
        return won;
    }
    private boolean won;

    public PlayScreen(BioDots game){
        backgroundColor = new Color(0, 70f/255f, 70f/255f, 1);

        this.game = game;
        cam = new OrthographicCamera();
        // creo un viewport con mi cámara y la llevo al centro
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, cam);
        cam.translate(game.WIDTH / 2, game.HEIGHT / 2);

        bacterias = new Array<Bacteria>();
        //bacteriaTimer = 0;

        antibiotics = new Array<Antibiotic>();
        world = new World(new Vector2(0, 0), true); // sin gravedad
        dr = new Box2DDebugRenderer();

        // 50% de morir en un principio
        initial_pOfDying = 0.5f;

        bar = new PowerBar(this, game.WIDTH / 2, barLift); //centrada en x

        //el tope de la barra es su separación de y=0 más su altura
        //la altura /2 es porque el barLift es hasta el centro de bar
        totalLift = barLift + (bar.getActorHeight()*bar.getMyScale())/2;

        // calculo los puntos que necesito de la función seno para rep
        bacteriaScale = new Array<Float>();
        double x = 0;
        while(x < 2*Math.PI){
            float y = 3;
            Float f = (float)((1f/(y - 1f))*(y - Math.cos(x)));
            bacteriaScale.add(f);
            x+=0.05;
        }

        //creo mi grid
        grid = new Grid(this, 0, totalLift, game.WIDTH, 4, 5);

        //la arena tiene w y h de grid
        arena = new Bounds(0, totalLift, grid.getWidth(), grid.getHeight());

        infobar = new InfoBar(this, 0, totalLift + arena.getHeight(), 182);

        ended = false;
        won = false;

        for(int i = 0; i < 10; i++){
            Random r = new Random();
            short type = (short)(r.nextInt(5) + 1); //el tipo de bacteria es aleatorio
            bacterias.add(new Bacteria(this, getNewBacteriaX(r.nextFloat(), arena), getNewBacteriaY(r.nextFloat(), arena), type, initial_pOfDying));
        }
    }

    //retorno valores X e Y para una nueva bacteria, según un random pasado
    public float getNewBacteriaX(float r, Bounds bounds){
        return r*bounds.getWidth()*0.84f + bounds.getX() + bounds.getWidth()*0.08f;
    }
    public float getNewBacteriaY(float r, Bounds bounds){
        return (r*bounds.getHeight()*0.9f + bounds.getY() + bounds.getHeight()*0.05f);
    }

    private void update(float delta){
        if(bacterias.size >= 60 || infobar.getTime() < 1 || (bar.getAverageP() > 0.0 && bar.getAverageP() < 0.05 && bacterias.size > 15)){
            lose();
            ended = true;
        } else if (bacterias.size < 1){
            win();
            ended = true;
            won = true;
        }

        //cada 3 segundos nueva bacteria
        /*if(bacteriaTimer>6 && bacterias.size<1){
            Random r = new Random();
            short type = (short)(r.nextInt(5) + 1); //el tipo de bacteria es aleatorio
            bacterias.add(new Bacteria(this, getNewBacteriaX(r.nextFloat(), arena), getNewBacteriaY(r.nextFloat(), arena), type, initial_pOfDying));
            bacteriaTimer=0;
        } else {
            //avanzo el timer de hace cuanto nació última bacteria
            bacteriaTimer+=delta;}*/


        // paso por todas las bacterias!
        for(int i = 0; i < bacterias.size; i++){
            Bacteria b = bacterias.get(i);
            b.update(delta);

            //si se está dividiendo la elimino
            if(b.isDividing()){
                b.getTexture().dispose(); // MUY IMPORTANTE
                bacterias.removeIndex(i);
                world.destroyBody(b.getBody());
                if(bacterias.size > 0){ //si es 0 ya no hay bacterias y da error
                    i--;    // si no hago esto me salteo una bacteria
                }
                continue;
            }

            //para cada bacteria, chequeo todos los antibioticos
            for(Antibiotic antibiotic: antibiotics)
                antibiotic.checkBacteria(b); //está cerca?
                if(b.isDead()){ // si la maté, la elimino
                    b.getTexture().dispose();
                    bacterias.removeIndex(i);
                    world.destroyBody(b.getBody());
                    infobar.updatePoints(100); // sumo puntos!
                    if(bacterias.size != 0){ //lo mismo que antes
                        i--;
                    }
                    break; // no sigo con el loop chequeando bacterias que no existen
            }

            bar.sumP(b); //sumo la probabilidad de que esta bacteria muera a la total
        }

        //para cada antibiótico
        for(int i = 0; i < antibiotics.size; i++){
            Antibiotic a = antibiotics.get(i);
            a.update(delta);
            if(a.toDestroy){    //si ya se terminó
                a.getTexture().dispose();
                antibiotics.removeIndex(i);
                i--;
            }
        }

        bar.update(delta);
        infobar.update(delta);

        //para cada bloque de grid
        grid.update(delta);

        world.step(delta,6,2); //avanza box2d

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(cam.combined); //dibujo según la cam

        update(delta); // sin esto no pasa nada

        game.batch.begin();

        for(Bacteria bacteria: bacterias){ // dibujo todas las bacterias
            bacteria.render(game.batch);
        }

        //dr.render(world, cam.combined);

        for(Antibiotic antibiotic: antibiotics){ //dibujo antibioticos
            antibiotic.render(game.batch);
        }

        grid.render(game.batch);

        bar.render(game.batch);

        infobar.render(game.batch);

        game.batch.end();
        bar.stage.draw(); // fuera de mi rango de batch porque lo inicializa de nuevo
        infobar.stage.draw();
    }


    private void lose(){
        backgroundColor = new Color(119f/255f, 10f/255f, 10f/255f, 1);
    }

    private void win(){
        backgroundColor = new Color(20f/255f, 98f/255f, 9f/255f, 1);
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

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() { //todos los disposables aca
        world.dispose();
        bar.getTexture().dispose();
        grid.dispose();
        infobar.dispose();
    }
}
