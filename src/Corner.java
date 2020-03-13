import javax.swing.*;
import java.awt.*;

public class Corner extends JPanel {
    boolean clicked = false;

    Grids grids = new Grids(0,0);

    Corner() {
        setSize(10,10);

        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (clicked) g.setColor(Color.GREEN);
        else g.setColor(Color.RED);

        g.fillOval(0,0,10,10);
    }
}
