package com.nicocharm.biodots.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nicocharm.biodots.Bacteria;
import com.nicocharm.biodots.BioDots;
import com.nicocharm.biodots.Button;
import com.nicocharm.biodots.Level;

import java.util.Random;

public class LevelScreen implements Screen, InputProcessor {

    BioDots game;

    private Color backgroundColor;
    private Stage stage;

    private boolean touchable;
    private float firstX;

    public BioDots getGame() {
        return game;
    }

    public OrthographicCamera cam;
    public Viewport viewport;

    private Button button;

    private Array<Level> levels;

    public Texture getBorder() {
        return border;
    }

    private Texture border;
    private Texture lockedBorder;

    private float pastX = -1;

    private int i;

    public boolean hasBeenLevelCreated() {
        return levelCreated;
    }

    public void setLevelCreated(boolean levelCreated) {
        this.levelCreated = levelCreated;
    }

    private boolean levelCreated = false;

    public LevelScreen(BioDots game){
        this.game = game;

        touchable = true;

        backgroundColor = new Color(0, 0 , 0.2f, 1);

        cam = new OrthographicCamera();
        // creo un viewport con mi cámara y la llevo al centro
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, cam);
        cam.translate(game.WIDTH / 2, game.HEIGHT / 2);

        stage = new Stage(viewport, game.batch);
        //stage.setDebugAll(true);

        border = (Texture)game.manager.get("level-border.png", Texture.class);
        lockedBorder = (Texture)game.manager.get("level-border-locked.png", Texture.class);

        levels = new Array<Level>();
        /*for(int i = 0; i < game.getLevels().size; i++){
            Level level = new Level(this, i, game.WIDTH/2 + game.WIDTH*i, game.HEIGHT/2 + 50);
            levels.add(level);
            stage.addActor(level.getLabel());
        }*/

        Level level0 = new Level(this, "TUTORIAL", 1, game.WIDTH/2, game.HEIGHT/2 + 50, false, 1);
        levels.add(level0);
        stage.addActor(level0.getLabel());

        Preferences preferences = Gdx.app.getPreferences("BioDots");

        /*if(game.isCompleted()){
            preferences.putInteger("lastLevel", levels.size-1);
        }*/

        int last = preferences.getInteger("lastLevel", 0);

        Gdx.app.log("tag6", "Last Level: " + last + "; array size: " + game.getLevels().size);

        if(last >= game.getLevels().size){
            Gdx.app.log("tag", "Decreasing last");
            last = game.getLevels().size - 1;
        }

        Gdx.app.log("tag6", "i is " + i);

        for(i = 1; i <= last; i++){
            Level level = new Level(this, /*"Nivel " + */Integer.toString(i), 1, game.WIDTH/2 + game.WIDTH*i, game.HEIGHT/2 + 50, false, 2.5f);
            levels.add(level);
            stage.addActor(level.getLabel());
        }

        Gdx.app.log("tag6", "i is " + i);

        if(i<game.getLevels().size){
            Level level = new Level(this, "No", 1,
                    game.WIDTH/2 + game.WIDTH*i, game.HEIGHT/2 + 50, true, 1);
            levels.add(level);
        } else {
            game.setCompleted(true);
            //createLevel();
        }
        Gdx.app.log("tag6", "i is " + i);

        BitmapFont font = (BitmapFont) game.manager.get("Roboto-Regular.ttf", BitmapFont.class);
        Label.LabelStyle style = new Label.LabelStyle();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = new Color(197/255f, 215/255f, 254/255f, 1);

        String text = "Elegí un nivel";
        Label title = new Label(text, style);
        title.setAlignment(Align.center);
        title.setFontScale(1.3f);
        GlyphLayout gl = new GlyphLayout(style.font, text);
        title.setPosition(game.WIDTH/2 - gl.width/2, game.HEIGHT - 140 - font.getLineHeight());

        stage.addActor(title);

