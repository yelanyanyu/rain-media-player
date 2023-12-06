package com.yelanyanyu.music;

import com.yelanyanyu.music.listener.PlaybackCompleteListener;
import com.yelanyanyu.music.player.SimpleMusicPlayer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.Player;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yelanyanyu
 */
@Slf4j
public class Mp3PlayerDecorator {
    private final static int ORIGIN = 0;
    private final static int PLAYING = 1;
    private final static int PAUSED = 2;
    private final static int FINISHED = 3;
    /**
     * the player actually doing all the work
     */
    private final Player player;
    /**
     * locking object used to communicate with player thread
     */
    private final Object playerLock = new Object();
    private final List<PlaybackCompleteListener> listeners = new ArrayList<>(List.of(SimpleMusicPlayer.INSTANCE));
    private FileInputStream fis;
    /**
     * status variable what player thread is doing/supposed to do
     */
    private int playerStatus = ORIGIN;

    public Mp3PlayerDecorator(final InputStream inputStream) {
        try {
            this.player = new Player(inputStream);
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    public Mp3PlayerDecorator(final String filePath) {
        try {
            this.fis = new FileInputStream(filePath);
            this.player = new Player(this.fis);
        } catch (FileNotFoundException | JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    public Mp3PlayerDecorator(final InputStream inputStream, final AudioDevice audioDevice) {
        try {
            this.player = new Player(inputStream, audioDevice);
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 注册观察者
     */
    public void addPlaybackCompleteListener(PlaybackCompleteListener listener) {
        listeners.add(listener);
    }

    /**
     * 移除观察者
     */
    public void removePlaybackCompleteListener(PlaybackCompleteListener listener) {
        listeners.remove(listener);
    }

    /**
     * 通知观察者播放完成
     */
    private void notifyPlaybackComplete() {
        log.debug("listeners size: {}", listeners.size());
        for (PlaybackCompleteListener listener : listeners) {
            listener.playbackCompleted();
        }
    }

    public boolean isCompleted() {
        return player.isComplete();
    }

    /**
     * Starts playback (resumes if paused)
     */
    public void play() {
        synchronized (playerLock) {

            switch (playerStatus) {
                case ORIGIN -> {
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
                default -> throw new IllegalStateException("Unexpected value: " + playerStatus);
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
     *
     * @return .
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
                    notifyPlaybackComplete();
                    break;
                }
            } catch (final JavaLayerException e) {
                notifyPlaybackComplete();
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