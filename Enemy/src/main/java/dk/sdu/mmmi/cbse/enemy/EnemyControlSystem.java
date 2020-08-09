package dk.sdu.mmmi.cbse.enemy;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.CollisionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.HitboxPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.LifePart;
import dk.sdu.mmmi.cbse.common.data.entityparts.MovingPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.TexturePart;
import dk.sdu.mmmi.cbse.common.services.IAI;
import dk.sdu.mmmi.cbse.commonenemy.Enemy;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.commonplayer.Player;
import dk.sdu.mmmi.cbse.commonweapon.data.Projectile;
import dk.sdu.mmmi.cbse.commonweapon.data.entityparts.OwnershipPart;
import dk.sdu.mmmi.cbse.commonweapon.data.entityparts.WeaponPart;
import dk.sdu.mmmi.cbse.commonweapon.services.IWeaponService;
import java.io.File;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;
import dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService;
import static dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService.EnemyType.SCOUT;
import static dk.sdu.mmmi.cbse.commonweapon.services.IWeaponService.WeaponType.BOSSWEAPON;
import static dk.sdu.mmmi.cbse.commonweapon.services.IWeaponService.WeaponType.MINIONWEAPON;
import java.awt.geom.Point2D;
import static java.lang.Math.atan2;
import java.util.List;
import java.util.Random;

/**
 * The EnemyControlSystem in the Enemy component handles enemy functionality.
 *
 * @author Group 7
 */
@ServiceProviders(value = {
	@ServiceProvider(service = IEntityProcessingService.class)
	,
	@ServiceProvider(service = IPostEntityProcessingService.class)
	,
	@ServiceProvider(service = IEnemySpawningService.class)
	,
	@ServiceProvider(service = IGamePluginService.class)
})
public class EnemyControlSystem implements IEntityProcessingService, IPostEntityProcessingService, IEnemySpawningService, IGamePluginService {

	private float setMoveDirectionTimer;
	private Random random;
	private Lookup lookup;

	public EnemyControlSystem() {
		setMoveDirectionTimer = 0;
		random = new Random();
		lookup = Lookup.getDefault();
	}

	/**
	 * The process method from IEntityProcessingService handles enemies
	 * functionality in main game loop. Specifically handles AI, moving, aiming
	 * and shooting.
	 *
	 * @param gameData
	 * @param world
	 */
	@Override
	public void process(GameData gameData, World world) {

		//Gets all enemy entities
		for (Entity enemy : world.getEntities(Enemy.class)) {

			//Increases lifetime/age of current enemy by delta
			((Enemy) enemy).increaseLifetime(gameData.getDelta());

			//Gets current enemy's relevant parts for the main game loop
			MovingPart movingPart = enemy.getPart(MovingPart.class);
			WeaponPart weaponPart = enemy.getPart(WeaponPart.class);

			IAI AI = getAI();
			Entity player = getPlayer(world);
			if (AI != null && player != null) {
				setMovingDirectionUsingAI(world, AI, enemy, player);
			} else {
				setMovingDirectionRandomly(gameData, enemy);
			}

			handleScoutMovement(enemy);

			//Moves enemy and then updates other positions on other parts
			movingPart.process(gameData, enemy);
			updateAllPositions(enemy);

			//Handles shooting if enemy has a weapon part
			if (weaponPart != null) {
				weaponPart.setShooting(true);
				weaponPart.process(gameData, enemy);
				handleShooting(gameData, world, enemy);
			}
		}
	}

	private Entity getPlayer(World world) {
		for (Entity entity : world.getEntities()) {
			if (entity.getClass().equals(Player.class)) {
				return entity;
			}
		}
		return null;
	}

	private void setMovingDirectionUsingAI(World world, IAI AI, Entity enemy, Entity player) {
		PositionPart positionPart = enemy.getPart(PositionPart.class);
		MovingPart movingPart = enemy.getPart(MovingPart.class);

		List<Point2D.Float> pointPath = AI.getPathToTarget(world, enemy, player);
		Point2D.Float firstPoint = pointPath.get(0);

		boolean isAbove = false;
		boolean isBelow = false;
		boolean isToRight = false;
		boolean isToLeft = false;

		float enemyX = positionPart.getX();
		float enemyY = positionPart.getY();
		float destinationX = firstPoint.x;
		float destinationY = firstPoint.y;

		if (enemyY < destinationY) {
			isAbove = true;
		}
		if (enemyY > destinationY) {
			isBelow = true;
		}
		if (enemyX < destinationX) {
			isToRight = true;
		}
		if (enemyX > destinationX) {
			isToLeft = true;
		}

		movingPart.setUp(isAbove);
		movingPart.setDown(isBelow);
		movingPart.setRight(isToRight);
		movingPart.setLeft(isToLeft);
	}

