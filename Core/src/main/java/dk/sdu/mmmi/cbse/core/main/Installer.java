package dk.sdu.mmmi.cbse.core.main;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.openide.modules.ModuleInstall;

/**
 *
 * @author Group 7
 */
public class Installer extends ModuleInstall {

	private static Game g;

	@Override
	public void restored() {
            /*
            * fix for test. 
            * when running tests the working dir is wrong , creating unexspected
            * results
            */
            String dir = System.getProperty( "user.dir");
            if(!dir.contains("target"))
            {
                System.setProperty( "user.dir",dir+"/target/straightupwizardbeansmodules/");
            }
		g = new Game();

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "StraightUp Wizard";
		cfg.width = 1088;
		cfg.height = 704;
		cfg.useGL30 = false;
		cfg.resizable = false;

		new LwjglApplication(g, cfg);
	}
}
