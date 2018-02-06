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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nicocharm.biodots.Bacteria;
import com.nicocharm.biodots.BioDots;
import com.nicocharm.biodots.Bounds;
import com.nicocharm.biodots.Button;
import com.nicocharm.biodots.MenuButton;

import java.util.Random;


public class MainMenu implements Screen, InputProcessor {

    public static final String JUGAR = "JUEGO RÁPIDO";
    public static final String DESAFIO = "NIVELES";
    public static final String ACERCA_DE = "ACERCA DE";
    public static final String SALIR = "SALIR";

    private Color backgroundColor;
    private Stage stage;

    public BioDots getGame() {
        return game;
    }

    private BioDots game;
    public OrthographicCamera cam;
    public Viewport viewport;

    private Label title;
    private int counter;

    private Array<MenuButton> buttons;
    private String[] texts = {MainMenu.SALIR, MainMenu.ACERCA_DE, MainMenu.DESAFIO, MainMenu.JUGAR};

    private Array<Bacteria> bacterias;

    private PlayScreen screenHelper;

    private Button config;
    private Button highScores;

    public MainMenu(BioDots game){
        this.game = game;
        backgroundColor = new Color(0, 0 , 0.2f, 1);

        cam = new OrthographicCamera();
        // creo un viewport con mi cámara y la llevo al centro
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, cam);
        cam.translate(game.WIDTH / 2, game.HEIGHT / 2);

        stage = new Stage(viewport, game.batch);

        BitmapFont font = (BitmapFont) game.manager.get("GloriaHallelujah.ttf", BitmapFont.class);
        Label.LabelStyle style = new Label.LabelStyle();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = new Color(197/255f, 215/255f, 254/255f, 1);

        String text = "Bio Dots";
        title = new Label(text, style);
        title.setAlignment(Align.center);
        GlyphLayout gl = new GlyphLayout(style.font, text);
        title.setPosition(game.WIDTH/2 - gl.width/2, game.HEIGHT - 20 - font.getLineHeight());
        counter = 0;
        stage.addActor(title);


        buttons = new Array<MenuButton>();

        float yoffset = 450f;
        float initialHeight = game.HEIGHT*0.8f;
        float buttonSpace = (initialHeight-yoffset)/texts.length + 18;
        for(int i = 0; i < texts.length; i++){

            MenuButton button = new MenuButton(game, game.WIDTH/2, yoffset + buttonSpace*i, texts[i], 0.75f);
            buttons.add(button);
            stage.addActor(button.getLabel());
        }

        bacterias = new Array<Bacteria>();
        ScreenCreator sc = new ScreenCreator();
        String[] goals = {""};
        screenHelper = new PlayScreen(game, sc, new Goal(goals){
            @Override
            public boolean met() {return false;}
            @Override
            public boolean failed() {
                return false;
            }
        });
        screenHelper.initialize();

        Bounds bounds = new Bounds(game.WIDTH*0.1f, game.HEIGHT*0.05f, game.WIDTH*0.8f, game.HEIGHT*0.9f);
        Random r = new Random();
        for(int i = 0; i < 17; i++){
            bacterias.add(new Bacteria(screenHelper, screenHelper.getNewBacteriaX(r.nextFloat(), bounds), screenHelper.getNewBacteriaY(r.nextFloat(), bounds), Bacteria.randomType(), 0));
        }

        config = new Button(game, game.WIDTH*(1/8f), 150, "config-icon.png", null, 0.2f);
        highScores = new Button(game, game.WIDTH*(7/8f), 130, null, "Máximos puntajes", 0.5f);

        highScores.setButtonPosition(game.WIDTH - highScores.getLabelWidth()/2 - 60, 130);
        stage.addActor(highScores.getLabel());
    }

    @Override
    public void show() {
        Preferences preferences = Gdx.app.getPreferences("BioDots");
        if(preferences.contains("lastLevel")){
            int current = preferences.getInteger("lastLevel", 0);
            MenuButton b = buttons.get(2); //botón niveles
            MenuButton b2 = buttons.get(3); // juego libre
            if(current == 0){
                b.setText("TUTORIAL");
                b2.setActive(false);
            } else {
                b.setText(DESAFIO);
                b2.setActive(true);
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        game.batch.begin();
        for(Bacteria b: bacterias){b.render(game.batch);}
        for(MenuButton button: buttons) button.render(game.batch);
        config.render(game.batch);
        game.batch.end();

        stage.draw();
    }

    private void update(float delta) {

        title.setFontScale(0.85f + game.getSineFunction().get(counter)*0.05f);
        counter++;
        if(counter == game.getSineFunction().size) counter = 0;

        for(Bacteria b: bacterias){
            b.update(delta, false);
        }
        screenHelper.getWorld().step(delta,6,2);
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
        MenuButton.resetID();
        screenHelper.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            Gdx.app.log("tag", "end");
            Gdx.app.exit();
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
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 v = new Vector3(screenX, screenY, 0);
        Vector3 v2 = cam.unproject(v);
        float x = v2.x;
        float y = v2.y;

        Gdx.app.log("tag", "INPUT!");

        if(x < 110 && y > game.HEIGHT - 110){
            Gdx.app.log("tag", "Reset process.");
            game.resetProcess();
            return false;
        } else if(x > game.WIDTH - 110 && y > game.HEIGHT - 110){
            Gdx.app.log("tag", "Advance all levels.");
            game.advanceAll();
            return false;
        }

        for(MenuButton mbutton: buttons){
            if(mbutton.pressed(x, y)){
                Gdx.app.log("tag", "ID: " + mbutton.id);
                //String buttonText = mbutton.getLabel().getText().toString();
                if(mbutton.id == 3){
                    Gdx.app.log("tag", "free game");
                    game.goToFreeGame();
                } else if(mbutton.id == 2){
                    Gdx.app.log("tag", "level");
                    //game.goToFirstLevel();
                    game.goToLevelScreen();
                } else if(mbutton.id == 1){
                    game.goToAboutScreen();
                } else if(mbutton.id == 0){
                    Gdx.app.log("tag", "end");
                    Gdx.app.exit();
                    //game.setToEnd = true;
                }


            }
        }

        /*if(game.setToEnd){
            game.end();
        }*/
        return true;
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
}
