package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 *
 * @author Group 7
 */
public interface ICollisionDetectionService {

	void collisionDetection(GameData gameData, World world);

}
