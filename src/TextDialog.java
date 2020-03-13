import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class TextDialog extends JFrame {
    TextDialog(String title, File file, Dimension preferredSize) throws FileNotFoundException {
        BorderLayout layout = new BorderLayout(5,5);
        setLayout(layout);

        setTitle(title);

        Scanner s = new Scanner(file);

        StringBuilder read = new StringBuilder();

        while (s.hasNextLine()) {
            read.append(s.nextLine() + "\n");
        }

        s.close();

        JTextArea textArea = new JTextArea(read.toString());
        JScrollPane pane = new JScrollPane(textArea);
        add(pane, BorderLayout.CENTER);

        pane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5,5,5,5),
                BorderFactory.createLineBorder(Color.BLACK,1)));

        setAlwaysOnTop(true);

        textArea.setEditable(false);

        setPreferredSize(preferredSize);
        pack();
        setVisible(true);

    }
}
