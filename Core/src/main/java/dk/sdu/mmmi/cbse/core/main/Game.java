package dk.sdu.mmmi.cbse.core.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Disposable;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.TexturePart;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IHUD;
import dk.sdu.mmmi.cbse.common.services.IMapService;
import dk.sdu.mmmi.cbse.core.managers.AssetsJarFileResolver;
import dk.sdu.mmmi.cbse.core.managers.GameInputProcessor;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import java.util.ArrayList;
import dk.sdu.mmmi.cbse.common.services.ICollisionDetectionService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;
import java.util.Map;
import dk.sdu.mmmi.cbse.common.services.IAudioService;

public class Game implements ApplicationListener {

	private static OrthographicCamera cam;
	private final Lookup lookup = Lookup.getDefault();
	private final GameData gameData = new GameData();
	private World world = new World();
	private List<IGamePluginService> gamePlugins = new CopyOnWriteArrayList<>();
	private Lookup.Result<IGamePluginService> result;
	private SpriteBatch batch;
	private List<Disposable> disposables;

	//Asset manager
	private AssetsJarFileResolver jfhr = new AssetsJarFileResolver();
	private AssetManager assMan = new AssetManager(jfhr);

	@Override
	public void create() {

		gameData.setDisplayWidth(Gdx.graphics.getWidth());
		gameData.setDisplayHeight(Gdx.graphics.getHeight());

		cam = new OrthographicCamera(gameData.getDisplayWidth(), gameData.getDisplayHeight());
		cam.translate(gameData.getDisplayWidth() / 2, gameData.getDisplayHeight() / 2);
		cam.update();

		batch = new SpriteBatch();
		disposables = new ArrayList<>();

		Gdx.input.setInputProcessor(new GameInputProcessor(gameData));

		result = lookup.lookupResult(IGamePluginService.class);
		result.addLookupListener(lookupListener);
		result.allItems();

		for (IGamePluginService plugin : result.allInstances()) {
			plugin.start(gameData, world);
			gamePlugins.add(plugin);
		}

		IMapService map = getMapService();
		if (map != null) {
			map.generateMap();
			map.loadRoom(world);
		}

		startBackgroundMusic();
	}

	@Override
	public void render() {
		try {
			gameData.setDelta(Gdx.graphics.getDeltaTime());
			gameData.getKeys().update();

			update();
			draw();
			playSounds();
		} catch (Exception e) {

		}
	}

	private void update() {
		// Update
		for (IEntityProcessingService entityProcessorService : getEntityProcessingServices()) {
			entityProcessorService.process(gameData, world);
		}

		// Collision detection
		ICollisionDetectionService collisionDetectionService = getCollisionDetectionService();
		if (collisionDetectionService != null) {
			collisionDetectionService.collisionDetection(gameData, world);
		}

		// Post Update
		for (IPostEntityProcessingService postEntityProcessorService : getPostEntityProcessingServices()) {
			postEntityProcessorService.postProcess(gameData, world);
		}

		//Map process
		IMapService mapService = getMapService();
		if (mapService != null) {
			mapService.process(world);
		}

		//HUD process
		IHUD hud = getHUD();
		if (hud != null) {
			hud.process(gameData, world);
		}
	}

	private void draw() {
		// clear screen to black
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		drawMap();

		//Draws all entities with TextureParts
		batch.setProjectionMatrix(cam.combined);
		batch.begin();

		for (TexturePart texPart : getOrderedTextureParts()) {
			String path = texPart.getTexturePath();

			if (!assMan.isLoaded(path)) {
				assMan.load(path, Texture.class);
				assMan.finishLoading();
			}

			Texture texture = assMan.get(path, Texture.class);

			Sprite sprite;

			if (!texture.getTextureData().isPrepared()) {
				texture.getTextureData().prepare();
			}
			Pixmap pixmap = texture.getTextureData().consumePixmap();
			Pixmap partTexture = new Pixmap(texPart.getCutWidth(), texPart.getCutHeight(), Format.RGBA8888);
			partTexture.drawPixmap(pixmap, 0, 0, texPart.getCutSourceX(), texPart.getCutSourceY(), texPart.getCutWidth(), texPart.getCutHeight());
			Texture tx = new Texture(partTexture, Format.RGBA8888, false);
			sprite = new Sprite(tx);

			disposables.add(pixmap);
			disposables.add(partTexture);
			disposables.add(tx);

			sprite.setSize(texPart.getWidth(), texPart.getHeight());
			sprite.setCenter(texPart.getX(), texPart.getY());

			sprite.draw(batch);
		}

		batch.end();

		IHUD hud = getHUD();
		if (hud != null) {
			drawHUD(hud);
		}

		for (Disposable disposable : disposables) {
			disposable.dispose();
		}
		disposables.clear();
	}

	private void drawMap() {
		IMapService map = getMapService();
		if (map == null) {
			return;
		}

		String path = map.getBackgroundTexturePath();
		if (path == null) {
			return;
		}

		if (!assMan.isLoaded(path)) {
			assMan.load(path, Texture.class);
			assMan.finishLoading();
		}

		batch.setProjectionMatrix(cam.combined);
		batch.begin();

		Texture texture = assMan.get(path, Texture.class);
		Sprite sprite = new Sprite(texture);

		sprite.setSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());
		sprite.setCenter(gameData.getDisplayWidth() / 2, gameData.getDisplayHeight() / 2);

