package org.asgs.editpad;
 
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
 
/**
 * Model object representing a document tab in a <code>JTabbedPane</code>.
 * ------------------------------
 * <b>No copyrights reserved.</b>
 * ------------------------------
 *  Do anything with this code!
 * @author asgs
 *
 */
public class DocumentTab {
 
        private JTextArea textEditor;
        
        private JScrollPane scrollPane;
        
        private String title;
        
        /** Creates a new <code>DocumentTab</code> with a given Title.
         * @param title
         */
        DocumentTab(String title) {
                textEditor = new JTextArea();
                textEditor.setLineWrap(true);
                textEditor.setWrapStyleWord(true);              
                scrollPane = new JScrollPane(textEditor, 
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                setTitle(title);
        }
 
        /**
         * @return the scrollPane
         */
        public JScrollPane getScrollPane() {
                return scrollPane;
        }
 
        /**
         * @param scrollPane the scrollPane to set
         */
        public void setScrollPane(JScrollPane scrollPane) {
                this.scrollPane = scrollPane;
        }
 
        /**
         * @return the textEditor
         */
        public JTextArea getTextEditor() {
                return textEditor;
        }
 
        /**
         * @param textEditor the textEditor to set
         */
        public void setTextEditor(JTextArea textEditor) {
                this.textEditor = textEditor;
        }
 
        /**
         * @return the title
         */
        public String getTitle() {
                return title;
        }
 
        /**
         * @param title the title to set
         */
        public void setTitle(String title) {
                this.title = title;
        }
}