	private void setMovingDirectionRandomly(GameData gameData, Entity enemy) {
		setMoveDirectionTimer += gameData.getDelta();
		if (setMoveDirectionTimer < 1) {
			return;
		}
		setMoveDirectionTimer = 0;

		MovingPart movingPart = enemy.getPart(MovingPart.class);

		movingPart.setUp(false);
		movingPart.setRight(false);
		movingPart.setDown(false);
		movingPart.setLeft(false);

		int direction = random.nextInt(4);
		switch (direction) {
			case 0:
				movingPart.setUp(true);
				break;
			case 1:
				movingPart.setRight(true);
				break;
			case 2:
				movingPart.setDown(true);
				break;
			case 3:
				movingPart.setLeft(true);
				break;
		}
	}

	private void handleScoutMovement(Entity enemy) {
		MovingPart movingPart = enemy.getPart(MovingPart.class);
		if (((Enemy) enemy).getType().equals(SCOUT)) {
			if ((float) (((Enemy) enemy).getLifetime() % 3.0) < 2.0) {
				movingPart.setAcceleration(-200);
				movingPart.setMaxSpeed(80);
			} else {
				movingPart.setAcceleration(1000);
				movingPart.setMaxSpeed(360);
			}
		}
	}

	/**
	 * The postProcess method from IPostEntityProcessingService handles collision
	 * and updates textures for the enemies.
	 *
	 * @param gameData
	 * @param world
	 */
	@Override
	public void postProcess(GameData gameData, World world) {
		for (Entity enemy : world.getEntities(Enemy.class)) {
			//Handles collision
			handleCollision(world, enemy);
			//Updates player textures
			updateTexture(gameData, (Enemy) enemy);
			//Updates other positions on other parts
			updateAllPositions(enemy);
		}
	}

	/**
	 * This method handles collision for an enemy entity.
	 *
	 * @param world
	 * @param enemy
	 */
	private void handleCollision(World world, Entity enemy) {
		HitboxPart enemyHitbox = enemy.getPart(HitboxPart.class);

		//Checks if an enemy has collided with an entity
		for (Entity entity : enemyHitbox.getCollidingEntities()) {
			Class type = entity.getClass();

			//Handles collision with projectile
			if (type.equals(Projectile.class)) {
				//Checks if the projectiles owner is player
				OwnershipPart ownershipPart = entity.getPart(OwnershipPart.class);
				Entity owner = ownershipPart.getOwner();
				if (owner.getClass().equals(Player.class)) {
					//Decreases life and removes enemy if it died
					LifePart lifePart = enemy.getPart(LifePart.class);
					lifePart.decreaseLife(1);
					if (lifePart.isDead()) {
						world.removeEntity(enemy);
					}
				}
			}
		}
	}

	/**
	 * This method handles updating the an enemy's textures.
	 *
	 * @param gameData
	 * @param entity
	 */
	private void updateTexture(GameData gameData, Enemy enemy) {
		TexturePart texturePart = enemy.getPart(TexturePart.class);
		MovingPart movingPart = enemy.getPart(MovingPart.class);

		//Shifts between default enemy texture cuts
		if ((float) (enemy.getLifetime() % 1.0) < 0.5) {
			if (movingPart.isLeft() || movingPart.isDown()) {
				texturePart.setCurrentCut("LEFT_1");
			} else if (movingPart.isRight() || movingPart.isUp()) {
				texturePart.setCurrentCut("RIGHT_1");
			}
		} else if (movingPart.isLeft() || movingPart.isDown()) {
			texturePart.setCurrentCut("LEFT_2");
		} else if (movingPart.isRight() || movingPart.isUp()) {
			texturePart.setCurrentCut("RIGHT_2");
		}
	}

