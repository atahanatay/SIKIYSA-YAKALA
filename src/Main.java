import javax.sound.midi.SoundbankResource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;


public class Main {
    static String gameName = "Sýkýysa Yakala";

    static JFrame frame = new JFrame();

    public static final int RED_CUBE = 0;
    public static final int BLUE_CUBE = 1;
    public static final int BLACK_CUBE = 2;
    public static final int NO = 4;
    public static final int DEVELOPER = 5;
    public static final int FAKE_BLOCK = 6;

    static boolean developerMode = false;

    static boolean lastSelectedFakeBlock = false;

    static Lines lines = new Lines();

    static boolean foundBlue = false;
    static boolean searchingBlue = false;

    static int blackMoves = 0;
    static int blackMovesLimit = 5;

    static int fakeBlockStage = 0;
    static int fakeBlockStageLimit = 5;

    static int turn = 0;
    static int selected = 0;
    static CubeBLACK selectedBlack;

    static FakeBLOCK fakeBlock = new FakeBLOCK();

    static JLabel label = new JLabel(blackMoves + "/" + blackMovesLimit);

    static CubeRED redCube = new CubeRED();
    static CubeBLUE blueCube = new CubeBLUE();
    static ArrayList<CubeBLACK> blackCubes = new ArrayList<>();
    static ArrayList<Corner> corners = new ArrayList<>();
    static ArrayList<CubeGREEN> greenCubes = new ArrayList<>();

    static GUI gui = new GUI();

    static int _level = 1;
    static int locationX = 0;
    static int locationY = 0;
    static int moveX = 0;
    static int moveY = 0;
    static String _beforeText;

    static KeyListener listener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyTyped(e);

            int num;