        float scale = 0.75f;
        float height = ((Texture)game.manager.get("menu-button.png", Texture.class)).getHeight() * scale;
        float offset = 120;
        button = new Button(game, game.WIDTH/2,  offset + height/2, "menu-button.png", "VOLVER", scale, 0);
        stage.addActor(button.getLabel());
    }

    @Override
    public void show() {
        if(game.isCompleted()){
            if(game.getLevels().size >= 51){
                game.getLevels().removeIndex(game.getLevels().size-1);
                levels.removeIndex(levels.size-1);
                levelCreated = false;
            }
            createLevel();
        }

        int current = Gdx.app.getPreferences("BioDots").getInteger("lastLevel", 0);

        if(current >= game.getLevels().size) current = game.getLevels().size - 1;



        for(int i = 0; i < levels.size; i++){
            float x = game.WIDTH/2 + game.WIDTH*(i - current);
            levels.get(i).setX(x);
            levels.get(i).setPreferedX(x);
        }
    }

    public void addLevel(){
        if(game.isCompleted())return;

        Gdx.app.log("tag", "i is " + i);
        Gdx.app.log("tag", "levels size is " + game.getLevels().size);

        if(i < game.getLevels().size){
            Gdx.app.log("tag", "creating level");
            Level last = levels.get(levels.size-1);
            last.setX(last.getX() + game.WIDTH);
            last.setPreferedX(last.getPreferedX() + game.WIDTH);
            Level level = new Level(this, /*"Nivel " + */Integer.toString(i), 1, game.WIDTH/2 + game.WIDTH*i, game.HEIGHT/2 + 50, false, 2.5f);
            levels.insert(levels.size - 1, level);
            stage.addActor(level.getLabel());
            i++;
        }

        if(i == game.getLevels().size){
            levels.removeIndex(levels.size-1);
            game.setCompleted(true);
        }
    }

    public void update(float delta){

        if(Gdx.input.isTouched()){
            Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 v2 = cam.unproject(v);
            float currentX = v2.x;

            if(pastX == -1){
                pastX = currentX;
                return;
            }

            float deltaX = currentX - pastX;
            Gdx.app.log("tag", "deltaX: " + deltaX);
            if(Math.abs(currentX - firstX) > 40) touchable = false;

            pastX = currentX;

            /*if(levels.get(0).getX() > game.WIDTH/2){
                for(int i = 0; i < levels.size; i++){
                    levels.get(i).setX(game.WIDTH/2 + game.WIDTH*i);
                }
                return;
            } else if(levels.get(levels.size-1).getX() < game.WIDTH/2){
                for(int i = 0; i < levels.size; i++){
                    levels.get(i).setX(game.WIDTH/2 + game.WIDTH*i - game.WIDTH*(levels.size-1));
                }
                return;
            }*/

            for(Level l: levels){
                l.translate(deltaX, 0);
            }
        } else {
            for(Level level: levels){
                level.update(delta);
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        game.batch.begin();
        for(Level level: levels){
            level.render(game.batch);
        }
        button.render(game.batch);
        game.batch.end();

        stage.draw();
    }

    public void reset(){
        for(int i = 0; i < levels.size; i++){
            float x = game.WIDTH/2 + game.WIDTH*i;
            levels.get(i).setX(x);
            levels.get(i).setPreferedX(x);
        }
    }

    private void createLevel(){
        Level ran = new Level(this, "?", 1, game.WIDTH/2, game.HEIGHT/2 + 50, false, 2.5f);
        levels.add(ran);
        stage.addActor(ran.getLabel());


        levelCreated = true;
        final Random r = new Random();


        ///////////////////////////////////////////////////////////

        // NIVEL CON PARÁMETROS ALEATORIOS...

        // Puedo hacer distintos tipos de niveles y switchear

        // tipo 0: baja pOfDying - 1 bloque
        // tipo 1: alta pOfRep - 3 bloques
        // tipo 2: alta stDev - 3 bloques
        // tipo 3: matá a un tipo de bacterias
        // tipo 4: llegá a una cantidad de puntos

        int switcher = r.nextInt(5);
        ScreenCreator level = new ScreenCreator();

        int nBacterias;
        short[] types;
        String[] goals = new String[2];
        PlayScreen screen;

        switch(switcher){
            case 0:
                level.setInitial_pOfDying(r.nextFloat()*0.2f + 0.5f);
                level.setMutationStDev(r.nextFloat()*0.02f + 0.05f);
                level.setMaxBlocks(1);
                level.setpOfRep(r.nextFloat()*0.001f + 0.0005f);

                nBacterias = r.nextInt(10) + 20;
                types = new short[nBacterias];
                for(int i = 0; i < nBacterias; i++){
                    types[i] = (short)(r.nextInt(5) + 1);
                }
                level.setBacteriaTypes(types);

                level.setInitialTime(120f);

                goals[0] = "Este nivel fue diseñado\n" +
                        "de manera aleatoria, sólo\n" +
                        "para vos :)";
                goals[1] = "En esta ocasión,\n" +
                        "las bacterias no\n" +
                        "mueren muy fácil...";

                screen = new PlayScreen(game, level, new Goal(goals){

                    @Override
                    public boolean met() {
                        return getScreen().getBacterias().size < 1;
                    }

                    @Override
                    public boolean failed() {
                        return false;
                    }
                });

                break;
            case 1:
                level.setInitial_pOfDying(r.nextFloat()*0.2f + 0.75f);
                level.setMutationStDev(r.nextFloat()*0.02f + 0.05f);
                level.setMaxBlocks(3);
                level.setpOfRep(r.nextFloat()*0.0040f + 0.0025f);

                nBacterias = r.nextInt(10) + 15;
                types = new short[nBacterias];
                for(int i = 0; i < nBacterias; i++){
                    types[i] = (short)(r.nextInt(5) + 1);
                }
                level.setBacteriaTypes(types);

                level.setInitialTime(120f);

                goals[0] = "Este nivel fue diseñado\n" +
                        "de manera aleatoria, sólo\n" +
                        "para vos :)";
                goals[1] = "En esta ocasión,\n" +
                        "las bacterias no\n" +
                        "paran de duplicarse...";

                screen = new PlayScreen(game, level, new Goal(goals){

                    @Override
                    public boolean met() {
                        return getScreen().getBacterias().size < 1;
                    }

                    @Override
                    public boolean failed() {
                        return false;
                    }
                });

                break;
            case 2:
                level.setInitial_pOfDying(r.nextFloat()*0.2f + 0.75f);
                level.setMutationStDev(r.nextFloat()*0.2f + 0.15f);
                level.setMaxBlocks(3);
                level.setpOfRep(r.nextFloat()*0.001f + 0.0005f);

                nBacterias = r.nextInt(10) + 20;
                types = new short[nBacterias];
                for(int i = 0; i < nBacterias; i++){
                    types[i] = (short)(r.nextInt(5) + 1);
                }
                level.setBacteriaTypes(types);

                level.setInitialTime(120f);

                goals[0] = "Este nivel fue diseñado\n" +
                        "de manera aleatoria, sólo\n" +
                        "para vos :)";
                goals[1] = "En esta ocasión,\n" +
                        "las bacterias están\n" +
                        "mutando muy rápido...";

                screen = new PlayScreen(game, level, new Goal(goals){

                    @Override
                    public boolean met() {
                        return getScreen().getBacterias().size < 1;
                    }

                    @Override
                    public boolean failed() {
                        return false;
                    }
                });

                break;
            case 3:
                level.setInitial_pOfDying(r.nextFloat()*0.3f + 0.6f);
                level.setMutationStDev(r.nextFloat()*0.04f + 0.08f);
                level.setMaxBlocks(1);
                level.setpOfRep(r.nextFloat()*0.001f + 0.0005f);

                nBacterias = r.nextInt(10) + 27;
                types = new short[nBacterias];
                final short wantedType = (short)(r.nextInt(5) + 1);
                short otherType1;
                short otherType2;

                while(true){
                    otherType1 = (short)(r.nextInt(5) + 1);
                    if(otherType1 != wantedType){
                        break;
                    }
                }

                while(true){
                    otherType2 = (short)(r.nextInt(5) + 1);
                    if(otherType2 != wantedType && otherType2 != otherType1){
                        break;
                    }
                }

                int desiredQuantity = nBacterias/4;
                int otherQuantity = desiredQuantity + r.nextInt(nBacterias/3) + nBacterias/4;

                for(int i = 0; i < nBacterias; i++){
                    if(i<desiredQuantity){
                        types[i] = wantedType;
                    } else if(i<otherQuantity){
                        types[i] = otherType1;
                    } else {
                        types[i] = otherType2;
                    }
                }
                level.setBacteriaTypes(types);

                level.setInitialTime(120f);

                goals[0] = "Este nivel fue diseñado\n" +
                        "de manera aleatoria, sólo\n" +
                        "para vos :)";
                goals[1] = "En esta ocasión,\n" +
                        "tenés que matar a\n" +
                        "cierto tipo de bacterias.\n" +
                        "¿Adivinás cuáles?";

                screen = new PlayScreen(game, level, new Goal(goals){

                    @Override
                    public boolean met() {
                        for(Bacteria b: getScreen().getBacterias()){
                            if(b.getType() != wantedType) return false;
                        }
                        return true;
                    }

                    @Override
                    public boolean failed() {
                        for(Bacteria b: getScreen().getBacterias()){
                            if(b.getType() == wantedType) return false;
                        }
                        return true;
                    }
                });

                break;
            case 4:
                level.setInitial_pOfDying(r.nextFloat()*0.3f + 0.6f);
                level.setMutationStDev(r.nextFloat()*0.04f + 0.08f);
                level.setMaxBlocks(1);
                level.setpOfRep(r.nextFloat()*0.001f + 0.0005f);

                nBacterias = r.nextInt(15) + 30;
                types = new short[nBacterias];
                for(int i = 0; i < nBacterias; i++){
                    types[i] = (short)(r.nextInt(5) + 1);
                }
                level.setBacteriaTypes(types);

                level.setInitialTime(120f);

                goals[0] = "Este nivel fue diseñado\n" +
                        "de manera aleatoria, sólo\n" +
                        "para vos :)";
                goals[1] = "En esta ocasión,\n" +
                        "tenés que hacer muchos\n" +
                        "puntos para ganar...";

                screen = new PlayScreen(game, level, new Goal(goals){

                    @Override
                    public boolean met() {
                        return getScreen().getInfobar().getPoints() >= r.nextInt(15000) + 15000;
                    }
                    @Override
                    public boolean failed() {
                        return false;
                    }
                });

                break;
            default:
                screen = null;
                break;
        }
        Gdx.app.log("tag5", "pOfRep: " + level.getpOfRep());
        game.addLevel(screen);
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            game.setToMenu(true);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchable = true;
        Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 v2 = cam.unproject(v);
        firstX = v2.x;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        Preferences preferences = Gdx.app.getPreferences("BioDots");
        int current = preferences.getInteger("lastLevel");
        Gdx.app.log("tag", "Highest level won: " + (current - 1));

        pastX = -1;

        Level level = levels.get(0);
        if(level.getX() - level.getPreferedX() > game.WIDTH/6 && !(levels.get(0).getX() > game.WIDTH/2)){
            for(Level l: levels){
                l.setPreferedX(l.getPreferedX() + game.WIDTH);
            }
            return false;
        } else if(level.getPreferedX() - level.getX() > game.WIDTH/6 && !(levels.get(levels.size-1).getX() < game.WIDTH/2)){
            for(Level l: levels){
                l.setPreferedX(l.getPreferedX() - game.WIDTH);
            }
            return false;
        }



        Vector3 v = new Vector3(screenX, screenY, 0);
        Vector3 v2 = cam.unproject(v);
        float x = v2.x;
        float y = v2.y;

        if(touchable){
            for(Level l: levels){
                if(l.pressed(x, y)){
                    if(!(levels.indexOf(l, true) == levels.size - 1) || game.isCompleted()){
                        game.setLevel(levels.indexOf(l, true));
                        reset();
                        return false;
                    };
                }
            }
        }



        if(this.button.pressed(x, y)){
            reset();
            game.setToMenu(true);
            return false;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }





    @Override
    public void resize(int width, int height) {

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
    public void dispose() {
        stage.dispose();
    }

    public Texture getLockedBorder() {
        return lockedBorder;
    }
}
