package dk.sdu.mmmi.cbse.common.data.entityparts;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;

/**
 * The Aiming class manages an entity's aim. The class holds radians, but not
 * position coordinates.
 *
 * @author Group 7
 */
public class AimingPart implements EntityPart {

	private float radians;
	private boolean left, right, up, down;

	public AimingPart() {
		this.radians = 0;
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

	public void setRadians(float radians) {
		this.radians = radians;
	}

	public float getRadians() {
		return radians;
	}
	
	public boolean isShooting() {
		return left || right || up || down;
	}

	@Override
	public void process(GameData gameData, Entity entity) {
		AimingPart aimingPart = entity.getPart(AimingPart.class);

		float radians = aimingPart.getRadians();

		// Aiming left
		if (left) {
			radians = 3.1415f;
		}

		// Aiming right
		if (right) {
			radians = 0;
		}

		// Aiming up
		if (up) {
			radians = 3.1415f * 0.5f;
		}

		// Aiming down
		if (down) {
			radians = 3.1415f * 1.5f;
		}

		aimingPart.setRadians(radians);
	}

}
