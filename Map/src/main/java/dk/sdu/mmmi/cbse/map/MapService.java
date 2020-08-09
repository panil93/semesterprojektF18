package dk.sdu.mmmi.cbse.map;

import dk.sdu.mmmi.cbse.map.data.entityparts.DoorPart;
import dk.sdu.mmmi.cbse.map.data.Door;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.HitboxPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.MovingPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.TexturePart;
import dk.sdu.mmmi.cbse.common.services.IAudioService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.commonenemy.Enemy;
import dk.sdu.mmmi.cbse.common.services.IHUD;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import dk.sdu.mmmi.cbse.commonplayer.Player;
import java.util.Random;
import org.openide.util.Lookup;
import dk.sdu.mmmi.cbse.common.services.IMapService;
import dk.sdu.mmmi.cbse.map.data.Stairs;

@ServiceProviders(value = {
	@ServiceProvider(service = IMapService.class)
	,
	@ServiceProvider(service = IGamePluginService.class)
})
public class MapService implements IMapService, IGamePluginService {

	private Map map;
	private int mapDifficulty;
	private MapGenerator mapGen;
	private Random baseSeeder;

	private Lookup lookup = Lookup.getDefault();

	public MapService() {
		mapDifficulty = 0;
		map = null;
	}

	/**
	 * Instantiates a new map with mapDifficulty 1. Only called once, when game is
	 * started.
	 */
	@Override
	public void generateMap() {
		baseSeeder = new Random();
		int baseSeed = baseSeeder.nextInt();
		mapGen = new MapGenerator(baseSeed);
		mapDifficulty = 1;
		map = mapGen.generateMap(mapDifficulty);
	}

	/**
	 * Loads the first room. Only used once, when game is started.
	 *
	 * @param world to add entities to.
	 */
	@Override
	public void loadRoom(World world) {
		if (map == null) {
			return;
		}

		map.loadRoom(world);
		IHUD HUD = getHUD();
		if (HUD != null) {
			HUD.clearMinimap(mapDifficulty, map.getCurrentRoom().hasDoorInDirection());
		}
	}

	/**
	 * Loads the room in the specified direction.
	 *
	 * @param world
	 * @param direction
	 */
	@Override
	public void loadRoom(World world, int direction) {
		if (map == null) {
			return;
		}

		map.loadRoom(world, direction);
		IHUD HUD = getHUD();
		if (HUD != null) {
			HUD.updateMinimap(direction, map.getCurrentRoom().hasDoorInDirection());
		}
	}

	@Override
	public String getBackgroundTexturePath() {
		if (map == null) {
			return null;
		}

		return map.getBackgroundTexturePath();
	}

	@Override
	public void nextLevel(World world) {
		if (map == null) {
			return;
		}

		mapDifficulty++;
		map = mapGen.generateMap(mapDifficulty);
		loadRoom(world);
	}

	@Override
	public void process(World world) {
		if (map == null) {
			return;
		}

		//Checks if room is defeated
		if (map.getCurrentRoom().isDefeated()) {

			//Goes to next room if door is colliding with player
			for (Entity door : world.getEntities(Door.class)) {
				DoorPart doorPart = door.getPart(DoorPart.class);
				HitboxPart doorHitbox = door.getPart(HitboxPart.class);

				if (!doorHitbox.isCollided()) {
					continue;
				}

				//Handles door collision with player
				for (Entity entity : doorHitbox.getCollidingEntities()) {
					Class type = entity.getClass();

					//Handles collision with player
					if (type.equals(Player.class)) {
						IAudioService audio = getAudioService();
						if (audio != null) {
							audio.playSound("dk-sdu-mmmi-cbse-Map.jar!/assets/door.mp3");
						}

						loadRoom(world, doorPart.getDirection());
						return;
					}
				}
			}

			//Goes to next level if stairs is colliding with player
			for (Entity stairs : world.getEntities(Stairs.class)) {
				HitboxPart stairsHitbox = stairs.getPart(HitboxPart.class);

				if (!stairsHitbox.isCollided()) {
					continue;
				}

				//Handles door collision with player
				for (Entity entity : stairsHitbox.getCollidingEntities()) {
					Class type = entity.getClass();

					//Handles collision with player
					if (type.equals(Player.class)) {
						//Sets the players speed to 0
						MovingPart movingPart = entity.getPart(MovingPart.class);
						movingPart.setDx(0);
						movingPart.setDy(0);

						//Loads next level
						nextLevel(world);
						return;
					}
				}
			}
			return;
		}

		//Does nothing if current room still has enemies
		if (!world.getEntities(Enemy.class).isEmpty()) {
			return;
		}

		map.getCurrentRoom().setDefeated(true);

		//Spawn stairs if it is a boss room
		if (map.getCurrentRoom().hasBoss()) {
			map.spawnStairs(world);
		}

		//Open all doors
		for (Entity door : world.getEntities(Door.class)) {
			TexturePart texPart = door.getPart(TexturePart.class);
			texPart.setCurrentCut("OPEN");
			texPart.updatePosition(door);
		}
	}

	@Override
	public int getMapDifficulty() {
		return mapDifficulty;
	}

	private IHUD getHUD() {
		return lookup.lookup(IHUD.class);
	}

	private IAudioService getAudioService() {
		return lookup.lookup(IAudioService.class);
	}

	@Override
	public void start(GameData gameData, World world) {

	}

	@Override
	public void stop(GameData gameData, World world) {
		//Remove all entities except player
		for (Entity entity : world.getEntities()) {
			if (entity.getClass() == Player.class) {
				continue;
			}
			world.removeEntity(entity);
		}
	}

}
