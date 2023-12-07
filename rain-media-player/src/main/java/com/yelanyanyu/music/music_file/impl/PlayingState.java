package com.yelanyanyu.music.music_file.impl;

import com.yelanyanyu.music.music_file.AbstractMusicState;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class PlayingState extends AbstractMusicState {

    @Override
    public void play(MusicStateContext context) {
        context.setState(new PlayingState());
        context.getPlayer().play();
    }

    @Override
    public void pause(MusicStateContext context) {
        context.setState(new PausingState());
        context.getPlayer().pause();
    }

    @Override
    public void stop(MusicStateContext context) {
        context.setState(new OriginMusicState());
        context.getPlayer().resetAndStop();
    }

    @Override
    public void nextSong(MusicStateContext context) {
        stop(context);
    }
}
