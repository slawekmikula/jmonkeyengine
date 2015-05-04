/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.materialdefinition.editor;

import com.jme3.gde.materialdefinition.icons.Icons;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.EditorKit;
import org.openide.awt.UndoRedo;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.Exceptions;

/**
 *
 * @author Nehon
 */
public class ShaderEditPanel extends JPanel {

    private DataObject currentDataObject = null;
    private MatDefEditorlElement parent = null;
    private UndoRedo.Manager undoRedoManager;
    private final String MIME = "text/x-glsl";

    /**
     * Creates new form ShaderEditPanel
     */
    public ShaderEditPanel() {
        initComponents();
        
        EditorKit ek = CloneableEditorSupport.getEditorKit(MIME);
        shaderEditorPane.setEditorKit(ek);
        shaderEditorPane.setContentType(MIME);

        shaderEditorPane.addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    saveCurrent();
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });
    }

    public void setParent(MatDefEditorlElement parent) {
        this.parent = parent;
        undoRedoManager = (UndoRedo.Manager) parent.getUndoRedo();
    }

    public void setFiles(String title, NodePanel.NodeType type, List<FileObject> fos, final Map<String, String> readOnlyFiles) {

        
        headerText.setText(title);
        headerText.setIcon(Icons.getIconForShaderType(type));
        boolean firstItem = true;
        for (Component component : buttonPanel.getComponents()) {
            buttonGroup1.remove((JToggleButton) component);
        }
        buttonPanel.removeAll();
        buttonPanel.repaint();

        for (FileObject fo : fos) {
            final Tab b = new Tab();
            b.setText(fo.getNameExt());
            buttonGroup1.add(b);
            try {
                b.dataObject = DataObject.find(fo);
                b.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveCurrent();
                        try {
                            switchEditableDoc(b);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                });
                if (firstItem) {
                    switchEditableDoc(b);
                    b.setSelected(true);
                    firstItem = false;
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            buttonPanel.add(b);
        }

        for (String key : readOnlyFiles.keySet()) {
            final Tab b = new Tab();
            b.setText(key);
            buttonGroup1.add(b);
            final String theKey = key;
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    switchReadOnlyDoc(readOnlyFiles.get(theKey));
                }

            });
            if (firstItem) {
                switchReadOnlyDoc(readOnlyFiles.get(key));
                b.setSelected(true);
                firstItem = false;
            }
            buttonPanel.add(b);
        }

    }

    private void switchEditableDoc(Tab b) throws IOException {
        if(currentDataObject != null){
            currentDataObject.getLookup().lookup(EditorCookie.class).close();
        }        
        shaderEditorPane.setDocument(b.dataObject.getLookup().lookup(EditorCookie.class).openDocument());
        undoRedoManager.discardAllEdits();
        shaderEditorPane.getDocument().addUndoableEditListener(undoRedoManager);
        shaderEditorPane.setEditable(true);
        currentDataObject = b.dataObject;
    }

    private void switchReadOnlyDoc(String text) {
        if(currentDataObject != null){
            currentDataObject.getLookup().lookup(EditorCookie.class).close();
        }
        shaderEditorPane.setText(text);
        undoRedoManager.discardAllEdits();
        shaderEditorPane.setEditable(false);
        currentDataObject = null;
    }

    public void saveCurrent() {
        if (currentDataObject != null && currentDataObject.isModified()) {
            FileLock lock = null;

            try {
                currentDataObject.getLookup().lookup(EditorCookie.class).saveDocument();
                currentDataObject.setModified(false);
                if (currentDataObject.getPrimaryFile().getExt().equalsIgnoreCase("j3sn")) {
                    parent.reload();
                }
                parent.refresh();
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                if (lock != null) {
                    lock.releaseLock();
                }
            }
        }
    }

    private class Tab extends JToggleButton {

        DataObject dataObject;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        shaderEditorPane = new javax.swing.JEditorPane();
        header = new javax.swing.JPanel();
        headerText = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();
        buttonPanel = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();

        jScrollPane1.setMinimumSize(new java.awt.Dimension(0, 0));

        shaderEditorPane.setBorder(null);
        shaderEditorPane.setText(org.openide.util.NbBundle.getMessage(ShaderEditPanel.class, "ShaderEditPanel.shaderEditorPane.text")); // NOI18N
        jScrollPane1.setViewportView(shaderEditorPane);

        header.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        headerText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        headerText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        headerText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jme3/gde/materialdefinition/icons/fragment.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(headerText, org.openide.util.NbBundle.getMessage(ShaderEditPanel.class, "ShaderEditPanel.headerText.text")); // NOI18N

        closeButton.setFont(new java.awt.Font("Tahoma", 1, 8)); // NOI18N
        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jme3/gde/materialdefinition/icons/out.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(closeButton, org.openide.util.NbBundle.getMessage(ShaderEditPanel.class, "ShaderEditPanel.closeButton.text")); // NOI18N
        closeButton.setToolTipText(org.openide.util.NbBundle.getMessage(ShaderEditPanel.class, "ShaderEditPanel.closeButton.toolTipText")); // NOI18N
        closeButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(header);
        header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addComponent(headerText, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(headerText, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        buttonPanel.setAlignmentX(0.0F);
        buttonPanel.setAlignmentY(0.0F);
        buttonPanel.setPreferredSize(new java.awt.Dimension(73, 29));
        buttonPanel.setLayout(new javax.swing.BoxLayout(buttonPanel, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(jToggleButton1, org.openide.util.NbBundle.getMessage(ShaderEditPanel.class, "ShaderEditPanel.jToggleButton1.text")); // NOI18N
        buttonPanel.add(jToggleButton1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
        shaderEditorPane.getDocument().removeUndoableEditListener(undoRedoManager);
        saveCurrent();


    }//GEN-LAST:event_closeButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel header;
    private javax.swing.JLabel headerText;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JEditorPane shaderEditorPane;
    // End of variables declaration//GEN-END:variables
}
