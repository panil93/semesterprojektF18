package dk.sdu.mmmi.cbse.commonweapon.services;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 *
 * @author Group 7
 */
public interface IWeaponService {

	public enum WeaponType {
		PLAYERWEAPON, BOSSWEAPON, MINIONWEAPON;
	}

	public void shoot(GameData gameData, World world, float x, float y, float radians, Entity owner);

}
