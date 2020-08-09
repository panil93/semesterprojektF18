package dk.sdu.mmmi.cbse.collisiondetection;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.CollisionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.HitboxPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.MovingPart;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import dk.sdu.mmmi.cbse.common.services.ICollisionDetectionService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

/**
 *
 * @author Group 7
 */
@ServiceProviders(value = {
	@ServiceProvider(service = ICollisionDetectionService.class)
	,
	@ServiceProvider(service = IGamePluginService.class)
})
public class CollisionDetectionSystem implements ICollisionDetectionService, IGamePluginService {

	@Override
	public void collisionDetection(GameData gameData, World world) {
		//Detect collision between HixboxParts
		detectHitboxPartCollisons(world);

		//Detect collision between CollisionParts
		handleCollisionPartCollisons(world);
	}

	private void detectHitboxPartCollisons(World world) {
		List<Entity> entities = getEntitiesWithHitboxPart(world);

		//Double loops over entities with having the same pair twice
		for (int i = 0; i < entities.size() - 1; i++) {
			for (int j = i + 1; j < entities.size(); j++) {
				Entity entity1 = entities.get(i);
				HitboxPart hitbox1 = entity1.getPart(HitboxPart.class);

				Entity entity2 = entities.get(j);
				HitboxPart hitbox2 = entity2.getPart(HitboxPart.class);

				//Continue if entities are not colliding
				if (!isColliding(hitbox1, hitbox2)) {
					continue;
				}

				hitbox1.addCollidingEntity(entity2);
				hitbox2.addCollidingEntity(entity1);
			}
		}
	}

	/**
	 * Gets all entities in World that has a HitboxPart. It also clear the
	 * collidingEntities list from them.
	 *
	 * @param world
	 * @return
	 */
	private List<Entity> getEntitiesWithHitboxPart(World world) {
		List<Entity> entities = new ArrayList<>();
		for (Entity entity : world.getEntities()) {
			HitboxPart hitbox = entity.getPart(HitboxPart.class);
			if (hitbox != null) {
				hitbox.clearCollidingEntities();
				entities.add(entity);
			}
		}
		return entities;
	}

	private boolean isColliding(HitboxPart hitbox1, HitboxPart hitbox2) {
		float centerDistanceX = Math.abs(hitbox1.getX() - hitbox2.getX());
		float centerDistanceY = Math.abs(hitbox1.getY() - hitbox2.getY());

		float allowedDistanceX = (hitbox1.getWidth() + hitbox2.getWidth()) / 2;
		float allowedDistanceY = (hitbox1.getHeight() + hitbox2.getHeight()) / 2;

		return (allowedDistanceX > centerDistanceX && allowedDistanceY > centerDistanceY);
	}

