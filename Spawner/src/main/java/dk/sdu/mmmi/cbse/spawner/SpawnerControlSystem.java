package dk.sdu.mmmi.cbse.spawner;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.services.IEntitySpawner;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.commoncoin.services.ICoinSpawningService;
import dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService;
import dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService.EnemyType;
import static dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService.EnemyType.BAT;
import static dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService.EnemyType.BOSS;
import static dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService.EnemyType.GHOST;
import static dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService.EnemyType.JELLYBEAST;
import static dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService.EnemyType.SCOUT;
import static dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService.EnemyType.SKELETONMAGE;
import static dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService.EnemyType.SKELETONWARRIOR;
import dk.sdu.mmmi.cbse.commonheart.services.IHeartSpawningService;
import dk.sdu.mmmi.cbse.commonobstacle.services.IObstacleSpawningService;
import static dk.sdu.mmmi.cbse.commonobstacle.services.IObstacleSpawningService.ObstacleType.CRATE;
import static dk.sdu.mmmi.cbse.commonobstacle.services.IObstacleSpawningService.ObstacleType.ROCK;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders(value = {
	@ServiceProvider(service = IEntitySpawner.class)
	,
	@ServiceProvider(service = IGamePluginService.class)
})
public class SpawnerControlSystem implements IEntitySpawner, IGamePluginService {

	private Lookup lookup;

	private Map<String, String> currentParameters;

	private Random random;
	private List<Position> availablePositions;

	public SpawnerControlSystem() {
		lookup = Lookup.getDefault();
		availablePositions = new ArrayList<>();
	}

	@Override
	public List<Entity> getEntities(int seed, Map<String, String> parameters) {
		List<Entity> entities = new ArrayList<>();

		random = new Random(seed);
		currentParameters = parameters;

		resetPositions();
		occupyDoorPositions();

		if (isEmpty()) {
			return entities;
		}

		if (hasBoss() && !isDefeated()) {
			entities.addAll(getBosses());
			return entities;
		}
		if (hasBoss() && isDefeated()) {
			return entities;
		}

		//Adds all obstacles first
		entities.addAll(getObstacles());

		if (!isDefeated()) {
			//Adds all enemies
			entities.addAll(getEnemies());
			//Adds all coins
			entities.addAll(getCoins());
			//Adds all hearts
			entities.addAll(getHearts());
		}

		return entities;
	}

	private void resetPositions() {
		availablePositions.clear();

		int tileAmountHorizontal = 13;
		int tileAmountVertical = 7;

		for (int y = 0; y < tileAmountVertical; y++) {
			for (int x = 0; x < tileAmountHorizontal; x++) {
				availablePositions.add(new Position(x, y));
			}
		}
	}

	private boolean isEmpty() {
		switch (currentParameters.get("IsEmpty")) {
			case "true":
				return true;
			case "false":
				return false;
			default:
				return false;
		}
	}

	private boolean hasBoss() {
		switch (currentParameters.get("HasBoss")) {
			case "true":
				return true;
			case "false":
				return false;
			default:
				return false;
		}
	}

	private boolean isDefeated() {
		switch (currentParameters.get("IsDefeated")) {
			case "true":
				return true;
			case "false":
				return false;
			default:
				return false;
		}
	}

	private int getDifficulty() {
		String difficulty = currentParameters.get("Difficulty");
		try {
			return Integer.parseInt(difficulty);
		} catch (NumberFormatException e) {
			System.out.println("Room parameter contains an illegal difficulty value");
			return -1;
		}
	}

	private void occupyDoorPositions() {
		occupyPosition(5, 6);
		occupyPosition(6, 6);
		occupyPosition(7, 6);
		occupyPosition(5, 5);
		occupyPosition(6, 5);
		occupyPosition(7, 5);
		occupyPosition(12, 4);
		occupyPosition(12, 3);
		occupyPosition(12, 2);
		occupyPosition(11, 4);
		occupyPosition(11, 3);
		occupyPosition(11, 2);
		occupyPosition(5, 0);
		occupyPosition(6, 0);
		occupyPosition(7, 0);
		occupyPosition(5, 1);
		occupyPosition(6, 1);
		occupyPosition(7, 1);
		occupyPosition(0, 4);
		occupyPosition(0, 3);
		occupyPosition(0, 2);
		occupyPosition(1, 4);
		occupyPosition(1, 3);
		occupyPosition(1, 2);
	}

