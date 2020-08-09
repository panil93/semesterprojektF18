package dk.sdu.mmmi.cbse.map;

import dk.sdu.mmmi.cbse.common.data.Entity;
import java.util.ArrayList;
import java.util.List;

public class Room {

	public static final int UP = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;

	private Room[] neighborRooms;
	private int seed;
	private boolean isDefeated;
	private boolean hasBoss;
	private boolean isEmpty;
	private List<Entity> savedItems;
	private boolean doorsOpen;

	public Room(int seed) {
		instantiateNeighborRooms();
		this.seed = seed;
		isDefeated = false;
		hasBoss = false;
		isEmpty = false;
		savedItems = new ArrayList<>();
	}

	private void instantiateNeighborRooms() {
		neighborRooms = new Room[4];
		for (Room room : neighborRooms) {
			room = null;
		}
	}

	public void addNeighbor(Room neighbor, int direction) {
		if (direction < 0 || direction > 3) {
			return;
		}
		neighborRooms[direction] = neighbor;
	}

	public Room[] getNeighborRooms() {
		return neighborRooms;
	}

	public Room getNeighborRoom(int direction) {
		return neighborRooms[direction];
	}

	public int getSeed() {
		return seed;
	}

	public boolean isDefeated() {
		return isDefeated;
	}

	public void setDefeated(boolean isDefeated) {
		this.isDefeated = isDefeated;
	}

	public boolean hasBoss() {
		return hasBoss;
	}

	public void setHasBoss(boolean hasBoss) {
		this.hasBoss = hasBoss;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public void addItem(Entity item) {
		savedItems.add(item);
	}

	public List<Entity> getSavedItems() {
		return savedItems;
	}

	public void clearSavedItems() {
		savedItems.clear();
	}

	public boolean isDoorsOpen() {
		return doorsOpen;
	}

	public void setDoorsOpen(boolean doorsOpen) {
		this.doorsOpen = doorsOpen;
	}

	public boolean[] hasDoorInDirection() {
		boolean[] doorPresent = new boolean[4];

		for (int i = 0; i < neighborRooms.length; i++) {
			if (neighborRooms[i] != null) {
				doorPresent[i] = true;
			}
		}

		return doorPresent;
	}
}
