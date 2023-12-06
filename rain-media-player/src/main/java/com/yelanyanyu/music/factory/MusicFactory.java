package com.yelanyanyu.music.factory;

import com.yelanyanyu.music.music_file.Music;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public interface MusicFactory {
    /**
     * Obtain mp3 Music by given filePath
     *
     * @param filePath .
     * @return .
     */
    Music createMp3Music(String filePath);

    /**
     * Obtain flac Music by given filePath. Not support.
     *
     * @param filePath .
     * @return .
     */
    Music createFlacMusic(String filePath);
}
