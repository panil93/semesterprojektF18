package dk.sdu.mmmi.cbse.player;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.entityparts.EntityPart;

/**
 *
 * @author Group 7
 */
public class CoolDownPart implements EntityPart {

	private float coolDown;

	public CoolDownPart() {
		coolDown = 0;
	}

	public void increaseCooldown(float dt) {
		coolDown += dt;
	}

	public boolean isCoolDownOver() {
		return coolDown == 0;
	}

	@Override
	public void process(GameData gameData, Entity entity) {
		float dt = gameData.getDelta();
		if (coolDown > 0) {
			coolDown -= dt;
			if (coolDown < 0) {
				coolDown = 0;
			}
		}

	}

}
