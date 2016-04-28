package app;

import javax.swing.*;

public class Game {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Minesweeper::new);
    }

}
