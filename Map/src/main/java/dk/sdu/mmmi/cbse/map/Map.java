package dk.sdu.mmmi.cbse.map;

import dk.sdu.mmmi.cbse.map.data.entityparts.DoorPart;
import dk.sdu.mmmi.cbse.map.data.Door;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.CollisionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.HitboxPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.TexturePart;
import dk.sdu.mmmi.cbse.common.services.IEntitySpawner;
import dk.sdu.mmmi.cbse.commoncoin.Coin;
import dk.sdu.mmmi.cbse.commonheart.Heart;
import dk.sdu.mmmi.cbse.commonplayer.Player;
import dk.sdu.mmmi.cbse.map.data.Stairs;
import dk.sdu.mmmi.cbse.map.data.Wall;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import org.openide.util.Lookup;

public class Map {

	private List<Room> rooms;
	private Room currentRoom;
	private java.util.Map<String, String> currentRoomSettings;
	private String backgroundTexturePath;
	private int mapDifficulty;
	private Lookup lookup;

	public Map(int mapDifficulty, List<Room> rooms) {
		this.mapDifficulty = mapDifficulty;
		instantiateRoomSettings();
		setBackgroundTexturePath();
		this.rooms = rooms;
		lookup = Lookup.getDefault();
	}

	private void instantiateRoomSettings() {
		currentRoomSettings = new HashMap<>();
		currentRoomSettings.put(IEntitySpawner.DIFFICULTY, Integer.toString(mapDifficulty));
	}

	private void setBackgroundTexturePath() {
		String backgroundImageName;
		switch (mapDifficulty) {
			case 1:
				backgroundImageName = "background_blue.png";
				break;
			case 2:
				backgroundImageName = "background_green.png";
				break;
			case 3:
				backgroundImageName = "background_red.png";
				break;
			default:
				backgroundImageName = "background_red.png";
				break;
		}
		//Fixes texture path
		if (System.getProperty("os.name").startsWith("Windows")) {

			backgroundTexturePath = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Map.jar!/assets/" + backgroundImageName);
			backgroundTexturePath = backgroundTexturePath.substring(2);
			backgroundTexturePath = backgroundTexturePath.replaceAll("\\\\", "/");
		} else {
			backgroundTexturePath = (new File("").getAbsolutePath() + "/target/straightupwizardbeansmodules/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Map.jar!/assets/background.png");
		}
	}

	public void loadRoom(World world) {
		for (Entity entity : world.getEntities()) {
			Class type = entity.getClass();
			//Does not remove if entity is player
			if (type == Player.class) {
				continue;
			}
			if (type == Coin.class || type == Heart.class) {
				currentRoom.addItem(entity);
			}
			world.removeEntity(entity);
		}

		currentRoom = rooms.get(0);
		setRoomSettings(-1);
		spawnWalls(world);
		spawnDoors(world);
		spawnEntities(world);
	}

	public void loadRoom(World world, int direction) {
		Room nextRoom = currentRoom.getNeighborRoom(direction);
		if (nextRoom == null) {
			return;
		}

		Entity player = null;

		//Removes all entities but saves player and items
		for (Entity entity : world.getEntities()) {
			Class type = entity.getClass();

			if (type == Player.class) {
				player = entity;
				continue;
			}
			if (type == Coin.class || type == Heart.class) {
				currentRoom.addItem(entity);
			}
			world.removeEntity(entity);
		}

		currentRoom = nextRoom;

		loadSavedItems(world);

		setRoomSettings(direction);

		movePlayer(player, direction);

		spawnWalls(world);
		spawnDoors(world);
		spawnEntities(world);

		if (currentRoom.isDefeated() && currentRoom.hasBoss()) {
			spawnStairs(world);
		}
	}

	public String getBackgroundTexturePath() {
		return backgroundTexturePath;
	}

	private void loadSavedItems(World world) {
		for (Entity item : currentRoom.getSavedItems()) {
			world.addEntity(item);
		}
		currentRoom.clearSavedItems();
	}

	private void spawnEntities(World world) {
		IEntitySpawner spawner = getEntitySpawner();
		if (spawner == null) {
			System.out.println("Entity spawner implementation not found.");
			return;
		}

		for (Entity entity : spawner.getEntities(currentRoom.getSeed(), currentRoomSettings)) {
			world.addEntity(entity);
		}
	}

	private IEntitySpawner getEntitySpawner() {
		return lookup.lookup(IEntitySpawner.class);
	}

