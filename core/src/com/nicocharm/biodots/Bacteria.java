package com.nicocharm.biodots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Array;
import com.nicocharm.biodots.screens.PlayScreen;
import com.nicocharm.biodots.screens.TutorialScreen;

import java.util.Random;


public class Bacteria extends Actor {

    // tipos de bacterias
    public static final short BACTERIA_BLUE = 1;
    public static final short BACTERIA_RED = 2;
    public static final short BACTERIA_ORANGE = 3;
    public static final short BACTERIA_PINK = 4;
    public static final short BACTERIA_GREEN = 5;

    // ID para que puedan ser reconocidas por el antibotico
    public static int ID_COUNT = 0;
    public int ID;

    // un target, una cuenta desde el ultimo y el limite para cambiar
    private Vector2 target;
    private float targetTimer;
    private float targetTimerLimit;

    //el path de la imagen y el tipo de bacteria
    private String path;
    private short type;
    private Vector2 savedVelocity;

    public short getType() {
        return type;
    }

    //llevo la cuenta de si se está reproduciendo
    private boolean reproducing;
    //cuento como va la función seno, y escalo acorde con repScale
    private int scaleCount;
    private float repScale;

    //me divido ya?
    public boolean isDividing() {
        return dividing;
    }
    private boolean dividing;

    //me mataron?
    public boolean isDead() {
        return isDead;
    }
    private boolean isDead;

    // qué probabilidad tengo de que me maten?
    public float getpOfDying() {return pOfDying;}
    private float pOfDying;

    // so they can be trapped in a block
    private Bounds bounds;
    private Block block;
    private boolean locked;

    private float savedAngle;

    public Bacteria (PlayScreen screen, float x, float y, short type, float pOfDying){
        super(screen, x, y);

        ID = Bacteria.ID_COUNT;
        Bacteria.ID_COUNT++; // aumento en 1 la ID general

        Filter filter = new Filter();
        filter.categoryBits = screen.BACTERIA_BIT; //soy bacteria
        filter.maskBits = screen.DEFAULT_BIT; //choco con cosas NO bacteria
        fixture.setFilterData(filter);
        this.type = type;

        scale = 0.37f;
        width = 350; //width de la imagen que creé
        height = 512; //height de la imagen
        setPath(type); //dependiendo de mi tipo switcheo las imagenes

        bounds = screen.getArena();
        target = getNewTarget(); //un nuevo target

        //mi v inicial es ir de donde estoy a mi target
        body.setLinearVelocity(target.cpy().sub(body.getPosition()));
        //mi ángulo coincide con mi velocidad inicial
        angle = body.getLinearVelocity().nor().angle();
        angle -=90;

        setVisuals(); //voy a buscar mi imagen y construyo la animación

        reproducing = false;
        dividing = false;
        repScale = 1;

        isDead = false;

        //pOfDying la asigno a un valor aleatorio
        //PERO distribuido normalmente alrededor de una media
        //esa media se la pasé al constructor y es pOfDying de madre

        Random r = new Random(); //debería optimizar
        this.pOfDying = (float)r.nextGaussian()*screen.getSettings().getMutationStDev() + pOfDying;

        //no dejo que sea más que 1 ni menos que 0
        if(this.pOfDying > 1){
            this.pOfDying = 1;
        } else if(this.pOfDying < 0.03){
            this.pOfDying = 0.03f;
        }

        locked = false;
    }

    //constructor para cosas extras que pasan en bacterias hijas
    public Bacteria(PlayScreen screen, float x, float y, short type, float pOfDying, Vector2 velocity, Vector2 target){
        this(screen, x, y, type, pOfDying);

        body.setLinearVelocity(velocity); //conservo la velocidad

        this.targetTimerLimit = 0.5f; //pasa medio segundo hasta el próximo
        this.target = target; //conservo el target

        //el ángulo también se mantiene
        angle = body.getLinearVelocity().nor().angle();
        angle -=90;
    }

    // simplemente busco la textura, seteo la animación y la inicializo
    @Override
    protected void setVisuals() {
        /*setTexture((Texture)screen.game.manager.get(path, Texture.class));
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 9; i++){
            frames.add(new TextureRegion(getTexture(), 0, i*(int)height, (int)width, (int)height));
        }
        for(int i = 8; i >= 0; i--){
            frames.add(new TextureRegion(getTexture(), 0, i*(int)height, (int)width, (int)height));
        }*/

        TextureAtlas atlas = (TextureAtlas)screen.game.manager.get(path, TextureAtlas.class);
        frames = new Array<TextureRegion>();
        for(int i = 1; i <= 9; i++){
            frames.add(atlas.findRegion("f" + i));
        }
        for(int i = 8; i >= 2; i--){
            frames.add(atlas.findRegion("f" + i));
        }

        animation = new Animation<TextureRegion>(1/40f, frames);

    }

