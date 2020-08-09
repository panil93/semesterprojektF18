package dk.sdu.mmmi.cbse.commonweapon.data.entityparts;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.entityparts.EntityPart;

/**
 *
 * @author Group 7
 */
public class OwnershipPart implements EntityPart {

	private Entity owner;

	public OwnershipPart(Entity owner) {
		this.owner = owner;
	}

	public Entity getOwner() {
		return owner;
	}

	@Override
	public void process(GameData gameData, Entity entity) {

	}

}
