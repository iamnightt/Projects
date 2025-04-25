import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TextEditor extends JFrame implements ActionListener {

    JTextArea textArea;
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem newMenuItem;
    JMenuItem openMenuItem;
    JMenuItem saveMenuItem;
    JMenuItem saveAsMenuItem;
    JMenuItem exitMenuItem;
    JFileChooser fileChooser;
    File currentFile;

    public TextEditor() {
        setTitle("Basic Text Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(textArea), BorderLayout.CENTER); // Use JScrollPane for scrolling

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");

        newMenuItem = new JMenuItem("New");
        openMenuItem = new JMenuItem("Open");
        saveMenuItem = new JMenuItem("Save");
        saveAsMenuItem = new JMenuItem("Save As");
        exitMenuItem = new JMenuItem("Exit");

        newMenuItem.addActionListener(this);
        openMenuItem.addActionListener(this);
        saveMenuItem.addActionListener(this);
        saveAsMenuItem.addActionListener(this);
        exitMenuItem.addActionListener(this);

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator(); // Adds a visual separator in the menu
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
        fileChooser.addChoosableFileFilter(txtFilter);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "New":
                newDocument();
                break;
            case "Open":
                openFile();
                break;
            case "Save":
                saveFile();
                break;
            case "Save As":
                saveAsFile();
                break;
            case "Exit":
                System.exit(0);
                break;
        }
    }

    private void newDocument() {
        textArea.setText("");
        currentFile = null;
        setTitle("Basic Text Editor"); // Reset title when creating a new document
    }

    private void openFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileReader reader = new FileReader(file)) {
                textArea.read(reader, null);
                currentFile = file;
                setTitle("Basic Text Editor - " + currentFile.getName());
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(this, "Could not open file.", "Error", JOptionPane.ERROR_MESSAGE);
                ioException.printStackTrace();
            }
        }
    }

    private void saveFile() {
        if (currentFile != null) {
            try (FileWriter writer = new FileWriter(currentFile)) {
                textArea.write(writer);
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(this, "Could not save file.", "Error", JOptionPane.ERROR_MESSAGE);
                ioException.printStackTrace();
            }
        } else {
            saveAsFile(); // If no file is currently open, behave like "Save As"
        }
    }

    private void saveAsFile() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt"); // Ensure .txt extension
            }
            try (FileWriter writer = new FileWriter(file)) {
                textArea.write(writer);
                currentFile = file;
                setTitle("Basic Text Editor - " + currentFile.getName());
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(this, "Could not save file.", "Error", JOptionPane.ERROR_MESSAGE);
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TextEditor());
    }
}