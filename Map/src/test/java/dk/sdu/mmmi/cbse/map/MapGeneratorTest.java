package dk.sdu.mmmi.cbse.map;

import java.util.ArrayList;
import java.util.List;
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
public class MapGeneratorTest {

	public MapGeneratorTest() {
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
	 * Test of generateMap method, of class MapGenerator.
	 * Generates two maps using the same base seed, and tests that they are the same.
	 */
	@Test
	public void testGenerateMap() {
		System.out.println("generateMap");

		int baseSeed = (int) Math.random();
		int difficulty = 1;
		MapGenerator instance1 = new MapGenerator(baseSeed);
		MapGenerator instance2 = new MapGenerator(baseSeed);
				
		Map result1 = instance1.generateMap(difficulty);
		Map result2 = instance2.generateMap(difficulty);
				
		//Tests that the size of the maps are the same.
		assertEquals(result1.getMapSize(), result2.getMapSize());
		
		//Tests that the difficulty of the maps is the same.
		assertEquals(result1.getMapDifficulty(), result2.getMapDifficulty());
		
		//Tests that all the rooms have neighbors in the same directions.
		List<Room> rooms1 = result1.getRooms();
		List<Room> rooms2 = result2.getRooms();
		for (int i = 0; i < result1.getMapSize(); i++) {
			Room[] neighbors1 = rooms1.get(i).getNeighborRooms();
			Room[] neighbors2 = rooms2.get(i).getNeighborRooms();
			for (int j = 0; j < neighbors1.length; j++) {
				if ((neighbors1[j] == null && neighbors2[j] != null) || (neighbors1[j] != null && neighbors2[j] == null)) {
					fail("Rooms aren't in the same layout.");
				}
			}
		}
	}

}
