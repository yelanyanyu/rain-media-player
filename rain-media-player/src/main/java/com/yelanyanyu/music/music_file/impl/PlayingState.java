package com.yelanyanyu.music.music_file.impl;

import com.yelanyanyu.music.music_file.AbstractMusicState;
import javazoom.jl.decoder.JavaLayerException;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class PlayingState extends AbstractMusicState {

    @Override
    public void play(MusicStateContext context) {
        context.setState(new PlayingState());
        try {
            context.getPlayer().play();
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pause(MusicStateContext context) {
        context.setState(new PausingState());

    }

    @Override
    public void stop(MusicStateContext context) {

    }

    @Override
    public void nextSong(MusicStateContext context) {

    }
}
