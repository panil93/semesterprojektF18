package dk.sdu.mmmi.cbse.audio;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import dk.sdu.mmmi.cbse.common.services.IAudioService;

@ServiceProviders(value = {
	@ServiceProvider(service = IAudioService.class)
	,
	@ServiceProvider(service = IGamePluginService.class)
})
public class AudioService implements IAudioService, IGamePluginService {

	private List<String> soundPaths = new ArrayList<>();

	@Override
	public float getVolume() {
		return 0.2f;
	}

	@Override
	public String getBackgroundMusicPath() {
		String path = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/dk-sdu-mmmi-cbse-Audio.jar!/assets/background.mp3");
		return fixPath(path);
	}

	@Override
	public List<String> getNextSoundPaths() {
		List<String> returnSoundPaths = new ArrayList<>();
		returnSoundPaths.addAll(soundPaths);
		soundPaths.clear();
		return returnSoundPaths;
	}

	@Override
	public void playSound(String relativePath) {
		String absolutePath = (new File("").getAbsolutePath() + "/straightupwizardbeansmodules/modules/" + relativePath);
		soundPaths.add(fixPath(absolutePath));
	}

	private String fixPath(String absolutePath) {
		if (System.getProperty("os.name").startsWith("Windows")) {
			absolutePath = absolutePath.substring(2);
			absolutePath = absolutePath.replaceAll("\\\\", "/");
		} else {
			String[] temp = absolutePath.split("application");
			absolutePath = temp[0] + "application/target/asteroidsnetbeansmodules" + temp[1];
		}
		return absolutePath;
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
