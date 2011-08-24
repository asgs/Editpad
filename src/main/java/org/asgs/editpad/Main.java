package org.asgs.editpad;
 
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
 
/** This is a very basic text editor.
 *  ------------------------------
 *  <b>No copyrights reserved.</b>
 *  ------------------------------
 *  Do anything with this code!  
 * @author asgs
 */
public class Main implements ActionListener {
 
        /**
         * The Main Editor Window.
         */
        private JFrame window;
        
        /**
         * The text editing window where user types/views the document.
         */
        private JTextArea textEditor;
        
        /**
         * Find dialog used to search text inside a document.
         */
        private JDialog findDialog;
        
        private List<Integer> searchIndices;
        
        private int currentSearchIndex;
        
        private boolean directionChanged;
        
        private boolean lastDirection = true;
        
        private JTabbedPane tabbedPane;
        
        private Container contentPane;
        
        private JScrollPane scrollPane;
        
        private int tabIndex = 0;
        
        private Map<DocumentTab, String> tabs;
        
        /** Main method.
         * @param args String[]
         */
        public static void main(String[] args) {
                final Main editor = new Main();
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                                editor.displayUI();
                        }
                });
        }
 
        /**
         * Prepares the initial UI of the editor.
         */
        private void displayUI() {
                tabs = new HashMap<DocumentTab, String>();
                window = new JFrame(Constants.APP_TITLE);
                window.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
                window.setVisible(true);
                window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                tabbedPane = new JTabbedPane();
                textEditor = new JTextArea();
                textEditor.setLineWrap(true);
                textEditor.setWrapStyleWord(true);
                contentPane = window.getContentPane();
                scrollPane = new JScrollPane(textEditor, 
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                contentPane.add(tabbedPane);
                tabbedPane.addTab(Constants.DOCUMENT_NAME, scrollPane);
                //tabbedPane.setPreferredSize(new Dimension(640,480));
                
                GridBagLayout layout = new GridBagLayout();
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.weightx = 1;
                constraints.weighty = 1;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                constraints.fill = GridBagConstraints.BOTH;
                
                JMenuBar menuBar = new JMenuBar();
                
                JMenu fileMenu = new JMenu(Constants.FILE);
                JMenu editMenu = new JMenu(Constants.EDIT);
                JMenu helpMenu = new JMenu(Constants.HELP);
                
                JMenuItem newTabMenuItem = new JMenuItem(Constants.NEW_TAB);
                JMenuItem openMenuItem = new JMenuItem(Constants.OPEN);
                JMenuItem saveMenuItem = new JMenuItem(Constants.SAVE);
                JMenuItem quitMenuItem = new JMenuItem(Constants.QUIT);
                
                JMenuItem helpMenuItem = new JMenuItem(Constants.HELP);
                JMenuItem documentationMenuItem = new JMenuItem(Constants.DOCS);
                JMenuItem aboutMenuItem = new JMenuItem(Constants.ABOUT);               
                
                newTabMenuItem.setActionCommand(Constants.NEW_TAB);
                newTabMenuItem.addActionListener(this);                         
                fileMenu.add(newTabMenuItem);
                
                openMenuItem.setMnemonic(KeyEvent.VK_O);
                openMenuItem.setActionCommand(Constants.OPEN);
                openMenuItem.addActionListener(this);
                fileMenu.add(openMenuItem);
                
                saveMenuItem.setMnemonic(KeyEvent.VK_S);
                saveMenuItem.setActionCommand(Constants.SAVE);
                saveMenuItem.addActionListener(this);
                fileMenu.add(saveMenuItem);             
                
                quitMenuItem.setMnemonic(KeyEvent.VK_Q);
                quitMenuItem.setActionCommand(Constants.QUIT);
                quitMenuItem.addActionListener(this);
                fileMenu.add(quitMenuItem);
                
                menuBar.add(fileMenu);
                
                JMenuItem findMenuItem = new JMenuItem(Constants.FIND);         
                findMenuItem.setActionCommand(Constants.FIND);
                findMenuItem.addActionListener(this);
                editMenu.add(findMenuItem);
                
                menuBar.add(editMenu);
                
                helpMenu.add(helpMenuItem);
                helpMenuItem.setMnemonic(KeyEvent.VK_H);
                helpMenu.add(documentationMenuItem);
                documentationMenuItem.setMnemonic(KeyEvent.VK_D);
                aboutMenuItem.setActionCommand(Constants.ABOUT);
                aboutMenuItem.addActionListener(this);
                helpMenu.add(aboutMenuItem);
                menuBar.add(helpMenu);
                
                window.setJMenuBar(menuBar);
                layout.setConstraints(tabbedPane, constraints);
                window.setLayout(layout);
                window.setVisible(true);
                
                try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
 
        /* (non-Javadoc) The Centralized place for handling all events.
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent event) {
                if (Constants.FIND.equals(event.getActionCommand())) {
                        //System.out.println("Inside Find action");
                        if (findDialog != null) {
                                unhideFindDialog();
                        } else {
                                createAndShowFindDialog();
                        }
                } else if (Constants.SEARCH.equals(event.getActionCommand())) {
                        JTextField textField = (JTextField)findDialog.getContentPane().getComponent(1);
                        JRadioButton forward = (JRadioButton)findDialog.getContentPane().getComponent(3);
 
                        boolean isForward = forward.isSelected();
                        String searchString = textField.getText();
                        String editorText = textEditor.getText();
 
                        if (searchString == null || "".equals(searchString.trim())) {
                                JOptionPane.showMessageDialog(findDialog, "Please enter a word to search!");
                                return;
                        }
 
                        directionChanged = lastDirection ^ isForward;
                        lastDirection = isForward;
 
                        if (searchIndices == null) {
                                searchIndices = new ArrayList<Integer>();
                                int index = 0;
                                while (editorText.indexOf(searchString, index + searchString.length()) != -1) {
                                        index = editorText.indexOf(searchString, index + searchString.length());
                                        searchIndices.add(index);
                                        System.out.println("Index:" + index);
                                }
                        }
                        if (currentSearchIndex >= searchIndices.size() || currentSearchIndex < 0) {
                                if (directionChanged && currentSearchIndex >= searchIndices.size()) {
                                        currentSearchIndex = searchIndices.size() - 2;
                                        System.out.println("Reducing the searchIndex by 2");
                                } else if (directionChanged && currentSearchIndex < 0) {
                                        currentSearchIndex = 1;
                                        System.out.println("Reducing the searchIndex to 1");
                                } else {
                                        System.out.println("currentSearchIndex:" + currentSearchIndex);
                                        if (searchIndices.size() > 0) {
                                                JOptionPane.showMessageDialog(findDialog, "No more search!");
                                        } else {
                                                JOptionPane.showMessageDialog(findDialog, "Keyword not found!");
                                        }
                                        return;
                                }
                        } 
                        if (isForward) {
                                System.out.println("Searching in forward direction.");
                                int contentIndex = searchIndices.get(currentSearchIndex);
                                textEditor.select(contentIndex, contentIndex + searchString.length());
                                currentSearchIndex++;
                        } else {
                                System.out.println("Searching in reverse direction.");
                                int contentIndex = searchIndices.get(currentSearchIndex);
                                textEditor.select(contentIndex, contentIndex + searchString.length());
                                currentSearchIndex--;
                        }
                } else if (Constants.NEW_TAB.equals(event.getActionCommand())) {
                        DocumentTab documentTab = createNewTab();
                        tabs.put(documentTab, documentTab.getTitle());
                } if (event.getActionCommand().equals(Constants.NEW)) {
                        String fileContents = textEditor.getText(); 
                        if (hasText(fileContents)) {    
                                int choice = JOptionPane.showConfirmDialog(window, "Save this file before creating a new one?", "Save file?", JOptionPane.YES_NO_OPTION);
                                if (choice == JOptionPane.YES_OPTION) {
                                                showFileSavePrompt(fileContents);
                                } else {
                                        textEditor.setText("");                         
                                }
                        } else {
                                textEditor.setText("");
                        }
                } else if (event.getActionCommand().equals(Constants.OPEN)) {
                        System.out.println("Open command");
                        JFileChooser fileChooser = new JFileChooser();
                        /*FileNameExtensionFilter fileExtensionFilter = new FileNameExtensionFilter("Text Files", "txt");
                        fileChooser.setFileFilter(fileExtensionFilter);*/
                        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(window)) {
                                File file = fileChooser.getSelectedFile();
                                System.out.println("File selected" + file.getName());
                                textEditor.setText(openFile(file));
                                textEditor.setCaretPosition(0);
                        }
                        //System.out.println("File not selected!");
                } else if (event.getActionCommand().equals(Constants.SAVE)) {
                        // To-DO Save the file.
                        showFileSavePrompt(textEditor.getText());
                } else if (event.getActionCommand().equals(Constants.QUIT) && 
                                        JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(window, "Are you sure you want to exit?", "Quit!", JOptionPane.YES_NO_OPTION)) {
                        window.dispose();
                        System.exit(0);
                        
                } else if (event.getActionCommand().equals(Constants.ABOUT)) {
                                JOptionPane.showMessageDialog(window, "Developed by asgs.\nNo Copyrights reserved.", "About Editpad", JOptionPane.INFORMATION_MESSAGE);
                } 
        }
        
        private boolean hasText(String source) {
                return source != null && !"".equals(source.trim()) ? true : false;
        }
        
        private void showFileSavePrompt(String fileContents) {
                // Show a save dialog to the user to save the file contents.
                JFileChooser fileChooser = new JFileChooser();
                /*FileNameExtensionFilter fileExtensionFilter = new FileNameExtensionFilter("Text Files"        , "txt");
                fileChooser.setFileFilter(fileExtensionFilter);*/
                if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(window)) {
                        // read the file name and call the saveFile method.
                        String fileName = fileChooser.getSelectedFile().getName();
                        if (fileName != null && !fileName.endsWith(".txt")) {
                                fileName = fileName + ".txt";
                        }
                        File file = new File(fileChooser.getSelectedFile().getParent() + File.separator + fileName);
                        saveFile(file, fileContents);
                }
        }
        
        private void saveFile(File file, String fileContents) {
                if(!file.exists()) {
                        // Write mode
                        try {
                                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                                writer.write(fileContents, 0, fileContents.length());
                                writer.close();
                        } catch (IOException exception) {
                                //
                                JOptionPane.showMessageDialog(window, "Error saving the file." + exception.getMessage() , "Error saving file!", JOptionPane.ERROR_MESSAGE);
                        }
                        
                } else {
                        // Append mode
                        try {
                                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                                writer.append(fileContents);
                                writer.close();
                        } catch (IOException exception) {
                                //
                                JOptionPane.showMessageDialog(window, "Error saving the file." + exception.getMessage() , "Error saving file!", JOptionPane.ERROR_MESSAGE);
                        }
                }
        }
        
        private String openFile(File file) {
                StringBuilder builder = null;
                if(file.exists()) {
                        // Write mode
                        try {
                                builder = new StringBuilder();
                                BufferedReader reader = new BufferedReader(new FileReader(file));
                                char[] characters = new char[4096];                             
                                int i;
                                while ((i = reader.read(characters, 0, characters.length)) != -1) {
                                        builder.append(characters);
                                }
                                reader.close();                         
                        } catch (IOException exception) {
                                //
                                JOptionPane.showMessageDialog(window, "Error saving the file." + exception.getMessage() , "Error saving file!", JOptionPane.ERROR_MESSAGE);
                        }               
                }
                return builder != null ? builder.toString().trim() : "";
        }
        
        /**
         * Creates and displays a Find Dialog window to search content inside
         * a document.
         */
        private void createAndShowFindDialog() {
                findDialog = new JDialog(window, "Search");
                findDialog.setSize(Constants.DIALOG_WIDTH, Constants.DIALOG_HEIGHT);
                findDialog.setLocationByPlatform(true);
                findDialog.setResizable(false);
                Container findDialogContentPane = findDialog.getContentPane();
                JLabel findLabel = new JLabel(Constants.FIND);
                JTextField textField = new JTextField(15);
                JLabel directionLabel = new JLabel(Constants.DIRECTION);
                ButtonGroup buttons = new ButtonGroup();
                JRadioButton forward = new JRadioButton(Constants.FORWARD, true);
                JRadioButton backward = new JRadioButton(Constants.BACKWARD);
                buttons.add(forward);
                buttons.add(backward);
 
                JCheckBox checkBox = new JCheckBox(Constants.CASE_INSENSITIVE);
 
                JButton searchButton = new JButton(Constants.SEARCH);
                searchButton.setActionCommand(Constants.SEARCH);
                searchButton.addActionListener(this);                           
 
                GridBagLayout layout = new GridBagLayout();
                findDialog.setLayout(layout);
                GridBagConstraints inputFieldConstraints = new GridBagConstraints();
                GridBagConstraints labelConstraints = new GridBagConstraints();
 
                labelConstraints.weightx = 0.0;
                labelConstraints.gridwidth = 1;
                labelConstraints.anchor = GridBagConstraints.WEST;
                layout.addLayoutComponent(findLabel, labelConstraints);
 
                inputFieldConstraints.weightx = 1.0;
                inputFieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
                inputFieldConstraints.anchor = GridBagConstraints.WEST;
                inputFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
                layout.addLayoutComponent(textField, inputFieldConstraints);
 
                labelConstraints.weightx = 0.0;
                labelConstraints.gridwidth = 1;
                labelConstraints.anchor = GridBagConstraints.WEST;                              
                layout.addLayoutComponent(directionLabel, labelConstraints);
 
                inputFieldConstraints.weightx = 0.0;
                inputFieldConstraints.gridwidth = 1;
                inputFieldConstraints.anchor = GridBagConstraints.WEST;
                layout.addLayoutComponent(forward, inputFieldConstraints);
 
                inputFieldConstraints.weightx = 0.0;
                inputFieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
                inputFieldConstraints.anchor = GridBagConstraints.WEST;
                layout.addLayoutComponent(backward, inputFieldConstraints);
 
                inputFieldConstraints.weightx = 0.0;
                inputFieldConstraints.gridwidth = 1;
                inputFieldConstraints.anchor = GridBagConstraints.WEST;
 
                layout.addLayoutComponent(checkBox, inputFieldConstraints);
 
                inputFieldConstraints.weightx = 0.0;
                inputFieldConstraints.gridwidth = GridBagConstraints.REMAINDER;
                inputFieldConstraints.anchor = GridBagConstraints.EAST;
                layout.addLayoutComponent(searchButton, inputFieldConstraints);
 
                findDialogContentPane.add(findLabel);
                findDialogContentPane.add(textField);
                findDialogContentPane.add(directionLabel);
                findDialogContentPane.add(forward);
                findDialogContentPane.add(backward);
                findDialogContentPane.add(checkBox);
                findDialogContentPane.add(searchButton);                                
                
                //findDialog.pack();
                findDialog.setVisible(true);
        }
        
        /**
         * Unhides the Find Dialog box and resets the <code>searchIndices</code>
         * and the <code>currentSearchIndex</code>
         */
        private void unhideFindDialog() {
                findDialog.setVisible(true);
                searchIndices = null;
                currentSearchIndex = 0; 
        }
 
        /**
         * Creates and adds a new tab to the <code>JTabbedPane</code> and sets
         * the focus on this new tab.
         */
        private DocumentTab createNewTab() {
                tabIndex++;
                String title = Constants.DOCUMENT_NAME + tabIndex;
                DocumentTab documentTab = new DocumentTab(title);
                tabbedPane.addTab(documentTab.getTitle(), documentTab.getScrollPane());
                tabbedPane.setSelectedIndex(tabIndex);
                return documentTab;
        }
        
        private void closeTab(int tabIndex) {
                
                tabbedPane.remove(tabIndex);            
        }
}
