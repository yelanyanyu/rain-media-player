package com.yelanyanyu;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */

@Slf4j
public class mp3Tests {
    public static ClassLoader classLoader;
    Player player;

    @Before
    public void before() {
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    @Test
    public void t1() throws InterruptedException {
        Thread playerThread = new Thread(() -> {
            try {
                System.out.println("run...");
                String path = Objects.requireNonNull(classLoader.getResource("1.mp3")).getFile().substring(1);
                player = new Player(Objects.requireNonNull(classLoader.getResourceAsStream("1.mp3")));
                Mp3File mp3File = new Mp3File(path);
                if (mp3File.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                    log.info("艺术家: {}", id3v2Tag.getArtist());
                    log.info("歌曲长度: {} s", mp3File.getLengthInSeconds());
                    log.info("name: {}", id3v2Tag.getTitle());
                    log.info("作曲: {}", id3v2Tag.getComposer());
                    log.info("作词: {}", id3v2Tag.getLyrics());
                }
                player.play(3800);
            } catch (JavaLayerException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        playerThread.start();
        playerThread.join(); // 等待播放线程结束
    }
}
