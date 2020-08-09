package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.TexturePart;
import java.util.List;
import java.util.Map;

/**
 *
 * @author group7
 */
public interface IHUD {

	public void process(GameData gameData, World world);
	
	public List<TexturePart> getTextures();
	
	public void updateMinimap(int direction, boolean[] doors);
	
	public void clearMinimap(int newSize, boolean[] doors);
	
	public Map<String, List<int[]>> getMinimap();

}
