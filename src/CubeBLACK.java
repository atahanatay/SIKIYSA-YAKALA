import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CubeBLACK extends JPanel {
    static int last = 0;
    int num;
    Grids grids = new Grids(0,0);
    Grids lastGrids = new Grids(Integer.MAX_VALUE, Integer.MAX_VALUE);
    boolean selected;
    boolean selectable;
    boolean fSelectable;
    boolean locked;

    static boolean win = false;

    static Color color;

    static Thread t = new Thread(() -> {
        while (true) {
            try {
                color = Color.YELLOW;

                Main.blackCubes.forEach(Component::repaint);

                Thread.sleep(1000);

                color = Color.GREEN;

                Main.blackCubes.forEach(Component::repaint);

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    CubeBLACK() {
        setSize(50,50);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectBlack();
            }
        });

        num = last;
        last++;
    }

    void selectBlack() {
        if (!locked) Main.requestSelectBlack(this);
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.BLACK);

        if (selected) {
            setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
            g.fillRect(5, 5, 40, 40);
        } else if (!win) {
            if (locked) {
                setBorder(BorderFactory.createLineBorder(Color.RED, 5));
                g.fillRect(5, 5, 40, 40);
            } else if (fSelectable) {
                setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));
                g.fillRect(5, 5, 40, 40);
            } else if (selectable) {
                setBorder(BorderFactory.createLineBorder(Color.ORANGE, 5));
                g.fillRect(5, 5, 40, 40);
            } else g.fillRect(0, 0, 50, 50);
        } else {
            setBorder(BorderFactory.createLineBorder(color, 5));
            g.fillRect(5, 5, 40, 40);
        }
    }
}
