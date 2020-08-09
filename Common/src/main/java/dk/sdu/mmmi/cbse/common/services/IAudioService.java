package dk.sdu.mmmi.cbse.common.services;

import java.util.List;

/**
 *
 * @author Group 7
 */
public interface IAudioService {

	public float getVolume();

	public String getBackgroundMusicPath();

	public List<String> getNextSoundPaths();

	public void playSound(String path);

}
