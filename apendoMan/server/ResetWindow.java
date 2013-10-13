/*
 * ResetWindow.java
 *
 * Created on 22 January 2001, 08:23
 */

package apendoMan.server;

/**
 *
 * @author  Kolonel Kustard
 * @version 
 */
public class ResetWindow extends java.awt.Frame {
    BehaviorServer server;
    
    /** Creates new form ResetWindow */
    public ResetWindow(BehaviorServer server) {
        this.server = server;
        initComponents ();
        pack ();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        button1 = new java.awt.Button();
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        }
        );
        
        button1.setFont(new java.awt.Font ("Dialog", 0, 11));
        button1.setLabel("Reset ApendoMan");
        button1.setName("button1");
        button1.setBackground(new java.awt.Color (212, 208, 200));
        button1.setForeground(java.awt.Color.black);
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetApendoMan(evt);
            }
        }
        );
        
        add(button1, java.awt.BorderLayout.CENTER);
        
    }//GEN-END:initComponents

  private void resetApendoMan(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetApendoMan
      server.reset();
  }//GEN-LAST:event_resetApendoMan

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit (0);
    }//GEN-LAST:event_exitForm

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        System.out.println("Cannot be run alone...");
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button button1;
    // End of variables declaration//GEN-END:variables

}