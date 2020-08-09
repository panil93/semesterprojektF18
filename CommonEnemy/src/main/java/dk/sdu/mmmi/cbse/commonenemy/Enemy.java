package dk.sdu.mmmi.cbse.commonenemy;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService.EnemyType;

/**
 *
 * @author Group 7
 */
public class Enemy extends Entity {

	private float lifetime = 0;
	private EnemyType type;

	public Enemy(EnemyType type) {
		this.type = type;
	}

	/**
	 * @return the lifetime
	 */
	public float getLifetime() {
		return lifetime;
	}

	/**
	 * @param deltaTime the amount of time lifetime is increased
	 */
	public void increaseLifetime(float deltaTime) {
		this.lifetime += deltaTime;
	}

	/**
	 * @return the type
	 */
	public EnemyType getType() {
		return type;
	}

}
