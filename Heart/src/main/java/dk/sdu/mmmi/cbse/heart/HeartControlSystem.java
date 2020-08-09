package dk.sdu.mmmi.cbse.heart;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.HitboxPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.LifePart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.TexturePart;
import dk.sdu.mmmi.cbse.common.services.IAudioService;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.commonheart.Heart;
import dk.sdu.mmmi.cbse.commonplayer.Player;
import java.io.File;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;
import dk.sdu.mmmi.cbse.commonheart.services.IHeartSpawningService;
import org.openide.util.Lookup;

/**
 * The HeartControlSystem in the Heart component handles creating heart entities
 * and collison with a heart entity.
 *
 * @author Group 7
 */
@ServiceProviders(value = {
	@ServiceProvider(service = IEntityProcessingService.class)
	,
	@ServiceProvider(service = IPostEntityProcessingService.class)
	,
	@ServiceProvider(service = IHeartSpawningService.class)
	,
	@ServiceProvider(service = IGamePluginService.class)
})
public class HeartControlSystem implements IEntityProcessingService, IPostEntityProcessingService, IHeartSpawningService, IGamePluginService {

	private Lookup lookup = Lookup.getDefault();

	/**
	 * The process method from IEntityProcessingService handles heart
	 * functionality in main game loop
	 *
	 * @param gameData
	 * @param world
	 */
	@Override
	public void process(GameData gameData, World world) {
		//Do nothing
	}

	/**
	 * The postProcess method from IPostEntityProcessingService handles collision
	 * with hearts.
	 *
	 * @param gameData
	 * @param world
	 */
	@Override
	public void postProcess(GameData gameData, World world) {
		//Loops through all heart entities, so that it is possible to check 
		//for all hearts in the game, if the player has collided with one of them
		for (Entity heart : world.getEntities(Heart.class)) {
			//Gets the HitboxPart (collision part) for the heart
			HitboxPart heartHitbox = heart.getPart(HitboxPart.class);

			//Handles collision for all entities colliding with the heart
			for (Entity entity : heartHitbox.getCollidingEntities()) {
				//Get the type of the current entity
				Class type = entity.getClass();

				//Handles collision with player
				if (type.equals(Player.class)) {
					//Get the player life part
					LifePart lifePart = entity.getPart(LifePart.class);

					//Increase the life part with 2
					lifePart.increaseLife(2);

					//Sound for picking up the heart
					IAudioService audio = getAudioService();
					if (audio != null) {
						audio.playSound("dk-sdu-mmmi-cbse-Heart.jar!/assets/pickUpHeart.mp3");
					}

					//Removes heart when colliding with player
					world.removeEntity(heart);
				}
			}
		}
	}

	/**
	 * This method creates a heart, those used in the game. The method gives the
	 * heart its width and height, a position, a texture/sprite/picture and the
	 * ability to handle collision.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return a heart as an entity
	 */
	@Override
	public Entity createHeart(float x, float y) {

		//Width and height of the heart. Variables used for collision
		float width = 40;
		float height = 32;

		//Create a new heart
		Entity heart = new Heart();

		//Add a PosistionPart to the heart, so that the heart has a posistion on the map
		heart.add(new PositionPart(x, y));

		//Add a CollisionPart to the heart, so that the heart can handle collision
		heart.add(new HitboxPart(width, height, 0, 0, x, y));

		//Create a path to the heart texture/sprite/asset/picture in the assets folder
		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Heart.jar!/assets/hearts.png");

		//Add heart-texture/sprite to the heart with priority 2. Priority 1 is first
		//to be displayed/drawn and priority 2 is second to be displayed on the screen 
		int priority = 2;
		heart.add(new TexturePart(path, priority));
		TexturePart texturePart = heart.getPart(TexturePart.class);

		//Add a cut to the texturePart. The cut says what on the texture/picture to display 
		texturePart.addCut("DEFAULT", 5, 4, 30, 0, 0, 0);
		texturePart.setCurrentCut("DEFAULT");
		texturePart.updatePosition(heart);

		//Return the heart entity
		return heart;
	}

	private IAudioService getAudioService() {
		return lookup.lookup(IAudioService.class);
	}

	@Override
	public void start(GameData gameData, World world) {
		//Do nothing
	}

	@Override
	public void stop(GameData gameData, World world) {
		//Remove all hearts
		for (Entity heart : world.getEntities(Heart.class)) {
			world.removeEntity(heart);
		}
	}

}
