package dk.sdu.mmmi.cbse.collisiondetection;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.HitboxPart;
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
public class CollisionDetectionSystemTest {

	public CollisionDetectionSystemTest() {
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
	 * Test of collisionDetection method, of class CollisionDetectionSystem.
	 */
	@Test
	public void testCollisionDetection() {
		System.out.println("collisionDetection");

		GameData gameData = null;
		World world = new World();
		addEntities(world);

		CollisionDetectionSystem instance = new CollisionDetectionSystem();
		instance.collisionDetection(gameData, world);

		int amountOfCollides = 0;
		for (Entity entity : world.getEntities()) {
			HitboxPart hitbox = entity.getPart(HitboxPart.class);
			if (hitbox.isCollided()) {
				amountOfCollides++;
			}
		}

		assertEquals(2, amountOfCollides);
	}

	/**
	 * Adds 3 entities of which two of them should be colliding.
	 *
	 * @param world
	 */
	private void addEntities(World world) {
		Entity entity;
		float width, height, offsetX, offsetY, hostX, hostY;

		entity = new Entity();
		width = 50;
		height = 50;
		offsetX = 0;
		offsetY = 0;
		hostX = 100;
		hostY = 200;
		entity.add(new HitboxPart(width, height, offsetX, offsetY, hostX, hostY));
		world.addEntity(entity);

		entity = new Entity();
		width = 50;
		height = 50;
		offsetX = 0;
		offsetY = 0;
		hostX = 140;
		hostY = 200;
		entity.add(new HitboxPart(width, height, offsetX, offsetY, hostX, hostY));
		world.addEntity(entity);

		entity = new Entity();
		width = 50;
		height = 50;
		offsetX = 0;
		offsetY = 0;
		hostX = 400;
		hostY = 200;
		entity.add(new HitboxPart(width, height, offsetX, offsetY, hostX, hostY));
		world.addEntity(entity);
	}

}
