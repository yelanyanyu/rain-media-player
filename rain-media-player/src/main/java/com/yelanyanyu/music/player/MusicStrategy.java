package com.yelanyanyu.music.player;

import com.yelanyanyu.music.music_file.impl.MusicStateContext;

/**
 * Devise strategies for the encapsulation of various music file processing techniques.
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public interface MusicStrategy {
    void play(MusicStateContext context);

    void pause(MusicStateContext context);

    void stop(MusicStateContext context);
}
