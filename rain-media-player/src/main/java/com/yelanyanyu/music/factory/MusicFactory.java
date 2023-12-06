package com.yelanyanyu.music.factory;

import com.yelanyanyu.music.music_file.AbstractMusic;

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
    AbstractMusic createMp3Music(String filePath);

    /**
     * Obtain flac Music by given filePath. Not support
     *
     * @param filePath .
     * @return .
     */
    AbstractMusic createFlacMusic(String filePath);
}
