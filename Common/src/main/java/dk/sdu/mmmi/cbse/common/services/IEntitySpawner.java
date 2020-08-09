package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.Entity;
import java.util.List;
import java.util.Map;

public interface IEntitySpawner {

	//Strings for settings.
	public static final String DIFFICULTY = "Difficulty";
	public final String ENTERDIRECTION = "EnterDirection";
	public final String ISEMPTY = "IsEmpty";
	public final String HASBOSS = "HasBoss";
	public final String ISDEFEATED = "IsDefeated";

	/**
	 * Returns entities that should be spawned in a room. Entities should vary
	 * depending on the parameters and the seed. Given the same seed the entities
	 * should also be the same (They might vary a depending on ENTERDIRECTION and
	 * ISDEFEATED).
	 *
	 * @param seed
	 * @param parameters
	 * @return
	 */
	public List<Entity> getEntities(int seed, Map<String, String> parameters);

}
