package dk.sdu.mmmi.cbse.hud;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.CoinPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.LifePart;
import dk.sdu.mmmi.cbse.common.data.entityparts.TexturePart;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author group7
 */
public class HUDControlSystemTest {
	
	public HUDControlSystemTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}
	
	/**
	 * Test of process and getTextures method, of class HUDControlSystem.
	 */
	@Test
	public void testGetTextures() {
		System.out.println("getTextures");
		HUDControlSystem instance = new HUDControlSystem();
		
		GameData gameData = null;
		World world = new World();
		world.addEntity(createPlayer());
		
		instance.process(gameData, world);
		List<TexturePart> result = instance.getTextures();
		
		//Tests that the list isn't null;
		assertNotNull(result);
		
		//Tests that the list isn't empty.
		assertFalse(result.isEmpty());
	}

	/**
	 * Test of getMinimap method, of class HUDControlSystem.
	 */
	@Test
	public void testGetMinimap() {
		System.out.println("getMinimap");
		HUDControlSystem instance = new HUDControlSystem();
		Map<String, List<int[]>> result = instance.getMinimap();
		assertNotNull(result);
	}

	/**
	 * Test of updateMinimap method, of class HUDControlSystem.
	 */
	@Test
	public void testUpdateMinimap() {
		System.out.println("updateMinimap");
		int direction = 0;
		int level = 1;
		boolean[] doors1 = new boolean[] {true, false, true, true};
		boolean[] doors2 = new boolean[] {false, false, false, true};
		HUDControlSystem instance = new HUDControlSystem();
		instance.clearMinimap(level, doors1);
		instance.updateMinimap(direction, doors2);
	}

	/**
	 * Test of clearMinimap method, of class HUDControlSystem.
	 */
	@Test
	public void testClearMinimap() {
		System.out.println("clearMinimap");
		int newSize = 5;
		boolean[] doors = new boolean[4];
		HUDControlSystem instance = new HUDControlSystem();
		int[][] oldMinimap = instance.getMinimapArray();
		instance.clearMinimap(newSize, doors);
		int[][] newMinimap = instance.getMinimapArray();
		
		//Tests that the old and new minimap aren't the same.
		assertNotEquals(oldMinimap, newMinimap);
		
		int size = newMinimap.length;
		int expSize = (newSize * 2 + 1) * 2 - 1;
		
		//Tests that the size of the minimap is correct.
		assertEquals(expSize, size);
	}
	
	private Entity createPlayer() {
		Entity player = new Entity();
		player.add(new LifePart(4));
		player.add(new CoinPart(0));
		
		return player;
	}
	
}
