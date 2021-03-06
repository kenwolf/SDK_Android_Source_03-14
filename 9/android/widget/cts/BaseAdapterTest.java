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

package android.widget.cts;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;

import android.database.DataSetObserver;
import android.test.AndroidTestCase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Test {@link BaseAdapter}.
 */
@TestTargetClass(BaseAdapter.class)
public class BaseAdapterTest extends AndroidTestCase {
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "this function always returns false.",
        method = "hasStableIds",
        args = {}
    )
    public void testHasStableIds() {
        BaseAdapter baseAdapter = new MockBaseAdapter();
        assertFalse(baseAdapter.hasStableIds());
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "registerDataSetObserver",
            args = {android.database.DataSetObserver.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "unregisterDataSetObserver",
            args = {android.database.DataSetObserver.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            method = "notifyDataSetChanged",
            args = {}
        )
    })
    public void testDataSetObserver() {
        BaseAdapter baseAdapter = new MockBaseAdapter();
        MockDataSetObserver dataSetObserver = new MockDataSetObserver();

        assertFalse(dataSetObserver.hasCalledOnChanged());
        baseAdapter.notifyDataSetChanged();
        assertFalse(dataSetObserver.hasCalledOnChanged());

        baseAdapter.registerDataSetObserver(dataSetObserver);
        baseAdapter.notifyDataSetChanged();
        assertTrue(dataSetObserver.hasCalledOnChanged());

        dataSetObserver.reset();
        assertFalse(dataSetObserver.hasCalledOnChanged());
        baseAdapter.unregisterDataSetObserver(dataSetObserver);
        baseAdapter.notifyDataSetChanged();
        assertFalse(dataSetObserver.hasCalledOnChanged());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "notifyDataSetInvalidated",
        args = {}
    )
    public void testNotifyDataSetInvalidated() {
        BaseAdapter baseAdapter = new MockBaseAdapter();
        MockDataSetObserver dataSetObserver = new MockDataSetObserver();

        assertFalse(dataSetObserver.hasCalledOnInvalidated());
        baseAdapter.notifyDataSetInvalidated();
        assertFalse(dataSetObserver.hasCalledOnInvalidated());

        baseAdapter.registerDataSetObserver(dataSetObserver);
        baseAdapter.notifyDataSetInvalidated();
        assertTrue(dataSetObserver.hasCalledOnInvalidated());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "this function always returns true.",
        method = "areAllItemsEnabled",
        args = {}
    )
    public void testAreAllItemsEnabled() {
        BaseAdapter baseAdapter = new MockBaseAdapter();
        assertTrue(baseAdapter.areAllItemsEnabled());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "this function always returns true.",
        method = "isEnabled",
        args = {int.class}
    )
    public void testIsEnabled() {
        BaseAdapter baseAdapter = new MockBaseAdapter();
        assertTrue(baseAdapter.isEnabled(0));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "getDropDownView",
        args = {int.class, android.view.View.class, android.view.ViewGroup.class}
    )
    public void testGetDropDownView() {
        BaseAdapter baseAdapter = new MockBaseAdapter();
        assertNull(baseAdapter.getDropDownView(0, null, null));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "this function always returns 0.",
        method = "getItemViewType",
        args = {int.class}
    )
    public void testGetItemViewType() {
        BaseAdapter baseAdapter = new MockBaseAdapter();
        assertEquals(0, baseAdapter.getItemViewType(0));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "this function always returns 1.",
        method = "getViewTypeCount",
        args = {}
    )
    public void testGetViewTypeCount() {
        BaseAdapter baseAdapter = new MockBaseAdapter();
        assertEquals(1, baseAdapter.getViewTypeCount());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        method = "isEmpty",
        args = {}
    )
    public void testIsEmpty() {
        MockBaseAdapter baseAdapter = new MockBaseAdapter();

        baseAdapter.setCount(0);
        assertTrue(baseAdapter.isEmpty());

        baseAdapter.setCount(1);
        assertFalse(baseAdapter.isEmpty());
    }

    private static class MockBaseAdapter extends BaseAdapter {
        private int mCount = 0;

        public void setCount(int count) {
            mCount = count;
        }

        public int getCount() {
            return mCount;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

    private static class MockDataSetObserver extends DataSetObserver {
        private boolean mCalledOnChanged = false;
        private boolean mCalledOnInvalidated = false;

        @Override
        public void onChanged() {
            super.onChanged();
            mCalledOnChanged = true;
        }

        public boolean hasCalledOnChanged() {
            return mCalledOnChanged;
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mCalledOnInvalidated = true;
        }

        public boolean hasCalledOnInvalidated() {
            return mCalledOnInvalidated;
        }

        public void reset() {
            mCalledOnChanged = false;
            mCalledOnInvalidated = false;
        }
    }
}