    public void update(float delta, boolean playable){
        timer += delta; //cuentan las bacterias
        targetTimer += delta; //desde el último target

        //voy a buscar mi posición del body - para eso existe box2d
        setPosition(body.getPosition().x, body.getPosition().y);

        //velocidad y ángulo
        if(!locked){
            calculateVelocity();
            angle = body.getLinearVelocity().nor().angle();
            angle -=90;
            if(playable && !(screen.getClass() == TutorialScreen.class)) handleReproduce(); //me reproduzco? solo si estoy libre y en un nivel
        }

        //nuevo target si se cumplió el límite
        if(targetTimer> targetTimerLimit) target = getNewTarget();

        if(!playable) return;

        Grid grid = screen.getGrid();

        /* Si no tengo un bloque asociado, fijate si estoy adentro de
        * algún bloque que esté activo. Si es así, asociame a ese bloque
        * y que mis bounds sean los suyos
        * Si tengo un bloque asociado, fijate si está activo. Si no es así,
        * desasocialo y que mis bounds vuelvan a ser los de la arena
        * */
        if(!locked){
            for(Block block: grid.getBlocks()){ // 60 * 20
                if(block.isActive() && block.isTouched(getX(), getY())){
                    this.block = block;
                    locked = true;

                    // gano puntos al encerrar bacterias
                    //screen.getInfobar().updatePoints(10);

                    bounds = block.getBounds();
                    savedVelocity = body.getLinearVelocity();
                    savedAngle = angle;
                    body.setLinearVelocity(0, 0);
                    target = getNewTarget();
                }
            }
        } else if((block != null && locked)&&(!block.isActive() || !block.isTouched(getX(),getY()))){
                block = null;
                bounds = screen.getArena();
                body.setLinearVelocity(savedVelocity);
                target = getNewTarget();
                locked = false;
            }



    }

    public void render(SpriteBatch batch){
            float drawingAngle;

            if(locked){
                drawingAngle = savedAngle;
            } else {
                drawingAngle = angle;
            }
            // dibujo el frame que corresponde, con cierta escala y rotación
            TextureRegion region = animation.getKeyFrame(timer, true);
            batch.draw(region,
                    getX() - (width/2),
                    getY() - (height/2),
                    (width/2),
                    (height/2),
                    width,
                    height,
                    scale*repScale,
                    scale*repScale,
                    drawingAngle);
    }



    private Vector2 getNewTarget(){ //devuelve un nuevo target
        Random r = new Random();
        //en screen tengo métodos que definen cuáles son las coordenadas
        //válidas para esto
        Vector2 t = new Vector2(screen.getNewBacteriaX(r.nextFloat(), bounds),
                screen.getNewBacteriaY(r.nextFloat(), bounds));

        //reseteo el timer y el límite
        targetTimer = 0;
        targetTimerLimit = r.nextFloat()*6 + 4;

        return t;
    }

    private void calculateVelocity(){ // el gran algoritmo
        Vector2 targetSaved = target.cpy(); //sino modifico target

        //si estoy muy cerca del target calculo otro
        if(targetSaved.sub(body.getPosition()).len()< 100){
            target = getNewTarget();
        }

        targetSaved = target.cpy();
        //esta es la magia que hace que parezcan vivas
        Vector2 desired = targetSaved.sub(getX(), getY()).nor().scl(100);
        body.applyForce((desired.sub(body.getLinearVelocity())).limit(50), body.getWorldCenter(), true);
    }

    private void handleReproduce(){
        if(!reproducing && screen.finished()){
            return;
        }

        if(screen.random.nextDouble() < screen.getpOfRep() && !reproducing) { //muy baja probabilidad
            reproducing = true;
            scaleCount = 0; //me reproduzco y empiezo a contar la funcion seno
        } else if (reproducing){
            if(scaleCount < screen.getBacteriaScale().size){
                //paso por los valores de seno ya creados y escalo a partir de eso
                repScale = screen.getBacteriaScale().get(scaleCount);
                scaleCount++;
            } else {
                divide(); //si me reproduzco y ya se me terminó la función
            }
        }
    }

    private void divide() {
        if(screen.finished()){
            return;
        }
        //creo dos nuevas bacterias y muero yo
        screen.getBacterias().add(new Bacteria(screen, getX(), getY(), getType(), pOfDying, body.getLinearVelocity(), target));
        screen.getBacterias().add(new Bacteria(screen, getX(), getY(), getType(), pOfDying, body.getLinearVelocity(), target));
        dividing = true;

        /*Sound s = (Sound) screen.game.manager.get("division.ogg", Sound.class);
        s.play(0.3f);*/

        //pierdo puntos al dividirse
        //screen.getInfobar().updatePoints(-120);
    }


    public void setPath(short type) {
        //switch el path para cada type
        switch(type){
            case BACTERIA_BLUE:
                path = "bacteria-blue.pack";
                break;
            case BACTERIA_GREEN:
                path = "bacteria-green.pack";
                break;
            case BACTERIA_ORANGE:
                path = "bacteria-orange.pack";
                break;
            case BACTERIA_PINK:
                path = "bacteria-pink.pack";
                break;
            case BACTERIA_RED:
                path = "bacteria-red.pack";
                break;
        }
    }

    public static short randomType(){
        Random r = new Random();
        int i = r.nextInt(5);
        switch (i){
            case 0:
                return BACTERIA_RED;
            case 1:
                return BACTERIA_ORANGE;
            case 2:
                return BACTERIA_BLUE;
            case 3:
                return BACTERIA_GREEN;
            case 4:
                return BACTERIA_PINK;
            default:
                return BACTERIA_BLUE;
        }
    }

    public void die(float pOfKilling) { //me muero, quizás, segun pOfDying
        Random r = new Random();
        float f = r.nextFloat();

        Gdx.app.log("tag7", "random is " + f + ", p is " + pOfDying*pOfKilling);

        if(f < pOfDying*pOfKilling){
            Gdx.app.log("tag", "I am dying!!");
            isDead = true;
            if(screen.game.getSound()){
                Sound s = (Sound) screen.game.manager.get("kill.ogg", Sound.class);
                s.play(0.17f);
            }


        }
    }

    public String getPath() {
        return path;
    }
}
