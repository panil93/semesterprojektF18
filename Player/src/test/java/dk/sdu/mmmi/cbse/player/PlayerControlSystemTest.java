package dk.sdu.mmmi.cbse.player;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import static dk.sdu.mmmi.cbse.common.data.GameKeys.W;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.commonplayer.Player;
import static org.junit.Assert.*;

/**
 * This is a test for the Player Control System in the Player component.
 *
 * @author Group 7
 */
public class PlayerControlSystemTest {

	private GameData gameData = new GameData();
	private World world = new World();

	/**
	 * This test checks the player control system's process method. Specifically
	 * checks if the player is moving.
	 *
	 */
	@org.junit.Test
	public void testProcess() {
		System.out.println("process: Check if player is moving when W key is down");
		PlayerControlSystem instance = new PlayerControlSystem();
		instance.start(gameData, world);

		Entity player = null;
		for (Entity entity : world.getEntities(Player.class)) {
			player = entity;
			break;
		}
		if (player == null) {
			fail("No player");
		}

		gameData.getKeys().setKey(W, true);
		gameData.setDelta(1);

		PositionPart positionPart = player.getPart(PositionPart.class);

		float startY = positionPart.getY();

		instance.process(gameData, world);

		float newY = positionPart.getY();

		assertTrue(startY < newY);

		System.out.println("process: Player is moving, when W key is down");
	}

	/**
	 * This test checks the player control system's start method. Specifically
	 * checks if the player is added to world.
	 */
	@org.junit.Test
	public void testStart() {
		System.out.println("start: Player entity should be added to world");

		int amount = world.getEntities().size();
		System.out.println("Entities in world before start: " + amount);

		PlayerControlSystem instance = new PlayerControlSystem();
		instance.start(gameData, world);

		int newAmount = world.getEntities().size();
		System.out.println("Entities in world after start: " + newAmount);

		assertTrue(amount < newAmount);
	}

}
