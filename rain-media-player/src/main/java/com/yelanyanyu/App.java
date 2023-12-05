package com.yelanyanyu;

import com.yelanyanyu.gui.MusicPlayerUi;

import javax.swing.*;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MusicPlayerUi::new);
    }
}