	/**
	 *
	 * This method creates an enemy of the given enemy type.
	 *
	 * @param x
	 * @param y
	 * @param radians
	 * @param enemyType
	 * @return the created enemy entity
	 */
	@Override
	public Entity createEnemy(float x, float y, float radians, EnemyType enemyType) {
		switch (enemyType) {
			case BAT:
				return createBat(x, y, radians, enemyType);
			case BOSS:
				return createBoss(x, y, radians, enemyType);
			case GHOST:
				return createGhost(x, y, radians, enemyType);
			case JELLYBEAST:
				return createJellyBeast(x, y, radians, enemyType);
			case SCOUT:
				return createScout(x, y, radians, enemyType);
			case SKELETONMAGE:
				return createSkeletonMage(x, y, radians, enemyType);
			case SKELETONWARRIOR:
				return createSkeletonWarrior(x, y, radians, enemyType);
			default:
				return null;
		}
	}

	/**
	 * This method creates a Bat entity and gives the enemy its parts
	 *
	 * @param x
	 * @param y
	 * @param radians
	 * @return the Bat entity
	 */
	private Entity createBat(float x, float y, float radians, EnemyType enemyType) {
		Entity enemy = new Enemy(enemyType);

		int life = 3;
		enemy.add(new LifePart(life));

		enemy.add(new PositionPart(x, y, radians));

		float deacceleration = 100;
		float acceleration = 300;
		float maxSpeed = 400;
		boolean allowWrapping = true;
		enemy.add(new MovingPart(deacceleration, acceleration, maxSpeed, allowWrapping));

		float collisionWidth = 64;
		float collisionHeight = 32;
		float collisionOffsetX = 0;
		float collisionOffsetY = 0;
		boolean isMovable = true;
		enemy.add(new CollisionPart(collisionWidth, collisionHeight, collisionOffsetX, collisionOffsetY, x, y, isMovable));

		float hitboxWidth = 64;
		float hitboxHeight = 32;
		float hitboxOffsetX = 0;
		float hitboxOffsetY = 0;
		enemy.add(new HitboxPart(hitboxWidth, hitboxHeight, hitboxOffsetX, hitboxOffsetY, x, y));

		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Enemy.jar!/assets/bat.png");
		int priority = 3;
		enemy.add(new TexturePart(path, priority));
		TexturePart texturePart = enemy.getPart(TexturePart.class);
		texturePart.addCut("RIGHT_1", 8, 6, 0, 0, 0, 1f);
		texturePart.addCut("LEFT_1", 8, 6, 10, 0, 0, 1f);
		texturePart.addCut("RIGHT_2", 8, 7, 0, 13, 0, -1.5f);
		texturePart.addCut("LEFT_2", 8, 7, 10, 13, 0, -1.5f);
		texturePart.setCurrentCut("RIGHT_1");
		texturePart.updatePosition(enemy);

		return enemy;
	}

	/**
	 * This method creates a Boss entity and gives the enemy its parts
	 *
	 * @param x
	 * @param y
	 * @param radians
	 * @return the Boss entity
	 */
	private Entity createBoss(float x, float y, float radians, EnemyType enemyType) {
		Entity enemy = new Enemy(enemyType);

		int life = 25;
		enemy.add(new LifePart(life));

		enemy.add(new PositionPart(x, y, radians));

		float deacceleration = 40;
		float acceleration = 70;
		float maxSpeed = 80;

		boolean allowWrapping = true;
		enemy.add(new MovingPart(deacceleration, acceleration, maxSpeed, allowWrapping));

		float collisionWidth = 64;
		float collisionHeight = 64;
		float collisionOffsetX = 0;
		float collisionOffsetY = 0;
		boolean isMovable = true;
		enemy.add(new CollisionPart(collisionWidth, collisionHeight, collisionOffsetX, collisionOffsetY, x, y, isMovable));

		float hitboxWidth = 64;
		float hitboxHeight = 64;
		float hitboxOffsetX = 0;
		float hitboxOffsetY = 0;
		enemy.add(new HitboxPart(hitboxWidth, hitboxHeight, hitboxOffsetX, hitboxOffsetY, x, y));

		enemy.add(new WeaponPart(BOSSWEAPON));

		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Enemy.jar!/assets/boss.png");
		int priority = 3;
		enemy.add(new TexturePart(path, priority));

		TexturePart texturePart = enemy.getPart(TexturePart.class);
		texturePart.addCut("RIGHT_1", 8, 9, 1, 1, 0, 0.5f);
		texturePart.addCut("LEFT_1", 8, 9, 11, 1, 0, 0.5f);
		texturePart.addCut("RIGHT_2", 8, 10, 1, 12, 0, 1f);
		texturePart.addCut("LEFT_2", 8, 10, 11, 12, 0, 1f);
		texturePart.setCurrentCut("RIGHT_1");
		texturePart.updatePosition(enemy);

		return enemy;
	}

