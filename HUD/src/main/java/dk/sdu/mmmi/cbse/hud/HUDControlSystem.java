package dk.sdu.mmmi.cbse.hud;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.CoinPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.LifePart;
import dk.sdu.mmmi.cbse.common.data.entityparts.TexturePart;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IHUD;
import dk.sdu.mmmi.cbse.commonplayer.Player;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders(value = {
	@ServiceProvider(service = IHUD.class)
	,
	@ServiceProvider(service = IGamePluginService.class)
})
public class HUDControlSystem implements IHUD, IGamePluginService {

	private int coins;
	private int lives;
	private int level;

	private List<TexturePart> textures = new ArrayList<>();

	//Paths to the assets.
	private String numbersPath = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-HUD.jar!/assets/numbers.png");
	private String coinPath = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-HUD.jar!/assets/coin.png");
	private String wholeHeartPath = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-HUD.jar!/assets/wholeheart.png");
	private String halfHeartPath = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-HUD.jar!/assets/halfheart.png");
	private String overlayPath = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-HUD.jar!/assets/HUD_overlay.png");

	//Variables for the minimap.
	private int[][] minimap;
	private int minimapX;
	private int minimapY;
	private int minimapSize;
	private int minimapPosX = 968;
	private int minimapPosY = 672;

	//Integers representing the different kinds of dots on the minimap.
	private int player = 1;
	private int room = 2;
	private int door = 3;

	@Override
	public void process(GameData gameData, World world) {
		List<Entity> players = world.getEntities(Player.class);

		if (players.isEmpty()) {
			lives = 0;
		}

		for (Entity player : players) {
			LifePart lp = player.getPart(LifePart.class);
			lives = lp.getLife();
			CoinPart cp = player.getPart(CoinPart.class);
			coins = cp.getCoins();
		}

		textures.clear();

		addOverlay();
		updateLives();
		updateCoins();
		updateLevelNumber();
	}

	@Override
	public List<TexturePart> getTextures() {
		return textures;
	}

	private void addOverlay() {
		TexturePart tp = new TexturePart(overlayPath, 0);
		tp.addCut("DEFAULT", 136, 16, 0, 0, 0, 0);
		tp.setCurrentCut("DEFAULT");
		tp.setPosition(544, 640);

		textures.add(tp);
	}

	private void updateLives() {
		float x = 672;
		float y = 616;
		float offset = 48;

		int wholeHearts = lives / 2;

		for (int i = 0; i < wholeHearts; i++) {
			x += offset;
			TexturePart tp = new TexturePart(wholeHeartPath, 0);
			tp.addCut("DEFAULT", 5, 4, 0, 0, 0, 0);
			tp.setCurrentCut("DEFAULT");
			tp.setPosition(x, y);

			textures.add(tp);
		}
		if (lives % 2 == 1) {
			x += offset;
			TexturePart tp = new TexturePart(halfHeartPath, 0);
			tp.addCut("DEFAULT", 5, 4, 0, 0, 0, 0);
			tp.setCurrentCut("DEFAULT");
			tp.setPosition(x, y);

			textures.add(tp);
		}

	}

	private void updateCoins() {
		String coinString = Integer.toString(coins);

		float x = 364;
		float y = 612;
		float offset = 32;

		for (char c : coinString.toCharArray()) {
			x += offset;
			TexturePart tp = new TexturePart(numbersPath, 0);

			int number = Character.getNumericValue(c);
			int cutX = 1 + (number * 4);
			tp.addCut("NUMBER", 3, 5, cutX, 1, 0, 0);
			tp.setCurrentCut("NUMBER");
			tp.setPosition(x, y);

			textures.add(tp);
		}
	}

	private void updateLevelNumber() {
		String levelString = Integer.toString(level);

		float x = 228;
		float y = 612;
		float offset = 32;

		for (char c : levelString.toCharArray()) {
			x += offset;
			TexturePart tp = new TexturePart(numbersPath, 0);
			int number = Character.getNumericValue(c);
			int cutX = 1 + (number * 4);
			tp.addCut("NUMBER", 3, 5, cutX, 7, 0, 0);
			tp.setCurrentCut("NUMBER");
			tp.setPosition(x, y);

			textures.add(tp);
		}
	}

	@Override
	public Map<String, List<int[]>> getMinimap() {
		Map returnMap = new HashMap<>();

		List<int[]> playerPosition = new ArrayList<>();
		List<int[]> roomPositions = new ArrayList<>();
		List<int[]> doorPositions = new ArrayList<>();

		for (int x = minimapX - 5; x < minimapX + 5; x++) {
			if (x < 0 || x >= minimapSize) {
				continue;
			}
			for (int y = minimapY - 5; y < minimapY + 5; y++) {
				if (y < 0 || y >= minimapSize) {
					continue;
				}
				if (minimap[x][y] == player) {
					playerPosition.add(newTile(x, y));
				} else if (minimap[x][y] == room) {
					roomPositions.add(newTile(x, y));
				} else if (minimap[x][y] == door) {
					doorPositions.add(newTile(x, y));
				}
			}
		}

		returnMap.put("player", playerPosition);
		returnMap.put("room", roomPositions);
		returnMap.put("door", doorPositions);

		return returnMap;
	}

	@Override
	public void updateMinimap(int direction, boolean[] doors) {
		minimap[minimapX][minimapY] = room;
		switch (direction) {
			case 0:
				minimapY -= 2;
				break;
			case 1:
				minimapX += 2;
				break;
			case 2:
				minimapY += 2;
				break;
			case 3:
				minimapX -= 2;
				break;
		}

		minimap[minimapX][minimapY] = player;
		addDoors(doors);
	}

	@Override
	public void clearMinimap(int newSize, boolean[] doors) {
		level = newSize;
		minimapSize = (level * 2 + 1) * 2 - 1;
		minimap = new int[minimapSize][minimapSize];

		minimapX = minimapSize / 2;
		minimapY = minimapSize / 2;

		minimap[minimapX][minimapY] = player;
		addDoors(doors);
	}

	private int[] newTile(int x, int y) {
		int[] newTile = new int[2];

		newTile[0] = minimapPosX + ((x + (5 - minimapX)) * 8);
		newTile[1] = minimapPosY - ((y + (5 - minimapY)) * 8);

		return newTile;
	}

	private void addDoors(boolean[] doors) {
		if (doors[0] == true) {
			minimap[minimapX][minimapY - 1] = door;
		}
		if (doors[1] == true) {
			minimap[minimapX + 1][minimapY] = door;
		}
		if (doors[2] == true) {
			minimap[minimapX][minimapY + 1] = door;
		}
		if (doors[3] == true) {
			minimap[minimapX - 1][minimapY] = door;
		}
	}

	public int getCoins() {
		return coins;
	}

	public int getLives() {
		return lives;
	}

	public int[][] getMinimapArray() {
		return minimap;
	}

	@Override
	public void start(GameData gameData, World world) {
		//TODO
	}

	@Override
	public void stop(GameData gameData, World world) {
		//TODO
	}

}
