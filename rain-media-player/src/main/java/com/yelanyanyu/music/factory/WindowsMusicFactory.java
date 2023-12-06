package com.yelanyanyu.music.factory;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.yelanyanyu.music.music_file.AbstractMusic;
import com.yelanyanyu.music.music_file.impl.MusicStateContext;
import com.yelanyanyu.music.music_file.impl.OriginMusicState;
import com.yelanyanyu.music.music_file.impl.WindowsMp3AbstractMusic;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class WindowsMusicFactory implements MusicFactory{
    @Override
    public AbstractMusic createMp3Music(String filePath) {
        log.debug("create music: {}", filePath);
        Mp3File mp3File = null;
        try {
            mp3File = new Mp3File(filePath);
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            throw new RuntimeException(e);
        }

        WindowsMp3AbstractMusic music = new WindowsMp3AbstractMusic();
        music.setFilePath(filePath);
        if (mp3File.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3File.getId3v2Tag();
            String artist = id3v2Tag.getArtist();
            String title = id3v2Tag.getTitle();
            if (artist != null) {
                music.setArtist(id3v2Tag.getArtist());
            }
            if (title != null) {
                music.setSongName(id3v2Tag.getTitle());
            }
            music.setLengthOfSecond(mp3File.getLengthInSeconds());
            music.setState(new OriginMusicState(), new MusicStateContext(filePath));
        }

        log.debug("mp3song-{}: {}   {}, path: {}, state: {}", music.getArtist(), music.getSongName(),
                music.getLengthOfSecond(), music.getFilePath(), music.getState());
        return music;
    }

    @Override
    public AbstractMusic createFlacMusic(String filePath) {

        return null;
    }
}