		sprite.draw(batch);

		batch.end();
	}

	private void drawHUD(IHUD hud) {
		batch.setProjectionMatrix(cam.combined);
		batch.begin();

		for (TexturePart texPart : hud.getTextures()) {
			String path = texPart.getTexturePath();

			if (!assMan.isLoaded(path)) {
				assMan.load(path, Texture.class);
				assMan.finishLoading();
			}

			Texture texture = assMan.get(path, Texture.class);

			Sprite sprite;

			if (!texture.getTextureData().isPrepared()) {
				texture.getTextureData().prepare();
			}
			Pixmap pixmap = texture.getTextureData().consumePixmap();
			Pixmap partTexture = new Pixmap(texPart.getCutWidth(), texPart.getCutHeight(), Format.RGBA8888);
			partTexture.drawPixmap(pixmap, 0, 0, texPart.getCutSourceX(), texPart.getCutSourceY(), texPart.getCutWidth(), texPart.getCutHeight());
			Texture tx = new Texture(partTexture, Format.RGBA8888, false);
			sprite = new Sprite(tx);

			disposables.add(pixmap);
			disposables.add(partTexture);
			disposables.add(tx);

			sprite.setSize(texPart.getWidth(), texPart.getHeight());
			sprite.setCenter(texPart.getX(), texPart.getY());

			sprite.draw(batch);
		}

		batch.end();

		drawMinimap(hud);
	}

	private void drawMinimap(IHUD hud) {
		ShapeRenderer sr = new ShapeRenderer();
		cam.update();
		sr.setProjectionMatrix(cam.combined);

		sr.begin(ShapeType.Filled);

		Map<String, List<int[]>> minimap = hud.getMinimap();

		sr.setColor(Color.GREEN);
		for (int[] tile : minimap.get("player")) {
			sr.rect(tile[0], tile[1], 8, 8);
		}
		sr.setColor(Color.BLACK);
		for (int[] tile : minimap.get("room")) {
			sr.rect(tile[0], tile[1], 8, 8);
		}
		sr.setColor(Color.GRAY);
		for (int[] tile : minimap.get("door")) {
			sr.rect(tile[0], tile[1], 8, 8);
		}

		sr.end();
		disposables.add(sr);
	}

	private void startBackgroundMusic() {
		IAudioService audio = getAudioService();
		if (audio == null) {
			return;
		}

		String musicPath = audio.getBackgroundMusicPath();
		float volume = audio.getVolume();

		if (!assMan.isLoaded(musicPath)) {
			assMan.load(musicPath, Music.class);
			assMan.finishLoading();
		}

		Music backgroundMusic = assMan.get(musicPath, Music.class);
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(volume);
		backgroundMusic.play();
	}

	private void playSounds() {
		IAudioService audio = getAudioService();
		if (audio == null) {
			return;
		}

		float volume = audio.getVolume();

		for (String path : audio.getNextSoundPaths()) {
			if (!assMan.isLoaded(path)) {
				assMan.load(path, Sound.class);
				assMan.finishLoading();
			}

			Sound sound = assMan.get(path, Sound.class);
			sound.play(volume);
		}
	}

	private List<TexturePart> getOrderedTextureParts() {
		List<TexturePart> orderedTextureParts = new ArrayList<>();
		List<TexturePart> textureParts = getTextureParts();
		for (int i = 1; i < 5; i++) {
			for (TexturePart texturePart : textureParts) {
				if (i == texturePart.getPriority()) {
					orderedTextureParts.add(texturePart);
				}
			}
		}
		return orderedTextureParts;
	}

	private List<TexturePart> getTextureParts() {
		List<TexturePart> textureParts = new ArrayList<>();
		for (Entity entity : world.getEntities()) {
			TexturePart texPart = entity.getPart(TexturePart.class);
			if (texPart != null) {
				textureParts.add(texPart);
			}
		}
		return textureParts;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

	private Collection<? extends IEntityProcessingService> getEntityProcessingServices() {
		return lookup.lookupAll(IEntityProcessingService.class);
	}

	private ICollisionDetectionService getCollisionDetectionService() {
		return lookup.lookup(ICollisionDetectionService.class);
	}

	private Collection<? extends IPostEntityProcessingService> getPostEntityProcessingServices() {
		return lookup.lookupAll(IPostEntityProcessingService.class);
	}

	private IMapService getMapService() {
		return lookup.lookup(IMapService.class);
	}

	private IHUD getHUD() {
		return lookup.lookup(IHUD.class);
	}

	private IAudioService getAudioService() {
		return lookup.lookup(IAudioService.class);
	}

	private final LookupListener lookupListener = new LookupListener() {
		@Override
		public void resultChanged(LookupEvent le) {

			Collection<? extends IGamePluginService> updated = result.allInstances();

			for (IGamePluginService us : updated) {
				// Newly installed modules
				if (!gamePlugins.contains(us)) {
					us.start(gameData, world);
					gamePlugins.add(us);
				}
			}

			// Stop and remove module
			for (IGamePluginService gs : gamePlugins) {
				if (!updated.contains(gs)) {
					gs.stop(gameData, world);
					gamePlugins.remove(gs);
				}
			}
		}

	};
}