	/**
	 * This method creates a Ghost entity and gives the enemy its parts
	 *
	 * @param x
	 * @param y
	 * @param radians
	 * @return the Ghost entity
	 */
	private Entity createGhost(float x, float y, float radians, EnemyType enemyType) {
		Entity enemy = new Enemy(enemyType);

		int life = 5;
		enemy.add(new LifePart(life));

		enemy.add(new PositionPart(x, y, radians));

		float deacceleration = 200;
		float acceleration = 200;
		float maxSpeed = 200;
		boolean allowWrapping = true;
		enemy.add(new MovingPart(deacceleration, acceleration, maxSpeed, allowWrapping));

		float collisionWidth = 40;
		float collisionHeight = 56;
		float collisionOffsetX = 0;
		float collisionOffsetY = 0;
		boolean isMovable = true;
		enemy.add(new CollisionPart(collisionWidth, collisionHeight, collisionOffsetX, collisionOffsetY, x, y, isMovable));

		float hitboxWidth = 40;
		float hitboxHeight = 56;
		float hitboxOffsetX = 0;
		float hitboxOffsetY = 0;
		enemy.add(new HitboxPart(hitboxWidth, hitboxHeight, hitboxOffsetX, hitboxOffsetY, x, y));

		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Enemy.jar!/assets/ghost.png");
		int priority = 3;
		enemy.add(new TexturePart(path, priority));
		TexturePart texturePart = enemy.getPart(TexturePart.class);
		texturePart.addCut("RIGHT_1", 6, 7, 0, 0, -0.5f, 0);
		texturePart.addCut("LEFT_1", 6, 7, 9, 0, 0.5f, 0);
		texturePart.addCut("RIGHT_2", 6, 8, 0, 11, -0.5f, 0.5f);
		texturePart.addCut("LEFT_2", 6, 8, 9, 11, 0.5f, 0.5f);
		texturePart.setCurrentCut("RIGHT_1");
		texturePart.updatePosition(enemy);

		return enemy;
	}

	/**
	 * This method creates a JellyBeast entity and gives the enemy its parts
	 *
	 * @param x
	 * @param y
	 * @param radians
	 * @return the JellyBeast entity
	 */
	private Entity createJellyBeast(float x, float y, float radians, EnemyType enemyType) {
		Entity enemy = new Enemy(enemyType);

		int life = 2;
		enemy.add(new LifePart(life));

		enemy.add(new PositionPart(x, y, radians));

		float deacceleration = 100;
		float acceleration = 100;
		float maxSpeed = 100;
		boolean allowWrapping = true;
		enemy.add(new MovingPart(deacceleration, acceleration, maxSpeed, allowWrapping));

		float collisionWidth = 48;
		float collisionHeight = 32;
		float collisionOffsetX = 0;
		float collisionOffsetY = 0;
		boolean isMovable = true;
		enemy.add(new CollisionPart(collisionWidth, collisionHeight, collisionOffsetX, collisionOffsetY, x, y, isMovable));

		float hitboxWidth = 48;
		float hitboxHeight = 32;
		float hitboxOffsetX = 0;
		float hitboxOffsetY = 0;
		enemy.add(new HitboxPart(hitboxWidth, hitboxHeight, hitboxOffsetX, hitboxOffsetY, x, y));

		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Enemy.jar!/assets/jellybeast.png");
		int priority = 3;
		enemy.add(new TexturePart(path, priority));
		TexturePart texturePart = enemy.getPart(TexturePart.class);
		texturePart.addCut("RIGHT_1", 8, 4, 0, 0, 0, 0);
		texturePart.addCut("LEFT_1", 8, 4, 10, 0, 0, 0);
		texturePart.addCut("RIGHT_2", 6, 5, 1, 11, 0, 0.5f);
		texturePart.addCut("LEFT_2", 6, 5, 11, 11, 0, 0.5f);
		texturePart.setCurrentCut("RIGHT_1");
		texturePart.updatePosition(enemy);

		return enemy;
	}

