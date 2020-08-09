/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.sdu.mmmi.cbse.obstacle;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.entityparts.CollisionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.HitboxPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.TexturePart;
import dk.sdu.mmmi.cbse.commonobstacle.Obstacle;
import dk.sdu.mmmi.cbse.commonobstacle.services.IObstacleSpawningService;
import static dk.sdu.mmmi.cbse.commonobstacle.services.IObstacleSpawningService.ObstacleType.CRATE;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Group 7
 */
public class ObstacleControlSystemTest {
    
    public ObstacleControlSystemTest() {
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
     * Test of createObstacle method, of class ObstacleControlSystem.
     */
@Test
    public void testCreateObstacle() {
	System.out.println("createObstacle");
	float x = (float) 0.06;
	float y = (float) 0.07;
	
	IObstacleSpawningService.ObstacleType obstacleType1 = CRATE;
	ObstacleControlSystem instance = new ObstacleControlSystem();
	Entity expResult = new Obstacle();
        expResult.add(new PositionPart(x, y, 0));
	float collisionWidth = 64;
		float collisionHeight = 64;
		float collisionOffsetX = 0;
		float collisionOffsetY = 0;
		boolean isMovable = false;
		expResult.add(new CollisionPart(collisionWidth, collisionHeight, collisionOffsetX, collisionOffsetY, x, y, isMovable));

		float hitboxWidth = 64;
		float hitboxHeight = 64;
		float hitboxOffsetX = 0;
		float hitboxOffsetY = 0;
		expResult.add(new HitboxPart(hitboxWidth, hitboxHeight, hitboxOffsetX, hitboxOffsetY, x, y));

		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Obstacle.jar!/assets/CRATE.png");
		int priority = 2;
		TexturePart texturePart = new TexturePart(path, priority);
		texturePart.addCut("DEFAULT", 8, 8, 0, 0, 0, 0);
		texturePart.setCurrentCut("DEFAULT");
		texturePart.updatePosition(expResult);
		expResult.add(texturePart);
	
	Entity result = instance.createObstacle(x, y, obstacleType1);
        TexturePart text = result.getPart(TexturePart.class); // -> nyt
	assertEquals("obstacle created correctly",texturePart.getTexturePath(), text.getTexturePath()); // -Ændret

	
    }

       
}
