package dk.sdu.mmmi.cbse.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Group 7
 */
public class MapGenerator {

	private Random mapGenRandom;
	private Random randomDirection;
	private int roomX;
	private int roomY;
	private int mapSize;

	public MapGenerator(int seed) {
		mapGenRandom = new Random(seed);
		randomDirection = new Random(seed);
	}

	/**
	 * Randomly generates a list of connected rooms.
	 *
	 * @param difficulty
	 * @return
	 */
	private List<Room> generateLevel(int difficulty) {
		List<Room> returnList = new ArrayList<>();

		//Amount of rooms to create depending on difficulty.
		int roomsAmount = difficulty * 2 + 3;

		//mapsize is the max width and height of the map.
		mapSize = difficulty * 2 + 1;
		roomX = mapSize / 2;
		roomY = mapSize / 2;

		//2D array used to generate a map only. Isn't returned.
		Room[][] rooms = new Room[mapSize][mapSize];

		//Creates the first room. The first room has no enemies.
		Room currentRoom = new Room(newSeed());
		currentRoom.setEmpty(true);
		rooms[roomX][roomY] = currentRoom;
		returnList.add(currentRoom);
		roomsAmount--;

		//Keeps generating new rooms as long as the amount hasn't been exceeded.
		while (roomsAmount > 0) {
			//Chooses a random direction to move.
			int direction = randomDirection();
			moveDirection(direction);
			Room nextRoom = rooms[roomX][roomY];

			//If a room doesn't exist in the moved direction, a new one is created.
			if (nextRoom == null) {
				nextRoom = new Room(newSeed());
				roomsAmount--;
				returnList.add(nextRoom);
			}

			//Adds doors between the rooms.
			currentRoom.addNeighbor(nextRoom, direction);
			nextRoom.addNeighbor(currentRoom, oppositeDirection(direction));

			currentRoom = nextRoom;
			rooms[roomX][roomY] = currentRoom;
		}
		//printMap(rooms, mapSize);

		//Set boss room. Avoids the first room.
		int bossRoom = mapGenRandom.nextInt(returnList.size() - 1) + 1;
		returnList.get(bossRoom).setHasBoss(true);

		return returnList;
	}

	/**
	 * The publicly available method for generating a new map.
	 *
	 * @param difficulty
	 * @return a new map using the set difficulty.
	 */
	public Map generateMap(int difficulty) {
		return new Map(difficulty, generateLevel(difficulty));
	}

	/**
	 * Used to generate a new seed for a room.
	 *
	 * @return a new seed to be used by a room.
	 */
	private int newSeed() {
		return mapGenRandom.nextInt();
	}

	/**
	 * Chooses a random direction to move. Only returns a possible direction.
	 *
	 * @return an integer representing a random direction.
	 */
	private int randomDirection() {
		//List for containing the possible directions to move.
		List<Integer> possibleDirections = new ArrayList<>();

		//Adds the possible directions to the list.
		if (roomX < mapSize - 1) {
			possibleDirections.add(1);
		}
		if (roomX > 0) {
			possibleDirections.add(3);
		}
		if (roomY < mapSize - 1) {
			possibleDirections.add(0);
		}
		if (roomY > 0) {
			possibleDirections.add(2);
		}

		//Picks a random direction for the list of possible directions.
		int random = randomDirection.nextInt(possibleDirections.size());
		int direction = possibleDirections.get(random);

		return direction;
	}

	/**
	 * Reverses a direction. Used for placing rooms in the next room.
	 *
	 * @param direction
	 * @return the direction opposite of the parameter.
	 */
	private int oppositeDirection(int direction) {
		switch (direction) {
			case 0:
				return 2;
			case 1:
				return 3;
			case 2:
				return 0;
			case 3:
				return 1;
		}
		return -1;
	}

	/**
	 * Changes the variable representing the current location.
	 *
	 * @param direction
	 */
	private void moveDirection(int direction) {
		switch (direction) {
			case 0:
				roomY++;
				break;
			case 1:
				roomX++;
				break;
			case 2:
				roomY--;
				break;
			case 3:
				roomX--;
				break;
		}
	}

	/**
	 * Prints the map layout as a 2D matrix to the console.
	 *
	 * @param rooms
	 * @param mapSize
	 */
	private void printMap(Room[][] rooms, int mapSize) {
		System.out.println("Printing map layout:");
		for (int i = mapSize - 1; i >= 0; i--) {
			for (int j = 0; j < mapSize; j++) {
				if (rooms[j][i] != null) {
					System.out.print("X");
				} else {
					System.out.print("0");
				}
			}
			System.out.println("");
		}
	}

}
