package com.yelanyanyu.music.player;

import com.yelanyanyu.music.factory.MusicFactory;
import com.yelanyanyu.music.factory.WindowsMusicFactory;
import com.yelanyanyu.music.music_file.Music;
import com.yelanyanyu.music.player.MusicPlayer;

import java.nio.file.Files;
import java.util.LinkedList;

/**
 * Basic music player class that only contains three base operation. Should users wish to expand upon the functionality, just extend from it.
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public enum SimpleMusicPlayer implements MusicPlayer {
    /**
     * Singleton Obj
     */
    INSTANCE;
    private LinkedList<Music> playList;

    private MusicFactory musicFactory = new WindowsMusicFactory();

    public void setPlayList(String folderPath) {

    }

    /**
     * 快进或者快退的步长
     */
    private int stepLength;


    @Override
    public void play() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }
}
