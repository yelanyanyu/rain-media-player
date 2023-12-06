package com.yelanyanyu.music.player;

import com.yelanyanyu.music.music_file.impl.MusicStateContext;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public abstract class AbstractMp3MusicStrategy implements MusicStrategy {
    @Override
    public void play(MusicStateContext context) {
        context.play();
    }

    @Override
    public void pause(MusicStateContext context) {
        context.pause();
    }

    @Override
    public void stop(MusicStateContext context) {
        context.stop();
    }
}
