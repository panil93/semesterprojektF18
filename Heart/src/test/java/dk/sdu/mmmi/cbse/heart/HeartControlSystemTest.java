package dk.sdu.mmmi.cbse.heart;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.commonheart.Heart;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * This is a test for the Heart Control System in the Heart component.
 * 
 * @author Group 7
 */
public class HeartControlSystemTest {

	private GameData gameData = new GameData();
	private World world = new World();

	
	/**
	 * This test checks the heart control system's createHeart method. Specifically
	 * checks if the heart is created with the right position.
	 */
	@Test
	public void testCreateHeart() {
		System.out.println("createHeart");
		float x = 3.4F;
		float y = 5.6F;
		
		HeartControlSystem instance = new HeartControlSystem();
		Entity result = instance.createHeart(x, y);
		assertTrue(result.getClass().equals(Heart.class));
		
		PositionPart positionPart = result.getPart(PositionPart.class);
		assertTrue(positionPart.getX() == x);
		assertTrue(positionPart.getY() == y);
	}
}
