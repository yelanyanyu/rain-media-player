package com.yelanyanyu.gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author yelanyanyu
 */
public class MusicPlayerUI {

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

    public MusicPlayerUI() {
        initUi();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MusicPlayerUI();
            }
        });
    }

    private void initUi() {
        // 主界面 JFrame
        mainFrame = new JFrame("音乐选择界面");
        JButton selectMusicButton = new JButton("选择音乐文件");
        selectMusicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 实现文件选择逻辑
                showPlayFrame(); // 显示播放界面
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
        /**
         * 音乐播放的进度条
         */
        JProgressBar progressBar = new JProgressBar();
        JButton playButton = new JButton("播放/暂停");
        JButton forwardButton = new JButton("前进");
        JButton backButton = new JButton("后退");
        JButton returnButton = new JButton("返回");
        JButton playlistButton = new JButton("播放列表");

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 实现播放/暂停逻辑
            }
        });

        forwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 实现向前跳 5 秒的逻辑
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 实现向后跳 5 秒的逻辑
            }
        });

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playFrame.setVisible(false);
                mainFrame.setVisible(true);
            }
        });

        playlistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPlaylistFrame();
            }
        });

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

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 实现添加歌曲到播放列表的逻辑
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 实现从播放列表删除歌曲的逻辑
            }
        });

        returnToPlayFrameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playlistFrame.setVisible(false);
                playFrame.setVisible(true);
            }
        });

        playlistFrame.setLayout(new FlowLayout());
        playlistFrame.add(addButton);
        playlistFrame.add(deleteButton);
        playlistFrame.add(returnToPlayFrameButton);
        playlistFrame.setSize(400, 200);
        playlistFrame.setVisible(true);
    }
}
