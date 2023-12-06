package com.yelanyanyu.music.music_file.impl;

import com.yelanyanyu.music.Mp3PlayerDecorator;
import com.yelanyanyu.music.music_file.AbstractMusicState;
import lombok.Data;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Data
public class MusicStateContext {
    private AbstractMusicState state;
    private Mp3PlayerDecorator player;

    public MusicStateContext() {
        this.state = new OriginMusicState();
    }

    public MusicStateContext(String filePath) {
        this();
        this.player = new Mp3PlayerDecorator(filePath);
    }

    public void play() {
        state.play(this);
    }

    public void pause() {
        state.pause(this);
    }

    public void stop() {
        state.stop(this);
    }
}
