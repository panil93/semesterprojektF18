package dk.sdu.mmmi.cbse.weapon;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.HitboxPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.TexturePart;
import dk.sdu.mmmi.cbse.common.services.IAudioService;
import dk.sdu.mmmi.cbse.commonenemy.Enemy;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.commonobstacle.Obstacle;
import dk.sdu.mmmi.cbse.commonplayer.Player;
import dk.sdu.mmmi.cbse.commonweapon.data.Projectile;
import dk.sdu.mmmi.cbse.commonweapon.data.entityparts.OwnershipPart;
import dk.sdu.mmmi.cbse.commonweapon.services.IWeaponService;
import java.io.File;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;
import org.openide.util.Lookup;

/**
 *
 * @author Group 7
 */
@ServiceProviders(value = {
	@ServiceProvider(service = IEntityProcessingService.class)
	,
	 @ServiceProvider(service = IPostEntityProcessingService.class)
	,
	 @ServiceProvider(service = IWeaponService.class)
	,
	 @ServiceProvider(service = IGamePluginService.class)
})
public class WeaponControlSystem implements IEntityProcessingService, IPostEntityProcessingService, IWeaponService, IGamePluginService {

	private float projectileSpeed = 360;

	private Lookup lookup = Lookup.getDefault();

	@Override
	public void process(GameData gameData, World world) {
		float dt = gameData.getDelta();

		for (Entity projectile : world.getEntities(Projectile.class)) {
			PositionPart positionPart = projectile.getPart(PositionPart.class);

			float x = positionPart.getX();
			float y = positionPart.getY();
			float radians = positionPart.getRadians();

			//Calculates and sets next position
			positionPart.setX((float) (x + Math.cos(radians) * projectileSpeed * dt));
			positionPart.setY((float) (y + Math.sin(radians) * projectileSpeed * dt));

			//Updates hitbox position after projectile position is updated
			HitboxPart hitboxPart = projectile.getPart(HitboxPart.class);
			hitboxPart.updatePosition(projectile);

			//Check if out of boundary
			if (isOutOfWorld(gameData, projectile)) {
				world.removeEntity(projectile);
			}
		}
	}

	private boolean isOutOfWorld(GameData gameData, Entity entity) {
		int width = gameData.getDisplayWidth();
		int height = gameData.getDisplayHeight();

		PositionPart positionPart = entity.getPart(PositionPart.class);

		if (positionPart.getX() <= 0 || positionPart.getX() > width || positionPart.getY() <= 0 || positionPart.getY() > height) {
			return true;
		}
		return false;
	}

	@Override
	public void postProcess(GameData gameData, World world) {
		//Handle collision and Texture parts
		for (Entity projectile : world.getEntities(Projectile.class)) {
			handleCollision(world, projectile);
			//Update Texture parts position
			TexturePart texturePart = projectile.getPart(TexturePart.class);
			texturePart.updatePosition(projectile);
		}
	}

	private void handleCollision(World world, Entity projectile) {
		HitboxPart projectileHitbox = projectile.getPart(HitboxPart.class);

		for (Entity entity : projectileHitbox.getCollidingEntities()) {
			Class type = entity.getClass();

			//Removes projectile if it collide with player, enemy or an obstacle
			if (type.equals(Enemy.class) || type.equals(Player.class) || type.equals(Obstacle.class)) {
				world.removeEntity(projectile);
			}

		}
	}

	@Override
	public void shoot(GameData gameData, World world, float x, float y, float radians, Entity owner) {
		//Plays sound when shooting
		IAudioService audio = getAudioService();
		if (audio != null) {
			audio.playSound("dk-sdu-mmmi-cbse-Weapon.jar!/assets/projectile.mp3");
		}

		Entity projectile = createProjectile(x, y, radians, owner);
		world.addEntity(projectile);
	}

	private Entity createProjectile(float x, float y, float radians, Entity owner) {
		Projectile projectile = new Projectile();

		//If radians are between 0 and PI/4 or between PI * 7/4 and PI*2 the x coordinate will be increased by 50
		if (radians >= 0 && radians < 3.1415f / 4 || radians >= 3.1415f * 1.75 && radians < 3.1415f * 2) {
			x = x + 50;
		} //If radians are between PI/4 and PI * 3/4 the y coordinate will be increased by 50
		else if (radians >= 3.1415f / 4 && radians < 3.1415f * 0.75) {
			y = y + 50;
		} //If radians are between PI * 3/4 and PI * 5/4 the x coordinate will be decreased by 50
		else if (radians >= 3.1415f * 0.75 && radians < 3.1415f * 1.25) {
			x = x - 50;
		} //If radians are between PI * 5/4 and PI * 7/4 the y coordinate will be decreased by 50
		else if (radians >= 3.1415f * 1.25 && radians < 3.1415f * 1.75) {
			y = y - 50;
		}

		projectile.add(new PositionPart(x, y, radians));
		projectile.add(new OwnershipPart(owner));

		float width = 32;
		float height = 32;
		float offsetX = 0;
		float offsetY = 0;
		projectile.add(new HitboxPart(width, height, offsetX, offsetY, x, y));

		String texturePath = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Weapon.jar!/assets/projectile.png");
		int priority = 4;
		TexturePart texturePart = new TexturePart(texturePath, priority);
		texturePart.addCut("DEFAULT", 4, 4, 0, 0, 0, 0);
		texturePart.setCurrentCut("DEFAULT");
		texturePart.updatePosition(owner);
		projectile.add(texturePart);

		return projectile;
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
		//Removes all projectiles
		for (Entity projectile : world.getEntities(Projectile.class)) {
			world.removeEntity(projectile);
		}
	}

}
