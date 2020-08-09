package dk.sdu.mmmi.cbse.spawner;

public class Position {

	private int x;
	private int y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof Position)) {
			return false;
		}

		Position otherPosition = (Position) other;
		return x == otherPosition.x && y == otherPosition.y;
	}

	@Override
	public int hashCode() {
		return x + y;
	}

}
