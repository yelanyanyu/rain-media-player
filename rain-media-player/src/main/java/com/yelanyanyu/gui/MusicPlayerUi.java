package com.yelanyanyu.gui;

import com.yelanyanyu.music.music_file.Music;
import com.yelanyanyu.music.player.MusicPlayer;
import com.yelanyanyu.music.player.SimpleMusicPlayer;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;

/**
 * @author yelanyanyu
 */
@Slf4j
public class MusicPlayerUi {
    /**
     * 主界面
     */
    private JFrame mainFrame;
    /**
     * 播放界面
     */
    private JFrame playFrame;
    /**
     * 播放列表界面
     */
    private JFrame playlistFrame;
    MusicPlayer musicPlayer = SimpleMusicPlayer.INSTANCE;

    public MusicPlayerUi() {
        initUi();
    }

    public MusicPlayerUi(String classPath) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(classPath);
            this.musicPlayer = (MusicPlayer) clazz.getDeclaredConstructor().newInstance();
            initUi();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static MusicPlayerUi getInstance(String classPath) {
        MusicPlayerUi ui = new MusicPlayerUi();
        try {
            Class<?> clazz = Class.forName(classPath);
            ui.musicPlayer = (MusicPlayer) clazz.getDeclaredConstructor().newInstance();
            return ui;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    private void initUi() {
        // Main interface JFrame
        mainFrame = new JFrame("Music Selection Interface");
        JButton selectMusicButton = new JButton("Select Music File");
        selectMusicButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Set to directories only

            int option = fileChooser.showOpenDialog(mainFrame);
            if (option == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = fileChooser.getSelectedFile();
                String folderPath = selectedFolder.getAbsolutePath();
                // Use the folderPath as needed
                log.info("Selected folder: {}", folderPath);
                // TODO: 初始化MusicPlayer, include play list

            }
        });

        mainFrame.setLayout(new FlowLayout());
        mainFrame.add(selectMusicButton);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 200);
        mainFrame.setVisible(true);
    }


    private void showPlayFrame() {
        // 播放界面 JFrameB
        playFrame = new JFrame("音乐播放界面");
        /*
         * 音乐播放的进度条
         */
        JProgressBar progressBar = new JProgressBar();
        JButton playButton = new JButton("播放/暂停");
        JButton forwardButton = new JButton("前进");
        JButton backButton = new JButton("后退");
        JButton returnButton = new JButton("返回");
        JButton playlistButton = new JButton("播放列表");

        playButton.addActionListener(e -> {
            // TODO: 实现播放/暂停逻辑
        });

        forwardButton.addActionListener(e -> {
            // TODO: 实现向前跳 5 秒的逻辑
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 实现向后跳 5 秒的逻辑
            }
        });

        returnButton.addActionListener(e -> {
            playFrame.setVisible(false);
            mainFrame.setVisible(true);
        });

        playlistButton.addActionListener(e -> showPlaylistFrame());

        playFrame.setLayout(new FlowLayout());
        playFrame.add(progressBar);
        playFrame.add(playButton);
        playFrame.add(forwardButton);
        playFrame.add(backButton);
        playFrame.add(returnButton);
        playFrame.add(playlistButton);
        playFrame.setSize(500, 300);
        playFrame.setVisible(true);
    }

    private void showPlaylistFrame() {
        // 播放列表界面 JFrameC
        playlistFrame = new JFrame("播放列表界面");
        JButton addButton = new JButton("添加");
        JButton deleteButton = new JButton("删除");
        JButton returnToPlayFrameButton = new JButton("返回");

        addButton.addActionListener(e -> {
            // TODO: 实现添加歌曲到播放列表的逻辑
        });

        deleteButton.addActionListener(e -> {
            // TODO: 实现从播放列表删除歌曲的逻辑
        });

        returnToPlayFrameButton.addActionListener(e -> {
            playlistFrame.setVisible(false);
            playFrame.setVisible(true);
        });

        playlistFrame.setLayout(new FlowLayout());
        playlistFrame.add(addButton);
        playlistFrame.add(deleteButton);
        playlistFrame.add(returnToPlayFrameButton);
        playlistFrame.setSize(400, 200);
        playlistFrame.setVisible(true);
    }
}
