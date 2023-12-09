package com.yelanyanyu.music.music_file.impl;

import com.yelanyanyu.music.music_file.AbstractMusicState;

/**
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class OriginMusicState extends AbstractMusicState {
    @Override
    public void play(MusicStateContext context) {
        context.setState(new PlayingState());
        context.getPlayer().play();
    }

    @Override
    public void pause(MusicStateContext context) {
        return;
    }

    @Override
    public void stop(MusicStateContext context) {
        return;
    }

    @Override
    public void nextSong(MusicStateContext context) {
        return;
    }
}
