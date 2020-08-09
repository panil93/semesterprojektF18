package dk.sdu.mmmi.cbse.ai;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.AimingPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.MovingPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.commonplayer.Player;
import dk.sdu.mmmi.cbse.player.PlayerControlSystem;
import java.awt.geom.Point2D;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Group 7
 */
public class AIControlSystemTest {

	private GameData gameData;
	private World world;

	public AIControlSystemTest() {
		this.gameData = new GameData();
		this.world = new World();

		this.gameData.setDisplayWidth(1024);
		this.gameData.setDisplayHeight(768);
	}

	/**
	 * Test of getPathToTarget method, of class AIControlSystem.
	 */
	@Test
	public void testGetPathToTarget() {
		System.out.println("getPathToTarget");

		// Player is added on start
		PlayerControlSystem PCS = new PlayerControlSystem();
		PCS.start(gameData, world);

		Entity player = getPlayer(world);
		if (player == null) {
			fail("Player entity for testing is null");
		}

		PositionPart playerPositionPart = player.getPart(PositionPart.class);

		if (playerPositionPart == null) {
			fail("Player entity position part for testing is null");
		}

		// The coordinates of the player
		float x = playerPositionPart.getX();
		float y = playerPositionPart.getY();

		Entity entity = new Entity();

		PositionPart entityPositionPart = new PositionPart(x + 128, y + 128);
		entity.add(entityPositionPart);

		MovingPart entityMovingPart = new MovingPart(10, 20, 20, false);
		entity.add(entityMovingPart);

		world.addEntity(entity);

		AIControlSystem AICS = new AIControlSystem();
		List<Point2D.Float> path = AICS.getPathToTarget(world, player, entity);

		assertTrue(path.size() > 0);
	}

	private Entity getPlayer(World world) {
		return world.getEntities(Player.class).get(0);
	}

}
