/*
 *  Copyright (c) 2009-2010 jMonkeyEngine
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 *  * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.materials;

import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.scene.SceneApplication;
import com.jme3.gde.core.scene.SceneRequest;
import com.jme3.gde.core.sceneexplorer.SceneExplorerTopComponent;
import com.jme3.gde.core.properties.SceneExplorerProperty;
import com.jme3.gde.core.properties.SceneExplorerPropertyEditor;
import com.jme3.material.Material;
import com.jme3.asset.MaterialKey;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author normenhansen
 */
@org.openide.util.lookup.ServiceProvider(service = SceneExplorerPropertyEditor.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class MaterialPropertyEditor implements PropertyEditor, SceneExplorerPropertyEditor {

    private LinkedList<PropertyChangeListener> listeners = new LinkedList<PropertyChangeListener>();
    private Material material = new Material();

    public void setValue(Object value) {
        if (value instanceof Material) {
            material = (Material) value;
        }
    }

    public Object getValue() {
        return material;
    }

    public boolean isPaintable() {
        return false;
    }

    public void paintValue(Graphics gfx, Rectangle box) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getJavaInitializationString() {
        return null;
    }

    public String getAsText() {
        String name = material.getAssetName();
        if (name == null) {
            name = "create j3m file";
        }
        return name;
    }

    public void setAsText(final String text) throws IllegalArgumentException {
        if ("create j3m file".equals(text)) {
            try {
                Node geom = SceneExplorerTopComponent.findInstance().getLastSelected();
                assert (geom != null);
                ProjectAssetManager pm = geom.getLookup().lookup(ProjectAssetManager.class);
                assert (pm != null);
                DataObject obj = geom.getLookup().lookup(DataObject.class);
                assert (obj != null);
                FileObject currentFile = obj.getPrimaryFile();
                FileObject currentFolder = pm.getAssetFolder().getFileObject("Materials/Generated");
                if (currentFolder == null) {
                    currentFolder = FileUtil.createFolder(pm.getAssetFolder(), "Materials/Generated");
                }
                int i = 0;
                String newFileName = currentFile.getName() + "-" + sanitizeFileName(geom.getName());
                FileObject newFile = currentFolder.getFileObject(newFileName, "j3m");
                while (newFile != null) {
                    i++;
                    newFileName = currentFile.getName() + "-" + sanitizeFileName(geom.getName()) + "-" + i;
                    newFile = currentFolder.getFileObject(newFileName, "j3m");
                }
                newFile = currentFolder.createData(newFileName, "j3m");
                EditableMaterialFile properties = new EditableMaterialFile(newFile, pm);
                material.setKey(new MaterialKey(pm.getRelativeAssetPath(newFile.getPath())));
                properties.setAsMaterial(material);
                currentFolder.refresh();
                applyMaterial(material.getAssetName());
            } catch (Exception ex) {

                Exceptions.printStackTrace(ex);
                return;
            }
        } else {
            applyMaterial(text);
        }
    }

    private String sanitizeFileName(String input) {
        return input.replaceAll("[^A-Za-z0-9 ]", "_");
    }

    private void applyMaterial(final String text) {
        try {
            Material old = material;
            SceneApplication.getApplication().enqueue(new Callable<Void>() {
                public Void call() throws Exception {
                    SceneRequest request = SceneApplication.getApplication().getCurrentSceneRequest();
                    request.getManager().deleteFromCache(new MaterialKey(text));
                    Material localMaterial = request.getManager().loadMaterial(text);
                    if (localMaterial != null) {
                        material = localMaterial;
                    }
                    return null;
                }
            }).get();
            notifyListeners(old, material);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public String[] getTags() {
        SceneRequest request = SceneApplication.getApplication().getCurrentSceneRequest();
        if (request == null) {
            return new String[]{};
        }
        if (material.getAssetName() == null) {
            String[] matsUnsorted = request.getManager().getMaterials();
            List<String> matList = Arrays.asList(matsUnsorted);
            Collections.sort(matList);
            String[] materials = matList.toArray(new String[0]);
            String[] mats = new String[materials.length + 1];
            mats[0] = ("create j3m file");
            for (int i = 0; i < materials.length; i++) {
                String string = materials[i];
                mats[i + 1] = string;
            }
            return mats;
        } else {
            String[] matsUnsorted = request.getManager().getMaterials();
            List<String> matList = Arrays.asList(matsUnsorted);
            Collections.sort(matList);
            return matList.toArray(new String[0]);
        }
    }

    public Component getCustomEditor() {
        ProjectAssetManager currentProjectAssetManager = null;

        if (currentProjectAssetManager == null) {
            currentProjectAssetManager = (ProjectAssetManager) SceneApplication.getApplication().getAssetManager();
        }
        MaterialBrowser materialBrowser = new MaterialBrowser(null, true, currentProjectAssetManager, this);
        return materialBrowser;


    }

    public boolean supportsCustomEditor() {
        return true;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Material before, Material after) {
        for (Iterator<PropertyChangeListener> it = listeners.iterator(); it.hasNext();) {
            PropertyChangeListener propertyChangeListener = it.next();
            //TODO: check what the "programmatic name" is supposed to be here.. for now its Quaternion
            propertyChangeListener.propertyChange(new PropertyChangeEvent(this, "Material", before, after));
        }
    }

    public void setEditor(Class valueType, SceneExplorerProperty prop) {
        if (valueType == Material.class) {
            prop.setPropertyEditorClass(MaterialPropertyEditor.class);
        }
    }
}
