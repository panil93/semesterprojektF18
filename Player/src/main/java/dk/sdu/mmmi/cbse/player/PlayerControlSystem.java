package dk.sdu.mmmi.cbse.player;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import static dk.sdu.mmmi.cbse.common.data.GameKeys.DOWN;
import static dk.sdu.mmmi.cbse.common.data.GameKeys.LEFT;
import static dk.sdu.mmmi.cbse.common.data.GameKeys.RIGHT;
import static dk.sdu.mmmi.cbse.common.data.GameKeys.UP;
import static dk.sdu.mmmi.cbse.common.data.GameKeys.W;
import static dk.sdu.mmmi.cbse.common.data.GameKeys.A;
import static dk.sdu.mmmi.cbse.common.data.GameKeys.S;
import static dk.sdu.mmmi.cbse.common.data.GameKeys.D;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.AimingPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.CoinPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.CollisionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.HitboxPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.LifePart;
import dk.sdu.mmmi.cbse.common.data.entityparts.MovingPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.TexturePart;
import dk.sdu.mmmi.cbse.common.services.IAudioService;
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

/**
 * The PlayerControlSystem in the Player component handles player functionality.
 *
 * @author Group 7
 */
@ServiceProviders(value = {
	@ServiceProvider(service = IEntityProcessingService.class)
	,
	@ServiceProvider(service = IPostEntityProcessingService.class)
	,
	@ServiceProvider(service = IGamePluginService.class)
})
public class PlayerControlSystem implements IEntityProcessingService, IPostEntityProcessingService, IGamePluginService {

	private Lookup lookup = Lookup.getDefault();

	/**
	 * The process method from IEntityProcessingService handles player
	 * functionality in main game loop. Specifically handles moving, aiming and
	 * shooting.
	 *
	 * @param gameData
	 * @param world
	 */
	@Override
	public void process(GameData gameData, World world) {

		//Gets the player entity
		for (Entity player : world.getEntities(Player.class)) {

			//Gets player's relevant parts for the main game loop
			MovingPart movingPart = player.getPart(MovingPart.class);
			CoolDownPart coolDownPart = player.getPart(CoolDownPart.class);
			AimingPart aimingPart = player.getPart(AimingPart.class);
			WeaponPart weaponPart = player.getPart(WeaponPart.class);

			//Checks which moving keys are down
			movingPart.setLeft(gameData.getKeys().isDown(A));
			movingPart.setRight(gameData.getKeys().isDown(D));
			movingPart.setUp(gameData.getKeys().isDown(W));
			movingPart.setDown(gameData.getKeys().isDown(S));

			//Calls for processing of movement and cooldown
			movingPart.process(gameData, player);
			coolDownPart.process(gameData, player);

			//Updates other positions on other parts
			updateAllPositions(player);

			//Handles aiming and shooting
			aimingPart.setLeft(gameData.getKeys().isDown(LEFT));
			aimingPart.setRight(gameData.getKeys().isDown(RIGHT));
			aimingPart.setUp(gameData.getKeys().isDown(UP));
			aimingPart.setDown(gameData.getKeys().isDown(DOWN));

			//Checks if the player is shooting
			weaponPart.setShooting(aimingPart.isShooting());

			//Calls for processing of aiming and shooting
			aimingPart.process(gameData, player);
			weaponPart.process(gameData, player);

			//Only handle shooting if one of the game keys for shooting is pressed
			if (gameData.getKeys().isDown(LEFT)
							|| gameData.getKeys().isDown(RIGHT)
							|| gameData.getKeys().isDown(UP)
							|| gameData.getKeys().isDown(DOWN)) {
				//Handle shooting
				handleShooting(gameData, world, player);
			}
		}
	}

	/**
	 * The postProcess method from IPostEntityProcessingService handles collision
	 * and updates textures for the player.
	 *
	 * @param gameData
	 * @param world
	 */
	@Override
	public void postProcess(GameData gameData, World world) {
		for (Entity player : world.getEntities(Player.class)) {
			//Handles collision
			handleCollision(world, player);
			//Updates player textures
			updateTexture(gameData, player);
			//Updates other positions on other parts
			updateAllPositions(player);
		}
	}

	/**
	 * This method handles collision for the player entity.
	 *
	 * @param world
	 * @param player
	 */
	private void handleCollision(World world, Entity player) {

		//Gets the player hitpart (collision part)
		HitboxPart playerHitbox = player.getPart(HitboxPart.class);

		//Handles each collision with an entity
		for (Entity entity : playerHitbox.getCollidingEntities()) {
			Class type = entity.getClass();
			boolean isHit = false;

			//Handles collision with enemy
			if (type.equals(Enemy.class)) {
				//Loses 1 life if cool down is over
				isHit = loseLife(player, 1);
			}

			//Handles collision with projectile
			if (type.equals(Projectile.class)) {
				//Checks if the projectiles owner is enemy
				OwnershipPart ownershipPart = entity.getPart(OwnershipPart.class);
				Entity owner = ownershipPart.getOwner();
				if (owner.getClass().equals(Enemy.class)) {
					//Loses 1 life if cool down is over
					isHit = loseLife(player, 1);
				}
			}

			if (isHit) {
				//Plays sound when hit
				IAudioService audio = getAudioService();
				if (audio != null) {
					audio.playSound("dk-sdu-mmmi-cbse-Player.jar!/assets/collisionWithMonsters.mp3");
				}

				//Checks if player is dead and handles it
				checkIfDead(world, player, entity);
			}
		}
	}

