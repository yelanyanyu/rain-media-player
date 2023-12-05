package com.yelanyanyu.music.factory;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public interface MusicFactory {
    /**
     * Obtain mp3 Music by given filePath
     *
     * @param filePath .
     */
    void createMp3Music(String filePath);

    /**
     * Obtain flac Music by given filePath
     *
     * @param filePath .
     */
    void createFlacMusic(String filePath);
}
