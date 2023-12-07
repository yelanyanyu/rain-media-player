package com.yelanyanyu.gui;

import com.yelanyanyu.music.listener.PlaybackCompleteListener;
import com.yelanyanyu.music.music_file.AbstractMusic;
import com.yelanyanyu.music.music_file.impl.PlayingState;
import com.yelanyanyu.music.player.MusicPlayer;
import com.yelanyanyu.music.player.SimpleMusicPlayer;
import com.yelanyanyu.music.player.WindowsMp3MusicStrategy;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yelanyanyu
 */
@Slf4j
public class MusicPlayerUi implements PlaybackCompleteListener {
    MusicPlayer musicPlayer = SimpleMusicPlayer.INSTANCE;
    private AbstractMusic currentMusic;
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

    public MusicPlayerUi() {
        initUi();
    }

    public MusicPlayerUi(String classPath) {
        Class<?> clazz;
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

    private AbstractMusic getCurrentMusic() {
        return ((SimpleMusicPlayer) this.musicPlayer).getCurrentMusic();
    }

    private void initUi() {
        log.info("MusicPlayerUi init...");
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
                SimpleMusicPlayer simpleMusicPlayer = (SimpleMusicPlayer) musicPlayer;
                // TODO: 这里将new WindowsMp3MusicStrategy()写死. 以后再改
                simpleMusicPlayer.init(folderPath, new WindowsMp3MusicStrategy(), this);
                this.currentMusic = getCurrentMusic();
                showPlayFrame();
            }
        });


        JButton goToPlayButton = new JButton("进入播放界面");
        goToPlayButton.addActionListener(e -> {
            if (this.playFrame != null) {
                this.playFrame.setVisible(true);
                return;
            }
            JOptionPane.showMessageDialog(mainFrame, "当前没有正在播放的列表, 请选择");
        });

        mainFrame.setLayout(new FlowLayout());
        mainFrame.add(selectMusicButton);
        mainFrame.add(goToPlayButton);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 200);
        mainFrame.setVisible(true);
    }


    private void showPlayFrame() {
        // 播放界面 JFrame
        playFrame = new JFrame("音乐播放界面");


        // 音乐播放的进度条
        JProgressBar progressBar = new JProgressBar();

        // 创建按钮
        JButton playButton = new JButton("播放/暂停");
        JButton forwardButton = new JButton("前进");
        JButton backButton = new JButton("后退");
        JButton returnButton = new JButton("返回");
        JButton playlistButton = new JButton("播放列表");

        // 为按钮添加事件处理逻辑
        playButton.addActionListener(e -> {
            AbstractMusic currentMusic = getCurrentMusic();
            if (currentMusic.getState().getClass() == PlayingState.class) {
                musicPlayer.pause();
            } else {
                log.debug("play.{}:{}.with state: {}", currentMusic.songName, currentMusic.artist, currentMusic.getState());
                musicPlayer.play();
            }
        });

        forwardButton.addActionListener(e -> {
            // TODO: 实现向前跳 5 秒的逻辑
        });

        backButton.addActionListener(e -> {
            // TODO: 实现向后跳 5 秒的逻辑
        });

        returnButton.addActionListener(e -> {
            playFrame.setVisible(false);
            // mainFrame.setVisible(true); // 假设 mainFrame 是已存在的其他 JFrame 的引用
        });

        playlistButton.addActionListener(e -> {
            showPlaylistFrame(); // 显示播放列表的方法
        });

        AbstractMusic currentMusic = getCurrentMusic();
        if (currentMusic == null) {
            return;
        }
        // 设置布局
        playFrame.setLayout(new BorderLayout());

        // 创建中间信息面板
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 1)); // 使用 GridLayout 来放置两个信息标签

        String artist = null, songName = null;
        artist = currentMusic.artist;
        songName = currentMusic.songName;

        infoPanel.add(new JLabel("歌曲名: " + artist));
        infoPanel.add(new JLabel("演唱者: " + songName));

        // 创建底部按钮面板
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        bottomPanel.add(forwardButton);
        bottomPanel.add(playButton);
        bottomPanel.add(backButton);

        // 创建顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(returnButton, BorderLayout.WEST);

        // 创建右侧播放列表按钮
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(playlistButton, BorderLayout.SOUTH);
        Border lineBorder = BorderFactory.createLineBorder(Color.GRAY);

        // 设置边框到面板
        infoPanel.setBorder(lineBorder);
        bottomPanel.setBorder(lineBorder);
        topPanel.setBorder(lineBorder);
        rightPanel.setBorder(lineBorder);

        // 将面板和组件添加到 JFrame
        playFrame.add(topPanel, BorderLayout.NORTH);
        playFrame.add(infoPanel, BorderLayout.CENTER);
        playFrame.add(bottomPanel, BorderLayout.SOUTH);
        playFrame.add(rightPanel, BorderLayout.EAST);

        // 设置窗口大小并显示
        playFrame.setSize(500, 300);
        playFrame.setVisible(true);
    }

    private Object[] getRowData(DefaultTableModel model, int row) {
        Object[] rowData = new Object[model.getColumnCount()];
        for (int i = 0; i < model.getColumnCount(); i++) {
            rowData[i] = model.getValueAt(row, i);
        }
        return rowData;
    }

    private void showPlaylistFrame() {
        // 播放列表界面 JFrame
        playlistFrame = new JFrame("播放列表界面");
        playlistFrame.setLayout(new BorderLayout());

        // 表格模型
        DefaultTableModel model = getDefaultTableModel();

        // 创建表格
        JTable table = new JTable(model);

        // 设置操作列的渲染器和编辑器
        Action playAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(playlistFrame, "播放歌曲");
                int row = (int) getValue("row");
                musicPlayer.play(row);

                // 3. 刷新playList
                playlistFrame.setVisible(false);
                playbackCompleted();
            }
        };

        Action deleteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = (int) getValue("row");
                // 获取该行的所有数据
                Object[] rowData = getRowData(model, row);
                int response = JOptionPane.showConfirmDialog(
                        playlistFrame,
                        "确定要删除这首歌曲吗?",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (response == JOptionPane.YES_OPTION) {
                    /*
                     用户确认删除
                     1. player中对应的歌曲删除
                        1. 检查当前歌曲是否正在播放，如果正在播放，则停止，并且删除；
                        2. 如果不在播放则直接删除；
                     2. 重新刷新playList
                    */
                    ((SimpleMusicPlayer) musicPlayer).deleteFromPlayList(row);

                    JOptionPane.showMessageDialog(playlistFrame, "歌曲已删除");
                    flushPlayListFrame();
                }
            }
        };

        new ButtonColumn(table, playAction, 3);
        new ButtonColumn(table, deleteAction, 4);

        // 将表格添加到滚动窗格中
        JScrollPane scrollPane = new JScrollPane(table);
        playlistFrame.add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));


        // 返回按钮
        JButton returnToPlayFrameButton = new JButton("返回");
        returnToPlayFrameButton.addActionListener(e -> {
            playlistFrame.setVisible(false);
            playFrame.setVisible(true);
        });

        JButton addSongButton = new JButton("添加歌曲");
        // addSongButton 的行为将由另外的代码定义
        addSongButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setMultiSelectionEnabled(true);
            int result = fileChooser.showOpenDialog(playlistFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                for (File file : fileChooser.getSelectedFiles()) {
                    String absolutePath = file.getAbsolutePath();
                    // 这里处理选中的文件，例如添加到播放列表
                    ((SimpleMusicPlayer) this.musicPlayer).addToPlayList(Paths.get(absolutePath));
                }
            }
            flushPlayListFrame();
        });
        // 将按钮添加到面板
        topPanel.add(returnToPlayFrameButton);
        topPanel.add(addSongButton);

        playlistFrame.add(topPanel, BorderLayout.SOUTH);

        playlistFrame.setSize(400, 300);
        playlistFrame.setVisible(true);
    }

    private void flushPlayListFrame() {
        flushCurrentMusic();
        playlistFrame.setVisible(false);
        playbackCompleted();
        showPlaylistFrame();
    }

    private DefaultTableModel getDefaultTableModel() {
        String[] columnNames = {"#", "歌曲名", "演唱者", "播放", "删除"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 3; // 只有操作列可以编辑
            }

            @Override
            public void addRow(Object[] rowData) {
                rowData[0] = this.getRowCount() + 1; // 设置自增列的值
                super.addRow(rowData);
            }
        };

        // 1. 从player得到playList链表
        LinkedList<AbstractMusic> playList = ((SimpleMusicPlayer) musicPlayer).getPlayList();
        // 2. 一次将数据加入表格
        if (!playList.isEmpty()) {
            playList.forEach(o -> model.addRow(new Object[]{null, o.songName, o.artist, "播放", "删除"}));
        }
        return model;
    }

    @Override
    public void playbackCompleted() {
        this.playFrame.setVisible(false);
        flushCurrentMusic();
        showPlayFrame();
    }

    private void flushCurrentMusic() {
        this.currentMusic = getCurrentMusic();
    }

    static class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
        JTable table;
        JButton renderButton;
        JButton editButton;
        String text;
        Action action;

        public ButtonColumn(JTable table, Action action, int column) {
            super();
            this.table = table;
            this.action = action;

            renderButton = new JButton();
            editButton = new JButton();
            editButton.setFocusPainted(false);
            editButton.addActionListener(this);

            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(column).setCellRenderer(this);
            columnModel.getColumn(column).setCellEditor(this);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (hasFocus) {
                renderButton.setForeground(table.getForeground());
                renderButton.setBackground(UIManager.getColor("Button.background"));
            } else if (isSelected) {
                renderButton.setForeground(table.getSelectionForeground());
                renderButton.setBackground(table.getSelectionBackground());
            } else {
                renderButton.setForeground(table.getForeground());
                renderButton.setBackground(UIManager.getColor("Button.background"));
            }

            renderButton.setText((value == null) ? "" : value.toString());
            return renderButton;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            text = (value == null) ? "" : value.toString();
            editButton.setText(text);
            return editButton;
        }

        @Override
        public Object getCellEditorValue() {
            return text;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped(); // 停止编辑
            // 从按钮获取行索引
            int selectedRow = table.getSelectedRow();
            // 将行索引作为属性传递给动作
            action.putValue("row", selectedRow);
            action.actionPerformed(e);
        }
    }
}
