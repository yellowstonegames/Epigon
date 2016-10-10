package squidpony.editor;

import java.lang.reflect.InvocationTargetException;

/**
 * This class prepares and launches the data editor.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class Editor {

    private EditorFrame frame;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws InterruptedException, InvocationTargetException {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        new Editor().go();
    }

    private void go() throws InterruptedException, InvocationTargetException {
        

        /* Create and display the form */
        java.awt.EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame = new EditorFrame();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
