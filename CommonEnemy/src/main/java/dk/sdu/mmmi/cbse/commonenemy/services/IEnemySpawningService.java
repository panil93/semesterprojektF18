package dk.sdu.mmmi.cbse.commonenemy.services;

import dk.sdu.mmmi.cbse.common.data.Entity;

public interface IEnemySpawningService {

	public enum EnemyType {
		BOSS, BAT, GHOST, JELLYBEAST, SCOUT, SKELETONMAGE, SKELETONWARRIOR;
	}

	public Entity createEnemy(float x, float y, float radians, EnemyType enemyType);

}
