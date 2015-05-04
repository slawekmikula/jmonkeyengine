/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.angelfont;

import com.jme3.gde.core.assets.ProjectAssetManager;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class AngelFontWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private int index;
    private WizardDescriptor wizard;
    private WizardDescriptor.Panel[] panels;

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                new AngelFontWizardPanel1(),
                new AngelFontWizardPanel2()
            };
            String[] steps = createSteps();
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    /**
     * Creates the files based on the info in the WizardDescriptor
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public Set<FileObject> instantiate() throws IOException {

        String fontName = (String) wizard.getProperty("font_name");
        String fileName = (String) wizard.getProperty("file_name");
        int fontSize = (Integer) wizard.getProperty("font_size");
        int imageSize = (Integer) wizard.getProperty("image_size");
        int style = (Integer) wizard.getProperty("font_style");
        int paddingX = (Integer) wizard.getProperty("padding_x");
        int paddingY = (Integer) wizard.getProperty("padding_y");
        int letterSpacing = (Integer) wizard.getProperty("letter_spacing");

        Project project = (Project) wizard.getProperty("project");
        ProjectAssetManager pm = project.getLookup().lookup(ProjectAssetManager.class);
        if (pm == null) {
            Logger.getLogger(AngelFontWizardIterator.class.getName()).log(Level.WARNING, "No ProjectAssetManager found!");
            return Collections.EMPTY_SET;
        }
        AngelFont font = FontCreator.buildFont(fontName, fileName, imageSize, fontSize, style, paddingX, paddingY, letterSpacing, false);
        BufferedImage fontImage = font.getImage();
        ByteBuffer scratch = ByteBuffer.allocateDirect(4 * fontImage.getWidth() * fontImage.getHeight());
        byte[] data = (byte[]) fontImage.getRaster().getDataElements(0, 0,
                fontImage.getWidth(), fontImage.getHeight(), null);
        scratch.clear();
        scratch.put(data);
        scratch.rewind();
        fileName = fileName.replaceAll(" ", "");
        FileObject imageFile;
        FileObject descriptionFile;
        try {
            //create PNG file
            imageFile = FileUtil.createData(pm.getAssetFolder(), "Interface/Fonts/" + fileName + ".png");
            OutputStream out = imageFile.getOutputStream();
            ImageIO.write(fontImage, "PNG", out);
            out.close();
            //create fnt file
            descriptionFile = FileUtil.createData(pm.getAssetFolder(), "Interface/Fonts/" + fileName + ".fnt");
            OutputStreamWriter out2 = new OutputStreamWriter(descriptionFile.getOutputStream());
            out2.write(font.getDescription());
            out2.close();
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
            return Collections.EMPTY_SET;
        }
        Set<FileObject> set = new HashSet<FileObject>();
        set.add(imageFile);
        set.add(descriptionFile);
        return set;
    }

    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }

    public WizardDescriptor.Panel current() {
        return getPanels()[index];
    }

    public String name() {
        return index + 1 + ". from " + getPanels().length;
    }

    public boolean hasNext() {
        return index < getPanels().length - 1;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    public void addChangeListener(ChangeListener l) {
    }

    public void removeChangeListener(ChangeListener l) {
    }

    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then uncomment
    // the following and call when needed: fireChangeEvent();
    /*
    private Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
    public final void addChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.add(l);
    }
    }
    public final void removeChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.remove(l);
    }
    }
    protected final void fireChangeEvent() {
    Iterator<ChangeListener> it;
    synchronized (listeners) {
    it = new HashSet<ChangeListener>(listeners).iterator();
    }
    ChangeEvent ev = new ChangeEvent(this);
    while (it.hasNext()) {
    it.next().stateChanged(ev);
    }
    }
     */
    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
    private String[] createSteps() {
        String[] beforeSteps = null;
        Object prop = wizard.getProperty("WizardPanel_contentData");
        if (prop != null && prop instanceof String[]) {
            beforeSteps = (String[]) prop;
        }

        if (beforeSteps == null) {
            beforeSteps = new String[0];
        }

        String[] res = new String[(beforeSteps.length - 1) + panels.length];
        for (int i = 0; i < res.length; i++) {
            if (i < (beforeSteps.length - 1)) {
                res[i] = beforeSteps[i];
            } else {
                res[i] = panels[i - beforeSteps.length + 1].getComponent().getName();
            }
        }
        return res;
    }
}