            if (((e.getKeyCode() >= 0x60 && e.getKeyCode() <= 0x64) || (e.getKeyCode() >= 0x30 && e.getKeyCode() <= 0x34)) && _level != 3 && _level != 6) {
                if (e.getKeyCode() >= 0x60 && e.getKeyCode() <= 0x64) num = e.getKeyCode() - 96;
                else num = e.getKeyCode() - 48;

                if (_level >= 7) {
                    if (turn == 4) _level = 4;
                    else _level = 1;
                }

                if (_level == 1) locationX = num;
                else if (_level == 2) locationY = num;
                else if (_level == 4) moveX = num;
                else if (_level == 5) moveY = num;

                if (_level < 7) _level++;

                System.out.println("Level:" + _level);
            } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                if (_level > 1 && _level < 7 && turn != 4) {
                    _level--;
                } else if (_level > 4 && turn == 4 && _level < 7) {
                    _level--;
                } else if (turn == 4 && _level >= 7) {
                    _level = 4;
                } else if (_level >= 7) {
                    _level = 1;
                }
                System.out.println(_level);
            } else if ((_level == 3 || _level == 6) && e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (_level < 7) _level++;

                System.out.println("Level Enter:" + _level);
            }

            if (_level == 4) {
                Object a = controlGridWithBlock(locationX, locationY, false);

                if (a instanceof CubeRED) requestSelectRed();
                else if (a instanceof CubeBLUE) requestSelectBlue();
                else if (a instanceof CubeBLACK) requestSelectBlack((CubeBLACK) a);
                else _level = 1;
            } else if (_level >= 7) {
                Object a = controlGridWithBlock(moveX, moveY, turn == 4);

                System.out.println(a.getClass().getName());

                if (a instanceof CubeGREEN) moveCube((CubeGREEN) a);
                else if (a instanceof CubeBLUE && turn == 4) requestSelectBlue();
                else if (a instanceof CubeBLUE && turn == 0) {
                    moveToBlue();
                    _level = 4;
                }
            }

            updateLabel();
        }
    };

    public static void updateLabel() {
        Runnable run = () -> {
            if (turn != 4) {
                if (_level == 1) {
                    label.setText(_beforeText);
                } else if (_level == 2) {
                    label.setText(_beforeText + " : " + locationX + "x");
                } else if (_level == 3) {
                    label.setText(_beforeText + " : " + locationX + "x" + locationY);
                } else if (_level == 4) {
                    label.setText(_beforeText + " : " + locationX + "x" + locationY + " ->");
                } else if (_level == 5) {
                    label.setText(_beforeText + " : " + locationX + "x" + locationY + " -> " + moveX);
                } else if (_level == 6) {
                    label.setText(_beforeText + " : " + locationX + "x" + locationY + " -> " + moveX + "x" + moveY);
                } else if (_level == 7) {
                    label.setText(_beforeText + " : " + locationX + "x" + locationY + " -> " + moveX + "x" + moveY + "*");
                }
            } else {
                if (_level == 4) {
                    label.setText(_beforeText + " : ->");
                } else if (_level == 5) {
                    label.setText(_beforeText + " : -> " + moveX);
                } else if (_level == 6) {
                    label.setText(_beforeText + " : -> " + moveX + "x" + moveY);
                } else if (_level == 7) {
                    label.setText(_beforeText + " : -> " + moveX + "x" + moveY + "*");
                }
            }
        };

        SwingUtilities.invokeLater(run);
    }

    public static void setLabelText(String text) {
        _beforeText = text;
        updateLabel();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::start);
    }

    public static void start() {
        frame.setIconImage(new ImageIcon(Main.class.getResource("Icon/logo3.png")).getImage());
        frame.setContentPane(gui);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setTitle(gameName);
        repaintAll();

        frame.addKeyListener(listener);

        JMenuBar bar = new JMenuBar();
        JMenu settingsMenu = new JMenu("Ayarlar");
        JMenuItem setBlackMovesLimit = new JMenuItem("Siyah hareket limitini ayarla");

        setBlackMovesLimit.addActionListener(e -> {
            try {
                int i = Integer.parseInt(JOptionPane.showInputDialog(null, "Hamle limitini girin", "Hamle limitini ayarla", JOptionPane.PLAIN_MESSAGE));

                if (i <= 0) throw new ZeroException();

                blackMovesLimit = i;
                blackMoves = 0;

                if (turn != 4) {
                    setLabelText(blackMoves + "/" + blackMovesLimit);
                }
            } catch (NumberFormatException e1) {
                JOptionPane.showMessageDialog(null, "Lütfen sayý giriniz", "Hata", JOptionPane.ERROR_MESSAGE);
            } catch (ZeroException e2) {
                JOptionPane.showMessageDialog(null, "Lütfen 0'dan büyük bir sayý giriniz", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        JMenuItem setFakeBlockStageLimit = new JMenuItem("Kýrmýzýnýn maviye ulaþmak için hamle limitini ayarla");

        setFakeBlockStageLimit.addActionListener(e -> {
            try {
                int i = Integer.parseInt(JOptionPane.showInputDialog(null, "Hamle limitini girin", "Hamle limitini ayarla", JOptionPane.PLAIN_MESSAGE));
                if (i <= 0) throw new ZeroException();

                fakeBlockStageLimit = i;
                fakeBlockStage = 0;

                if (turn == 4) {
                    setLabelText("Maviye ulaþýn: " + fakeBlockStage + "/" + fakeBlockStageLimit + " - 'O':Ýptal");
                }
            } catch (NumberFormatException e1) {
                JOptionPane.showMessageDialog(null, "Lütfen sayý giriniz", "Hata", JOptionPane.ERROR_MESSAGE);
            } catch (ZeroException e2) {
                JOptionPane.showMessageDialog(null, "Lütfen 0'dan büyük bir sayý giriniz", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        JMenuItem startAgain = new JMenuItem("Baþtan baþla");

        startAgain.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(
                    null,
                    "Emin Misiniz?",
                    "Baþtan baþla",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                startInit();
            }
        });

        settingsMenu.add(setBlackMovesLimit);
        settingsMenu.add(setFakeBlockStageLimit);
        settingsMenu.add(startAgain);
        bar.add(settingsMenu);

        JMenu aboutMenu = new JMenu("Hakkýnda");

        JMenuItem about = new JMenuItem("Oyun Hakkýnda");

        about.addActionListener(e -> {
            try {
                new TextDialog("Hakkýnda", Main.class.getResource("About.html"), new Dimension(500, 500));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        JMenuItem howToPlay = new JMenuItem("Nasýl Oynanýr");

        howToPlay.addActionListener(e -> {
            try {
                new TextDialog("Nasýl Oynanýr", Main.class.getResource("HowToPlay.html"), new Dimension(500, 500));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        aboutMenu.add(about);
        aboutMenu.add(howToPlay);
        bar.add(aboutMenu);

        frame.setJMenuBar(bar);
        frame.pack();

        label.setSize(400, 15);
        label.setLocation(50, 0);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Monospaced", Font.BOLD, label.getFont().getSize()));
        label.setForeground(Color.RED);
        label.setBackground(frame.getBackground());
        label.setOpaque(true);

        gui.add(label);
        gui.add(lines);

        gui.setLayer(label, 0);
        gui.setLayer(lines, -1);

        for (int i = 0; i < 4; i++) {
            Corner c = new Corner();
            corners.add(c);

            if (i == 0) setPos(c, 0, 0, 0, 0);
            else if (i == 1) setPos(c, 490, 0, 4, 0);
            else if (i == 2) setPos(c, 490, 490, 4, 4);
            else setPos(c, 0, 490, 0, 4);
        }

        repaintAll();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_6 && e.isControlDown()) {
                    openDeveloperMode();
                } else if (e.getKeyCode() == KeyEvent.VK_O) {
                    if (searchingBlue) {
                        fakeBlockStage = fakeBlockStageLimit;
                        tryFind();
                    }
                }
            }
        });

        for (int i = 0; i < 6; i++) {
            CubeBLACK black = new CubeBLACK();
            blackCubes.add(black);
        }

        startInit();
    }

    public static void startInit() {
        setPos(redCube, 1, 2);
        setPos(blueCube, 3, 2);

        blackMoves = 0;
        fakeBlockStage = 0;
        selected = 0;

        setLabelText(blackMoves + "/" + blackMovesLimit);

        for (int i = 0; i < blackCubes.size(); i++) {
            CubeBLACK black = blackCubes.get(i);

            if (i == 0) {
                setPos(black, 0, 1);
            } else if (i == 1) {
                setPos(black, 0, 3);
            } else if (i == 2) {
                setPos(black, 4, 1);
            } else if (i == 3) {
                setPos(black, 4, 3);
            } else if (i == 4) {
                setPos(black, 2, 0);
            } else {
                setPos(black, 2, 4);
            }
        }

        corners.forEach(corner -> corner.clicked = false);

        cancelWin();

        setTurn(0);

        blueCube.selectableWithFakeBlock = false;

        repaintAll();
    }

    public static void cancelWin() {
        CubeRED.win = false;
        CubeBLACK.win = false;

        CubeRED.t.interrupt();
        CubeBLACK.t.interrupt();
    }

    public static void openDeveloperMode() {
        if (developerMode) {
            developerMode = false;
            setTurn(3);
        } else {
            developerMode = true;
            setTurn(0);
        }
        repaintAll();
    }

    public static void setTurn(int t) {
        if (t != 1) for (CubeBLACK cb : blackCubes) cb.locked = false;

        if (blackMoves == blackMovesLimit && t != 5) {
            t = 2;
            blackMoves = 0;
        }

        turn = t;

        System.out.println(t);

        if (t == 0) {
            redCube.selectable = true;
            blueCube.selectable = true;
            blackCubes.forEach(cubeBLACK -> {
                cubeBLACK.selectable = false;
                cubeBLACK.fSelectable = false;
            });

            repaintAll();
        } else if (t == 1) {
            blackCubes.forEach(cubeBLACK -> {
                cubeBLACK.selectable = true;
                cubeBLACK.fSelectable = false;
            });
            redCube.selectable = false;
            blueCube.selectable = false;

            repaintAll();
        } else if (t == 2) {
            blackCubes.forEach(cubeBLACK -> {
                cubeBLACK.selectable = true;
                cubeBLACK.fSelectable = true;
            });
            redCube.selectable = false;
            blueCube.selectable = false;

            repaintAll();
        } else if (t == 3) {
            blackCubes.forEach(cubeBLACK -> {
                cubeBLACK.selectable = true;
                cubeBLACK.fSelectable = false;
            });
            redCube.selectable = true;
            blueCube.selectable = true;

            repaintAll();
        } else if (t == 4) {
            blackCubes.forEach(cubeBLACK -> {
                cubeBLACK.selectable = false;
                cubeBLACK.fSelectable = false;
            });
            redCube.selectable = false;
            blueCube.selectable = false;

            repaintAll();
        } else if (t == 5) {
            blackCubes.forEach(cubeBLACK -> {
                cubeBLACK.selectable = false;
                cubeBLACK.fSelectable = false;
            });
            redCube.selectable = false;
            blueCube.selectable = false;

            repaintAll();
        }
    }

    public static void setPos(CubeBLUE p, int x, int y) {
        p.setLocation((x * 100) + 25, (y * 100) + 25);
        p.grids.setValues(x, y);
        gui.add(p, 3);
        gui.moveToFront(p);
    }

    public static void setPos(CubeRED p, int x, int y) {
        p.setLocation((x * 100) + 25, (y * 100) + 25);
        p.grids.setValues(x, y);
        gui.add(p, 3);
        gui.moveToFront(p);
    }

    public static void setPos(CubeBLACK p, int x, int y) {
        p.setLocation((x * 100) + 25, (y * 100) + 25);
        p.grids.setValues(x, y);
        gui.add(p, 3);
    }

    public static void setPos(CubeGREEN p, int x, int y) {
        p.setLocation((x * 100) + 25, (y * 100) + 25);
        p.grids.setValues(x, y);
        gui.add(p, 3);
        gui.moveToFront(p);
    }

    public static void setPos(Corner p, int x, int y, int x1, int y1) {
        p.setLocation(x, y);
        p.grids.setValues(x1, y1);
        gui.add(p, 3);
        gui.moveToFront(p);
    }

    public static void setPos(FakeBLOCK p, int x, int y) {
        p.setLocation((x * 100) + 25, (y * 100) + 25);
        p.grids.setValues(x, y);
        gui.add(p, 3);
        gui.moveToFront(p);
    }

    public static void setGreenCubes(int type, int x, int y) {
        if (type == RED_CUBE) {
            if (controlGrid(redCube.grids.x + 1, redCube.grids.y))
                setPos(new CubeGREEN(), redCube.grids.x + 1, redCube.grids.y);
            if (controlGrid(redCube.grids.x - 1, redCube.grids.y))
                setPos(new CubeGREEN(), redCube.grids.x - 1, redCube.grids.y);
            if (controlGrid(redCube.grids.x, redCube.grids.y + 1))
                setPos(new CubeGREEN(), redCube.grids.x, redCube.grids.y + 1);
            if (controlGrid(redCube.grids.x, redCube.grids.y - 1))
                setPos(new CubeGREEN(), redCube.grids.x, redCube.grids.y - 1);
            if (controlGrid(redCube.grids.x + 1, redCube.grids.y + 1))
                setPos(new CubeGREEN(), redCube.grids.x + 1, redCube.grids.y + 1);
            if (controlGrid(redCube.grids.x - 1, redCube.grids.y - 1))
                setPos(new CubeGREEN(), redCube.grids.x - 1, redCube.grids.y - 1);
            if (controlGrid(redCube.grids.x + 1, redCube.grids.y - 1))
                setPos(new CubeGREEN(), redCube.grids.x + 1, redCube.grids.y - 1);
            if (controlGrid(redCube.grids.x - 1, redCube.grids.y + 1))
                setPos(new CubeGREEN(), redCube.grids.x - 1, redCube.grids.y + 1);
            gui.repaint();

            repaintAll();
        } else if (type == BLUE_CUBE || type == BLACK_CUBE || type == FAKE_BLOCK) {
            blueCube.selectableWithFakeBlock = false;

            for (int i = x + 1; i < 5; i++) {
                if (controlGridWithBlock(i, y, false) instanceof CubeBLUE) blueCube.selectableWithFakeBlock = true;

                if (!controlGrid(i, y)) break;
                else setPos(new CubeGREEN(), i, y);
            }

            for (int i = x - 1; i >= 0; i--) {
                if (controlGridWithBlock(i, y, false) instanceof CubeBLUE) blueCube.selectableWithFakeBlock = true;

                if (!controlGrid(i, y)) break;
                else setPos(new CubeGREEN(), i, y);
            }

            for (int i = y + 1; i < 5; i++) {
                if (controlGridWithBlock(x, i, false) instanceof CubeBLUE) blueCube.selectableWithFakeBlock = true;

                if (!controlGrid(x, i)) break;
                else setPos(new CubeGREEN(), x, i);
            }

            for (int i = y - 1; i >= 0; i--) {
                if (controlGridWithBlock(x, i, false) instanceof CubeBLUE) blueCube.selectableWithFakeBlock = true;

                if (!controlGrid(x, i)) break;
                else setPos(new CubeGREEN(), x, i);
            }

            int x1;
            int y1;

            x1 = x + 1;
            y1 = y + 1;
            while (x1 < 5 && y1 < 5) {
                if (controlGridWithBlock(x1, y1, false) instanceof CubeBLUE) blueCube.selectableWithFakeBlock = true;

                if (!controlGrid(x1, y1)) break;
                else setPos(new CubeGREEN(), x1, y1);

                x1++;
                y1++;
            }

            x1 = x - 1;
            y1 = y - 1;
            while (x1 >= 0 && y1 >= 0) {
                if (controlGridWithBlock(x1, y1, false) instanceof CubeBLUE) blueCube.selectableWithFakeBlock = true;

                if (!controlGrid(x1, y1)) break;
                else setPos(new CubeGREEN(), x1, y1);

                x1--;
                y1--;
            }

            x1 = x + 1;
            y1 = y - 1;
            while (x1 < 5 && y1 >= 0) {
                if (controlGridWithBlock(x1, y1, false) instanceof CubeBLUE) blueCube.selectableWithFakeBlock = true;

                if (!controlGrid(x1, y1)) break;
                else setPos(new CubeGREEN(), x1, y1);

                x1++;
                y1--;
            }

            x1 = x - 1;
            y1 = y + 1;
            while (x1 >= 0 && y1 < 5) {
                if (controlGridWithBlock(x1, y1, false) instanceof CubeBLUE) blueCube.selectableWithFakeBlock = true;

                if (!controlGrid(x1, y1)) break;
                else setPos(new CubeGREEN(), x1, y1);

                x1--;
                y1++;
            }
        } else if (type == DEVELOPER) {
            for (int x1 = 0; x1 < 5; x1++) {
                for (int y1 = 0; y1 < 5; y1++) {
                    if (controlGrid(x1, y1)) setPos(new CubeGREEN(), x1, y1);
                }
            }
        } else if (type == NO) {
            removeGreens();
            gui.repaint();
        }

        gui.repaint();
    }

    public static void removeGreens() {
        for (Component comp : gui.getComponents()) {
            if (comp instanceof CubeGREEN) gui.remove(comp);
        }
    }

    public static void moveCube(CubeGREEN g) {
        boolean pass = false;

        if (selected == RED_CUBE) {
            redCube.lastGrids = new Grids(redCube.grids.x, redCube.grids.y);

            setPos(redCube, g.grids.x, g.grids.y);
            if (blackMoves == blackMovesLimit) blackMoves = 0;

            for (CubeBLACK cb : blackCubes) cb.locked = false;
        } else if (selected == BLUE_CUBE) {
            blueCube.lastGrids = new Grids(blueCube.grids.x, blueCube.grids.y);

            setPos(blueCube, g.grids.x, g.grids.y);
            if (blackMoves == blackMovesLimit) blackMoves = 0;

            for (CubeBLACK cb : blackCubes) cb.locked = false;
        } else if (selected == BLACK_CUBE) {

            if (!selectedBlack.fSelectable)
                selectedBlack.lastGrids = new Grids(selectedBlack.grids.x, selectedBlack.grids.y);

            setPos(selectedBlack, g.grids.x, g.grids.y);
            if (turn != 2 && turn != 3) blackMoves++;
            else if (turn != 3) selectedBlack.locked = true;

            repaintAll();
        } else if (selected == FAKE_BLOCK) {
            System.out.println(g.grids.x + " " + g.grids.y);

            setPos(fakeBlock, g.grids.x, g.grids.y);

            locationX = g.grids.x;
            locationY = g.grids.y;

            fakeBlockStage++;
            setLabelText("Maviye ulaþýn: " + fakeBlockStage + "/" + fakeBlockStageLimit + " - 'O':Ýptal");

            System.out.println("trying find...");
            tryFind();
            pass = true;

            repaintAll();
        }

        if (!lastSelectedFakeBlock) {
            if (turn == 0) setTurn(1);
            else if (turn == 1) setTurn(0);
            else if (turn == 2) setTurn(1);
        }

        lastSelectedFakeBlock = false;

        deSelectAll();

        moveX = g.grids.x;
        moveY = g.grids.y;
        if (!pass) _level = 7;
        updateLabel();

        for (Corner c : corners) {
            if (c.grids.x == redCube.grids.x && c.grids.y == redCube.grids.y) c.clicked = true;
        }

        gui.repaint();

        if (selected == FAKE_BLOCK) {
            requestSelectFakeBlock();
        } else {
            setLabelText(blackMoves + "/" + blackMovesLimit);
            deSelectAll();
        }

        if (!controlAround()) blackWin();
        if (controlCorners()) redWin();
    }

    public static void blackWin() {
        setLabelText("Siyah Takým Kazandý!");
        _level = 1;
        CubeBLACK.win = true;
        CubeBLACK.t.start();
        setTurn(5);
        repaintAll();
    }

    public static void redWin() {
        setLabelText("Kýrmýzý Takým Kazandý!");
        _level = 1;
        CubeRED.win = true;
        CubeRED.t.start();
        setTurn(5);
        repaintAll();
    }

    public static void repaintAll() {
        frame.revalidate();
        frame.repaint();
        redCube.repaint();
        blueCube.repaint();
        gui.revalidate();
        gui.repaint();
        blackCubes.forEach(CubeBLACK::repaint);
    }

    public static boolean controlAround() {
        boolean _a = (controlGridWithBlock(redCube.grids.x + 1, redCube.grids.y, false) != null && !(controlGridWithBlock(redCube.grids.x + 1, redCube.grids.y, false) instanceof CubeBLACK));
        boolean _b = (controlGridWithBlock(redCube.grids.x - 1, redCube.grids.y, false) != null && !(controlGridWithBlock(redCube.grids.x - 1, redCube.grids.y, false) instanceof CubeBLACK));
        boolean _c = (controlGridWithBlock(redCube.grids.x, redCube.grids.y + 1, false) != null && !(controlGridWithBlock(redCube.grids.x, redCube.grids.y + 1, false) instanceof CubeBLACK));
        boolean _d = (controlGridWithBlock(redCube.grids.x, redCube.grids.y - 1, false) != null && !(controlGridWithBlock(redCube.grids.x, redCube.grids.y - 1, false) instanceof CubeBLACK));
        boolean _e = (controlGridWithBlock(redCube.grids.x + 1, redCube.grids.y + 1, false) != null && !(controlGridWithBlock(redCube.grids.x + 1, redCube.grids.y + 1, false) instanceof CubeBLACK));
        boolean _f = (controlGridWithBlock(redCube.grids.x - 1, redCube.grids.y - 1, false) != null && !(controlGridWithBlock(redCube.grids.x - 1, redCube.grids.y - 1, false) instanceof CubeBLACK));
        boolean _g = (controlGridWithBlock(redCube.grids.x - 1, redCube.grids.y + 1, false) != null && !(controlGridWithBlock(redCube.grids.x - 1, redCube.grids.y + 1, false) instanceof CubeBLACK));
        boolean _h = (controlGridWithBlock(redCube.grids.x + 1, redCube.grids.y - 1, false) != null && !(controlGridWithBlock(redCube.grids.x + 1, redCube.grids.y - 1, false) instanceof CubeBLACK));

        return (_a || _b || _c || _d || _e || _f || _g || _h);
    }

    public static boolean controlCorners() {
        boolean out = true;

        for (Corner c : corners) {
            if (!c.clicked) {
                out = false;
                break;
            }
        }

        return out;
    }

    public static boolean controlGrid(int x, int y) {
        if (redCube.grids.x == x && redCube.grids.y == y && turn != 4) return false;
        else if (blueCube.grids.x == x && blueCube.grids.y == y) return false;
        else {
            for (CubeBLACK blackCube : blackCubes) {
                if (blackCube.grids.x == x && blackCube.grids.y == y) return false;
            }

            return true;
        }
    }

    public static JPanel controlGridWithBlock(int x, int y, boolean forFakeBlock) {
        if (redCube.grids.x == x && redCube.grids.y == y && !forFakeBlock) {
            return redCube;
        } else if (blueCube.grids.x == x && blueCube.grids.y == y) return blueCube;
        else {
            for (CubeBLACK blackCube : blackCubes) {
                if (blackCube.grids.x == x && blackCube.grids.y == y) return blackCube;
            }

            for (Component c : gui.getComponents()) {
                if (c instanceof CubeGREEN) {
                    if (((CubeGREEN) c).grids.x == x && ((CubeGREEN) c).grids.y == y) {
                        return (CubeGREEN) c;
                    }
                }
            }

            if (x >= 0 && x < 5 && y >= 0 && y < 5) return new JPanel();
            return null;
        }
    }


    public static void findBlue() {
        fakeBlockStage = 0;

        searchingBlue = true;
        foundBlue = false;

        setTurn(4);

        setLabelText("Maviye ulaþýn: " + fakeBlockStage + "/" + fakeBlockStageLimit + " - 'O':Ýptal");

        redCube.lastGrids = redCube.grids;

        for (Component c : gui.getComponents()) {
            if (c instanceof CubeRED) gui.remove(c);
        }

        gui.add(fakeBlock);

        setPos(fakeBlock, redCube.grids.x, redCube.grids.y);

        requestSelectFakeBlock();
    }

    public static void tryFind() {
        if (fakeBlockStage == fakeBlockStageLimit || foundBlue) {
            searchingBlue = false;
            continueBlue();

            _level = 1;
        }

        updateLabel();
    }

    public static void requestSelectFakeBlock() {
        setGreenCubes(FAKE_BLOCK, fakeBlock.grids.x, fakeBlock.grids.y);
        selected = FAKE_BLOCK;
    }

    public static void requestSelectRed() {
        if (turn == 0) {
            deSelectAll();
            redCube.selected = true;
            selected = RED_CUBE;
            gui.repaint();

            setGreenCubes(RED_CUBE, 0, 0);

            locationX = redCube.grids.x;
            locationY = redCube.grids.y;
            _level = 4;
            updateLabel();
        } else if (turn == 3) {
            deSelectAll();
            redCube.selected = true;
            selected = RED_CUBE;
            gui.repaint();

            setGreenCubes(DEVELOPER, 0, 0);

            locationX = redCube.grids.x;
            locationY = redCube.grids.y;
            _level = 4;
            updateLabel();
        }
    }

    public static void requestSelectBlue() {

        if (turn == 0) {
            deSelectAll();
            blueCube.selected = true;
            selected = BLUE_CUBE;
            gui.repaint();

            setGreenCubes(BLUE_CUBE, blueCube.grids.x, blueCube.grids.y);

            locationX = blueCube.grids.x;
            locationY = blueCube.grids.y;
            _level = 4;
            updateLabel();
        } else if (turn == 3) {
            deSelectAll();
            blueCube.selected = true;
            selected = BLUE_CUBE;
            gui.repaint();

            setGreenCubes(DEVELOPER, 0, 0);

            locationX = blueCube.grids.x;
            locationY = blueCube.grids.y;
            _level = 4;
            updateLabel();
        } else if (turn == 4 && blueCube.selectableWithFakeBlock) {
            foundBlue = true;
            tryFind();
        }
    }

    public static void requestSelectBlack(CubeBLACK b) {
        if (turn == 1 || turn == 2) {
            deSelectAll();
            b.selected = true;
            selected = BLACK_CUBE;
            selectedBlack = b;
            gui.repaint();

            setGreenCubes(BLACK_CUBE, b.grids.x, b.grids.y);

            locationX = b.grids.x;
            locationY = b.grids.y;
            _level = 4;
            updateLabel();
        } else if (turn == 3) {
            deSelectAll();
            b.selected = true;
            selected = BLACK_CUBE;
            selectedBlack = b;
            gui.repaint();

            setGreenCubes(DEVELOPER, 0, 0);

            locationX = b.grids.x;
            locationY = b.grids.y;
            _level = 4;
            updateLabel();
        }
    }

    public static void deSelectAll() {
        redCube.selected = false;
        blackCubes.forEach(cubeBLACK -> cubeBLACK.selected = false);
        blueCube.selected = false;

        setGreenCubes(NO, 0, 0);

        gui.repaint();
    }

    public static void moveToBlue() {
        findBlue();
        repaintAll();
    }

    public static void continueBlue() {
        System.out.println("continue blue");

        selected = NO;

        _level = 1;
        setLabelText(blackMoves + "/" + blackMovesLimit);

        if (foundBlue) {
            int x = redCube.grids.x;
            int y = redCube.grids.y;

            setPos(redCube, blueCube.grids.x, blueCube.grids.y);
            setPos(blueCube, x, y);

            setGreenCubes(NO, 0, 0);
            deSelectAll();

            for (Corner c : corners) {
                if (c.grids.x == redCube.grids.x && c.grids.y == redCube.grids.y) c.clicked = true;
            }


            for (Component c : gui.getComponents()) {
                if (c instanceof FakeBLOCK) gui.remove(c);
            }

            gui.repaint();
            setTurn(1);
        } else {
            lastSelectedFakeBlock = true;

            setPos(redCube, redCube.grids.x, redCube.grids.y);

            setGreenCubes(NO, 0, 0);
            deSelectAll();

            for (Component c : gui.getComponents()) {
                if (c instanceof FakeBLOCK) gui.remove(c);
            }

            gui.repaint();
            setTurn(1);

            JOptionPane.showMessageDialog(null, "Kýrmýzý maviye ulaþamaz", "Hata", JOptionPane.PLAIN_MESSAGE);
        }
    }
}