import javax.swing.*;
import java.awt.*;

public class Lines extends JPanel {
    Lines() {
        setSize(500,500);
        setLayout(null);

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                JLabel label = new JLabel(x + "x" + y);
                label.setFont(new Font("Monospaced", Font.ITALIC, 15));
                label.setSize(100,100);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setLocation(x*100, (y*100) + 40);
                add(label);
            }
        }
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
