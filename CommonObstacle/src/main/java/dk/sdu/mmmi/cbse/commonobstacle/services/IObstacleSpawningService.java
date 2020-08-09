package dk.sdu.mmmi.cbse.commonobstacle.services;

import dk.sdu.mmmi.cbse.common.data.Entity;

public interface IObstacleSpawningService {

	public enum ObstacleType {
		ROCK, CRATE
	}

	public Entity createObstacle(float x, float y, ObstacleType obstacleType);

}
