/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.sdu.mmmi.cbse.enemy;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.commonenemy.Enemy;
import dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService;
import dk.sdu.mmmi.cbse.commonenemy.services.IEnemySpawningService.EnemyType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Daniel
 */
public class EnemyControlSystemTest {

	private GameData gameData = new GameData();
	private World world = new World();

	public EnemyControlSystemTest() {
	}

	/**
	 * Test of process method, of class EnemyControlSystem.
	 */
	@Test
	public void testProcess() {
		System.out.println("process: Check if enemy is moving");
		EnemyControlSystem instance = new EnemyControlSystem();

		world.addEntity(instance.createEnemy(256, 256, 0, EnemyType.BAT));

		Entity enemy = null;
		for (Entity entity : world.getEntities(Enemy.class)) {
			enemy = entity;
			break;
		}
		if (enemy == null) {
			fail("No enemy");
		}
		gameData.setDelta(1);

		PositionPart positionPart = enemy.getPart(PositionPart.class);

		float startY = positionPart.getY();

		instance.process(gameData, world);

		float newY = positionPart.getY();

		assertTrue(startY != newY);

		System.out.println("process: Enemy is moving");
	}

	/**
	 * Test of createEnemy method, of class EnemyControlSystem.
	 */
	@Test
	public void testCreateEnemy() {
		System.out.println("createEnemy");
		float x = 256.0F;
		float y = 256.0F;
		float radians = 0.0F;
		EnemyType enemyType = EnemyType.BAT;
		EnemyControlSystem instance = new EnemyControlSystem();
		Entity enemy = instance.createEnemy(x, y, radians, enemyType);
		world.addEntity(enemy);
		assertTrue(world.getEntities(Enemy.class).size() == 1);
	}

}
