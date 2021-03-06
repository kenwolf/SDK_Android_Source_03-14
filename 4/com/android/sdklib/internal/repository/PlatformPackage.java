/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.sdklib.internal.repository;

import com.android.sdklib.AndroidVersion;
import com.android.sdklib.IAndroidTarget;
import com.android.sdklib.SdkConstants;
import com.android.sdklib.SdkManager;
import com.android.sdklib.internal.repository.Archive.Arch;
import com.android.sdklib.internal.repository.Archive.Os;
import com.android.sdklib.repository.SdkRepository;

import org.w3c.dom.Node;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * Represents a platform XML node in an SDK repository.
 */
public class PlatformPackage extends Package {

    private static final String PROP_VERSION       = "Platform.Version";      //$NON-NLS-1$
    private static final String PROP_MIN_TOOLS_REV = "Platform.MinToolsRev";  //$NON-NLS-1$

    /** The package version, for platform, add-on and doc packages. */
    private final AndroidVersion mVersion;

    /** The version, a string, for platform packages. */
    private final String mVersionName;

    /**
     * The minimal revision of the tools package required by this extra package, if > 0,
     * or {@link #MIN_TOOLS_REV_NOT_SPECIFIED} if there is no such requirement.
     */
    private final int mMinToolsRevision;

    /**
     * The value of {@link #mMinToolsRevision} when the {@link SdkRepository#NODE_MIN_TOOLS_REV}
     * was not specified in the XML source.
     */
    public static final int MIN_TOOLS_REV_NOT_SPECIFIED = 0;

    /**
     * Creates a new platform package from the attributes and elements of the given XML node.
     * <p/>
     * This constructor should throw an exception if the package cannot be created.
     */
    PlatformPackage(RepoSource source, Node packageNode, Map<String,String> licenses) {
        super(source, packageNode, licenses);
        mVersionName = XmlParserUtils.getXmlString(packageNode, SdkRepository.NODE_VERSION);
        int apiLevel = XmlParserUtils.getXmlInt   (packageNode, SdkRepository.NODE_API_LEVEL, 0);
        String codeName = XmlParserUtils.getXmlString(packageNode, SdkRepository.NODE_CODENAME);
        if (codeName.length() == 0) {
            codeName = null;
        }
        mVersion = new AndroidVersion(apiLevel, codeName);

        mMinToolsRevision = XmlParserUtils.getXmlInt(packageNode, SdkRepository.NODE_MIN_TOOLS_REV,
                MIN_TOOLS_REV_NOT_SPECIFIED);
    }

    /**
     * Creates a new platform package based on an actual {@link IAndroidTarget} (which
     * must have {@link IAndroidTarget#isPlatform()} true) from the {@link SdkManager}.
     * This is used to list local SDK folders in which case there is one archive which
     * URL is the actual target location.
     */
    PlatformPackage(IAndroidTarget target, Properties props) {
        super(  null,                       //source
                props,                      //properties
                target.getRevision(),       //revision
                null,                       //license
                target.getDescription(),    //description
                null,                       //descUrl
                Os.getCurrentOs(),          //archiveOs
                Arch.getCurrentArch(),      //archiveArch
                target.getLocation()        //archiveOsPath
                );

        mVersion = target.getVersion();
        mVersionName  = target.getVersionName();

        mMinToolsRevision = Integer.parseInt(getProperty(props, PROP_MIN_TOOLS_REV,
                Integer.toString(MIN_TOOLS_REV_NOT_SPECIFIED)));
    }

    /**
     * Save the properties of the current packages in the given {@link Properties} object.
     * These properties will later be given to a constructor that takes a {@link Properties} object.
     */
    @Override
    void saveProperties(Properties props) {
        super.saveProperties(props);

        mVersion.saveProperties(props);

        if (mVersionName != null) {
            props.setProperty(PROP_VERSION, mVersionName);
        }

        if (mMinToolsRevision != MIN_TOOLS_REV_NOT_SPECIFIED) {
            props.setProperty(PROP_MIN_TOOLS_REV, Integer.toString(mMinToolsRevision));
        }
    }

    /** Returns the version, a string, for platform packages. */
    public String getVersionName() {
        return mVersionName;
    }

    /** Returns the package version, for platform, add-on and doc packages. */
    public AndroidVersion getVersion() {
        return mVersion;
    }

    /**
     * The minimal revision of the tools package required by this extra package, if > 0,
     * or {@link #MIN_TOOLS_REV_NOT_SPECIFIED} if there is no such requirement.
     */
    public int getMinToolsRevision() {
        return mMinToolsRevision;
    }

    /** Returns a short description for an {@link IDescription}. */
    @Override
    public String getShortDescription() {
        String s;

        if (mVersion.isPreview()) {
            s = String.format("SDK Platform Android %1$s (Preview)", getVersionName());
        } else {
            s = String.format("SDK Platform Android %1$s, API %2$d",
                getVersionName(),
                mVersion.getApiLevel());
        }

        if (mMinToolsRevision != MIN_TOOLS_REV_NOT_SPECIFIED) {
            s += String.format(" (tools rev: %1$d)", mMinToolsRevision);
        }

        return s;
    }

    /** Returns a long description for an {@link IDescription}. */
    @Override
    public String getLongDescription() {
        return getShortDescription() + ".";
    }

    /**
     * Computes a potential installation folder if an archive of this package were
     * to be installed right away in the given SDK root.
     * <p/>
     * A platform package is typically installed in SDK/platforms/android-"version".
     * However if we can find a different directory under SDK/platform that already
     * has this platform version installed, we'll use that one.
     *
     * @param osSdkRoot The OS path of the SDK root folder.
     * @param suggestedDir A suggestion for the installation folder name, based on the root
     *                     folder used in the zip archive.
     * @param sdkManager An existing SDK manager to list current platforms and addons.
     * @return A new {@link File} corresponding to the directory to use to install this package.
     */
    @Override
    public File getInstallFolder(String osSdkRoot, String suggestedDir, SdkManager sdkManager) {

        // First find if this platform is already installed. If so, reuse the same directory.
        for (IAndroidTarget target : sdkManager.getTargets()) {
            if (target.isPlatform() &&
                    target.getVersion().equals(mVersion) &&
                    target.getVersionName().equals(getVersionName())) {
                return new File(target.getLocation());
            }
        }

        File platforms = new File(osSdkRoot, SdkConstants.FD_PLATFORMS);
        File folder = new File(platforms, String.format("android-%s", getVersionName())); //$NON-NLS-1$

        return folder;
    }

    @Override
    public boolean sameItemAs(Package pkg) {
        if (pkg instanceof PlatformPackage) {
            PlatformPackage newPkg = (PlatformPackage)pkg;

            // check they are the same platform.
            return newPkg.getVersion().equals(this.getVersion());
        }

        return false;
    }
}
