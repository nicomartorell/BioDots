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
import com.nicocharm.biodots.BioDots;
import com.nicocharm.biodots.Button;
import com.nicocharm.biodots.Level;

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

        for(i = 0; i <= Gdx.app.getPreferences("BioDots").getInteger("lastLevel", 0); i++){
            Level level = new Level(this, "Nivel " + Integer.toString(i), 1, game.WIDTH/2 + game.WIDTH*i, game.HEIGHT/2 + 50, false);
            levels.add(level);
            stage.addActor(level.getLabel());
        }

        if(i<game.getLevels().size){
            Level level = new Level(this, "No", 1,
                    game.WIDTH/2 + game.WIDTH*i, game.HEIGHT/2 + 50, true);
            levels.add(level);
        } else {
            game.setCompleted(true);
        }


        BitmapFont font = (BitmapFont) game.manager.get("Roboto-Bold.ttf", BitmapFont.class);
        Label.LabelStyle style = new Label.LabelStyle();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = new Color(197/255f, 215/255f, 254/255f, 1);

        String text = "Elegí tu nivel";
        Label title = new Label(text, style);
        title.setAlignment(Align.center);
        GlyphLayout gl = new GlyphLayout(style.font, text);
        title.setPosition(game.WIDTH/2 - gl.width/2, game.HEIGHT - 140 - font.getLineHeight());

        stage.addActor(title);

        float scale = 0.75f;
        float height = ((Texture)game.manager.get("menu-button.png", Texture.class)).getHeight() * scale;
        float offset = 120;
        button = new Button(game, game.WIDTH/2,  offset + height/2, "menu-button.png", "VOLVER", scale, 0);
        stage.addActor(button.getLabel());
    }

    public void addLevel(){
        if(game.isCompleted())return;

        if(i < game.getLevels().size){
            Level last = levels.get(levels.size-1);
            last.setX(last.getX() + game.WIDTH);
            last.setPreferedX(last.getPreferedX() + game.WIDTH);
            Level level = new Level(this, "Nivel " + Integer.toString(i), 1, game.WIDTH/2 + game.WIDTH*i, game.HEIGHT/2 + 50, false);
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
    public void show() {

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
