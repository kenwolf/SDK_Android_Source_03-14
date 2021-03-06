/*
 * Copyright (C) 2008 The Android Open Source Project
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

package android.graphics.cts;

import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargetClass;
import android.graphics.Rasterizer;
import android.test.AndroidTestCase;

@TestTargetClass(Rasterizer.class)
public class RasterizerTest extends AndroidTestCase {

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test finalize function",
            method = "finalize",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Test finalize function",
            method = "Rasterizer",
            args = {}
        )
    })
    public void testFinalize() {
        MockRasterizer mr = new MockRasterizer();
        try {
            mr.finalize();
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    class MockRasterizer extends Rasterizer {
        @Override
        public void finalize() throws Throwable {
            super.finalize();
        }
    }
}
