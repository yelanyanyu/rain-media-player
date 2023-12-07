package com.yelanyanyu.gui;

import com.yelanyanyu.music.player.MusicPlayer;
import com.yelanyanyu.music.player.SimpleMusicPlayer;
import com.yelanyanyu.music.player.WindowsMp3MusicStrategy;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

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
                // 这里写死. 以后再改
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
        // 播放列表界面 JFrame
        playlistFrame = new JFrame("播放列表界面");
        playlistFrame.setLayout(new BorderLayout());

        // 表格模型
        String[] columnNames = {"歌曲名", "演唱者", "播放", "删除"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2; // 只有操作列可以编辑
            }
        };

        // 添加两行示例数据
        model.addRow(new Object[]{"歌曲1", "演唱者A", "播放", "删除"});
        model.addRow(new Object[]{"歌曲2", "演唱者B", "播放", "删除"});

        // 创建表格
        JTable table = new JTable(model);

        // 设置操作列的渲染器和编辑器
        Action playAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 实现播放逻辑
                JOptionPane.showMessageDialog(playlistFrame, "播放歌曲");
            }
        };

        Action deleteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 实现删除逻辑
                JOptionPane.showMessageDialog(playlistFrame, "删除歌曲");
            }
        };

        new ButtonColumn(table, playAction, 2);
        new ButtonColumn(table, deleteAction, 3);

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

    class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
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
            fireEditingStopped();
            action.actionPerformed(e);
        }
    }
}
