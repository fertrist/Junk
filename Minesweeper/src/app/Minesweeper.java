package app;
import java.awt.*;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class Minesweeper extends JFrame implements ActionListener, ContainerListener {

    int frameWidth, frameHeight, totalRows, totalColumns, clickedRow, clickedColumn, numOfMines, detectedMines = 0,
    //настройки для перезапуска игры с теми же параметрами
    savedLevel = 1, savedBlockRow, savedBlockColumn, savedNumOfMines = 10;

    //координаты окружающих точек относительно текущего квадрата поля
    int[] surroundingRowCoordinates = {-1, -1, -1, 0, 1, 1, 1, 0};
    int[] surroundingColumnCoordinates = {-1, 0, 1, 1, 1, 0, -1, -1};
    JButton[][] blocks;
    int[][] minesCount;
    int[][] color;
    ImageIcon[] imageIcons = new ImageIcon[14];
    JPanel panelBorder = new JPanel();
    JPanel panelDisplay = new JPanel();
    JTextField minesTextField, timeTextField;
    JButton resetButton = new JButton("");
    Random randomRow = new Random();
    Random randomColumn = new Random();
    boolean check = true, startTime = false;
    Stopwatch stopwatch;
    MouseHandler mouseHandler;
    Point point;

    Minesweeper() {
        super("Minesweeper");
        setLocation(400, 300);

        setIcons();
        setPanel(1, 0, 0, 0);
        setMenu();

        stopwatch = new Stopwatch();
        resetButton.addActionListener(actionEvent -> {
            try {
                stopwatch.stop();
                setPanel(savedLevel, savedBlockRow, savedBlockColumn, savedNumOfMines);
            } catch (Exception ex) {
                setPanel(savedLevel, savedBlockRow, savedBlockColumn, savedNumOfMines);
            }
            resetBasicVars();
        });
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Сброс переменных отвечающих за ячейки поля и таймеры.
     */
    public void resetBasicVars() {
        check = true;
        startTime = false;
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                color[i][j] = 'w';
            }
        }
    }

    /**
     * Выбор параметров поля, установки дисплея таймера и счетчика мин, графические настройки.
     */
    public void setPanel(int level, int setr, int setc, int setm) {
        if (level == 1) {
            frameWidth = 200;
            frameHeight = 300;
            totalRows = 10;
            totalColumns = 10;
            numOfMines = 10;
        } else if (level == 2) {
            frameWidth = 320;
            frameHeight = 416;
            totalRows = 16;
            totalColumns = 16;
            numOfMines = 40;
        } else if (level == 3) {
            frameWidth = 400;
            frameHeight = 520;
            totalRows = 20;
            totalColumns = 20;
            numOfMines = 70;
        } else if (level == 4) {
            frameWidth = (20 * setc);
            frameHeight = (24 * setr);
            totalRows = setr;
            totalColumns = setc;
            numOfMines = setm;
        }

        savedBlockRow = totalRows;
        savedBlockColumn = totalColumns;
        savedNumOfMines = numOfMines;

        setSize(frameWidth, frameHeight);
        setResizable(false);
        detectedMines = numOfMines;
        point = this.getLocation();

        blocks = new JButton[totalRows][totalColumns];
        minesCount = new int[totalRows][totalColumns];
        color = new int[totalRows][totalColumns];
        mouseHandler = new MouseHandler();

        getContentPane().removeAll();
        panelBorder.removeAll();

        minesTextField = new JTextField("" + numOfMines, 3);
        minesTextField.setEditable(false);
        minesTextField.setFont(new Font("DigtalFont.TTF", Font.BOLD, 25));
        minesTextField.setBackground(Color.BLACK);
        minesTextField.setForeground(Color.RED);
        minesTextField.setBorder(BorderFactory.createLoweredBevelBorder());
        timeTextField = new JTextField("000", 3);
        timeTextField.setEditable(false);
        timeTextField.setFont(new Font("DigtalFont.TTF", Font.BOLD, 25));
        timeTextField.setBackground(Color.BLACK);
        timeTextField.setForeground(Color.RED);
        timeTextField.setBorder(BorderFactory.createLoweredBevelBorder());
        resetButton.setIcon(imageIcons[11]);
        resetButton.setBorder(BorderFactory.createLoweredBevelBorder());

        panelDisplay.removeAll();
        panelDisplay.setLayout(new BorderLayout());
        panelDisplay.add(minesTextField, BorderLayout.WEST);
        panelDisplay.add(resetButton, BorderLayout.CENTER);
        panelDisplay.add(timeTextField, BorderLayout.EAST);
        panelDisplay.setBorder(BorderFactory.createLoweredBevelBorder());

        panelBorder.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLoweredBevelBorder()));
        panelBorder.setPreferredSize(new Dimension(frameWidth, frameHeight));
        panelBorder.setLayout(new GridLayout(0, totalColumns));
        panelBorder.addContainerListener(this);

        /* Начальная установка каждой ячейки поля. */
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                blocks[i][j] = new JButton("");
                blocks[i][j].addMouseListener(mouseHandler);
                panelBorder.add(blocks[i][j]);
            }
        }
        resetBasicVars();

        panelBorder.revalidate();
        panelBorder.repaint();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().addContainerListener(this);
        getContentPane().repaint();
        getContentPane().add(panelBorder, BorderLayout.CENTER);
        getContentPane().add(panelDisplay, BorderLayout.NORTH);
        setVisible(true);
    }

    /**
     * Установить пункты меню и соответсвующие обработчики.
     */
    public void setMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu game = new JMenu("Game");
        JMenuItem menuItem = new JMenuItem("New game");
        final JCheckBoxMenuItem beginner = new JCheckBoxMenuItem("Beginner");
        final JCheckBoxMenuItem intermediate = new JCheckBoxMenuItem("Intermediate");
        final JCheckBoxMenuItem expert = new JCheckBoxMenuItem("Expert");
        final JCheckBoxMenuItem custom = new JCheckBoxMenuItem("Custom");
        final JMenuItem exit = new JMenuItem("Exit");
        final JMenu help = new JMenu("Help");
        final JMenuItem helpItem = new JMenuItem("Help");

        ButtonGroup status = new ButtonGroup();
        menuItem.addActionListener(actionEvent -> setPanel(1, 0, 0, 0));
        beginner.addActionListener(
            actionEvent -> {
                panelBorder.removeAll();
                resetBasicVars();
                setPanel(1, 0, 0, 0);
                panelBorder.revalidate();
                panelBorder.repaint();
                beginner.setSelected(true);
                savedLevel = 1;
            });
        intermediate.addActionListener(
            actionEvent -> {
                panelBorder.removeAll();
                resetBasicVars();
                setPanel(2, 0, 0, 0);
                panelBorder.revalidate();
                panelBorder.repaint();
                intermediate.setSelected(true);
                savedLevel = 2;
            });
        expert.addActionListener(
            actionEvent -> {
                panelBorder.removeAll();
                resetBasicVars();
                setPanel(3, 0, 0, 0);
                panelBorder.revalidate();
                panelBorder.repaint();
                expert.setSelected(true);
                savedLevel = 3;
            });

        custom.addActionListener(
            actionEvent -> {
                resetBasicVars();
                panelBorder.revalidate();
                panelBorder.repaint();
                custom.setSelected(true);
                savedLevel = 4;
            });

        exit.addActionListener(event -> System.exit(0));

        helpItem.addActionListener(event -> JOptionPane.showMessageDialog(null, "instruction"));
        setJMenuBar(bar);

        status.add(beginner);
        status.add(intermediate);
        status.add(expert);
        status.add(custom);

        game.add(menuItem);
        game.addSeparator();
        game.add(beginner);
        game.add(intermediate);
        game.add(expert);
        game.add(custom);
        game.addSeparator();
        game.add(exit);
        help.add(helpItem);

        bar.add(game);
        bar.add(help);
    }

    /**
     * Обработчик событий мыши для ячеек поля.
     */
    class MouseHandler extends MouseAdapter {

        public void mouseClicked(MouseEvent mouseEvent) {
            if (check) {
                for (int i = 0; i < totalRows; i++) {
                    for (int j = 0; j < totalColumns; j++) {
                        if (mouseEvent.getSource() == blocks[i][j]) {
                            clickedRow = i;
                            clickedColumn = j;
                            i = totalRows;
                            break;
                        }
                    }
                }
                setMines();
                countSurroundingMines();
                check = false;
            }
            showCellValueAfterClick(mouseEvent);
            checkAndHandleVictory();
            if (!startTime) {
                stopwatch.start();
                startTime = true;
            }
        }
    }

    /**
     * Проверка и обработка победного исхода.
     */
    public void checkAndHandleVictory() {
        int q = 0;
        for (int k = 0; k < totalRows; k++) {
            for (int l = 0; l < totalColumns; l++) {
                if (color[k][l] == 'w') {
                    q = 1;
                }
            }
        }
        if (q == 0) {
            for (int k = 0; k < totalRows; k++) {
                for (int l = 0; l < totalColumns; l++) {
                    blocks[k][l].removeMouseListener(mouseHandler);
                }
            }
            stopwatch.stop();
            JOptionPane.showMessageDialog(this, "You won!");
        }
    }

    /**
     * Показать результат правого/левого клика на ячейку.
     * @param e событие мыши.
     */
    public void showCellValueAfterClick(MouseEvent e) {
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                if (e.getSource() == blocks[i][j]) {
                    //не нажата ли правая клавиша мыши
                    if (!e.isMetaDown()) {
                        //если ячейка с флагом
                        if (blocks[i][j].getIcon() == imageIcons[10]) {
                            if (detectedMines < numOfMines) {
                                detectedMines++;
                            }
                            minesTextField.setText("" + detectedMines);
                        }
                        //если в ячейке находилась мина
                        if (minesCount[i][j] == -1) {
                            for (int k = 0; k < totalRows; k++) {
                                for (int l = 0; l < totalColumns; l++) {
                                    //показать все мины на поле
                                    if (minesCount[k][l] == -1) {
                                        blocks[k][l].setIcon(imageIcons[9]);
                                        blocks[k][l].removeMouseListener(mouseHandler);
                                    }
                                    blocks[k][l].removeMouseListener(mouseHandler);
                                }
                            }
                            stopwatch.stop();
                            resetButton.setIcon(imageIcons[12]);
                            JOptionPane.showMessageDialog(null, "You lose...");
                        } else if (minesCount[i][j] == 0) {    //если ячейка без мины, открыть все смежные пустые ячейки
                            discoverAllLinkedFieldsDfs(i, j);
                        } else {
                            //если к ячейке прилягают мины, показать их количество
                            blocks[i][j].setIcon(imageIcons[minesCount[i][j]]);
                            color[i][j] = 'b';
                            break;
                        }
                        //если нажата правая клавиша мыши
                    } else {
                        if (detectedMines != 0) {
                            //поставить флажок и отнять общее кол-во мин
                            if (blocks[i][j].getIcon() == null) {
                                detectedMines--;
                                blocks[i][j].setIcon(imageIcons[10]);
                            }
                            minesTextField.setText("" + detectedMines);
                        }
                    }
                }
            }
        }
    }

    /**
     * Вычисление количества прилегающих мин к каждой клетке поля.
     */
    public void countSurroundingMines() {
        int row, column;
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                int value = 0;
                int R, C;
                row = i;
                column = j;
                if (minesCount[row][column] != -1) {
                    for (int k = 0; k < 8; k++) {
                        R = row + surroundingRowCoordinates[k];
                        C = column + surroundingColumnCoordinates[k];

                        if (R >= 0 && C >= 0 && R < totalRows && C < totalColumns) {
                            if (minesCount[R][C] == -1) {
                                value++;
                            }
                        }
                    }
                    minesCount[row][column] = value;
                }
            }
        }
    }

    /**
     * Нахождение смежных полей, которые могут быть открыты.
     * @param row строка исходной клетки
     * @param col колонка исходной клетки
     */
    public void discoverAllLinkedFieldsDfs(int row, int col) {
        int R, C;
        color[row][col] = 'b';
        blocks[row][col].setBackground(Color.GRAY);
        blocks[row][col].setIcon(imageIcons[minesCount[row][col]]);
        for (int i = 0; i < 8; i++) {
            R = row + surroundingRowCoordinates[i];
            C = col + surroundingColumnCoordinates[i];
            if (R >= 0 && R < totalRows && C >= 0 && C < totalColumns && color[R][C] == 'w') {
                if (minesCount[R][C] == 0) {
                    discoverAllLinkedFieldsDfs(R, C);
                } else {
                    blocks[R][C].setIcon(imageIcons[minesCount[R][C]]);
                    color[R][C] = 'b';
                }
            }
        }
    }

    /**
     * Расставить мины случайным образом.
     */
    public void setMines() {
        int row, col;
        //флаг прохождения ячеек
        Boolean[][] flags = new Boolean[totalRows][totalColumns];
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalColumns; j++) {
                flags[i][j] = true;
                minesCount[i][j] = 0;
            }
        }
        flags[clickedRow][clickedColumn] = false;
        color[clickedRow][clickedColumn] = 'b';
        for (int i = 0; i < numOfMines; i++) {
            row = randomRow.nextInt(totalRows);
            col = randomColumn.nextInt(totalColumns);
            if (flags[row][col]) {
                minesCount[row][col] = -1;
                color[row][col] = 'b';
                flags[row][col] = false;
            } else {
                i--;
            }
        }
    }

    /**
     * Загрузка картинок.
     */
    public void setIcons() {
        String name;
        for (int i = 0; i <= 8; i++) {
            name = i + ".gif";
            imageIcons[i] = new ImageIcon(name);
        }
        imageIcons[9] = new ImageIcon("mine.gif");
        imageIcons[10] = new ImageIcon("flag.gif");
        imageIcons[11] = new ImageIcon("new game.gif");
        imageIcons[12] = new ImageIcon("crape.gif");
    }

    /**
     * Таймер, в потоке ежесекундно высчитывает и обновляет время.
     */
    public class Stopwatch extends JFrame implements Runnable {
        long startTime;
        Thread updater;
        boolean isRunning = false;
        long a = 0;
        Runnable displayUpdater = () -> {
            Stopwatch.this.displayElapsedTime(a);
            a++;
        };

        public void stop() {
            long elapsed = a;
            isRunning = false;
            try {
                updater.join();
            } catch (InterruptedException ignored) {
            }
            displayElapsedTime(elapsed);
            a = 0;
        }

        private void displayElapsedTime(long elapsedTime) {
            if (elapsedTime >= 0 && elapsedTime < 9) {
                timeTextField.setText("00" + elapsedTime);
            } else if (elapsedTime > 9 && elapsedTime < 99) {
                timeTextField.setText("0" + elapsedTime);
            } else if (elapsedTime > 99 && elapsedTime < 999) {
                timeTextField.setText("" + elapsedTime);
            }
        }

        public void run() {
            try {
                while (isRunning) {
                    SwingUtilities.invokeAndWait(displayUpdater);
                    Thread.sleep(1000);
                }
            } catch (java.lang.reflect.InvocationTargetException ite) {
                ite.printStackTrace(System.err);
            } catch (InterruptedException ignored) {
            }
        }

        public void start() {
            startTime = System.currentTimeMillis();
            isRunning = true;
            updater = new Thread(this);
            updater.start();
        }
    }

    class CustomizationForm extends JFrame implements ActionListener {

        JTextField rowsTextField, columnsTextField, minesTextField;
        JLabel labelRows, labelColumns, labelMines;
        JButton okButton, cancelButton;
        int rowCount, columnCount, mineCount;

        CustomizationForm() {
            super("Customization");
            setSize(180, 200);
            setResizable(false);
            setLocation(point);

            rowsTextField = new JTextField();
            columnsTextField = new JTextField();
            minesTextField = new JTextField();

            okButton = new JButton("OK");
            cancelButton = new JButton("Cancel");

            okButton.addActionListener(this);
            cancelButton.addActionListener(this);

            labelRows = new JLabel("Row");
            labelColumns = new JLabel("Column");
            labelMines = new JLabel("Mine");

            getContentPane().setLayout(new GridLayout(0, 2));

            getContentPane().add(labelRows);
            getContentPane().add(rowsTextField);
            getContentPane().add(labelColumns);
            getContentPane().add(columnsTextField);
            getContentPane().add(labelMines);
            getContentPane().add(minesTextField);

            getContentPane().add(okButton);
            getContentPane().add(cancelButton);

            setVisible(true);
        }

        /**
         * Принятие/сброс пользовательских настроек.
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == okButton) {
                try {
                    rowCount = Integer.parseInt(rowsTextField.getText());
                    columnCount = Integer.parseInt(columnsTextField.getText());
                    mineCount = Integer.parseInt(minesTextField.getText());
                    setPanel(4, getRows(), getColumns(), getMines());
                    dispose();
                } catch (Exception any) {
                    JOptionPane.showMessageDialog(this, "Wrong value.");
                    rowsTextField.setText("");
                    columnsTextField.setText("");
                    minesTextField.setText("");
                }
            }
            if (e.getSource() == cancelButton) {
                dispose();
            }
        }

        public int getRows() {
            if (rowCount > 30) {
                return 30;
            } else if (rowCount < 10) {
                return 10;
            } else {
                return rowCount;
            }
        }

        public int getColumns() {
            if (columnCount > 30) {
                return 30;
            } else if (columnCount < 10) {
                return 10;
            } else {
                return columnCount;
            }
        }

        public int getMines() {
            if (mineCount > ((getRows() - 1) * (getColumns() - 1))) {
                return ((getRows() - 1) * (getColumns() - 1));
            } else if (mineCount < 10) {
                return 10;
            } else {
                return mineCount;
            }
        }
    }

    public void componentAdded(ContainerEvent ce) {
    }

    public void componentRemoved(ContainerEvent ce) {
    }

    public void actionPerformed(ActionEvent ae) {
    }
}