	/**
	 * Decreases players life if cool down is over. Returns true if cool down was
	 * over.
	 *
	 * @param player
	 * @param life
	 * @return Returns true if the player did lose life
	 */
	private boolean loseLife(Entity player, int life) {
		CoolDownPart coolDownPart = player.getPart(CoolDownPart.class);
		//Checks if cool down is over
		if (coolDownPart.isCoolDownOver()) {
			//Resets cooldown
			coolDownPart.increaseCooldown(1);
			LifePart lifePart = player.getPart(LifePart.class);
			//Decreases life
			lifePart.decreaseLife(life);
			return true;
		}
		return false;
	}

	/**
	 * Checks if player is dead. Removes player from world and plays a sound if
	 * player died.
	 *
	 * @param world
	 * @param player
	 */
	private void checkIfDead(World world, Entity player, Entity entity) {
		LifePart lifePart = player.getPart(LifePart.class);

		if (lifePart.isDead()) {
			//Plays sound of dying player
			IAudioService audio = getAudioService();
			if (audio != null) {
				audio.playSound("dk-sdu-mmmi-cbse-Player.jar!/assets/deadSound.mp3");
			}

			//Removes player
			world.removeEntity(player);
		}
	}

	/**
	 * This method handles updating the player's textures.
	 *
	 * @param gameData
	 * @param entity
	 */
	private void updateTexture(GameData gameData, Entity entity) {
		TexturePart texturePart = entity.getPart(TexturePart.class);

		//Check if the player is shooting, while moving
		if (gameData.getKeys().isDown(RIGHT) == true || gameData.getKeys().isDown(LEFT) == true || gameData.getKeys().isDown(UP) == true || gameData.getKeys().isDown(DOWN) == true) {
			if (gameData.getKeys().isDown(A) == true && gameData.getKeys().isDown(W) == true) {
				texturePart.setCurrentCut("SHOTLEFTUP");
			} else if (gameData.getKeys().isDown(D) == true && gameData.getKeys().isDown(W) == true) {
				texturePart.setCurrentCut("SHOTRIGHTUP");
			} else if (gameData.getKeys().isDown(A) == true) {
				texturePart.setCurrentCut("SHOTLEFT");
			} else if (gameData.getKeys().isDown(D) == true) {
				texturePart.setCurrentCut("SHOTRIGHT");
			} else if (gameData.getKeys().isDown(W) == true) {
				texturePart.setCurrentCut("SHOTLEFTUP");
			} else if (gameData.getKeys().isDown(S) == true) {
				texturePart.setCurrentCut("SHOTRIGHT");
			} //Check if player is shooting to the left, while standing still
			else if (gameData.getKeys().isDown(LEFT) == true && gameData.getKeys().isDown(A) == false && gameData.getKeys().isDown(W) == false && gameData.getKeys().isDown(D) == false && gameData.getKeys().isDown(S) == false) {
				texturePart.setCurrentCut("SHOTLEFT");
			} else {
				//If standing still, while shooting and not shooting to the left, then the player has the default shooting position
				texturePart.setCurrentCut("SHOTRIGHT");
			}
		} else {
			//Check if the player is mowing
			if (gameData.getKeys().isDown(A) == true && gameData.getKeys().isDown(W) == true) {
				texturePart.setCurrentCut("WALKLEFTUP");
			} else if (gameData.getKeys().isDown(D) == true && gameData.getKeys().isDown(W) == true) {
				texturePart.setCurrentCut("WALKRIGHTUP");
			} else if (gameData.getKeys().isDown(A) == true) {
				texturePart.setCurrentCut("WALKLEFT");
			} else if (gameData.getKeys().isDown(D) == true) {
				texturePart.setCurrentCut("WALKRIGHT");
			} else if (gameData.getKeys().isDown(W) == true) {
				texturePart.setCurrentCut("WALKRIGHTUP");
			} else if (gameData.getKeys().isDown(S) == true) {
				texturePart.setCurrentCut("WALKRIGHT");
			} else {
				//If standing still and not shooting, then the player has the default position
				texturePart.setCurrentCut("WALKRIGHT");
			}
		}
	}

	/**
	 * This method handles player shooting
	 *
	 * @param gameData
	 * @param world
	 * @param entity
	 */
	private void handleShooting(GameData gameData, World world, Entity entity) {
		//Gets the player's weaponPart
		WeaponPart weaponPart = entity.getPart(WeaponPart.class);

		//Checks if the player is ready to shoot
		if (!weaponPart.isShootingReady()) {
			return;
		}

		//Gets weapon service
		IWeaponService service = getWeaponService();
		if (service == null) {
			return;
		}
		//Gets the position- and aiming- part
		PositionPart pos = entity.getPart(PositionPart.class);
		AimingPart aim = entity.getPart(AimingPart.class);

		//Creating bullet creation parameters
		float x = pos.getX();
		float y = pos.getY();
		float radians = aim.getRadians();

		//Create bullet
		service.shoot(gameData, world, x, y, radians, entity);
	}

