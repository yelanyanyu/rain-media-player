package com.yelanyanyu.music;

import com.yelanyanyu.music.listener.PlaybackCompleteListener;
import com.yelanyanyu.music.player.SimpleMusicPlayer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.Player;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author yelanyanyu
 */
@Slf4j
public class Mp3PlayerDecorator {
    private final static int ORIGIN = 0;
    private final static int PLAYING = 1;
    private final static int PAUSED = 2;
    private final static int FINISHED = 3;
    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(2, 5,
            1L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(3),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());
    /**
     * locking object used to communicate with player thread
     */
    private final Object playerLock = new Object();
    private final List<PlaybackCompleteListener> listeners = new ArrayList<>(List.of(SimpleMusicPlayer.INSTANCE));
    /**
     * the player actually doing all the work
     */
    private Player player;
    private String filePath;
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
            this.filePath = filePath;
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
        synchronized (playerLock) {
            for (PlaybackCompleteListener listener : listeners) {
                listener.playbackCompleted();
            }
        }
    }

    private void notifyUi() {
        listeners.get(1).playbackCompleted();
    }

    public boolean isCompleted() {
        return player.isComplete();
    }

    /**
     * Starts playback (resumes if paused)
     */
//    public void play() {
//        synchronized (playerLock) {
//            switch (playerStatus) {
//                case ORIGIN -> {
//                    final Runnable r = () -> {
//                        log.debug("thread run...");
//                        playInternal();
//                    };
//                    playerStatus = PLAYING;
//                    THREAD_POOL.execute(r);
//                }
//                case PAUSED -> resume();
//                case FINISHED -> {
//                    resetPlayer();
//                    this.playerStatus = ORIGIN;
//                    notifyUi();
//                    play();
//                }
//                // 默认情况可以省略，因为没有执行任何操作
//                default -> {
//                    notifyUi();
//                }
//            }
//
//        }
//    }
    public void play() {
        synchronized (playerLock) {
            if (playerStatus == PLAYING) {
                // 如果已经在播放，则不做任何操作
                notifyUi();
                return;
            }
            if (playerStatus == FINISHED) {
                resetPlayer();
                this.playerStatus = ORIGIN;
                notifyUi();
            }
            if (playerStatus == ORIGIN || playerStatus == PAUSED) {
                startPlaybackThread();
            }
            // 其他状态不启动新的播放线程
        }
    }

    private void startPlaybackThread() {
        if (playerStatus != PLAYING) {
            playerStatus = PLAYING;
            THREAD_POOL.execute(this::playInternal);
        }
    }

    private void resetPlayer() {
        try {
            close();
            this.fis = new FileInputStream(this.filePath);
            this.player = new Player(this.fis);
        } catch (FileNotFoundException | JavaLayerException e) {
            throw new RuntimeException(e);
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
    public void resetAndStop() {
        synchronized (playerLock) {
            playerStatus = FINISHED;
            resetPlayer();
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
                e.printStackTrace();
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