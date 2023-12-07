package com.yelanyanyu.music.music_file;

import com.yelanyanyu.music.music_file.impl.MusicStateContext;
import lombok.*;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class AbstractMusic {
    public String songName = "Unknown";
    public String artist = "Unknown";
    public long lengthOfSecond;
    public String filePath;
    public MusicStateContext state;
    public String format;

    public void setState(AbstractMusicState state, MusicStateContext context) {
        this.state = context;
        this.state.setState(state);
    }

    public AbstractMusicState getState() {
        return this.state.getState();
    }

    @Override
    public String toString() {
        return "AbstractMusic{" +
                "songName='" + songName + '\'' +
                ", artist='" + artist + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}
