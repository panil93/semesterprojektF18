package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.World;

/**
 *
 * @author Group 7
 */
public interface IMapService {
	
	public static final int UP = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;

	public void generateMap();

	public void loadRoom(World world);

	public void loadRoom(World world, int direction);

	public void nextLevel(World world);

	public void process(World world);

	public int getMapDifficulty();

	public String getBackgroundTexturePath();

}
