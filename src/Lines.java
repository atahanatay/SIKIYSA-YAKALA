import javax.swing.*;
import java.awt.*;

public class Lines extends JPanel {
    Lines() {
        setSize(500,500);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int i = 0; i < 4; i++) {
            g.drawLine((i * 100) + 100, 0, (i * 100) + 100, 500);
            g.drawLine(0, (i * 100) + 100, 500, (i * 100) + 100);
        }
    }
}
