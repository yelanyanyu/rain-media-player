package com.yelanyanyu.music.player;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public interface SimpleMusicPlayerDecorator extends MusicPlayer{
    SimpleMusicPlayer INSTANCE = SimpleMusicPlayer.INSTANCE;

    @Override
    default void play() {
        // do something
    }

    @Override
    default void pause() {
        // do something
    }

    @Override
    default void stop() {
        // do something
    }
}
