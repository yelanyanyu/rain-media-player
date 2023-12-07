package com.yelanyanyu.gui;

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
import java.util.LinkedList;
import java.util.List;

/**
 * @author yelanyanyu
 */
@Slf4j
public class MusicPlayerUi {
    MusicPlayer musicPlayer = SimpleMusicPlayer.INSTANCE;
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
                // TODO: 这里写死. 以后再改
                simpleMusicPlayer.init(folderPath, new WindowsMp3MusicStrategy());
                showPlayFrame();
            }
        });

        mainFrame.setLayout(new FlowLayout());
        mainFrame.add(selectMusicButton);
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
            // TODO: 实现播放/暂停逻辑
            AbstractMusic currentMusic = ((SimpleMusicPlayer) musicPlayer).getCurrentMusic();
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

        // 设置布局
        playFrame.setLayout(new BorderLayout());

        // 创建中间信息面板
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 1)); // 使用 GridLayout 来放置两个信息标签
        infoPanel.add(new JLabel("歌曲名"));
        infoPanel.add(new JLabel("演唱者"));

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

        // TODO: 渲染playList的数据
        // 1. 从player得到playList链表
        LinkedList<AbstractMusic> playList = ((SimpleMusicPlayer) musicPlayer).getPlayList();
        // 2. 一次将数据加入表格
        playList.forEach(o -> {
            model.addRow(new Object[]{null, o.songName, o.artist, "播放", "删除"});
        });

        // 创建表格
        JTable table = new JTable(model);

        // 设置操作列的渲染器和编辑器
        Action playAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 实现播放逻辑 - wait for test
                JOptionPane.showMessageDialog(playlistFrame, "播放歌曲");
                int row = (int) getValue("row");
                Object[] rowData = getRowData(model, row);
                if (row == 1) {
                    // 1. 若播放的歌曲就是第一个，则直接调用 player.play()
                    musicPlayer.play();
                } else if (row > 1) {
                    // 2. 若不是第一个，则需要调用player.play(index)
                    musicPlayer.play(row - 1);
                }

                // 3. 刷新playList
                playlistFrame.setVisible(false);
                showPlaylistFrame();
            }
        };

        Action deleteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = (int) getValue("row");
                // 获取该行的所有数据
                Object[] rowData = getRowData(model, row);
                log.debug("row1: {}", rowData[0]);
                int response = JOptionPane.showConfirmDialog(
                        playlistFrame,
                        "确定要删除这首歌曲吗?",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (response == JOptionPane.YES_OPTION) {
                    // 用户确认删除
                    // TODO: 实现删除逻辑
                    // 例如：model.removeRow(row);
                    // 1. player中对应的歌曲删除

                    // 2. 重新刷新playList

                    JOptionPane.showMessageDialog(playlistFrame, "歌曲已删除");
                } else {
                    // 用户取消删除，返回播放界面
                    // 如果需要返回播放界面，执行相关逻辑
                    // 例如：playlistFrame.setVisible(false);
                    // playFrame.setVisible(true);
                }
            }
        };

        new ButtonColumn(table, playAction, 3);
        new ButtonColumn(table, deleteAction, 4);

        // 将表格添加到滚动窗格中
        JScrollPane scrollPane = new JScrollPane(table);
        playlistFrame.add(scrollPane, BorderLayout.CENTER);

        // 返回按钮
        JButton returnToPlayFrameButton = new JButton("返回");
        returnToPlayFrameButton.addActionListener(e -> {
            playlistFrame.setVisible(false);
            playFrame.setVisible(true);
        });

        playlistFrame.add(returnToPlayFrameButton, BorderLayout.SOUTH);

        playlistFrame.setSize(400, 300);
        playlistFrame.setVisible(true);
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
