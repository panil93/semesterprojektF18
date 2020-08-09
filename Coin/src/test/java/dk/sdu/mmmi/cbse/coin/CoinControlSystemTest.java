package dk.sdu.mmmi.cbse.coin;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.commoncoin.Coin;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * This is a test for the Coin Control System in the Coin component.
 *
 * @author Group 7
 */
public class CoinControlSystemTest {

	private GameData gameData = new GameData();
	private World world = new World();

	/**
	 * This test checks the coin control system's createCoin method. Specifically
	 * checks if the coin is created with the right position.
	 */
	@Test
	public void testCreateCoin() {
		System.out.println("createCoin");
		float x = 3.4F;
		float y = 5.6F;

		CoinControlSystem instance = new CoinControlSystem();
		Entity result = instance.createCoin(x, y);
		assertTrue(result.getClass().equals(Coin.class));

		PositionPart positionPart = result.getPart(PositionPart.class);
		assertTrue(positionPart.getX() == x);
		assertTrue(positionPart.getY() == y);
	}
}
