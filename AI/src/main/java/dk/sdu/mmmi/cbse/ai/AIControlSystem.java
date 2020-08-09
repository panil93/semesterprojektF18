package dk.sdu.mmmi.cbse.ai;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.MovingPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.services.IAI;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.commonobstacle.Obstacle;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 *
 * @author Group 7
 */
@ServiceProviders(value = {
	@ServiceProvider(service = IAI.class)
	,
	@ServiceProvider(service = IGamePluginService.class)
})
public class AIControlSystem implements IAI, IGamePluginService {

	@Override
	public List<Point2D.Float> getPathToTarget(World world, Entity sourceEntity, Entity targetEntity) {
		MovingPart sourceMovingPart = sourceEntity.getPart(MovingPart.class);
		PositionPart sourcePositionPart = sourceEntity.getPart(PositionPart.class);

		PositionPart targetPositionPart = targetEntity.getPart(PositionPart.class);

		if (sourceMovingPart == null
						|| sourcePositionPart == null
						|| targetPositionPart == null) {
			return null;
		}

		Point2D.Float roomStart = new Point2D.Float(128, 128);
		Point2D.Float roomEnd = new Point2D.Float(960, 576);
		Point2D.Float source = new Point2D.Float(sourcePositionPart.getX(), sourcePositionPart.getY());
		Point2D.Float target = new Point2D.Float(targetPositionPart.getX(), targetPositionPart.getY());

		ArrayList<Point2D.Float> obstacles = new ArrayList<>();
		for (Entity obstacle : world.getEntities(Obstacle.class)) {
			PositionPart obstaclePositionPart = obstacle.getPart(PositionPart.class);
			obstacles.add(new Point2D.Float(obstaclePositionPart.getX(), obstaclePositionPart.getY()));
		}

		return getPathToTarget(roomStart, roomEnd, 64, source, target, obstacles);
	}

	private ArrayList<Point2D.Float> getPathToTarget(
					Point2D.Float roomStartPixelPoint,
					Point2D.Float roomEndPixelPoint,
					int nodePixelSize,
					Point2D.Float sourcePixelPoint,
					Point2D.Float goalPixelPoint,
					ArrayList<Point2D.Float> obstacles) {
		Point endPoint = pointFromPixelPoint(roomStartPixelPoint, roomEndPixelPoint, nodePixelSize);
		int width = endPoint.x;
		int height = endPoint.y;

		NodeMap map = createMap(width, height);

		// Converts ostacle pixel coordinates to node coordinates
		ArrayList<Node> obstacleNodes = new ArrayList<>();
		for (Point2D.Float obstaclePixelPoint : obstacles) {
			Point obstaclePoint = pointFromPixelPoint(roomStartPixelPoint, obstaclePixelPoint, nodePixelSize);
			obstacleNodes.add(map.getFromPoint(obstaclePoint));
		}
		// Nodes with obstacles in are removed
		map.handleObstacles(obstacleNodes);

		Point sourcePoint = pointFromPixelPoint(roomStartPixelPoint, sourcePixelPoint, nodePixelSize);
		Point goalPoint = pointFromPixelPoint(roomStartPixelPoint, goalPixelPoint, nodePixelSize);

		Node source = map.getFromPoint(sourcePoint);
		Node goal = map.getFromPoint(goalPoint);

		ArrayList<Node> nodePath = getAStarNodePath(map, source, goal);
		ArrayList<Point2D.Float> path = convertNodePathToPixelPath(roomStartPixelPoint, nodePath, nodePixelSize);

		return path;
	}

	private NodeMap createMap(int width, int height) {
		return new NodeMap(width, height);
	}

	private Point pointFromPixelPoint(Point2D.Float start, Point2D.Float coordinate, int nodePixelSize) {
		int x = (int) ((coordinate.getX() - start.getX()) / nodePixelSize);
		int y = (int) ((coordinate.getY() - start.getY()) / nodePixelSize);
		return new Point(x, y);
	}

	private ArrayList<Node> getAStarNodePath(NodeMap map, Node source, Node goal) {
		ArrayList<Node> closedSet = new ArrayList<>();
		PQHeap openSet = new PQHeap(map.size());

		source.setGScore(0);
		source.setFScore(map.heuristicCostEstimate(source, goal));

		openSet.insert(source);

		while (!openSet.isEmpty()) {
			Node current = (Node) openSet.extractMin().data;

			if (current == null) {
				return null;
			}

			if (current.equals(goal)) {
				ArrayList<Node> path = reconstructPath(current);
				return path;
			}

			closedSet.add(current);

			for (Node neighbor : map.getNodeNeighbors(current)) {
				if (closedSet.contains(neighbor)) {
					continue;
				}

				int tentativeGScore = map.get(current).getGScore() + 1;

				if (tentativeGScore >= map.get(neighbor).getGScore()) {
					continue;
				}

				neighbor.setCameFrom(current);

				map.get(neighbor).setGScore(tentativeGScore);
				map.get(neighbor).setFScore(map.get(neighbor).getGScore() + map.heuristicCostEstimate(neighbor, goal));

				if (!openSet.containsNode(neighbor)) {
					openSet.insert(neighbor);
				}
			}
		}

		return null;
	}

	private ArrayList<Node> reconstructPath(Node node) {
		Node current = node;
		ArrayList<Node> path = new ArrayList<>();
		while (current.getCameFrom() != null) {
			path.add(0, current);
			current = current.getCameFrom();
		}
		return path;
	}

	private ArrayList<Point2D.Float> convertNodePathToPixelPath(Point2D.Float roomStartPixelPoint, ArrayList<Node> nodePath, int nodePixelSize) {
		ArrayList<Point2D.Float> path = new ArrayList<>();
		for (Node node : nodePath) {
			Point point = new Point(node.getX(), node.getY());
			path.add(pixelPointFromPoint(roomStartPixelPoint, point, nodePixelSize));
		}
		return path;
	}

	private Point2D.Float pixelPointFromPoint(Point2D.Float start, Point point, int nodePixelSize) {
		float x = (float) (point.x * nodePixelSize) + start.x + (nodePixelSize / 2);
		float y = (float) (point.y * nodePixelSize) + start.y + (nodePixelSize / 2);
		return new Point2D.Float(x, y);
	}

	@Override
	public void start(GameData gameData, World world) {
		//Do nothing
	}

	@Override
	public void stop(GameData gameData, World world) {
		//Do nothing
	}

}
