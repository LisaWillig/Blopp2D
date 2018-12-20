package com.mygdx.blopp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;


public class MainMenuScreen implements Screen {
	
    final Blopp game;
   
	OrthographicCamera camera;
	private TextButton btnStart, btnHighscore;
	public static TextField txMassPlanet;
	public static String userName;
	Stage stage=new Stage();
	Skin skin= new Skin(Gdx.files.internal("uiskin.json"));
	
	
	public MainMenuScreen(final Blopp gam) {
		game = gam;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 414, 736);
		
		
		Gdx.input.setInputProcessor(stage);
		
		btnStart= new TextButton("Start Game", skin);
		btnStart.setPosition(162,500);
		btnStart.setSize(100,50);
		
		btnHighscore= new TextButton("Highscore", skin);
		btnHighscore.setPosition(162,350);
		btnHighscore.setSize(100,50);
		
		txMassPlanet=new TextField("Enter name here", skin);
		txMassPlanet.setPosition(100,50);
		txMassPlanet.setSize(100,50);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		
		game.batch.begin();
		game.font.draw(game.batch, "Welcome to Blopp ", 150, 650);
		game.font.draw(game.batch, "Tap anywhere to begin!", 300, 200);
		game.batch.end();
		
		
		btnStart.addListener(new ClickListener(){
			public boolean touchDown(InputEvent e, float x, float y, int point, int button){
				//System.out.println(txMassPlanet.getText());
				return true;
			}
		});
		
		
		
		txMassPlanet.setTextFieldListener(new TextFieldListener() {
			
			public void keyTyped(TextField txMassPlanet, char key) {
				 userName=txMassPlanet.getText();
				//if (Gdx.input.isKeyPressed(Keys.ANY_KEY)){
					//System.out.println(true);
				//}
				// TODO Auto-generated method stub
				
			}
		});
		
		stage.addActor(txMassPlanet);
		stage.addActor(btnStart);
		stage.addActor(btnHighscore);
		
		System.out.println(userName);
	
		stage.act();
		stage.draw();
		
		if (btnStart.isPressed()==true) {
			game.setScreen(new GameScreen(game));
			dispose();}
		
		if (btnHighscore.isPressed()==true) {
			game.setScreen(new HighscoreScreen(game));
			dispose();}
		
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {

		
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}