	private void occupyPosition(int x, int y) {
		availablePositions.remove(new Position(x, y));
	}

	/**
	 * Creates a PositionPart using x and y which is the positions of a tile and
	 * using radians which is the pointing direction.
	 *
	 * @param x tile x position
	 * @param y tile y position
	 * @param radians pointing direction
	 * @return a new PositionPart given the parameters
	 */
	private PositionPart getPositionPart(int x, int y, float radians) {
		float roomStartX = 128;
		float roomStartY = 128;
		float tileWidth = 64;
		float tileHeight = 64;
		return new PositionPart(roomStartX + tileWidth / 2 + tileWidth * x, roomStartY + tileHeight / 2 + tileHeight * y, radians);
	}

	private List<PositionPart> getAvailablePositions(int amount) {
		return getAvailablePositions(0, amount);
	}

	private List<PositionPart> getAvailablePositions(float radians, int amount) {
		List<PositionPart> positionParts = new ArrayList<>();

		while (amount != 0) {
			if (availablePositions.isEmpty()) {
				break;
			}

			//Chooses a tile at random from available tiles
			int randomIndex = random.nextInt(availablePositions.size());
			Position position = availablePositions.get(randomIndex);

			//Occupies that tile and creates a PositionPart from its coordinates
			occupyPosition(position.getX(), position.getY());
			positionParts.add(getPositionPart(position.getX(), position.getY(), radians));

			amount--;
		}

		return positionParts;
	}

	/**
	 *
	 *
	 * @return
	 */
	private List<Entity> getBosses() {
		List<Entity> bosses = new ArrayList<>();

		//Returns nothing if there is no EnemySpawningService available
		IEnemySpawningService enemyService = getEnemySpawningService();
		if (enemyService == null) {
			return bosses;
		}

		float radians = getLookingDirection();
		int amount = getBossAmount();

		for (PositionPart pos : getAvailablePositions(radians, amount)) {
			bosses.add(enemyService.createEnemy(pos.getX(), pos.getY(), pos.getRadians(), BOSS));
		}

		return bosses;
	}

	/**
	 * Spawns one boss at difficulty 1 and then +1 boss for each 3 difficulty
	 * level increased capping at 3 bosses. That is level 1 = 1 boss, level 2 = 1
	 * boss, level 3 = 1 boss, level 4 = 2 boss etc.
	 *
	 * @return amount of bosses that should be spawned depending on difficulty
	 */
	private int getBossAmount() {
		int cap = 3;
		int amount;
		int difficulty = getDifficulty();
		if (difficulty < 1) {
			return 1;
		}
		amount = (difficulty - 1) / 3 + 1;
		if (amount > cap) {
			amount = cap;
		}
		return amount;
	}

	private List<Entity> getObstacles() {
		List<Entity> obstacles = new ArrayList<>();

		//Returns nothing if there is no ObstacleSpawningService available
		IObstacleSpawningService obstacleservice = getObstacleSpawningService();
		if (obstacleservice == null) {
			return obstacles;
		}

		int amount = random.nextInt(4) + 1; //A random amount ranging from 1 to 4

		for (PositionPart pos : getAvailablePositions(amount)) {

			//Chooses an obstacle type at random.
			int randomObstacleType = random.nextInt(2);

			if (randomObstacleType == 0) {
				obstacles.add(obstacleservice.createObstacle(pos.getX(), pos.getY(), CRATE));
			} else {
				obstacles.add(obstacleservice.createObstacle(pos.getX(), pos.getY(), ROCK));
			}
		}

		return obstacles;
	}

	private List<Entity> getCoins() {
		List<Entity> coins = new ArrayList<>();

		//Returns nothing if there is no CoinSpawningService available
		ICoinSpawningService coinservice = getCoinSpawningService();
		if (coinservice == null) {
			return coins;
		}

		int decider = random.nextInt(3); //There is 1/3 chance for spawning a coin

		if (decider == 0) {
			PositionPart pos = getAvailablePositions(1).get(0);
			coins.add(coinservice.createCoin(pos.getX(), pos.getY()));
		}

		return coins;
	}