	private void spawnWalls(World world) {
		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Map.jar!/assets/" + getImageName());
		float x, y, width, height;
		boolean isMovable = false;
		TexturePart texPart;
		int priority = 1;

		//Creates north wall
		Entity wallNorth = new Wall();

		x = 544;
		y = 620;
		wallNorth.add(new PositionPart(x, y, 0));

		width = 992;
		height = 88;
		wallNorth.add(new CollisionPart(width, height, 0, 0, x, y, isMovable));

		texPart = new TexturePart(path, priority);
		texPart.addCut("DEFAULT", 124, 11, 43, 1, 0, 0);
		texPart.setCurrentCut("DEFAULT");
		texPart.updatePosition(wallNorth);
		wallNorth.add(texPart);

		//Creates east wall
		Entity wallEast = new Wall();

		x = 1004;
		y = 352;
		wallEast.add(new PositionPart(x, y, 0));

		width = 88;
		height = 608;
		wallEast.add(new CollisionPart(width, height, 0, 0, x, y, isMovable));

		texPart = new TexturePart(path, priority);
		texPart.addCut("DEFAULT", 11, 76, 31, 1, 0, 0);
		texPart.setCurrentCut("DEFAULT");
		texPart.updatePosition(wallEast);
		wallEast.add(texPart);

		//Creates south wall
		Entity wallSouth = new Wall();

		x = 544;
		y = 84;
		wallSouth.add(new PositionPart(x, y, 0));

		width = 992;
		height = 88;
		wallSouth.add(new CollisionPart(width, height, 0, 0, x, y, isMovable));

		texPart = new TexturePart(path, priority);
		texPart.addCut("DEFAULT", 124, 11, 43, 13, 0, 0);
		texPart.setCurrentCut("DEFAULT");
		texPart.updatePosition(wallSouth);
		wallSouth.add(texPart);

		//Creates west wall
		Entity wallWest = new Wall();

		x = 84;
		y = 352;
		wallWest.add(new PositionPart(x, y, 0));

		width = 88;
		height = 608;
		wallWest.add(new CollisionPart(width, height, 0, 0, x, y, isMovable));

		texPart = new TexturePart(path, priority);
		texPart.addCut("DEFAULT", 11, 76, 19, 1, 0, 0);
		texPart.setCurrentCut("DEFAULT");
		texPart.updatePosition(wallWest);
		wallWest.add(texPart);

		world.addEntity(wallNorth);
		world.addEntity(wallSouth);
		world.addEntity(wallWest);
		world.addEntity(wallEast);
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	private void spawnDoors(World world) {
		String doorStatus;
		if (currentRoom.isDefeated()) {
			doorStatus = "OPEN";
		} else {
			doorStatus = "CLOSED";
		}
		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Map.jar!/assets/" + getImageName());

		float width = 64;
		float height = 64;

		if (currentRoom.getNeighborRoom(0) != null) {
			Entity northDoor = new Door();
			float x = 544;
			float y = 608;
			northDoor.add(new PositionPart(x, y, 0));
			northDoor.add(new HitboxPart(width, height, 0, 0, x, y));
			northDoor.add(new DoorPart(0));

			int priority = 2;
			TexturePart texPart = new TexturePart(path, priority);
			texPart.addCut("CLOSED", 8, 8, 1, 1, 0, 0);
			texPart.addCut("OPEN", 8, 8, 10, 1, 0, 0);
			texPart.setCurrentCut(doorStatus);
			texPart.updatePosition(northDoor);
			northDoor.add(texPart);

			world.addEntity(northDoor);
		}
		if (currentRoom.getNeighborRoom(1) != null) {
			Entity eastDoor = new Door();
			float x = 992;
			float y = 352;
			eastDoor.add(new PositionPart(x, y, 0));
			eastDoor.add(new HitboxPart(width, height, 0, 0, x, y));
			eastDoor.add(new DoorPart(1));

			int priority = 2;
			TexturePart texPart = new TexturePart(path, priority);
			texPart.addCut("CLOSED", 8, 8, 1, 10, 0, 0);
			texPart.addCut("OPEN", 8, 8, 10, 10, 0, 0);
			texPart.setCurrentCut(doorStatus);
			texPart.updatePosition(eastDoor);
			eastDoor.add(texPart);

			world.addEntity(eastDoor);
		}
		if (currentRoom.getNeighborRoom(2) != null) {
			Entity southDoor = new Door();
			float x = 544;
			float y = 96;
			southDoor.add(new PositionPart(x, y, 0));
			southDoor.add(new HitboxPart(width, height, 0, 0, x, y));
			southDoor.add(new DoorPart(2));

			int priority = 2;
			TexturePart texPart = new TexturePart(path, priority);
			texPart.addCut("CLOSED", 8, 8, 1, 28, 0, 0);
			texPart.addCut("OPEN", 8, 8, 10, 28, 0, 0);
			texPart.setCurrentCut(doorStatus);
			texPart.updatePosition(southDoor);
			southDoor.add(texPart);

			world.addEntity(southDoor);
		}
		if (currentRoom.getNeighborRoom(3) != null) {
			Entity westDoor = new Door();
			float x = 96;
			float y = 352;
			westDoor.add(new PositionPart(x, y, 0));
			westDoor.add(new HitboxPart(width, height, 0, 0, x, y));
			westDoor.add(new DoorPart(3));

			int priority = 2;
			TexturePart texPart = new TexturePart(path, priority);
			texPart.addCut("CLOSED", 8, 8, 1, 19, 0, 0);
			texPart.addCut("OPEN", 8, 8, 10, 19, 0, 0);
			texPart.setCurrentCut(doorStatus);
			texPart.updatePosition(westDoor);
			westDoor.add(texPart);

			world.addEntity(westDoor);
		}
	}

	/**
	 * Sets the current rooms settings for the spawner.
	 *
	 * @param direction is the enter direction of the player
	 */
	private void setRoomSettings(int direction) {
		//Checks if room should be empty
		if (currentRoom.isEmpty()) {
			currentRoomSettings.put(IEntitySpawner.ISEMPTY, "true");
		} else {
			currentRoomSettings.put(IEntitySpawner.ISEMPTY, "false");
		}

		//Checks if room should have a boss
		if (currentRoom.hasBoss()) {
			currentRoomSettings.put(IEntitySpawner.HASBOSS, "true");
		} else {
			currentRoomSettings.put(IEntitySpawner.HASBOSS, "false");
		}

		//Checks if room should have enemies
		if (currentRoom.isDefeated()) {
			currentRoomSettings.put(IEntitySpawner.ISDEFEATED, "true");
		} else {
			currentRoomSettings.put(IEntitySpawner.ISDEFEATED, "false");
		}

		//Sets the enter direction
		switch (direction) {
			case 0:
				currentRoomSettings.put(IEntitySpawner.ENTERDIRECTION, "down");
				break;
			case 1:
				currentRoomSettings.put(IEntitySpawner.ENTERDIRECTION, "left");
				break;
			case 2:
				currentRoomSettings.put(IEntitySpawner.ENTERDIRECTION, "up");
				break;
			case 3:
				currentRoomSettings.put(IEntitySpawner.ENTERDIRECTION, "right");
				break;
			default:
				currentRoomSettings.put(IEntitySpawner.ENTERDIRECTION, "null");
				break;
		}

	}

	private void movePlayer(Entity player, int direction) {
		PositionPart posPart = player.getPart(PositionPart.class);
		switch (direction) {
			case 0:
				posPart.setPosition(544, 160);
				break;
			case 1:
				posPart.setPosition(160, 352);
				break;
			case 2:
				posPart.setPosition(544, 544);
				break;
			case 3:
				posPart.setPosition(928, 352);
		}
	}

	public Room getCurrentRoom() {
		return currentRoom;
	}

	private String getImageName() {
		String imageName;
		switch (mapDifficulty) {
			case 1:
				imageName = "room_blue.png";
				break;
			case 2:
				imageName = "room_green.png";
				break;
			case 3:
				imageName = "room_red.png";
				break;
			default:
				imageName = "room_red.png";
				break;
		}
		return imageName;
	}

	public int getMapSize() {
		return rooms.size();
	}

	public int getMapDifficulty() {
		return mapDifficulty;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void spawnStairs(World world) {
		Entity stairs = new Stairs();

		String path;
		String imageName = getImageName();
		if (System.getProperty("os.name").startsWith("Windows")) {
			path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Map.jar!/assets/" + imageName);
			path = path.replaceAll("\\\\", "/");
		} else {
			path = (new File("").getAbsolutePath() + "/target/straightupwizardbeansmodules/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Map.jar!/assets/" + imageName);
		}

		float x = 544;
		float y = 384;
		float width = 64;
		float height = 64;

		stairs.add(new PositionPart(x, y, 0));
		stairs.add(new HitboxPart(width, height, 0, 0, x, y));

		int priority = 2;
		TexturePart texturePart = new TexturePart(path, priority);
		texturePart.addCut("DEFAULT", 8, 8, 1, 37, 0, 0);
		texturePart.setCurrentCut("DEFAULT");
		texturePart.updatePosition(stairs);
		stairs.add(texturePart);

		world.addEntity(stairs);
	}

	public void removeStairs(World world) {
		for (Entity stairs : world.getEntities(Stairs.class)) {
			world.removeEntity(stairs);
		}
	}

}
