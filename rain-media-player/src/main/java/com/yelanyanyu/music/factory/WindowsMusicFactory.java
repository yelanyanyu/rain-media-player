package com.yelanyanyu.music.factory;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.yelanyanyu.music.music_file.Music;
import com.yelanyanyu.music.music_file.impl.OriginMusicState;
import com.yelanyanyu.music.music_file.impl.WindowsMp3Music;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class WindowsMusicFactory implements MusicFactory{
    @Override
    public Music createMp3Music(String filePath) {
        Mp3File mp3File = null;
        try {
            mp3File = new Mp3File(filePath);
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            throw new RuntimeException(e);
        }

        WindowsMp3Music music = new WindowsMp3Music();
        music.setFilePath(filePath);
        if (mp3File.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3File.getId3v2Tag();
            music.setArtist(id3v2Tag.getArtist());
            music.setSongName(id3v2Tag.getTitle());
            music.setLengthOfSecond(mp3File.getLengthInSeconds());
            music.setState(new OriginMusicState());
        }

        log.debug("mp3song-{}: {}   {}, path: {}, state: {}", music.getArtist(), music.getSongName(),
                music.getLengthOfSecond(), music.getFilePath(), music.getState());
        return music;
    }

    @Override
    public Music createFlacMusic(String filePath) {

        return null;
    }
}
