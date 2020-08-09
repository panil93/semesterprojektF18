package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.World;
import java.awt.geom.Point2D;
import java.util.List;

/**
 *
 * @author Group 7
 */
public interface IAI {

	public List<Point2D.Float> getPathToTarget(World world, Entity source, Entity target);

}
