package dk.sdu.mmmi.cbse.map.data.entityparts;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.entityparts.EntityPart;

/**
 *
 * @author Group 7
 */
public class DoorPart implements EntityPart {

	private int direction;

	public DoorPart(int direction) {
		this.direction = direction;
	}

	@Override
	public void process(GameData gameData, Entity entity) {
		//Do nothing
	}

	public int getDirection() {
		return direction;
	}

}
