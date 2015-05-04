/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.ogrexml;

import com.jme3.asset.ModelKey;
import com.jme3.gde.core.assets.ProjectAssetManager;
import com.jme3.gde.core.assets.SpatialAssetDataObject;
import com.jme3.scene.Spatial;
import com.jme3.scene.plugins.ogre.OgreMeshKey;
import java.io.IOException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Exceptions;

@MIMEResolver.NamespaceRegistration(
        displayName = "OgreXML File",
        mimeType = "text/ogrexml+xml",
        elementName = "mesh",
        checkedExtension = {"xml", "meshxml"}
)
@DataObject.Registration(displayName = "OgreXML File", mimeType = "text/ogrexml+xml", iconBase = "com/jme3/gde/ogrexml/ogre-logo.png")
@ActionReferences(value = {
    @ActionReference(id =
    @ActionID(category = "jMonkeyPlatform", id = "com.jme3.gde.core.assets.actions.ConvertModel"), path = "Loaders/text/ogrexml+xml/Actions", position = 10),
    @ActionReference(id =
    @ActionID(category = "jMonkeyPlatform", id = "com.jme3.gde.core.assets.actions.OpenModel"), path = "Loaders/text/ogrexml+xml/Actions", position = 20),
    @ActionReference(id =
    @ActionID(category = "Edit", id = "org.openide.actions.CutAction"), path = "Loaders/text/ogrexml+xml/Actions", position = 200, separatorBefore = 100),
    @ActionReference(id =
    @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"), path = "Loaders/text/ogrexml+xml/Actions", position = 300, separatorAfter = 400),
    @ActionReference(id =
    @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"), path = "Loaders/text/ogrexml+xml/Actions", position = 500),
    @ActionReference(id =
    @ActionID(category = "System", id = "org.openide.actions.RenameAction"), path = "Loaders/text/ogrexml+xml/Actions", position = 600, separatorAfter = 700),
    @ActionReference(id =
    @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"), path = "Loaders/text/ogrexml+xml/Actions", position = 800, separatorAfter = 900),
    @ActionReference(id =
    @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"), path = "Loaders/text/ogrexml+xml/Actions", position = 1000, separatorAfter = 1100),
    @ActionReference(id =
    @ActionID(category = "System", id = "org.openide.actions.ToolsAction"), path = "Loaders/text/ogrexml+xml/Actions", position = 1200),
    @ActionReference(id =
    @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"), path = "Loaders/text/ogrexml+xml/Actions", position = 1300)
})
public class OgreXMLDataObject extends SpatialAssetDataObject {

    public OgreXMLDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
    }

    @Override
    public synchronized ModelKey getAssetKey() {
        if(super.getAssetKey() instanceof OgreMeshKey){
            return (OgreMeshKey)assetKey;
        }
        assetKey = new OgreMeshKey(super.getAssetKey().getName());
        return (OgreMeshKey)assetKey;
    }
    
    @Override
    public synchronized Spatial loadAsset() {
        if (isModified() && savable != null) {
            return (Spatial) savable;
        }
        ProjectAssetManager mgr = getLookup().lookup(ProjectAssetManager.class);
        if (mgr == null) {
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("File is not part of a project!\nCannot load without ProjectAssetManager."));
            return null;
        }
        String name = getPrimaryFile().getName();
        int idx = name.toLowerCase().indexOf(".mesh");
        if(idx!=-1){
            name = name.substring(0, idx);
        }
        String matName = ((OgreMeshKey)getAssetKey()).getMaterialName();
        if(matName == null){
            matName = name;
        }
        FileObject sourceMatFile = getPrimaryFile().getParent().getFileObject(matName, "material");
        if (sourceMatFile == null || !sourceMatFile.isValid()) {
            Confirmation msg = new NotifyDescriptor.Confirmation(
                    "No material file found for " + getPrimaryFile().getNameExt() + "\n"
                    + "A file named " + matName + ".material should be in the same folder.\n"
                    + "Press OK to import mesh only.",
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.WARNING_MESSAGE);
            Object result = DialogDisplayer.getDefault().notify(msg);
            if (!NotifyDescriptor.OK_OPTION.equals(result)) {
                return null;
            }
        }
        
        FileLock lock = null;
        try {
            lock = getPrimaryFile().lock();
            mgr.deleteFromCache(getAssetKey());
            listListener.start();
            Spatial spatial = mgr.loadModel(getAssetKey());
            listListener.stop();
            savable = spatial;
            lock.releaseLock();
            return spatial;
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (lock != null) {
                lock.releaseLock();
            }
        }
        return null;
    }

}
