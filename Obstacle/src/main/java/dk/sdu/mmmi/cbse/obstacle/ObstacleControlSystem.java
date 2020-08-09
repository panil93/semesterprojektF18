package dk.sdu.mmmi.cbse.obstacle;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.CollisionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.HitboxPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.TexturePart;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.commonobstacle.Obstacle;
import dk.sdu.mmmi.cbse.commonobstacle.services.IObstacleSpawningService;
import java.io.File;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders(value = {
	@ServiceProvider(service = IObstacleSpawningService.class)
	,
	@ServiceProvider(service = IGamePluginService.class)
})
public class ObstacleControlSystem implements IObstacleSpawningService, IGamePluginService {

	@Override
	public Entity createObstacle(float x, float y, ObstacleType obstacleType) {
		Entity obstacle;

		switch (obstacleType) {
			case ROCK:
				obstacle = createRock(x, y);
				break;
			case CRATE:
				obstacle = createCrate(x, y);
				break;
			default:
				obstacle = null;
				break;
		}

		return obstacle;
	}

	public Entity createCrate(float x, float y) {
		Entity crate = new Obstacle();

		crate.add(new PositionPart(x, y, 0));

		float collisionWidth = 64;
		float collisionHeight = 64;
		float collisionOffsetX = 0;
		float collisionOffsetY = 0;
		boolean isMovable = false;
		crate.add(new CollisionPart(collisionWidth, collisionHeight, collisionOffsetX, collisionOffsetY, x, y, isMovable));

		float hitboxWidth = 64;
		float hitboxHeight = 64;
		float hitboxOffsetX = 0;
		float hitboxOffsetY = 0;
		crate.add(new HitboxPart(hitboxWidth, hitboxHeight, hitboxOffsetX, hitboxOffsetY, x, y));

		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Obstacle.jar!/assets/CRATE.png");
		int priority = 2;
		TexturePart texturePart = new TexturePart(path, priority);
		texturePart.addCut("DEFAULT", 8, 8, 0, 0, 0, 0);
		texturePart.setCurrentCut("DEFAULT");
		texturePart.updatePosition(crate);
		crate.add(texturePart);

		return crate;
	}

	public Entity createRock(float x, float y) {
		Entity rock = new Obstacle();

		rock.add(new PositionPart(x, y, 0));

		float collisionWidth = 64;
		float collisionHeight = 64;
		float collisionOffsetX = 0;
		float collisionOffsetY = 0;
		boolean isMovable = false;
		rock.add(new CollisionPart(collisionWidth, collisionHeight, collisionOffsetX, collisionOffsetY, x, y, isMovable));

		float hitboxWidth = 64;
		float hitboxHeight = 64;
		float hitboxOffsetX = 0;
		float hitboxOffsetY = 0;
		rock.add(new HitboxPart(hitboxWidth, hitboxHeight, hitboxOffsetX, hitboxOffsetY, x, y));

		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Obstacle.jar!/assets/ROCK.png");
		int priority = 2;
		TexturePart texturePart = new TexturePart(path, priority);
		texturePart.addCut("DEFAULT", 8, 8, 0, 0, 0, 0);
		texturePart.setCurrentCut("DEFAULT");
		texturePart.updatePosition(rock);
		rock.add(texturePart);

		return rock;
	}

	@Override
	public void start(GameData gameData, World world) {
		//Do nothing
	}

	@Override
	public void stop(GameData gameData, World world) {
		//Remove all obstacles
		for (Entity obstacle : world.getEntities(Obstacle.class)) {
			world.removeEntity(obstacle);
		}
	}

}
