package dk.sdu.mmmi.cbse.commonweapon.data.entityparts;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.entityparts.EntityPart;
import dk.sdu.mmmi.cbse.commonweapon.services.IWeaponService.WeaponType;

public class WeaponPart implements EntityPart {

	private float shootGap;
	private float shootTimer;

	private boolean isShootingReady;
	private boolean isShooting;

	public WeaponPart(WeaponType type) {
		switch (type) {
			case PLAYERWEAPON:
				createPlayerWeapon();
				break;
			case MINIONWEAPON:
				createMinionWeapon();
				break;
			case BOSSWEAPON:
				createBossWeapon();
				break;
		}
	}

	public void setShooting(boolean isShooting) {
		this.isShooting = isShooting;
	}

	public boolean isShootingReady() {
		return isShootingReady;
	}

	@Override
	public void process(GameData gameData, Entity entity) {
		if (isShooting) {
			shootTimer -= gameData.getDelta();

			if (shootTimer < 0) {
				isShootingReady = true;
				shootTimer += shootGap;
			} else {
				isShootingReady = false;
			}
		} else {
			shootTimer = 0;
		}
	}

	private void createPlayerWeapon() {
		shootGap = (float) (1 / 3.0);
		shootTimer = 0;
		isShootingReady = false;
	}

	private void createMinionWeapon() {
		shootGap = (float) (1 / 0.8);
		shootTimer = 0;
		isShootingReady = false;
	}

	private void createBossWeapon() {
		shootGap = (float) (1 / 1.6);
		shootTimer = 0;
		isShootingReady = false;
	}

}