	/**
	 * This method creates a Scout entity and gives the enemy its parts
	 *
	 * @param x
	 * @param y
	 * @param radians
	 * @return the Scout entity
	 */
	private Entity createScout(float x, float y, float radians, EnemyType enemyType) {
		Entity enemy = new Enemy(enemyType);

		int life = 6;
		enemy.add(new LifePart(life));

		enemy.add(new PositionPart(x, y, radians));

		float deacceleration = 150;
		float acceleration = -200;
		float maxSpeed = 200;
		boolean allowWrapping = true;
		enemy.add(new MovingPart(deacceleration, acceleration, maxSpeed, allowWrapping));

		float collisionWidth = 64;
		float collisionHeight = 64;
		float collisionOffsetX = 0;
		float collisionOffsetY = 0;
		boolean isMovable = true;
		enemy.add(new CollisionPart(collisionWidth, collisionHeight, collisionOffsetX, collisionOffsetY, x, y, isMovable));

		float hitboxWidth = 64;
		float hitboxHeight = 64;
		float hitboxOffsetX = 0;
		float hitboxOffsetY = 0;
		enemy.add(new HitboxPart(hitboxWidth, hitboxHeight, hitboxOffsetX, hitboxOffsetY, x, y));

		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Enemy.jar!/assets/scout.png");
		int priority = 3;
		enemy.add(new TexturePart(path, priority));
		TexturePart texturePart = enemy.getPart(TexturePart.class);
		texturePart.addCut("RIGHT_1", 8, 9, 1, 1, 0, 0.5f);
		texturePart.addCut("LEFT_1", 8, 9, 11, 1, 0, 0.5f);
		texturePart.addCut("RIGHT_2", 8, 10, 1, 12, 0, 1f);
		texturePart.addCut("LEFT_2", 8, 10, 11, 12, 0, 1f);
		texturePart.setCurrentCut("RIGHT_1");
		texturePart.updatePosition(enemy);

		return enemy;
	}

	/**
	 * This method creates a SkeletonMage entity and gives the enemy its parts
	 *
	 * @param x
	 * @param y
	 * @param radians
	 * @return the SkeletonMage entity
	 */
	private Entity createSkeletonMage(float x, float y, float radians, EnemyType enemyType) {
		Entity enemy = new Enemy(enemyType);

		int life = 6;
		enemy.add(new LifePart(life));

		enemy.add(new PositionPart(x, y, radians));

		float deacceleration = 100;
		float acceleration = 200;
		float maxSpeed = 200;
		boolean allowWrapping = true;
		enemy.add(new MovingPart(deacceleration, acceleration, maxSpeed, allowWrapping));

		float collisionWidth = 64;
		float collisionHeight = 64;
		float collisionOffsetX = 0;
		float collisionOffsetY = 0;
		boolean isMovable = true;
		enemy.add(new CollisionPart(collisionWidth, collisionHeight, collisionOffsetX, collisionOffsetY, x, y, isMovable));

		float hitboxWidth = 64;
		float hitboxHeight = 64;
		float hitboxOffsetX = 0;
		float hitboxOffsetY = 0;
		enemy.add(new HitboxPart(hitboxWidth, hitboxHeight, hitboxOffsetX, hitboxOffsetY, x, y));

		enemy.add(new WeaponPart(MINIONWEAPON));

		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Enemy.jar!/assets/skeletonmage.png");
		int priority = 3;
		enemy.add(new TexturePart(path, priority));

		TexturePart texturePart = enemy.getPart(TexturePart.class);
		texturePart.addCut("RIGHT_1", 8, 8, 0, 0, 0, 0);
		texturePart.addCut("LEFT_1", 8, 8, 12, 0, 0, 0);
		texturePart.addCut("RIGHT_2", 9, 9, 0, 11, 0.5f, 0.5f);
		texturePart.addCut("LEFT_2", 9, 9, 11, 11, -0.5f, 0.5f);
		texturePart.setCurrentCut("RIGHT_1");
		texturePart.updatePosition(enemy);

		return enemy;
	}

