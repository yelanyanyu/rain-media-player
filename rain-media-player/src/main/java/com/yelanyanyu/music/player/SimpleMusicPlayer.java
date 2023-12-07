package com.yelanyanyu.music.player;

import com.yelanyanyu.music.factory.MusicFactory;
import com.yelanyanyu.music.factory.WindowsMusicFactory;
import com.yelanyanyu.music.listener.PlaybackCompleteListener;
import com.yelanyanyu.music.music_file.AbstractMusic;
import com.yelanyanyu.music.music_file.impl.MusicStateContext;
import com.yelanyanyu.music.music_file.impl.OriginMusicState;
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

    public static void main(String[] args) throws InterruptedException {
        SimpleMusicPlayer instance = SimpleMusicPlayer.INSTANCE;
        instance.init("D:\\myCode\\formal-projects\\simple-media-player\\rain-media-player\\src\\test\\resources", new WindowsMp3MusicStrategy());
        instance.play();
    }

    public void init(final String folderPath, final MusicStrategy musicStrategy) {
        this.playList = new LinkedList<>();
        this.musicStrategy = musicStrategy;
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

    public void deleteFromPlayList(Path path) {
        // TODO: 从队列中指定删除歌曲
    }

    private MusicStateContext currentContext() {
        AbstractMusic first = playList.getFirst();
        if (this.context == null) {
//            this.context = new MusicStateContext(first.filePath);
            this.context = first.state;
        }
        return this.context;
    }

    public AbstractMusic getCurrentMusic() {
        return playList.getFirst();
    }

    /**
     * play song by MusicStateContext
     */
    @Override
    public void play() {
        musicStrategy.play(currentContext());
        log.info("play {}.{}: {}", getCurrentMusic().songName, getCurrentMusic().format, getCurrentMusic().artist);
    }

    @Override
    public void pause() {
        musicStrategy.pause(currentContext());
        log.info("pause {}.{}: {}", getCurrentMusic().songName, getCurrentMusic().format, getCurrentMusic().artist);
    }

    @Override
    public void stop() {
        musicStrategy.stop(currentContext());
        log.info("stop {}.{}: {}", getCurrentMusic().songName, getCurrentMusic().format, getCurrentMusic().artist);
    }

    @Override
    public void play(int index) {

    }

    /**
     * 当音乐播放完毕的时候, 将当前歌曲放在队尾，然后指针下移, 准备播放下一首歌曲
     */
    @Override
    public void playbackCompleted() {
        AbstractMusic first = playList.pollFirst();
        playList.addLast(first);
        this.context = null;
        play();
    }
}
