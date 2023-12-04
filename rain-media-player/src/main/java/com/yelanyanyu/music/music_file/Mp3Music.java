package com.yelanyanyu.music.music_file;

import com.yelanyanyu.music.music_file.Music;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public abstract class Mp3Music implements Music{
    private String songName;
    private String artist;
    private long lengthOfSecond;
    private String filePath;
}
