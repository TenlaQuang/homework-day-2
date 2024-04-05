package Views;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import Controllers.TextEditorController;

import java.awt.*;
import java.io.File;
import java.util.List;

public class TextEditor extends JFrame {
    private JTextArea textArea;
    private JTree fileTree;

    public TextEditor() {
        super("Text Editor");

        textArea = new JTextArea();
        textArea.setBackground(Color.DARK_GRAY);
        textArea.setForeground(Color.WHITE);
        JScrollPane textScrollPane = new JScrollPane(textArea);

        fileTree = new JTree();
        fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane fileScrollPane = new JScrollPane(fileTree);

        JButton openButton = new JButton("Open");
        openButton.setBackground(Color.LIGHT_GRAY);
        openButton.setFocusable(false);
        openButton.addActionListener(e -> openFile());

        JButton saveButton = new JButton("Save");
        saveButton.setBackground(Color.LIGHT_GRAY);
        saveButton.setFocusable(false);
        saveButton.addActionListener(e -> saveFile());

        JButton browseButton = new JButton("Browse");
        browseButton.setBackground(Color.LIGHT_GRAY);
        browseButton.setFocusable(false);
        browseButton.addActionListener(e -> browseDirectory());


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(openButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(browseButton);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(fileScrollPane, BorderLayout.WEST);
        contentPane.add(textScrollPane, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile.isFile()) {
                List<String> content = TextEditorController.loadFromFile(selectedFile.getAbsolutePath());
                if (content != null) {
                    StringBuilder sb = new StringBuilder();
                    for (String line : content) {
                        sb.append(line).append("\n");
                    }
                    textArea.setText(sb.toString());
                }
            } else if (selectedFile.isDirectory()) {
                DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(selectedFile);
                populateTree(selectedFile, rootNode);
                fileTree.setModel(new DefaultTreeModel(rootNode));
            }
        }
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getAbsolutePath();
            String content = textArea.getText();
            try {
                TextEditorController.saveToFile(fileName, content);
                JOptionPane.showMessageDialog(this, "File saved successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void browseDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(selectedDirectory);
            populateTree(selectedDirectory, rootNode);
            fileTree.setModel(new DefaultTreeModel(rootNode));
        }
    }

    private void populateTree(File directory, DefaultMutableTreeNode parentNode) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
                parentNode.add(node);
                if (file.isDirectory()) {
                    populateTree(file, node);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TextEditor textView = new TextEditor();
            textView.setVisible(true);
        });
    }
}