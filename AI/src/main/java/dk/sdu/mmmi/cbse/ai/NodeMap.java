package dk.sdu.mmmi.cbse.ai;

import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Group 7
 */
public class NodeMap {

	private ArrayList<Node> map = new ArrayList<>();

	public NodeMap(int xMax, int yMax) {
		for (int i = 0; i < xMax; i++) {
			for (int j = 0; j < yMax; j++) {
				map.add(new Node(i, j));
			}
		}
	}

	public Node get(Node node) {
		for (Node element : getMap()) {
			if (element.equals(node)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * @return the map
	 */
	public ArrayList<Node> getMap() {
		return map;
	}

	public Node getFromXY(int x, int y) {
		for (Node node : getMap()) {
			if (node.getX() == x && node.getY() == y) {
				return node;
			}
		}
		return null;
	}

	public Node getFromPoint(Point point) {
		return getFromXY(point.x, point.y);
	}

	public ArrayList<Node> getNodeNeighbors(Node node) {
		ArrayList<Node> neighbors = new ArrayList<>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				Node neighbor = this.getFromXY(node.getX() + i, node.getY() + j);
				if (neighbor == null || neighbor.equals(node)) {
					continue;
				}
				neighbors.add(neighbor);
			}
		}
		return neighbors;
	}

	public int heuristicCostEstimate(Node start, Node goal) {
		int dX = Math.abs(start.getX() - goal.getX());
		int dY = Math.abs(start.getY() - goal.getY());

		return dX > dY ? dX : dY;
	}

	private void removeNode(Node node) {
		if (map.contains(node)) {
			map.remove(node);
		}
	}

	public void handleObstacles(ArrayList<Node> obstacles) {
		for (Node obstacle : obstacles) {
			removeNode(obstacle);
		}
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}
}
