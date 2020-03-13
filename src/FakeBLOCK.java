import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FakeBLOCK extends JPanel {
    Grids grids = new Grids(0,0);
    boolean finished = false;
    boolean returned = false;

    FakeBLOCK() {
        setSize(50,50);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Main.requestSelectFakeBlock();
                repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.YELLOW);
        g.fillRect(0,0,50,50);
    }
}