	/**
	 * This method creates a SkeletonWarrior entity and gives the enemy its parts
	 *
	 * @param x
	 * @param y
	 * @param radians
	 * @return the SkeletonWarrior entity
	 */
	private Entity createSkeletonWarrior(float x, float y, float radians, EnemyType enemyType) {
		//Create enemy entity
		Entity enemy = new Enemy(enemyType);

		//Adds life part to enemy
		int life = 10;
		enemy.add(new LifePart(life));

		//Adds position part to enemy 
		enemy.add(new PositionPart(x, y, radians));

		//Creates parameters for moving part 
		float deacceleration = 100;
		float acceleration = 300;
		float maxSpeed = 500;
		boolean allowWrapping = true;
		//Adds moving part to enemy
		enemy.add(new MovingPart(deacceleration, acceleration, maxSpeed, allowWrapping));

		//Create parameters for collision part
		float collisionWidth = 64;
		float collisionHeight = 64;
		float collisionOffsetX = 0;
		float collisionOffsetY = 0;
		boolean isMovable = true;
		//Adds collision part to enemy
		enemy.add(new CollisionPart(collisionWidth, collisionHeight, collisionOffsetX, collisionOffsetY, x, y, isMovable));

		//Create parameters for hitbox part
		float hitboxWidth = 64;
		float hitboxHeight = 64;
		float hitboxOffsetX = 0;
		float hitboxOffsetY = 0;
		//Adds hitbox part to enemy
		enemy.add(new HitboxPart(hitboxWidth, hitboxHeight, hitboxOffsetX, hitboxOffsetY, x, y));

		//Creates file path to enemy texture
		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Enemy.jar!/assets/skeletonwarrior.png");
		//Add enemy-texture/sprite to the enemy with priority 3. Priority 1 is first
		//to be displayed/drawn and priority 3 is third to be displayed on the screen 
		int priority = 3;
		enemy.add(new TexturePart(path, priority));
		TexturePart texturePart = enemy.getPart(TexturePart.class);
		//Adds different texture cuts to the texture part
		texturePart.addCut("RIGHT_1", 8, 9, 0, 0, 0, 0.5f);
		texturePart.addCut("LEFT_1", 8, 9, 12, 0, 0, 0.5f);
		texturePart.addCut("RIGHT_2", 8, 9, 1, 12, 0, 0.5f);
		texturePart.addCut("LEFT_2", 8, 9, 11, 12, 0, 0.5f);
		//Sets a initial cut for the enemy
		texturePart.setCurrentCut("RIGHT_1");
		texturePart.updatePosition(enemy);

		//Returns the enemy entity
		return enemy;

	}

	/**
	 * This method updates the Collision-, Hitbox- and texture- part according to
	 * the new position part.
	 *
	 */
	private void updateAllPositions(Entity enemy) {
		//Get the collision-, hitbox- and texture- parts
		CollisionPart collisionPart = enemy.getPart(CollisionPart.class);
		HitboxPart hitboxPart = enemy.getPart(HitboxPart.class);
		TexturePart texturePart = enemy.getPart(TexturePart.class);

		//Updates the collision-, hitbox- and texture- parts
		collisionPart.updatePosition(enemy);
		hitboxPart.updatePosition(enemy);
		texturePart.updatePosition(enemy);
	}

	/**
	 * Lookup AI Service
	 *
	 * @return IAI
	 */
	private IAI getAI() {
		return lookup.lookup(IAI.class);
	}

	/**
	 * This method handles enemy shooting
	 *
	 * @param gameData
	 * @param world
	 * @param entity
	 */
	private void handleShooting(GameData gameData, World world, Entity enemy) {
		//Gets the enemey's weaponPart
		WeaponPart weaponPart = enemy.getPart(WeaponPart.class);
		//Checks if the enemy is ready to shoot
		if (!weaponPart.isShootingReady()) {
			return;
		}

		//Gets weapon service
		IWeaponService service = getWeaponService();
		if (service == null) {
			return;
		}

		//Gets the position- and aiming- part
		PositionPart pos = enemy.getPart(PositionPart.class);

		//Creating bullet creation parameters
		float x = pos.getX();
		float y = pos.getY();
		float radians = getRadiansTowardsPlayer(world, pos);

		if (radians == -1) {
			return;
		}

		//Create bullet
		service.shoot(gameData, world, x, y, radians, enemy);
	}

	private float getRadiansTowardsPlayer(World world, PositionPart enemyPos) {
		Entity player = getPlayer(world);
		if (player == null) {
			return -1;
		}

		PositionPart playerPos = player.getPart(PositionPart.class);

		double pX = playerPos.getX();
		double pY = playerPos.getY();
		double eX = enemyPos.getX();
		double eY = enemyPos.getY();

		float radians = (float) atan2(pY - eY, pX - eX);

		radians = radians < 0 ? radians + 2 * (float) Math.PI : radians;

		return radians;
	}

	/**
	 * Lookup Weapon Service
	 *
	 * @return IWeaponService
	 */
	private IWeaponService getWeaponService() {
		return Lookup.getDefault().lookup(IWeaponService.class);
	}

	@Override
	public void start(GameData gameData, World world) {
		//Do nothing
	}

	@Override
	public void stop(GameData gameData, World world) {
		//Removes all enemies
		for (Entity enemy : world.getEntities(Enemy.class)) {
			world.removeEntity(enemy);
		}
	}

}
