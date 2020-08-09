package dk.sdu.mmmi.cbse.weapon;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.HitboxPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.MovingPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.commonenemy.Enemy;
import dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService.EnemyType;
import dk.sdu.mmmi.cbse.commonplayer.Player;
import dk.sdu.mmmi.cbse.commonweapon.data.Projectile;
import dk.sdu.mmmi.cbse.commonweapon.data.entityparts.OwnershipPart;
import dk.sdu.mmmi.cbse.commonweapon.data.entityparts.WeaponPart;
import dk.sdu.mmmi.cbse.commonweapon.services.IWeaponService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
public class WeaponControlSystemTest {

	public WeaponControlSystemTest() {
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
	 * Test of shoot method, of class WeaponControlSystem.
	 */

	@Test
    public void testCollision() {
        /*
        Setup Entities and default variables needed
         */
        GameData gameData = new GameData();
        World world = new World();
        Player owner = new Player();

        float radians = (float) 3;
        float x = (float) 1;
        float y = (float) 2;
        owner.add(new PositionPart(x, y, radians));

        //Creates parameters for moving part 
        float deacceleration = 700;
        float acceleration = 1000;
        float maxSpeed = 300;
        boolean allowWrapping = false;
        
        //Adds moving part to player
        owner.add(new MovingPart(deacceleration, acceleration, maxSpeed, allowWrapping));

        //Create parameters for hitbox part
        float hitboxWidth = 64;
        float hitboxHeight = 64;
        float hitboxOffsetX = 0;
        float hitboxOffsetY = 0;
        //Adds hitbox part to player
        owner.add(new HitboxPart(hitboxWidth, hitboxHeight, hitboxOffsetX, hitboxOffsetY, x, y));

        owner.add(new WeaponPart(IWeaponService.WeaponType.PLAYERWEAPON));
        //Add Ememy

        x = x + 50;
        float width = 32;
        float height = 32;
        float offsetX = 0;
        float offsetY = 0;
        Enemy e = new Enemy(EnemyType.BOSS);
        e.add(new HitboxPart(width, height, offsetX, offsetY, x, y));
        e.add(new WeaponPart(IWeaponService.WeaponType.MINIONWEAPON));
        
        //Add our entities to the world
        world.addEntity(owner);
        world.addEntity(e);
        
        //fire weapon and detect collision changes
        WeaponControlSystem wcs = new WeaponControlSystem();
        wcs.shoot(gameData, world, x, y, radians, owner);
        int listOfWorld = world.getEntities().size(); // java pass by references , so to make it easy we just store the size instead of reference to world object that would need to be cloned.
        detectHitboxPartCollisons(world); //updates the entities with hitbox data - taken from CollisionDetection module.
        wcs.postProcess(gameData, world); // handle the collision
        
        assertTrue("collision detected! Enemy has been removed", listOfWorld >= world.getEntities().size());

    }
    	private void detectHitboxPartCollisons(World world) {
		List<Entity> entities = getEntitiesWithHitboxPart(world);

		//Double loops over entities with having the same pair twice
		for (int i = 0; i < entities.size() - 1; i++) {
			for (int j = i + 1; j < entities.size(); j++) {
				Entity entity1 = entities.get(i);
				HitboxPart hitbox1 = entity1.getPart(HitboxPart.class);

				Entity entity2 = entities.get(j);
				HitboxPart hitbox2 = entity2.getPart(HitboxPart.class);

				//Continue if entities are not colliding
				if (!isColliding(hitbox1, hitbox2)) {
					continue;
				}

				hitbox1.addCollidingEntity(entity2);
				hitbox2.addCollidingEntity(entity1);
			}
		}
	}

	/**
	 * Gets all entities in World that has a HitboxPart. It also clear the
	 * collidingEntities list from them.
	 *
	 * @param world
	 * @return
	 */
	private List<Entity> getEntitiesWithHitboxPart(World world) {
		List<Entity> entities = new ArrayList<>();
		for (Entity entity : world.getEntities()) {
			HitboxPart hitbox = entity.getPart(HitboxPart.class);
			if (hitbox != null) {
				hitbox.clearCollidingEntities();
				entities.add(entity);
			}
		}
		return entities;
	}
        private boolean isColliding(HitboxPart hitbox1, HitboxPart hitbox2) {
		float centerDistanceX = Math.abs(hitbox1.getX() - hitbox2.getX());
		float centerDistanceY = Math.abs(hitbox1.getY() - hitbox2.getY());

		float allowedDistanceX = (hitbox1.getWidth() + hitbox2.getWidth()) / 2;
		float allowedDistanceY = (hitbox1.getHeight() + hitbox2.getHeight()) / 2;

		return (allowedDistanceX > centerDistanceX && allowedDistanceY > centerDistanceY);
	}
}
