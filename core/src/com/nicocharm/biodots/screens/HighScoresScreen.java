package com.nicocharm.biodots.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nicocharm.biodots.BioDots;
import com.nicocharm.biodots.Button;


public class HighScoresScreen implements Screen, InputProcessor {
    BioDots game;

    private Color backgroundColor;
    private Stage stage;

    public OrthographicCamera cam;
    public Viewport viewport;

    private Button button;

    private float pastY;
    private float tableHeight;
    private float padding;

    private long lastTime = -1;
    private long thisTime = -1;
    private float velocity = 0;
    private double deltaT = 0;

    Table table;
    Label.LabelStyle style;

    public HighScoresScreen(BioDots game){
        this.game = game;

        backgroundColor = new Color(0, 0 , 0.2f, 1);

        cam = new OrthographicCamera();
        // creo un viewport con mi cámara y la llevo al centro
        viewport = new FitViewport(game.WIDTH, game.HEIGHT, cam);
        cam.translate(game.WIDTH / 2, game.HEIGHT / 2);

        stage = new Stage(viewport, game.batch);

        BitmapFont font = (BitmapFont) game.manager.get("Roboto-Regular.ttf", BitmapFont.class);
        style = new Label.LabelStyle();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style.font = font;
        style.fontColor = new Color(197/255f, 215/255f, 254/255f, 1);

        String text = "Puntajes";
        Label title = new Label(text, style);
        title.setAlignment(Align.center);
        title.setFontScale(1.3f);
        GlyphLayout gl = new GlyphLayout(style.font, text);
        title.setPosition(game.WIDTH/2 - gl.width/2, game.HEIGHT - 140 - font.getLineHeight());
        stage.addActor(title);

        table = new Table();
        table.setFillParent(true);
        table.setClip(false);

        padding = title.getStyle().font.getLineHeight()*1.3f + 210;
        tableHeight = padding;

        stage.addActor(table);

        float scale = 0.75f;
        float height = ((Texture)game.manager.get("menu-button.png", Texture.class)).getHeight() * scale;
        button = new Button(game, game.WIDTH/2, game.HEIGHT - tableHeight - height + 100, "menu-button.png", "VOLVER", scale, 0);
        stage.addActor(button.getLabel());
    }

    @Override
    public void show() {
        table.clear();
        tableHeight = padding + 100;

        Preferences preferences = Gdx.app.getPreferences("BioDots");

        String s = preferences.getString("scoreFree", "Free Game&" + 0);
        String[] values = s.split("&");
        Label l = new Label("Juego libre", style);
        l.setFontScale(0.8f);
        table.add(l).expandX().padTop(30);;
        l = new Label(values[1], style);
        l.setFontScale(0.8f);
        table.add(l).expandX().padTop(30);;
        table.row();

        for(int i = 0; i<game.getLevels().size; i++){
            if(preferences.contains("score" + i)){
                Gdx.app.log("tag", "It's there");
            } else {
                Gdx.app.log("tag", "It's not there");
            }
            s = preferences.getString("score" + i, i + "&" + 0);
            Gdx.app.log("tag", s);
            values = s.split("&");
            l = new Label("Nivel " + values[0], style);
            l.setFontScale(0.8f);
            table.add(l).expandX().padTop(30);
            l = new Label(values[1], style);
            l.setFontScale(0.8f);
            table.add(l).expandX().padTop(30);;
            table.row();
        }

        table.top().padTop(padding);
        table.pack();

        for(int i = 0; i < table.getRows(); i++){
            tableHeight += table.getRowHeight(i);
        }

        float scale = 0.75f;
        float height = ((Texture)game.manager.get("menu-button.png", Texture.class)).getHeight() * scale;
        button.setButtonPosition(game.WIDTH/2, game.HEIGHT - tableHeight - height + 100);
    }

    public void setAsInput(){
        InputMultiplexer multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        multiplexer.addProcessor(this);
        multiplexer.addProcessor(stage);
        cam.position.set(game.WIDTH/2, game.HEIGHT/2, 0);
        pastY = -1;
        //Gdx.input.setInputProcessor(stage);
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
        pastY = -1;

        Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 v2 = cam.unproject(v);
        float x = v2.x;
        float y = v2.y;

        if(this.button.pressed(x, y)){
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
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update();

        game.batch.begin();
        button.render(game.batch);
        game.batch.end();

        stage.draw();
    }

    private void update() {
        lastTime = thisTime;
        thisTime = System.nanoTime();
        deltaT = ((double)(thisTime - lastTime))/1000000000.0;

        if(tableHeight < game.HEIGHT) return;

        if(Gdx.input.isTouched()) {
            Vector3 v = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 v2 = cam.unproject(v);
            float currentY = v2.y;

            if(pastY == -1 || lastTime == -1){
                pastY = currentY;
                return;
            }

            float deltaY = pastY - currentY;

            if(cam.position.y + deltaY > game.HEIGHT/2){
                cam.position.set(cam.position.x, game.HEIGHT/2, cam.position.z);
                cam.update();
                return;
            } else if(cam.position.y + deltaY < 3*game.HEIGHT/2 - tableHeight - button.getScaledHeight() - 100){
                cam.position.set(cam.position.x, 3*game.HEIGHT/2  - tableHeight - button.getScaledHeight() - 100, cam.position.z);
                cam.update();
                return;
            }

            cam.translate(0, deltaY);
            cam.update();


            velocity = (float)(deltaY / deltaT); //virtual px per second

        } else if (velocity != 0){
            float deltaY = velocity*(float)deltaT;

            cam.translate(0, deltaY);
            cam.update();
            velocity *= 0.94f;
            //if(velocity < 0.04f && velocity > -0.04f) velocity = 0;

            if(cam.position.y + deltaY > game.HEIGHT/2){
                cam.position.set(cam.position.x, game.HEIGHT/2, cam.position.z);
                velocity = 0;
                cam.update();
            } else if(cam.position.y + deltaY < 3*game.HEIGHT/2 - tableHeight - button.getScaledHeight() - 100){
                cam.position.set(cam.position.x, 3*game.HEIGHT/2  - tableHeight - button.getScaledHeight() - 100, cam.position.z);
                velocity = 0;
                cam.update();
            }
        }
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
