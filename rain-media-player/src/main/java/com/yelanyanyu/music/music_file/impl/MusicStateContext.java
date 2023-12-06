package com.yelanyanyu.music.music_file.impl;

import com.yelanyanyu.music.music_file.AbstractMusicState;
import javazoom.jl.player.Player;
import lombok.Data;
import lombok.Setter;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Data
public class MusicStateContext {
    private AbstractMusicState state;
    private Player player;

    public MusicStateContext() {
        this.state = new OriginMusicState();
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