	private void handleCollisionPartCollisons(World world) {
		List<Entity> entities = getEntitiesWithCollisionPart(world);

		//Double loops over entities with having the same pair twice
		for (int i = 0; i < entities.size() - 1; i++) {
			for (int j = i + 1; j < entities.size(); j++) {
				Entity entity1 = entities.get(i);
				CollisionPart col1 = entity1.getPart(CollisionPart.class);

				Entity entity2 = entities.get(j);
				CollisionPart col2 = entity2.getPart(CollisionPart.class);

				//Continues if both entites are static
				if (!col1.isMovable() && !col2.isMovable()) {
					continue;
				}

				//Continue if entities are not colliding
				if (!isColliding(col1, col2)) {
					continue;
				}

				Entity movingEntity;
				CollisionPart movingCol, staticCol;

				//Handles collision for the case where both entities are movable
				if (col1.isMovable() && col2.isMovable()) {
					//Finds radian between the two entities
					float distanceX = col2.getX() - col1.getX();
					float distanceY = col2.getY() - col1.getY();

					float hypotenuse = (float) sqrt(distanceX * distanceX + distanceY * distanceY);

					float newDx = distanceX / hypotenuse * 400;
					float newDy = distanceY / hypotenuse * 400;

					MovingPart movePart1 = entity1.getPart(MovingPart.class);
					movePart1.setDx(movePart1.getDx() - newDx);
					movePart1.setDy(movePart1.getDy() - newDy);

					MovingPart movePart2 = entity2.getPart(MovingPart.class);
					movePart2.setDx(movePart2.getDx() + newDx);
					movePart2.setDy(movePart2.getDy() + newDy);
				}//Handles collision for the case where one entity is immovable and the other is movable
				else {

					//Finds out which one is static
					if (col1.isMovable()) {
						movingEntity = entity1;
						movingCol = col1;
						staticCol = col2;
					} else {
						movingEntity = entity2;
						movingCol = col2;
						staticCol = col1;
					}

					float staticX = staticCol.getX();
					float staticY = staticCol.getY();
					float staticHalfWidth = staticCol.getWidth() / 2;
					float staticHalfHeight = staticCol.getHeight() / 2;

					float movePrevX = movingCol.getPrevX();
					float movePrevY = movingCol.getPrevY();
					float moveHalfHeight = movingCol.getHeight() / 2;
					float moveHalfWidth = movingCol.getWidth() / 2;

					//Calculate the top, bottem, left side and right side of the static entity
					float left = staticX - staticHalfWidth;
					float right = staticX + staticHalfWidth;
					float top = staticY + staticHalfHeight;
					float bottom = staticY - staticHalfHeight;

					int horisontalPos;
					if (right <= movePrevX - moveHalfWidth) {
						horisontalPos = 1;
					} else if (left >= movePrevX + moveHalfWidth) {
						horisontalPos = -1;
					} else {
						horisontalPos = 0;
					}

					int verticalPos;
					if (top <= movePrevY - moveHalfHeight) {
						verticalPos = 1;
					} else if (bottom >= movePrevY + moveHalfHeight) {
						verticalPos = -1;
					} else {
						verticalPos = 0;
					}

					//If side case
					if (horisontalPos * verticalPos == 0) {
						//If from left or right
						MovingPart movePart = movingEntity.getPart(MovingPart.class);
						if (verticalPos == 0) {
							//Sets the movingParts X
							int direction = movingCol.getPrevX() > staticX ? 1 : -1;
							movingCol.setX(staticX + (staticHalfWidth + moveHalfWidth) * direction);
							movePart.setDx(0);
						}//If from above or below
						else {
							//Sets the movingParts Y
							int direction = movingCol.getPrevY() > staticY ? 1 : -1;
							movingCol.setY(staticY + (staticHalfHeight + moveHalfHeight) * direction);
							movePart.setDy(0);
						}

						movingCol.updatePositionPart(movingEntity);

					}//If corner case
					else {
						//This case is not handled because it is too complex and the case is rare anyway
					}
				}
			}
		}
	}

	/**
	 * Gets all entities in World that has a CollisionPart.
	 *
	 * @param world
	 * @return
	 */
	private List<Entity> getEntitiesWithCollisionPart(World world) {
		List<Entity> entities = new ArrayList<>();
		for (Entity entity : world.getEntities()) {
			CollisionPart collisionPart = entity.getPart(CollisionPart.class);
			if (collisionPart != null) {
				entities.add(entity);
			}
		}
		return entities;
	}

	private boolean isColliding(CollisionPart col1, CollisionPart col2) {
		float centerDistanceX = Math.abs(col1.getX() - col2.getX());
		float centerDistanceY = Math.abs(col1.getY() - col2.getY());

		float allowedDistanceX = (col1.getWidth() + col2.getWidth()) / 2;
		float allowedDistanceY = (col1.getHeight() + col2.getHeight()) / 2;

		return (allowedDistanceX > centerDistanceX && allowedDistanceY > centerDistanceY);
	}

	@Override
	public void start(GameData gameData, World world) {
		//Do nothing
	}

	@Override
	public void stop(GameData gameData, World world) {
		//Clears all detected collision
		for (Entity entity : world.getEntities()) {
			HitboxPart hitboxPart = entity.getPart(HitboxPart.class);
			if (hitboxPart != null) {
				hitboxPart.clearCollidingEntities();
			}
		}
	}

}
