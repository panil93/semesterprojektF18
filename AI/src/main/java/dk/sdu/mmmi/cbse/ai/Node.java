package dk.sdu.mmmi.cbse.ai;

import java.awt.Point;

/**
 *
 * @author Group 7
 */
public class Node {

	private Point point;
	private int gScore;
	private int fScore;
	private Node cameFrom;

	public Node(int x, int y) {
		this.point = new Point(x, y);
		this.gScore = Integer.MAX_VALUE;
		this.fScore = Integer.MAX_VALUE;
		this.cameFrom = null;
	}

	@Override
	public boolean equals(Object object) {
		Node node = (Node) object;

		if (node == null) {
			return false;
		}
		if (node.getX() == getX() && node.getY() == getY()) {
			return true;
		}

		return false;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return point.x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return point.y;
	}

	/**
	 * @return the gScore
	 */
	public int getGScore() {
		return gScore;
	}

	/**
	 * @param gScore the gScore to set
	 */
	public void setGScore(int gScore) {
		this.gScore = gScore;
	}

	/**
	 * @return the fScore
	 */
	public int getFScore() {
		return fScore;
	}

	/**
	 * @param fScore the fScore to set
	 */
	public void setFScore(int fScore) {
		this.fScore = fScore;
	}

	/**
	 * @return the cameFrom
	 */
	public Node getCameFrom() {
		return cameFrom;
	}

	/**
	 * @param cameFrom the cameFrom to set
	 */
	public void setCameFrom(Node cameFrom) {
		this.cameFrom = cameFrom;
	}

}
