package dk.sdu.mmmi.cbse.common.data.entityparts;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import static java.lang.Math.sqrt;

/**
 *
 * @author Group7
 */
public class MovingPart implements EntityPart {

	private float dx, dy;
	private float deceleration, acceleration;
	private float maxSpeed;
	private boolean left, right, up, down;
	private boolean allowWrapping;
	private float lastdx;
	private float lastdy;

	public MovingPart(float deceleration, float acceleration, float maxSpeed, boolean allowWrapping) {
		this.deceleration = deceleration;
		this.acceleration = acceleration;
		this.maxSpeed = maxSpeed;
		this.allowWrapping = allowWrapping;
	}

	public float getDx() {
		return dx;
	}

	public void setDx(float dx) {
		this.dx = dx;
	}

	public float getDy() {
		return dy;
	}

	public void setDy(float dy) {
		this.dy = dy;
	}

	public void setDeceleration(float deceleration) {
		this.deceleration = deceleration;
	}

	public float getAcceleration() {
		return this.acceleration;
	}

	public void setAcceleration(float acceleration) {
		this.acceleration = acceleration;
	}

	public float getMaxSpeed() {
		return this.maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public void setSpeed(float speed) {
		this.acceleration = speed;
		this.maxSpeed = speed;
	}

	/**
	 * @return the left
	 */
	public boolean isLeft() {
		return left;
	}

	/**
	 * @return the right
	 */
	public boolean isRight() {
		return right;
	}

	/**
	 * @return the up
	 */
	public boolean isUp() {
		return up;
	}

	/**
	 * @return the down
	 */
	public boolean isDown() {
		return down;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public void setAllowWrapping(boolean allow) {
		this.allowWrapping = allow;
	}

	@Override
	public void process(GameData gameData, Entity entity) {
		PositionPart positionPart = entity.getPart(PositionPart.class);
		float x = positionPart.getX();
		float y = positionPart.getY();
		float radians = positionPart.getRadians();
		float dt = gameData.getDelta();

		// Moving left
		if (isLeft()) {
			dx -= acceleration * dt;
		}

		// Moving right
		if (isRight()) {
			dx += acceleration * dt;
		}

		// Moving up            
		if (isUp()) {
			dy += acceleration * dt;
		}

		// Moving down            
		if (isDown()) {
			dy -= acceleration * dt;
		}

		// deccelerating
		float vec = (float) sqrt(dx * dx + dy * dy);
		if (vec > 0) {
			dx -= (dx / vec) * deceleration * dt;
			dy -= (dy / vec) * deceleration * dt;
		}
		if (vec > maxSpeed) {
			dx = (dx / vec) * maxSpeed;
			dy = (dy / vec) * maxSpeed;
		}

		//Jiggle fix
		if ((lastdx < 0 && dx > 0) || (lastdx > 0 && dx < 0)) {
			dx = 0;
		}
		if ((lastdy < 0 && dy > 0) || (lastdy > 0 && dy < 0)) {
			dy = 0;
		}

		//Set last dx and dy.
		lastdx = dx;
		lastdy = dy;

		// set position
		x += dx * dt;
		y += dy * dt;

		if (allowWrapping) {
			if (x > gameData.getDisplayWidth()) {
				x = 0;
			} else if (x < 0) {
				x = gameData.getDisplayWidth();
			}
			if (y > gameData.getDisplayHeight()) {
				y = 0;
			} else if (y < 0) {
				y = gameData.getDisplayHeight();
			}
		}

		positionPart.setX(x);
		positionPart.setY(y);

		positionPart.setRadians(radians);
	}

}
