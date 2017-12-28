package com.nicocharm.biodots.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nicocharm.biodots.BioDots;
import com.nicocharm.biodots.MenuButton;


public class MainMenu implements Screen, InputProcessor {

    public static final String JUGAR = "JUEGO RÁPIDO";
    public static final String DESAFIO = "DESAFIO";
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

    private Array<MenuButton> buttons;
    private String[] texts = {MainMenu.SALIR, MainMenu.ACERCA_DE, MainMenu.DESAFIO, MainMenu.JUGAR};

    public MainMenu(BioDots game){
        this.game = game;
        backgroundColor = new Color(0, 0 , 0.2f, 1);

        cam = new OrthographicCamera();
        // creo un viewport con mi cámara y la llevo al centro
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, cam);
        cam.translate(game.WIDTH / 2, game.HEIGHT / 2);

        stage = new Stage(viewport, game.batch);

        BitmapFont font = BioDots.fontManager.get("GloriaHallelujah.ttf", 240);
        Label.LabelStyle style = new Label.LabelStyle();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = new Color(197/255f, 215/255f, 254/255f, 1);

        String text = "Bio Dots";
        Label title = new Label(text, style);
        title.setAlignment(Align.center);
        GlyphLayout gl = new GlyphLayout(style.font, text);
        title.setPosition(game.WIDTH/2 - gl.width/2, game.HEIGHT - 20 - font.getLineHeight());

        stage.addActor(title);


        buttons = new Array<MenuButton>();

        float yoffset = 250f;
        float initialHeight = game.HEIGHT*0.8f;
        float buttonSpace = (initialHeight-yoffset)/texts.length + 30;


        for(int i = 0; i < texts.length; i++){
            MenuButton button = new MenuButton(null, game.WIDTH/2, yoffset + buttonSpace*i, texts[i], 0.85f);
            buttons.add(button);
            stage.addActor(button.getLabel());
        }

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        for(MenuButton button: buttons) button.render(game.batch);
        game.batch.end();

        stage.draw();
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
        for(MenuButton button: buttons) button.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
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

        for(MenuButton mbutton: buttons){
            if(mbutton.pressed(x, y)){
                //String buttonText = mbutton.getLabel().getText().toString();
                if(mbutton.id == 3){
                    game.goToFreeGame();
                } else if(mbutton.id == 2){
                    game.goToFirstLevel();
                } else if(mbutton.id == 0){
                    game.setToEnd = true;
                }


            }
        }

        if(game.setToEnd){
            game.end();
        }
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
