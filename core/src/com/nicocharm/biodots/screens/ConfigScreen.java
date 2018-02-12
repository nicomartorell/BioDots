package com.nicocharm.biodots.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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

/**
 * Created by Nicolas on 10/02/2018.
 */

public class ConfigScreen implements Screen, InputProcessor {

    BioDots game;

    private Color backgroundColor;
    private Stage stage;

    public OrthographicCamera cam;
    public Viewport viewport;

    private Button button;

    private Array<Label> labels;
    private Array<Button> buttons;

    public ConfigScreen(BioDots game){
        this.game = game;

        backgroundColor = new Color(0, 0 , 0.2f, 1);

        cam = new OrthographicCamera();
        // creo un viewport con mi cámara y la llevo al centro
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, cam);
        cam.translate(game.WIDTH / 2, game.HEIGHT / 2);

        stage = new Stage(viewport, game.batch);

        BitmapFont font = (BitmapFont) game.manager.get("Roboto-Regular.ttf", BitmapFont.class);
        Label.LabelStyle style = new Label.LabelStyle();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = new Color(197/255f, 215/255f, 254/255f, 1);

        String text = "Configuración";
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

        buttons = new Array<Button>();
        labels = new Array<Label>();

        String[] buttonNames = {"Efectos", "Música"};

        int margin = 150;

        for(int i = 0; i < buttonNames.length; i++){
            Label l = new Label(buttonNames[i], style);
            l.setFontScale(0.8f);
            float height1 = l.getStyle().font.getLineHeight() + 40;
            l.setPosition(margin, game.HEIGHT*0.74f - height1*i);
            stage.addActor(l);
            labels.add(l);
        }

        for(int i = 0; i < buttonNames.length; i++){
            Button l = new Button(game, game.WIDTH*(7/8f), 630, null, "Si", 0.8f, "Roboto-Regular.ttf", i + 1);
            float height1 = l.getLabel().getStyle().font.getLineHeight() + 40;
            l.getLabel().setAlignment(Align.right);
            l.setButtonPosition(game.WIDTH - margin, game.HEIGHT*0.74f + height1*(0.5f - i) - 40);
            stage.addActor(l.getLabel());
            buttons.add(l);
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
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 v2 = cam.unproject(v);
        float x = v2.x;
        float y = v2.y;

        if(this.button.pressed(x, y)){
            game.setToMenu(true);
            return false;
        }

        for(Button b: buttons){
            if(b.pressed(x, y)){
                if(b.getId() == 1){
                    if(game.getSound()){
                        game.setSound(false);
                        b.setText("No");
                    } else if(!game.getSound()){
                        game.setSound(true);
                        b.setText("Si");
                    }
                } else if(b.getId() == 2){
                    if(game.getMusic()){
                        game.setMusic(false);
                        b.setText("No");
                        final Music s = (Music) game.manager.get("menu-music.ogg", Music.class);
                        s.stop();
                    } else if(!game.getMusic()){
                        game.setMusic(true);
                        b.setText("Si");
                    }
                }
            }
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
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        button.render(game.batch);
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
    }
}
