import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CubeRED extends JPanel {
    boolean selected = false;
    boolean selectable = false;
    boolean hide = false;
    Grids lastGrids = new Grids(Integer.MAX_VALUE, Integer.MAX_VALUE);

    Grids grids = new Grids(0,0);

    static boolean win = false;

    static Color color;

    static Thread t = new Thread(() -> {
        while (true) {
            try {
                color = Color.YELLOW;

                Main.redCube.repaint();

                Thread.sleep(1000);

                color = Color.GREEN;

                Main.redCube.repaint();

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    CubeRED() {
        setSize(50,50);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestSelect();
                repaint();
            }
        });

        repaint();
    }

    void requestSelect() {
        if (!hide) Main.requestSelectRed();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.RED);

        if (!hide) {
            if (selected) {
                setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
                g.fillRect(5,5,40,40);
            } else if (!win) {
                if (selectable) {
                    setBorder(BorderFactory.createLineBorder(Color.ORANGE, 5));
                    g.fillRect(5,5,40,40);
                }
                else g.fillRect(0,0,50,50);
            } else {
                setBorder(BorderFactory.createLineBorder(color, 5));
                g.fillRect(5, 5, 40, 40);
            }
        } else {
            setBorder(null);
        }
    }

}
