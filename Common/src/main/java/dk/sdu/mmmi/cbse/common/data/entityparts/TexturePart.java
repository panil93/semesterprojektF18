package dk.sdu.mmmi.cbse.common.data.entityparts;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Group 7
 */
public class TexturePart implements EntityPart {

	private String texturePath;

	//1: walls - 2: doors, obstacles - 3: player and enemies - 4: projectiles
	private int priority;

	private float width;
	private float height;
	private float x;
	private float y;

	private Map<String, TextureCut> cuts;
	private TextureCut currentCut;

	public TexturePart(String texturePath, int priority) {
		this.texturePath = texturePath;
		fixTexturePath();

		this.priority = priority;
		this.cuts = new HashMap<>();
	}

	public String getTexturePath() {
		return texturePath;
	}

	public int getPriority() {
		return priority;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public int getCutSourceX() {
		return currentCut.getX();
	}

	public int getCutSourceY() {
		return currentCut.getY();
	}

	public int getCutWidth() {
		return currentCut.getWidth();
	}

	public int getCutHeight() {
		return currentCut.getHeight();
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	private void fixTexturePath() {
		if (System.getProperty("os.name").startsWith("Windows")) {
			//texturePath = texturePath.substring(2);
			texturePath = texturePath.replaceAll("\\\\", "/");
		} else {
			String[] temp = texturePath.split("application");
			texturePath = temp[0] + "application/target/asteroidsnetbeansmodules" + temp[1];
		}
	}

	public void addCut(String cutName, int cutWidth, int cutHeight, int cutSourceX, int cutSourceY, float cutOffsetX, float cutOffsetY) {
		cuts.put(cutName, new TextureCut(cutWidth, cutHeight, cutSourceX, cutSourceY, cutOffsetX, cutOffsetY));
	}

	public void setCurrentCut(String cutName) {
		TextureCut cut = cuts.get(cutName);
		if (cut != null) {
			currentCut = cut;
			width = cut.getWidth() * 8;
			height = cut.getHeight() * 8;
		}
	}

	@Override
	public void process(GameData gameData, Entity entity) {

	}

	public void updatePosition(Entity entity) {
		PositionPart positionPart = entity.getPart(PositionPart.class);

		x = positionPart.getX() + currentCut.getOffsetX() * 8;
		y = positionPart.getY() + currentCut.getOffsetY() * 8;
	}

}
