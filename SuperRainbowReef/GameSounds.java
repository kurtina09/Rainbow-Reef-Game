/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperRainbowReef;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author monalimirel, motiveg
 */
public class GameSounds {

    private AudioInputStream soundStream;
    private Clip clip;
    private int type;//1 for sounds that needs to be played all the time
    // 2 for sounds that only need to be played once

    public GameSounds(int type, String soundFile) {
        this.type = type;
        try {
            //soundStream = AudioSystem.getAudioInputStream(GameSounds.class.getResource(this.soundFile));
            soundStream = AudioSystem.getAudioInputStream(this.getClass().getClassLoader().getResource(soundFile));
            clip = AudioSystem.getClip();
            clip.open(soundStream);
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println(e.getMessage() + "No sound documents are found");
        }
        if (this.type == 1) {
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        clip.start();
                        clip.loop(clip.LOOP_CONTINUOUSLY);
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(GameSounds.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            };
            Thread thread = new Thread(myRunnable);
            thread.start();
        }
    }

    public void play() {
        clip.start();
    }

    public void stop() {
        clip.stop();
    }

    public Clip getClip() {
        return clip;
    }

    public void resetClip() {
        clip.setFramePosition(0);
    }
}
