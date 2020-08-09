package dk.sdu.mmmi.cbse.coin;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.CoinPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.HitboxPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.TexturePart;
import dk.sdu.mmmi.cbse.common.services.IAudioService;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.commoncoin.Coin;
import dk.sdu.mmmi.cbse.commonplayer.Player;
import java.io.File;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;
import dk.sdu.mmmi.cbse.commoncoin.services.ICoinSpawningService;

/**
 * The CoinControlSystem in the Coin component handles creating coin entities
 * and collison with a coin entity.
 *
 * @author Group 7
 */
@ServiceProviders(value = {
	@ServiceProvider(service = IEntityProcessingService.class)
	,
	@ServiceProvider(service = IPostEntityProcessingService.class)
	,
	@ServiceProvider(service = ICoinSpawningService.class)
	,
	@ServiceProvider(service = IGamePluginService.class)
})
public class CoinControlSystem implements IEntityProcessingService, IPostEntityProcessingService, ICoinSpawningService, IGamePluginService {

	private Lookup lookup = Lookup.getDefault();

	/**
	 * The process method from IEntityProcessingService handles coin functionality
	 * in main game loop
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
	 * with coins.
	 *
	 * @param gameData
	 * @param world
	 */
	@Override
	public void postProcess(GameData gameData, World world) {
		//Loops through all coin entities, so that it is possible to check 
		//for all coins in the game, if the player has collided with one of them
		for (Entity coin : world.getEntities(Coin.class)) {
			HitboxPart coinHitbox = coin.getPart(HitboxPart.class);

			//Handles collision for all entities colliding with the coin
			for (Entity entity : coinHitbox.getCollidingEntities()) {
				//Get the type of the current entity
				Class type = entity.getClass();

				//Handles collision with player
				if (type.equals(Player.class)) {
					//Get the player coin part
					CoinPart coinPart = entity.getPart(CoinPart.class);

					//Increment coinPart by 1
					int coins = coinPart.getCoins();
					coins++;
					coinPart.setCoins(coins);

					//Sound for picking up the Coin
					IAudioService audio = getAudioService();
					if (audio != null) {
						audio.playSound("dk-sdu-mmmi-cbse-Coin.jar!/assets/pickUpCoin.mp3");
					}

					//Removes coin when colliding with player
					world.removeEntity(coin);
				}
			}
		}
	}

	/**
	 * This method creates a coin, those used in the game. The method gives the
	 * coin its width and height, a position, a texture/sprite/picture and the
	 * ability to handle collision.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return a coin as an entity
	 */
	@Override
	public Entity createCoin(float x, float y) {

		//Width and height of the coin. Variables used for collision
		float width = 24;
		float height = 40;

		//Create a new coin
		Entity coin = new Coin();

		//Add a PosistionPart to the coin, so that the coin has a posistion on the map
		coin.add(new PositionPart(x, y));

		//Add a CollisionPart to the coin, so that the coin can handle collision
		coin.add(new HitboxPart(width, height, 0, 0, x, y));

		//Create a path to the coin texture/sprite/asset/picture in the assets folder
		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Coin.jar!/assets/coins.png");

		//Add coin-texture/sprite to the coin with priority 2. Priority 1 is first
		//to be displayed/drawn and priority 2 is second to be displayed on the screen 
		int priority = 2;
		coin.add(new TexturePart(path, priority));
		TexturePart texturePart = coin.getPart(TexturePart.class);

		//Add a cut to the texturePart. The cut says what on the texture/picture to display 
		texturePart.addCut("DEFAULT", 3, 5, 0, 0, 0, 0);
		texturePart.setCurrentCut("DEFAULT");
		texturePart.updatePosition(coin);

		//Return the coin entity
		return coin;
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
		//Removes all coins
		for (Entity coin : world.getEntities(Coin.class)) {
			world.removeEntity(coin);
		}
	}

}
