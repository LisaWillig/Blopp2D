package com.mygdx.blopp;
import com.mygdx.blopp.MyGestureListener;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.physics.box2d.utils.*;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;

import static com.mygdx.blopp.MainMenuScreen.txtVal;

import static utils.constants.PPM; 
import static utils.constants.*;

import java.util.HashMap; 
import java.util.Map;
import java.util.Arrays;

public class GameScreen implements Screen {
  	final Blopp game;
	
	Sound dropSound;
	Music BackgroundMusic;
	
	OrthographicCamera camera;
	
	CircleShape PlanetShape;
	CircleShape BloppShape;
	CircleShape ChildrenShape, ChildrenShape2;
	
	World world;
	Box2DDebugRenderer debugRenderer;
	ShapeRenderer shapeRenderer;
	
	BodyDef BloppDef, PlanetDef, PlanetDef2, ChildrenDef,ChildrenDef2;
	Body Blopp, Planet1, Planet2, Children1, Children2, Planet3;
	FixtureDef BloppFixtureDef, PlanetFixtureDef, ChildrenFixtureDef;
	
	RopeJoint wd;
	Array<Body> AllBodies;
	ArrayList<Body> Planets;
	ArrayList<Body> Bodies, Children;
	
	Color Col;

	Texture MaxCol, MinCol;
	
	Vector2 Distance,Gravity, Touch,VelocityBlopp, Impulse, Coordinate;
	Vector3 TouchPos, BloppPos, CoordinatePix,GridVector, Planet1Pos, Planet2Pos;
	float eatingRadius;
	
    ShapeRenderer shapeDebugger;

	long lastSpawnTime;
	int VELOCITY_ITERATIONS=6,POSITION_ITERATIONS=2, Button;

	double GravityMagnitude;

	
	public GameScreen(final Blopp gam) {
		this.game = gam;
	
		//World
		world= new World(new Vector2(0,0),true); // Create Game world
		debugRenderer= new Box2DDebugRenderer();
		shapeDebugger=new ShapeRenderer();
		
		Bodies=new ArrayList<Body>();
		AllBodies= new Array<Body>();
		Children = new ArrayList<Body>();
		Planets=new ArrayList<Body>();
		
		TouchPos = new Vector3();
		
		//Sound
		dropSound = Gdx.audio.newSound(Gdx.files.internal("BinaryBlackHoles_Large.wav"));
		BackgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Freesound_107847__cormi__restless-01a.wav"));
		BackgroundMusic.setLooping(true);
		
		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, (Gdx.graphics.getWidth()/PPM), (Gdx.graphics.getHeight()/PPM));
		
		// create Bodies of World
		createBlopp();
		Blopp.setLinearDamping(4);
		Blopp.setAngularDamping(4);
		 