	private List<Entity> getHearts() {
		List<Entity> hearts = new ArrayList<>();

		//Returns nothing if there is no HeartSpawningService available
		IHeartSpawningService heartservice = getHeartSpawningService();
		if (heartservice == null) {
			return hearts;
		}

		//Chance of spawning heart is 1/10 + (1/2) / difficulty
		if (shouldSpawnHeart()) {
			PositionPart pos = getAvailablePositions(1).get(0);
			hearts.add(heartservice.createHeart(pos.getX(), pos.getY()));
		}

		return hearts;
	}

	/**
	 * Chance of spawning heart is 1/10 + (1/2) / difficulty.
	 *
	 * @return
	 */
	private boolean shouldSpawnHeart() {
		int chance = 10 + 50 / getDifficulty();
		int r = random.nextInt(100);
		return r < chance;
	}

	private List<Entity> getEnemies() {
		List<Entity> enemies = new ArrayList<>();

		//Returns nothing if there is no EnemySpawningService available
		IEnemySpawningService enemyService = getEnemySpawningService();
		if (enemyService == null) {
			return enemies;
		}

		float radians = getLookingDirection(); //The direction all enemies are going to look
		int amount = random.nextInt(3) + 2; //A random amount ranging from 2 to 5
		List<EnemyType> enemyTypes = getAvailableEnemyTypes(); //Gets available enemy types (depends on map difficulty)

		for (PositionPart pos : getAvailablePositions(radians, amount)) {
			//Chooses an enemy type at random
			EnemyType enemyType = enemyTypes.get(random.nextInt(enemyTypes.size()));

			enemies.add(enemyService.createEnemy(pos.getX(), pos.getY(), pos.getRadians(), enemyType));
		}

		return enemies;
	}

	/**
	 *
	 * @return the radians for looking at the direction of the enter direction
	 */
	private float getLookingDirection() {
		float radians;
		switch (currentParameters.get("EnterDirection")) {
			case "up":
				radians = 3.1416f * 0.5f;
				break;
			case "right":
				radians = 0;
				break;
			case "down":
				radians = 3.1416f * 1.5f;
				break;
			case "left":
				radians = 3.1416f;
				break;
			default:
				radians = 3.1416f * 0.5f;
		}
		return radians;
	}

	private List<EnemyType> getAvailableEnemyTypes() {
		List<EnemyType> enemyTypes = new ArrayList<>();

		switch (getDifficulty()) {
			case 1:
				enemyTypes.add(JELLYBEAST);
				enemyTypes.add(SCOUT);
				break;
			case 2:
				enemyTypes.add(JELLYBEAST);
				enemyTypes.add(SCOUT);
				enemyTypes.add(GHOST);
				enemyTypes.add(BAT);
				break;
			case 3:
				enemyTypes.add(JELLYBEAST);
				enemyTypes.add(SCOUT);
				enemyTypes.add(GHOST);
				enemyTypes.add(BAT);
				enemyTypes.add(SKELETONMAGE);
				enemyTypes.add(SKELETONWARRIOR);
				break;
			default:
				enemyTypes.add(SCOUT);
				enemyTypes.add(GHOST);
				enemyTypes.add(BAT);
				enemyTypes.add(SKELETONMAGE);
				enemyTypes.add(SKELETONWARRIOR);
				break;
		}

		return enemyTypes;
	}

	private IObstacleSpawningService getObstacleSpawningService() {
		return lookup.lookup(IObstacleSpawningService.class);
	}

	private IEnemySpawningService getEnemySpawningService() {
		return lookup.lookup(IEnemySpawningService.class);
	}

	private ICoinSpawningService getCoinSpawningService() {
		return lookup.lookup(ICoinSpawningService.class);
	}

	private IHeartSpawningService getHeartSpawningService() {
		return lookup.lookup(IHeartSpawningService.class);
	}

	@Override
	public void start(GameData gameData, World world) {
		//Do nothing
	}

	@Override
	public void stop(GameData gameData, World world) {
		//Do nothing
	}

}