	/**
	 * The start method from IGamePluginService handles creation of the player.
	 *
	 * @param gameData
	 * @param world
	 */
	@Override
	public void start(GameData gameData, World world) {
		//Creating player creation parameters
		float x = gameData.getDisplayWidth() / 2;
		float y = gameData.getDisplayHeight() / 2;
		float radians = 0;
		//Creates and adds to world
		world.addEntity(createPlayer(x, y, radians));
	}

	/**
	 * The stop method from IGamePluginService removes the player from world.
	 *
	 * @param gameData
	 * @param world
	 */
	@Override
	public void stop(GameData gameData, World world) {
		// Remove entities
		for (Entity player : world.getEntities(Player.class)) {
			world.removeEntity(player);
		}
	}

	/**
	 * This method creates the player entity and gives the player its parts
	 *
	 * @param x
	 * @param y
	 * @param radians
	 * @return the player as an entity
	 */
	private Entity createPlayer(float x, float y, float radians) {
		//Create player entity
		Entity player = new Player();

		//Adds life part to player
		int life = 8;
		player.add(new LifePart(life));

		//Adds coin part to player
		int coins = 0;
		player.add(new CoinPart(coins));

		//Adds position part to player 
		player.add(new PositionPart(x, y, radians));

		//Creates parameters for moving part 
		float deacceleration = 700;
		float acceleration = 1200;
		float maxSpeed = 300;
		boolean allowWrapping = false;
		//Adds moving part to player
		player.add(new MovingPart(deacceleration, acceleration, maxSpeed, allowWrapping));

		//Adds aiming part to player
		player.add(new AimingPart());

		//Create parameters for collision part
		float collisionWidth = 64;
		float collisionHeight = 64;
		float collisionOffsetX = 0;
		float collisionOffsetY = 0;
		boolean isMovable = true;
		//Adds collision part to player
		player.add(new CollisionPart(collisionWidth, collisionHeight, collisionOffsetX, collisionOffsetY, x, y, isMovable));

		//Create parameters for hitbox part
		float hitboxWidth = 64;
		float hitboxHeight = 64;
		float hitboxOffsetX = 0;
		float hitboxOffsetY = 0;
		//Adds hitbox part to player
		player.add(new HitboxPart(hitboxWidth, hitboxHeight, hitboxOffsetX, hitboxOffsetY, x, y));

		player.add(new WeaponPart(IWeaponService.WeaponType.PLAYERWEAPON));

		//Adds cooldown part to player
		player.add(new CoolDownPart());

		//Creates file path to player texture
		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Player.jar!/assets/wizard.png");
		//Add player-texture/sprite to the player with priority 3. Priority 1 is first
		//to be displayed/drawn and priority 3 is third to be displayed on the screen 
		int priority = 3;
		player.add(new TexturePart(path, priority));
		TexturePart texturePart = player.getPart(TexturePart.class);
		//Adds different texture cuts to the texture part
		texturePart.addCut("WALKRIGHT", 8, 9, 1, 1, 0, 0.5f);
		texturePart.addCut("WALKRIGHTUP", 8, 9, 11, 1, 0, 0.5f);
		texturePart.addCut("WALKLEFTUP", 8, 9, 21, 1, 0, 0.5f);
		texturePart.addCut("WALKLEFT", 8, 9, 31, 1, 0, 0.5f);
		texturePart.addCut("SHOTRIGHT", 8, 10, 1, 11, 0, 1f);
		texturePart.addCut("SHOTRIGHTUP", 8, 10, 11, 11, 0, 1f);
		texturePart.addCut("SHOTLEFTUP", 8, 10, 21, 11, 0, 1f);
		texturePart.addCut("SHOTLEFT", 8, 10, 31, 11, 0, 1f);

		//Sets a initial cut for the player
		texturePart.setCurrentCut("WALKRIGHT");
		texturePart.updatePosition(player);

		//Returns the player entity
		return player;
	}

	/**
	 * This method updates the Collision-, Hitbox- and texture- part according to
	 * the new position part.
	 *
	 */
	private void updateAllPositions(Entity player) {
		//Get the collision-, hitbox- and texture- parts
		CollisionPart collisionPart = player.getPart(CollisionPart.class);
		HitboxPart hitboxPart = player.getPart(HitboxPart.class);
		TexturePart texturePart = player.getPart(TexturePart.class);

		//Updates the collision-, hitbox- and texture- parts
		collisionPart.updatePosition(player);
		hitboxPart.updatePosition(player);
		texturePart.updatePosition(player);
	}

	private IWeaponService getWeaponService() {
		return lookup.lookup(IWeaponService.class);
	}

	private IAudioService getAudioService() {
		return lookup.lookup(IAudioService.class);
	}

}
