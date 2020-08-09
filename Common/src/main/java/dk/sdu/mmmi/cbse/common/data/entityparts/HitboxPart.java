package dk.sdu.mmmi.cbse.common.data.entityparts;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Group 7
 */
public class HitboxPart implements EntityPart {

	private Set<Entity> collidingEntities;

	private float width;
	private float height;
	private float x;
	private float y;
	private float prevX;
	private float prevY;
	private float offsetX;
	private float offsetY;

	public HitboxPart(float width, float height, float offsetX, float offsetY, float hostX, float hostY) {
		this.collidingEntities = new HashSet<>();
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;

		x = hostX + offsetX;
		y = hostY + offsetY;
	}

	public Set<Entity> getCollidingEntities() {
		return collidingEntities;
	}

	public void addCollidingEntity(Entity entity) {
		collidingEntities.add(entity);
	}

	public void clearCollidingEntities() {
		collidingEntities.clear();
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getPrevX() {
		return prevX;
	}

	public float getPrevY() {
		return prevY;
	}

	public boolean isCollided() {
		return !collidingEntities.isEmpty();
	}

	@Override
	public void process(GameData gameData, Entity entity) {

	}

	public void updatePosition(Entity entity) {
		PositionPart positionPart = entity.getPart(PositionPart.class);

		prevX = x;
		prevY = y;

		x = positionPart.getX() + offsetX;
		y = positionPart.getY() + offsetY;
	}

}
