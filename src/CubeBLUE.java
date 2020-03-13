import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CubeBLUE extends JPanel {
    Grids grids = new Grids(0,0);
    Grids lastGrids = new Grids(Integer.MAX_VALUE, Integer.MAX_VALUE);
    boolean selected = false;
    boolean selectable = false;
    boolean selectableWithFakeBlock = false;

    CubeBLUE() {
        setSize(50,50);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                operation(e.isControlDown());
                repaint();
            }
        });
    }

    public static void operation(boolean CTRLClicked) {
        if (!CTRLClicked) Main.requestSelectBlue();
        else Main.moveToBlue();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.BLUE);

        if (selected) {
            setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
            g.fillRect(5,5,40,40);
        } else if (!CubeRED.win) {
            if (selectable) {
                setBorder(BorderFactory.createLineBorder(Color.ORANGE, 5));
                g.fillRect(5,5,40,40);
            }
            else g.fillRect(0,0,50,50);
        } else {
            setBorder(BorderFactory.createLineBorder(CubeRED.color, 5));
            g.fillRect(5, 5, 40, 40);
        }
    }
}
