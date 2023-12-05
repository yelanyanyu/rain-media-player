package com.yelanyanyu.music.music_file;

import lombok.Data;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Data
public abstract class AbstractMp3Music implements Music{
    private String songName;
    private String artist;
    private long lengthOfSecond;
    private String filePath;
    private AbstractMusicState state;
}
