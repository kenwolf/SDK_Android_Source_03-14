/*
 * Copyright (C) 2010 The Android Open Source Project
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

package android.drm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The utility class used in the DRM Framework. This inclueds APIs for file operations
 * and ExtendedMetadataParser for parsing extended metadata BLOB in DRM constraints.
 *
 */
public class DrmUtils {
    /* Should be used when we need to read from local file */
    /* package */ static byte[] readBytes(String path) throws IOException {
        File file = new File(path);
        return readBytes(file);
    }

    /* Should be used when we need to read from local file */
    /* package */ static byte[] readBytes(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
        byte[] data = null;

        try {
            int length = bufferedStream.available();
            if (length > 0) {
                data = new byte[length];
                // read the entire data
                bufferedStream.read(data);
             }
        } finally {
            quiteDispose(bufferedStream);
            quiteDispose(inputStream);
        }
        return data;
    }

    /* package */ static void writeToFile(final String path, byte[] data) throws IOException {
        /* check for invalid inputs */
        FileOutputStream outputStream = null;

        if (null != path && null != data) {
            try {
                outputStream = new FileOutputStream(path);
                outputStream.write(data);
            } finally {
                quiteDispose(outputStream);
            }
        }
    }

    /* package */ static void removeFile(String path) throws IOException {
        File file = new File(path);
        file.delete();
    }

    private static void quiteDispose(InputStream stream) {
        try {
            if (null != stream) {
                stream.close();
            }
        } catch (IOException e) {
            // no need to care, at least as of now
        }
    }

    private static void quiteDispose(OutputStream stream) {
        try {
            if (null != stream) {
                stream.close();
            }
        } catch (IOException e) {
            // no need to care
        }
    }

    /**
     * Get an instance of ExtendedMetadataParser to be used for parsing
     * extended metadata BLOB in DRM constraints. <br>
     *
     * extendedMetadata BLOB is retrieved by specifing
     * key DrmStore.ConstraintsColumns.EXTENDED_METADATA.
     *
     * @param extendedMetadata BLOB in which key-value pairs of extended metadata are embedded.
     *
     */
    public static ExtendedMetadataParser getExtendedMetadataParser(byte[] extendedMetadata) {
        return new ExtendedMetadataParser(extendedMetadata);
    }

    /**
     * Utility parser to parse the extended meta-data embedded inside DRM constraints<br><br>
     *
     * Usage example<br>
     * byte[] extendedMetadata<br>
     * &nbsp;&nbsp;&nbsp;&nbsp; =
     *         constraints.getAsByteArray(DrmStore.ConstraintsColumns.EXTENDED_METADATA);<br>
     * ExtendedMetadataParser parser = getExtendedMetadataParser(extendedMetadata);<br>
     * Iterator keyIterator = parser.keyIterator();<br>
     * while (keyIterator.hasNext()) {<br>
     *     &nbsp;&nbsp;&nbsp;&nbsp;String extendedMetadataKey = keyIterator.next();<br>
     *     &nbsp;&nbsp;&nbsp;&nbsp;String extendedMetadataValue =
     *             parser.get(extendedMetadataKey);<br>
     * }
     */
    public static class ExtendedMetadataParser {
        HashMap<String, String> mMap = new HashMap<String, String>();

        private int readByte(byte[] constraintData, int arrayIndex) {
            //Convert byte[] into int.
            return (int)constraintData[arrayIndex];
        }

        private String readMultipleBytes(
                byte[] constraintData, int numberOfBytes, int arrayIndex) {
            byte[] returnBytes = new byte[numberOfBytes];
            for (int j = arrayIndex, i = 0; j < arrayIndex + numberOfBytes; j++,i++) {
                returnBytes[i] = constraintData[j];
            }
            return new String(returnBytes);
        }

        /*
         * This will parse the following format
         * KeyLengthValueLengthKeyValueKeyLength1ValueLength1Key1Value1..\0
         */
        private ExtendedMetadataParser(byte[] constraintData) {
            //Extract KeyValue Pair Info, till terminator occurs.
            int index = 0;

            while (index < constraintData.length) {
                //Parse Key Length
                int keyLength = readByte(constraintData, index);
                index++;

                //Parse Value Length
                int valueLength = readByte(constraintData, index);
                index++;

                //Fetch key
                String strKey = readMultipleBytes(constraintData, keyLength, index);
                index += keyLength;

                //Fetch Value
                String strValue = readMultipleBytes(constraintData, valueLength, index);
                index += valueLength;
                mMap.put(strKey, strValue);
            }
        }

        public Iterator<String> iterator() {
            return mMap.values().iterator();
        }

        public Iterator<String> keyIterator() {
            return mMap.keySet().iterator();
        }

        public String get(String key) {
            return mMap.get(key);
        }
    }
}

