package com.yelanyanyu.music.factory;

/**
 * For now, Linux support are not feasible.
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class LinuxMusicFactory implements MusicFactory {
    @Override
    public void createMp3Music(String filePath) {
        // wait
    }

    @Override
    public void createFlacMusic(String filePath) {
        // wait
    }
}
