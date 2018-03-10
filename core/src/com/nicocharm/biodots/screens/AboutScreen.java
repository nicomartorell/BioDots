package com.nicocharm.biodots.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nicocharm.biodots.BioDots;
import com.nicocharm.biodots.Button;

/**
 * Created by Nicolas on 01/01/2018.
 */

public class AboutScreen implements Screen, InputProcessor {

    private BioDots game;
    private Color backgroundColor;
    private Stage stage;
    public OrthographicCamera cam;
    public Viewport viewport;
    private long firstTouch;
    private long secondTouch;
    private float pastY;
    private float tableHeight;
    private float padding;
    private Button button;

    private long lastTime = -1;
    private long thisTime = -1;
    private float velocity = 0;
    private double deltaT = 0;


    public AboutScreen(BioDots game){
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

        BitmapFont font2 = (BitmapFont) game.manager.get("Roboto-Bold.ttf", BitmapFont.class);
        Label.LabelStyle style2 = new Label.LabelStyle();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        style2.font = font2;
        style2.fontColor = new Color(248/255f, 95/255f, 95/255f, 1);

        String text = "**¿Qué es Antibium?\n" +
                "\n" +
                "Bio Dots es un juego de estrategia\n" +
                "y habilidad, basado vagamente en\n" +
                "conceptos de biología. La idea\n" +
                "del juego es mostrar lo difíciles\n" +
                "e incontrolables que pueden\n" +
                "ser las bacterias y otros organis-\n" +
                "mos a pesar de nuestros mejores\n" +
                "esfuerzos para controlarlos.\n" +
                "\n" +
                "Las bacterias de este juego\n" +
                "evolucionan como todos los\n" +
                "seres vivos en nuestro planeta.\n" +
                "Eso quiere decir que cada\n" +
                "bacteria es más o menos pro-\n" +
                "pensa a morir al ser atacada por\n" +
                "un antibiótico. Las que sobreviven\n" +
                "más (por pura suerte!) tienen\n" +
                "más tiempo para reproducirse y\n" +
                "sus hijas también tendrán la mis-\n" +
                "ma mutación que las deja sobrevivir\n" +
                "más. Entonces pasa el tiempo...\n" +
                "\n" +
                "\n" +
                "**¿Por qué un nivel es más\n" +
                "**difícil al pasar el tiempo?\n" +
                "\n" +
                "El juego no tiene un reloj que\n" +
                "va aumentando la dificultad al\n" +
                "pasar el tiempo. Nada parecido.\n" +
                "Pasa lo mismo que en el mundo\n" +
                "real. Como las bacterias que más\n" +
                "se reproducen son las más capaces\n" +
                "de resistir a los antibióticos, al\n" +
                "pasar el tiempo solo quedan las\n" +
                "bacterias más fuertes. Es decir, si\n" +
                "el jugador no se apura a matar a\n" +
                "las bacterias, el porcentaje de\n" +
                "bacterias que mueren con cada\n" +
                "antibiótico (visible arriba a la\n" +
                "izquierda) va a ir bajando, hasta\n" +
                "que el juego se vuelva casi\n" +
                "imposible y el jugador pierda.\n" +
                "\n" +
                "\n" +
                "**¿Por qué esto es importante?\n" +
                "\n" +
                "Este proceso es exactamente el\n" +
                "que pasa en nuestro cuerpo\n" +
                "cuando tenemos alguna infección.\n" +
                "Las bacterias se reproducen. Al\n" +
                "tomar un antibiótico, matamos\n" +
                "por azar a muchas, y quedan\n" +
                "algunas otras. En general, quedan\n" +
                "las que son más fuertes. Si uno\n" +
                "deja de tomar el antibiótico\n" +
                "antes de tiempo, las bacterias\n" +
                "se reproducen y se vuelven casi\n" +
                "imposibles de matar. En cambio, si\n" +
                "uno sigue aplicando el antibiótico\n" +
                "sin dejar pasar mucho tiempo, lo-\n" +
                "gra matar a todas las bacterias\n" +
                "que todavía viven, no queda nadie\n" +
                "para reproducirse, y el proceso\n" +
                "se corta a la mitad. Ahí les\n" +
                "ganamos a las bacterias.\n" +
                "\n" +
                "\n" +
                "**¿Puedo dejar de leer y jugar?\n" +
                "\n" +
                "¡Por supuesto! Si leíste hasta\n" +
                "acá muchas gracias. Nunca dejes\n" +
                "de tomar un antibiótico antes\n" +
                "de tiempo, ni tomes un antibiótico\n" +
                "cuando no te hace falta: ¡eso\n" +
                "sólo entrena a las bacterias en\n" +
                "tu cuerpo para sobrevivir mejor!\n" +
                "Cuando lo necesites en serio,\n" +
                "quizás ya no sirva.\n" +
                "\n" +
                "Pero este juego también es para\n" +
                "divertirse y matar bacterias de to-\n" +
                "dos los colores y formas posibles.\n" +
                "\n" +
                "**Suerte y ¡A jugar!";
        String[] lines = text.split("\n");
        Table table = new Table();
        table.setFillParent(true);
        table.setClip(false);

        Gdx.app.log("tag", "About lines: " + lines.length);

        padding = 100;
        tableHeight = 2*padding;

        for(int i = 0; i < lines.length; i++){
            Label.LabelStyle s = style;
            if(lines[i].startsWith("**")){
                s = style2;
                lines[i] = lines[i].replace("**", "");
            }
            Label l = new Label(lines[i], s);
            l.setFontScale(0.75f);
            table.add(l).left().row();
        }


        table.top().padTop(padding).padLeft(25);
        //table.align(Align.left);
        table.pack();
        stage.addActor(table);

        for(int i = 0; i < table.getRows(); i++){
            tableHeight += table.getRowHeight(i);
        }

        //tableHeight = 2*padding + table.getRowHeight(0)*table.getRows();

        float scale = 0.75f;
        float height = ((Texture)game.manager.get("menu-button.png", Texture.class)).getHeight() * scale;
        button = new Button(game, game.WIDTH/2, game.HEIGHT - tableHeight - height + padding, "menu-button.png", "VOLVER", scale, 0);
        stage.addActor(button.getLabel());
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
    public void render(float delta) {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update();

        game.batch.begin();
        button.render(game.batch);
        game.batch.end();

        stage.draw();

        /*stage.act();*/
    }

    private void update() {
        lastTime = thisTime;
        thisTime = System.nanoTime();
        deltaT = ((double)(thisTime - lastTime))/1000000000.0;

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
        firstTouch = System.nanoTime();

        /*Vector3 v = new Vector3(screenX, screenY, 0);
        Vector3 v2 = cam.unproject(v);
        //float x = v2.x;
        touchDownY = v2.y;*/
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        secondTouch = System.nanoTime();


        pastY = -1;

        Vector3 v = new Vector3(screenX, screenY, 0);
        Vector3 v2 = cam.unproject(v);
        float x = v2.x;
        float y = v2.y;

        if(this.button.pressed(x, y)){
            game.setToMenu(true);
            return false;
        }

        //proyectá la coordenada de pantalla a las virtuales
        /*Vector3 v = new Vector3(screenX, screenY, 0);
        Vector3 v2 = cam.unproject(v);
        //float x = v2.x;
        float y = v2.y;

        float deltaY = touchDownY - y;

        if(cam.position.y + deltaY > game.HEIGHT/2){
            cam.position.set(cam.position.x, game.HEIGHT/2, cam.position.z);
            cam.update();
            return false;
        } else if(cam.position.y + deltaY < game.HEIGHT/2 - stage.getHeight()){
            cam.position.set(cam.position.x, game.HEIGHT/2 - stage.getHeight(), cam.position.z);
            cam.update();
            return false;
        }

        cam.translate(0, deltaY);
        cam.update();

        Gdx.app.log("tag", "Touch up!");*/
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
        //cam.translate(0, amount);
        //cam.update();
        Gdx.app.log("tag", "Scrolled!");
        return false;
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        //viewport.update(width, height);
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
