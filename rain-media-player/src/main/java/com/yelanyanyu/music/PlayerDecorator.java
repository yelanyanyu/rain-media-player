package com.yelanyanyu.music;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.Player;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class PlayerDecorator {
    private final static int NOTSTARTED = 0;
    private final static int PLAYING = 1;
    private final static int PAUSED = 2;
    private final static int FINISHED = 3;
    // the player actually doing all the work
    private final Player player;
    // locking object used to communicate with player thread
    private final Object playerLock = new Object();
    private FileInputStream fis;
    // status variable what player thread is doing/supposed to do
    private int playerStatus = NOTSTARTED;

    public PlayerDecorator(final InputStream inputStream) {
        try {
            this.player = new Player(inputStream);
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    public PlayerDecorator(final String filePath) {
        try {
            this.fis = new FileInputStream(filePath);
            this.player = new Player(this.fis);
        } catch (FileNotFoundException | JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    public PlayerDecorator(final InputStream inputStream, final AudioDevice audioDevice) {
        try {
            this.player = new Player(inputStream, audioDevice);
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] argv) {
        try {
            FileInputStream input = new FileInputStream("D:\\myCode\\formal-projects\\simple-media-player\\rain-media-player\\src\\test\\resources\\1.mp3");
            PlayerDecorator player = new PlayerDecorator(input);
// start playing
            player.play();
// after 5 secs, pause
            Thread.sleep(5000);
            player.pause();
// after 5 secs, resume
            Thread.sleep(5000);
            player.resume();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts playback (resumes if paused)
     */
    public void play() throws JavaLayerException {
        synchronized (playerLock) {

            switch (playerStatus) {
                case NOTSTARTED -> {
                    final Runnable r = () -> {
                        log.debug("thread run...");
                        playInternal();
                    };
                    final Thread t = new Thread(r);
                    // t.setDaemon(true);
                    t.setPriority(Thread.MAX_PRIORITY);
                    playerStatus = PLAYING;
                    t.start();
                }
                case PAUSED -> resume();
                // 默认情况可以省略，因为没有执行任何操作
            }

        }
    }

    /**
     * Pauses playback. Returns true if new state is PAUSED.
     */
    public boolean pause() {
        synchronized (playerLock) {
            if (playerStatus == PLAYING) {
                playerStatus = PAUSED;
            }
            return playerStatus == PAUSED;
        }
    }

    /**
     * Resumes playback. Returns true if the new state is PLAYING.
     */
    public boolean resume() {
        synchronized (playerLock) {
            if (playerStatus == PAUSED) {
                playerStatus = PLAYING;
                playerLock.notifyAll();
            }
            return playerStatus == PLAYING;
        }
    }

    /**
     * Stops playback. If not playing, does nothing
     */
    public void stop() {
        synchronized (playerLock) {
            playerStatus = FINISHED;
            playerLock.notifyAll();
        }
    }

    private void playInternal() {
        while (playerStatus != FINISHED) {
            try {
                if (!player.play(1)) {
                    break;
                }
            } catch (final JavaLayerException e) {
                break;
            }
            // check if paused or terminated
            synchronized (playerLock) {
                while (playerStatus == PAUSED) {
                    try {
                        playerLock.wait();
                    } catch (final InterruptedException e) {
                        // terminate player
                        break;
                    }
                }
            }
        }
        close();
    }

    /**
     * Closes the player, regardless of current state.
     */
    public void close() {
        synchronized (playerLock) {
            playerStatus = FINISHED;
        }
        try {
            player.close();
        } catch (final Exception e) {
// ignore, we are terminating anyway
        }
    }
}