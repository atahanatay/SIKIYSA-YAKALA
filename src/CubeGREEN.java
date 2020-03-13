import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CubeGREEN extends JPanel {
    boolean activated = true;
    Grids grids = new Grids(0,0);

    CubeGREEN() {
        setSize(50,50);
        repaint();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                _move();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (activated) g.setColor(Color.GREEN);
        else g.setColor(Color.GRAY);
        g.fillRect(0,0,50,50);
    }

    void _move() {
        if (activated) Main.moveCube(this);
        repaint();
    }
}
