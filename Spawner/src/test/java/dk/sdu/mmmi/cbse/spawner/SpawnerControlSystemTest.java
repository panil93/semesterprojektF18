package dk.sdu.mmmi.cbse.spawner;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.services.IEntitySpawner;
import java.util.HashMap;
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
 * @author Group 7
 */
public class SpawnerControlSystemTest {

	public SpawnerControlSystemTest() {
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
	 * Test of getEntities method, of class SpawnerControlSystem.
	 */
	@Test
	public void testGetEntities() {
		System.out.println("getEntities");

		int seed = 100;
		Map<String, String> parameters = new HashMap<>();
		instantiateRoomSettings(parameters);

		SpawnerControlSystem instance = new SpawnerControlSystem();

		List<Entity> result = instance.getEntities(seed, parameters);
		assertEquals(true, result.isEmpty()); //Should be empty because no services are available
	}

	private void instantiateRoomSettings(Map parameters) {
		parameters.put(IEntitySpawner.DIFFICULTY, "1");
		parameters.put(IEntitySpawner.ENTERDIRECTION, "right");
		parameters.put(IEntitySpawner.HASBOSS, "false");
		parameters.put(IEntitySpawner.ISDEFEATED, "false");
		parameters.put(IEntitySpawner.ISEMPTY, "false");
	}

}
