/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.eclipse.org/org/documents/epl-v10.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.ide.eclipse.adt.internal.resources.manager;

import com.android.ide.eclipse.adt.internal.resources.ResourceItem;
import com.android.ide.eclipse.adt.internal.resources.ResourceType;
import com.android.ide.eclipse.adt.internal.resources.configurations.FolderConfiguration;
import com.android.ide.eclipse.adt.internal.resources.manager.files.IAbstractFile;
import com.android.ide.eclipse.adt.internal.resources.manager.files.IAbstractFolder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Resource Folder class. Contains list of {@link ResourceFile}s,
 * the {@link FolderConfiguration}, and a link to the workspace {@link IFolder} object.
 */
public final class ResourceFolder extends Resource {
    ResourceFolderType mType;
    FolderConfiguration mConfiguration;
    IAbstractFolder mFolder;
    ArrayList<ResourceFile> mFiles = null;
    private final boolean mIsFramework;

    /**
     * Creates a new {@link ResourceFolder}
     * @param type The type of the folder
     * @param config The configuration of the folder
     * @param folder The associated {@link IAbstractFolder} object.
     * @param isFrameworkRepository
     */
    public ResourceFolder(ResourceFolderType type, FolderConfiguration config,
            IAbstractFolder folder, boolean isFrameworkRepository) {
        mType = type;
        mConfiguration = config;
        mFolder = folder;
        mIsFramework = isFrameworkRepository;
    }

    /**
     * Adds a {@link ResourceFile} to the folder.
     * @param file The {@link ResourceFile}.
     */
    public void addFile(ResourceFile file) {
        if (mFiles == null) {
            mFiles = new ArrayList<ResourceFile>();
        }

        mFiles.add(file);
    }

    /**
     * Attempts to remove the {@link ResourceFile} associated with a specified {@link IFile}.
     * @param file the IFile object.
     * @return the {@link ResourceFile} that was removed.
     */
    public ResourceFile removeFile(IFile file) {
        if (mFiles != null) {
            int count = mFiles.size();
            for (int i = 0 ; i < count ; i++) {
                ResourceFile resFile = mFiles.get(i);
                if (resFile != null) {
                    IFile iFile = resFile.getFile().getIFile();
                    if (iFile != null && iFile.equals(file)) {
                        mFiles.remove(i);
                        touch();
                        return resFile;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Returns the {@link IFolder} associated with this object.
     */
    public IAbstractFolder getFolder() {
        return mFolder;
    }

    /**
     * Returns the {@link ResourceFolderType} of this object.
     */
    public ResourceFolderType getType() {
        return mType;
    }

    /**
     * Returns whether the folder is a framework resource folder.
     */
    public boolean isFramework() {
        return mIsFramework;
    }

    /**
     * Returns the list of {@link ResourceType}s generated by the files inside this folder.
     */
    public Collection<ResourceType> getResourceTypes() {
        ArrayList<ResourceType> list = new ArrayList<ResourceType>();

        if (mFiles != null) {
            for (ResourceFile file : mFiles) {
                ResourceType[] types = file.getResourceTypes();

                // loop through those and add them to the main list,
                // if they are not already present
                if (types != null) {
                    for (ResourceType resType : types) {
                        if (list.indexOf(resType) == -1) {
                            list.add(resType);
                        }
                    }
                }
            }
        }

        return list;
    }

    /*
     * (non-Javadoc)
     * @see com.android.ide.eclipse.editors.resources.manager.Resource#getConfiguration()
     */
    @Override
    public FolderConfiguration getConfiguration() {
        return mConfiguration;
    }

    /**
     * Returns whether the folder contains a file with the given name.
     * @param name the name of the file.
     */
    public boolean hasFile(String name) {
        return mFolder.hasFile(name);
    }

    /**
     * Returns the {@link ResourceFile} matching a {@link IAbstractFile} object.
     * @param file The {@link IFile} object.
     * @return the {@link ResourceFile} or null if no match was found.
     */
    public ResourceFile getFile(IAbstractFile file) {
        if (mFiles != null) {
            for (ResourceFile f : mFiles) {
                if (f.getFile().equals(file)) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * Returns the {@link ResourceFile} matching a {@link IFile} object.
     * @param file The {@link IFile} object.
     * @return the {@link ResourceFile} or null if no match was found.
     */
    public ResourceFile getFile(IFile file) {
        if (mFiles != null) {
            for (ResourceFile f : mFiles) {
                IFile iFile = f.getFile().getIFile();
                if (iFile != null && iFile.equals(file)) {
                    return f;
                }
            }
        }
        return null;
    }


    /**
     * Returns the {@link ResourceFile} matching a given name.
     * @param filename The name of the file to return.
     * @return the {@link ResourceFile} or <code>null</code> if no match was found.
     */
    public ResourceFile getFile(String filename) {
        if (mFiles != null) {
            for (ResourceFile f : mFiles) {
                if (f.getFile().getName().equals(filename)) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * Returns whether a file in the folder is generating a resource of a specified type.
     * @param type The {@link ResourceType} being looked up.
     */
    public boolean hasResources(ResourceType type) {
        // Check if the folder type is able to generate resource of the type that was asked.
        // this is a first check to avoid going through the files.
        ResourceFolderType[] folderTypes = FolderTypeRelationship.getRelatedFolders(type);

        boolean valid = false;
        for (ResourceFolderType rft : folderTypes) {
            if (rft == mType) {
                valid = true;
                break;
            }
        }

        if (valid) {
            if (mFiles != null) {
                for (ResourceFile f : mFiles) {
                    if (f.hasResources(type)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Get the list of {@link ResourceItem} of a specific type generated by all the files
     * in the folder.
     * This method must make sure not to create duplicates.
     * @param type The type of {@link ResourceItem} to return.
     * @param projectResources The global Project Resource object, allowing the implementation to
     * query for already existing {@link ResourceItem}
     * @return The list of <b>new</b> {@link ResourceItem}
     * @see ProjectResources#findResourceItem(ResourceType, String)
     */
    public Collection<ProjectResourceItem> getResources(ResourceType type,
            ProjectResources projectResources) {
        Collection<ProjectResourceItem> list = new ArrayList<ProjectResourceItem>();
        if (mFiles != null) {
            for (ResourceFile f : mFiles) {
                list.addAll(f.getResources(type, projectResources));
            }
        }
        return list;
    }
}
