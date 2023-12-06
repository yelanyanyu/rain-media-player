package com.yelanyanyu.music.factory;

import com.yelanyanyu.music.music_file.AbstractMusic;

/**
 * For now, Linux support are not feasible.
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class LinuxMusicFactory implements MusicFactory {
    @Override
    public AbstractMusic createMp3Music(String filePath) {
        // wait

        return null;
    }

    @Override
    public AbstractMusic createFlacMusic(String filePath) {
        // wait


        return null;
    }
}
