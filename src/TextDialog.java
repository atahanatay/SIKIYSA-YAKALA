import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class TextDialog extends JFrame {
    TextDialog(String title, URL file, Dimension preferredSize) throws IOException {
        BorderLayout layout = new BorderLayout(5,5);
        setLayout(layout);
        setTitle(title);

        JEditorPane textArea = new JEditorPane();
        textArea.setPage(file);
        textArea.setEditable(false);

        JScrollPane pane = new JScrollPane(textArea);
        add(pane, BorderLayout.CENTER);

        pane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5,5,5,5),
                BorderFactory.createLineBorder(Color.BLACK,1)));

        setAlwaysOnTop(true);

        setPreferredSize(preferredSize);
        pack();
        setVisible(true);
    }
}
