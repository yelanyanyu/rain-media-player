package com.yelanyanyu.music.player;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public interface MusicPlayer {
    void play();

    void pause();

    void stop();

    /**
     * 从低index首歌曲开始播放
     * @param index .
     */
    void play(int index);
}
