import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class Sounds {

    private String FILEPATH;
    private boolean isloop;
    private Clip clip;

    // saves the path to a file
    Sounds(String file, boolean loop)
    {
        FILEPATH = file;
        isloop = loop;
    }

    // mute
    public void stop()
    {
        clip.stop();
    }

    public void play()
    {
        File musicPath = new File(FILEPATH);

        try
        {
            if(musicPath.exists())
            {
                // opening a file with sounds
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);

                // sound volume setting
                FloatControl gainControl =
                        (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f);

                // if it has to play continuously
                if(isloop)
                    clip.loop(Clip.LOOP_CONTINUOUSLY);

                // sound start
                clip.start();
            }
            else
            {
                System.out.println("Can't find file");
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
