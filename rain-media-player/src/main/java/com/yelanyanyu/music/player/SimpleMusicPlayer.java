package com.yelanyanyu.music.player;

import com.yelanyanyu.gui.MusicPlayerUi;
import com.yelanyanyu.music.factory.MusicFactory;
import com.yelanyanyu.music.factory.WindowsMusicFactory;
import com.yelanyanyu.music.listener.PlaybackCompleteListener;
import com.yelanyanyu.music.music_file.AbstractMusic;
import com.yelanyanyu.music.music_file.impl.MusicStateContext;
import com.yelanyanyu.music.music_file.impl.PausingState;
import com.yelanyanyu.music.music_file.impl.PlayingState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Basic music player class that only contains three base operation. Should users wish to expand upon the functionality, just extend from it.
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public enum SimpleMusicPlayer implements MusicPlayer, PlaybackCompleteListener {
    /**
     * Singleton Obj
     */
    INSTANCE;
    private static final HashSet<String> musicFileFormat = new HashSet<>(Arrays.asList("mp3", "flac", "wav", "aac", "ogg", "wma", "alac", "aiff"));
    private final MusicFactory musicFactory = new WindowsMusicFactory();
    @Getter
    private LinkedList<AbstractMusic> playList;
    private MusicStrategy musicStrategy;
    private MusicStateContext context;
    private AbstractMusic currentMusic;
    /**
     * 快进或者快退的步长
     */
    private int stepLength;

    public void init(final String folderPath, final MusicStrategy musicStrategy, MusicPlayerUi ui) {
        this.playList = new LinkedList<>();
        this.musicStrategy = musicStrategy;
        ((WindowsMusicFactory) musicFactory).setUi(ui);
        try {
            Files.walk(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .filter(this::isMusicFile)
                    .forEach(this::addToPlayList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isMusicFile(Path path) {
        String fileName = path.toAbsolutePath().toString();
        String format = fileName.substring(fileName.lastIndexOf('.') + 1);
        return musicFileFormat.contains(format);
    }

    public void addToPlayList(Path path) {
        String absPath = path.toAbsolutePath().toString();
        String format = absPath.substring(absPath.lastIndexOf('.') + 1);
        AbstractMusic abstractMusic = switch (format) {
            case "mp3" -> musicFactory.createMp3Music(path.toAbsolutePath().toString());
            case "flac" -> musicFactory.createFlacMusic(path.toAbsolutePath().toString());
            default -> null;
        };
        if (abstractMusic != null) {
            log.info("music add to list: {}", abstractMusic);
            playList.addLast(abstractMusic);
        }
    }

    /**
     * 1. 检查当前选中歌曲状态是否为播放中(只有index == 0 的歌曲才有可能播放);
     * 1. 如果在播放中，就先stopAndReset, 然后从队列中删除;
     * 2. 如果不在播放中, 就直接删除;
     * 2. 第一首歌曲有两种可能的状态-> 初始状态，播放状态;
     * 1. 对于初始状态，直接删除；
     * 2. 对于播放状态则stop再删除, 然后播放下一首歌曲;
     * 3. 或者说：
     * 1. 对于状态为播放中或者暂停中的音乐就先stop再删除，再播放下一首；
     * 2. 对于其他状态的音乐就直接删除
     *
     * @param index .
     */
    public void deleteFromPlayList(int index) {
        AbstractMusic cur = getCurrentMusic();
        if (cur == null) {
            return;
        }
        log.debug("cur state: {}", cur.getState());
        if (cur.getState().getClass() == PlayingState.class || cur.getState().getClass() == PausingState.class) {
            if (index == 0) {
                resetAndStop();
            }
            this.playList.remove(index);
            play();
        } else {
            this.playList.remove(index);
        }

    }

    private MusicStateContext currentContext() {
        AbstractMusic cur = getCurrentMusic();
        if (cur == null) {
            return null;
        }
        this.context = cur.state;
        return this.context;
    }

    public AbstractMusic getCurrentMusic() {
        if (this.playList.isEmpty()) {
            return null;
        }
        return playList.getFirst();
    }

    /**
     * play song by MusicStateContext
     */
    @Override
    public void play() {
        if (this.playList.isEmpty()) {
            return;
        }
        musicStrategy.play(currentContext());
        log.info("play {}.{}: {}", getCurrentMusic().songName, getCurrentMusic().format, getCurrentMusic().artist);
    }

    @Override
    public void pause() {
        if (this.playList.isEmpty()) {
            return;
        }
        musicStrategy.pause(currentContext());
        log.info("pause {}.{}: {}", getCurrentMusic().songName, getCurrentMusic().format, getCurrentMusic().artist);
    }

    @Override
    public void resetAndStop() {
        if (this.playList.isEmpty()) {
            return;
        }
        musicStrategy.stop(currentContext());
        log.info("stop {}.{}: {}", getCurrentMusic().songName, getCurrentMusic().format, getCurrentMusic().artist);
    }

    @Override
    public void play(int index) {
        resetAndStop();
        for (int i = 0; i < index; i++) {
            next();
        }
        // 如果当前有正在播放的音乐，就将其停止
        play();
    }

    /**
     * 将下一首歌曲放置到队首，当前歌曲移动到队尾，为播放下一首歌曲做准备
     */
    private void next() {
        AbstractMusic first = playList.pollFirst();
        playList.addLast(first);
        this.context = null;
    }

    /**
     * 当音乐播放完毕的时候, 将当前歌曲放在队尾，然后指针下移, 准备播放下一首歌曲
     */
    @Override
    public void playbackCompleted() {
        next();
        play();
    }
}