		//GravityPlanets
		Planet1=AddPlanet(200, 200, 50);
		Planet2=AddPlanet(450, 300, 50);
		Planet3=AddPlanet(300, 650, 30);
		
		
		for (int i = 0; i<10; i++){
			AddChildren(ThreadLocalRandom.current().nextInt(0, 300),
					ThreadLocalRandom.current().nextInt(0, 300),
					ThreadLocalRandom.current().nextInt(1, 30));
		}
		
		
		world.getBodies(AllBodies);
		camera.update();
	}

	private void createBlopp() {
		BloppDef = new BodyDef();
		BloppDef.type = BodyType.DynamicBody;
		BloppDef.position.set(100/PPM, 300/PPM);
		BloppShape = new CircleShape(); 
		BloppShape.setRadius(10/PPM);
		
		BloppFixtureDef = new FixtureDef();
		BloppFixtureDef.shape = BloppShape;
		BloppFixtureDef.density = 100f; 
		BloppFixtureDef.friction = 100f;
		BloppFixtureDef.restitution = 0.5f; 

		Blopp = world.createBody(BloppDef); 
		Bodies.add(Blopp);
		Blopp.createFixture(BloppFixtureDef);
	}

	@Override
	public void render(float delta) {

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		doPhysicsStep(delta);
	
		camera.update();
		debugRenderer.render(world, camera.combined);

		//TouchControl
		Gdx.input.setInputProcessor(new GestureDetector(new MyGestureListener(this)));
	    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
		   TouchPos = new Vector3 (Gdx.input.getX(), Gdx.input.getY(), 0);
		}
		
		//calculate correct Position _ Transformation of coordinate systems
		camera.unproject(TouchPos);
		Touch =new Vector2(TouchPos.x, TouchPos.y);
		
		//Calculate Impulse caused by Touch
		ImpulseMag = Blopp.getMass()*TargetVelocity;
		Impulse= new Vector2();
		Impulse.set(Touch).sub(Blopp.getPosition());
		Impulse.nor();
		Impulse.scl(ImpulseMag);

		eatingRadius = calculateEatingRadius(Blopp);
		for (Body body : Bodies){
			for (Body Secondbody : AllBodies){
				if (body != Secondbody){
					body.applyForceToCenter(AddGravity(Secondbody, body),true);
				}
			}
		}

		for (Body child : new ArrayList<Body>(Children)){
			float xD=Math.abs(child.getPosition().x)-Math.abs(Blopp.getPosition().x);
			float yD=Math.abs(child.getPosition().y)-Math.abs(Blopp.getPosition().y);
		
			float sqDist=xD*xD+yD*yD-eatingRadius;
		
			boolean collision = sqDist <=((child.getFixtureList().first().getShape().getRadius()+BloppShape.getRadius())*
					(child.getFixtureList().first().getShape().getRadius()+BloppShape.getRadius()));
			if (collision == true){
				increaseBlopp(Blopp, child);
				System.out.println(eatingRadius);
			}
		}
		
		
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
			{Blopp.applyLinearImpulse(Impulse,  Blopp.getPosition(), true);}
		Planet1.setTransform(200/PPM, 200/PPM, 0);	
		Planet2.setTransform(450/PPM,  300/PPM, 0);
	
		game.batch.setProjectionMatrix(camera.combined);
		shapeDebugger=new ShapeRenderer();
		shapeRenderer= new ShapeRenderer();
		game.batch.begin();
		Gdx.gl20.glLineWidth(5);
		game.batch.end();
		}

		
	public Vector2 AddGravity(Body FirstBody, Body SecondBody){
		Distance=SecondBody.getPosition().sub(FirstBody.getPosition());
		GravityMagnitude= SecondBody.getMass()* GravityConstant * FirstBody.getMass()/(Math.pow(Distance.len(),2));
		Gravity= Distance.scl((float)GravityMagnitude);
		return Gravity; 
	}
	
	public void AddGravity2(Body FirstBody, Body SecondBody){
		Distance=SecondBody.getPosition().sub(FirstBody.getPosition());
		GravityMagnitude= SecondBody.getMass()* GravityConstant * FirstBody.getMass()/(Math.pow(Distance.len(),2));
		Gravity= Distance.scl((float)GravityMagnitude);
		FirstBody.applyForceToCenter(Gravity, true);
		SecondBody.applyForceToCenter(Gravity, true);
	}
	

	public Body AddPlanet(float PosX, float PosY, float Radius){
		
		PlanetDef= new BodyDef();
		PlanetDef.type= BodyType.DynamicBody;
		PlanetDef.position.set(PosX/PPM,PosY/PPM);
		
		PlanetShape=new CircleShape();
		PlanetShape.setRadius(Radius/PPM);
		PlanetFixtureDef= new FixtureDef();
		PlanetFixtureDef.shape=PlanetShape;
		PlanetFixtureDef.density=10f;
		PlanetFixtureDef.friction=1;
		PlanetFixtureDef.restitution=0.5f;
		
		Body Planet;
		Planet= world.createBody(PlanetDef);
		Planet.createFixture(PlanetFixtureDef);
		Planets.add(Planet);
		return Planet;
		
	}
	
	public float calculateEatingRadius(Body Blopp){
		return (float) (Blopp.getFixtureList().first().getShape().getRadius()*2);
	}
	
	public Body increaseBlopp(Body Blopp, Body Added){
		if (Blopp.getFixtureList().size != 0){
			float oldRadius = Blopp.getFixtureList().first().getShape().getRadius();
			float addedRadius = Added.getFixtureList().first().getShape().getRadius();
			Blopp.getFixtureList().first().getShape().setRadius(oldRadius+addedRadius);
			if (Added.getFixtureList().size != 0){
				Added.destroyFixture(Added.getFixtureList().first());
				Children.remove(Added);
				world.destroyBody(Added);
			}
			dropSound.play();
		}
		return Blopp;
	}
	
	
	public Body AddChildren(float PosX, float PosY, float Radius){

		ChildrenDef= new BodyDef();
		ChildrenDef.type=BodyType.DynamicBody;
		ChildrenDef.position.set(PosX/PPM,PosY/PPM);
		ChildrenShape=new CircleShape();
		ChildrenShape.setRadius(Radius/PPM);
				
		ChildrenFixtureDef= new FixtureDef();
		ChildrenFixtureDef.shape=ChildrenShape;
		ChildrenFixtureDef.density=1f;
		ChildrenFixtureDef.friction=1;
		ChildrenFixtureDef.restitution=0.5f;		
		
		Body Child;
		Child=world.createBody(ChildrenDef);
		Child.createFixture(ChildrenFixtureDef);
		
		Bodies.add(Child);
		Children.add(Child);
		
		return Child;
	}
	
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// start the playback of the background music
		// when the screen is shown
		BackgroundMusic.play();
	}

	@Override
	public void hide() {
	}

	public void doPhysicsStep(float deltaTime) {
		// fixed time step
		// max frame time to avoid spiral of death (on slow devices)
		float frameTime = Math.min(deltaTime, 0.25f);
		accumulator += frameTime;
		while (accumulator >= TIME_STEP) {
			world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
			accumulator -= TIME_STEP;
		}
    }
	
	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {

		dropSound.dispose();
		BackgroundMusic.dispose();
		for (Body body : AllBodies){
			body.getFixtureList().first().getShape().dispose();
		}	
	}
}