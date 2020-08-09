package dk.sdu.mmmi.cbse.common.data.entityparts;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;

/**
 *
 * @author Group 7
 */
public class LifePart implements EntityPart {

	private int life;
	private int maxLife;

	public LifePart(int maxLife) {
		this.maxLife = maxLife;
		this.life = maxLife;
	}

	public int getLife() {
		return life;
	}

	public int getMaxLife() {
		return maxLife;
	}

	public void increaseLife(int life) {
		this.life += life;
		checkBorder();
	}

	public void decreaseLife(int life) {
		this.life -= life;
		checkBorder();
	}

	/**
	 * Checks if life is below 0 or above max life and then adjust life.
	 *
	 * @param life
	 */
	private void checkBorder() {
		if (life > maxLife) {
			life = maxLife;
		} else if (life < 0) {
			life = 0;
		}
	}

	public boolean isDead() {
		return life == 0;
	}

	@Override
	public void process(GameData gameData, Entity entity) {

	}

}
