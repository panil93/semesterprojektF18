package dk.sdu.mmmi.cbse.common.data.entityparts;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;

/**
 *
 * @author Group 7
 */
public class CollisionPart implements EntityPart {

	private float width;
	private float height;
	private float x;
	private float y;
	private float prevX;
	private float prevY;
	private float offsetX;
	private float offsetY;
	private boolean isMovable;

	public CollisionPart(float width, float height, float offsetX, float offsetY, float hostX, float hostY, boolean isMovable) {
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.isMovable = isMovable;

		x = hostX + offsetX;
		y = hostY + offsetY;
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

	public boolean isMovable() {
		return isMovable;
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

	public void updatePositionPart(Entity entity) {
		PositionPart positionPart = entity.getPart(PositionPart.class);

		positionPart.setX(x - offsetX);
		positionPart.setY(y - offsetY);
	}

}
