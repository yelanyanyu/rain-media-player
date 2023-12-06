package com.yelanyanyu.music.music_file;

import com.yelanyanyu.music.music_file.impl.MusicStateContext;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public abstract class AbstractMusicState {
    /**
     * play
     */
    public abstract void play(MusicStateContext context);

    /**
     * pause
     */
    public abstract void pause(MusicStateContext context);

    /**
     * stop
     */
    public abstract void stop(MusicStateContext context);

    /**
     * next song
     * @param context .
     */
    public abstract void nextSong(MusicStateContext context);
}
