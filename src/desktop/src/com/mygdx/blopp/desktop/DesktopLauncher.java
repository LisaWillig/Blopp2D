package com.mygdx.blopp.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.blopp.Blopp;
import com.badlogic.gdx.*;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
	    config.title = "Blopp";
	    config.width = 414;
	    config.height =736;
		new LwjglApplication(new Blopp(), config);
	}
}
