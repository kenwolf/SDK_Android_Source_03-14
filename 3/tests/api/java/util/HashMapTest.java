/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package tests.api.java.util;

import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass; 

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import tests.support.Support_MapTest2;
import tests.support.Support_UnmodifiableCollectionTest;

@TestTargetClass(HashMap.class) 
public class HashMapTest extends junit.framework.TestCase {
    class MockMap extends AbstractMap {
        public Set entrySet() {
            return null;
        }
        public int size(){
            return 0;
        }
    }
    
    private static class MockMapNull extends AbstractMap {
        public Set entrySet() {
            return null;
        }

        public int size() {
            return 10;
        }
    }
    
    HashMap hm;

    final static int hmSize = 1000;

    Object[] objArray;

    Object[] objArray2;
    
    /**
     * @tests java.util.HashMap#HashMap()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "HashMap",
        args = {}
    )
    public void test_Constructor() {
        // Test for method java.util.HashMap()
        new Support_MapTest2(new HashMap()).runTest();

        HashMap hm2 = new HashMap();
        assertEquals("Created incorrect HashMap", 0, hm2.size());
    }

    /**
     * @tests java.util.HashMap#HashMap(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "HashMap",
        args = {int.class}
    )
    public void test_ConstructorI() {
        // Test for method java.util.HashMap(int)
        HashMap hm2 = new HashMap(5);
        assertEquals("Created incorrect HashMap", 0, hm2.size());
        try {
            new HashMap(-1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            //expected
        }

        HashMap empty = new HashMap(0);
        assertNull("Empty hashmap access", empty.get("nothing"));
        empty.put("something", "here");
        assertTrue("cannot get element", empty.get("something") == "here");
    }

    /**
     * @tests java.util.HashMap#HashMap(int, float)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "HashMap",
        args = {int.class, float.class}
    )
    public void test_ConstructorIF() {
        // Test for method java.util.HashMap(int, float)
        HashMap hm2 = new HashMap(5, (float) 0.5);
        assertEquals("Created incorrect HashMap", 0, hm2.size());
        try {
            new HashMap(0, 0);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            //expected
        }

        HashMap empty = new HashMap(0, 0.75f);
        assertNull("Empty hashtable access", empty.get("nothing"));
        empty.put("something", "here");
        assertTrue("cannot get element", empty.get("something") == "here");
    }

    /**
     * @tests java.util.HashMap#HashMap(java.util.Map)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "HashMap",
        args = {java.util.Map.class}
    )
    public void test_ConstructorLjava_util_Map() {
        // Test for method java.util.HashMap(java.util.Map)
        Map myMap = new TreeMap();
        for (int counter = 0; counter < hmSize; counter++)
            myMap.put(objArray2[counter], objArray[counter]);
        HashMap hm2 = new HashMap(myMap);
        for (int counter = 0; counter < hmSize; counter++)
            assertTrue("Failed to construct correct HashMap", hm
                    .get(objArray2[counter]) == hm2.get(objArray2[counter]));
        
        try {
            Map mockMap = new MockMap();
            hm = new HashMap(mockMap);
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            //empty
        }
    }

    /**
     * @tests java.util.HashMap#clear()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "clear",
        args = {}
    )
    public void test_clear() {
        // Test for method void java.util.HashMap.clear()
        hm.clear();
        assertEquals("Clear failed to reset size", 0, hm.size());
        for (int i = 0; i < hmSize; i++)
            assertNull("Failed to clear all elements",
                    hm.get(objArray2[i]));
        
    }

    /**
     * @tests java.util.HashMap#clone()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "clone",
        args = {}
    )
    public void test_clone() {
        // Test for method java.lang.Object java.util.HashMap.clone()
        HashMap hm2 = (HashMap) hm.clone();
        assertTrue("Clone answered equivalent HashMap", hm2 != hm);
        for (int counter = 0; counter < hmSize; counter++)
            assertTrue("Clone answered unequal HashMap", hm
                    .get(objArray2[counter]) == hm2.get(objArray2[counter]));

        HashMap map = new HashMap();
        map.put("key", "value");
        // get the keySet() and values() on the original Map
        Set keys = map.keySet();
        Collection values = map.values();
        assertEquals("values() does not work", 
                "value", values.iterator().next());
        assertEquals("keySet() does not work", 
                "key", keys.iterator().next());
        AbstractMap map2 = (AbstractMap) map.clone();
        map2.put("key", "value2");
        Collection values2 = map2.values();
        assertTrue("values() is identical", values2 != values);
        // values() and keySet() on the cloned() map should be different
        assertEquals("values() was not cloned", 
                "value2", values2.iterator().next());
        map2.clear();
        map2.put("key2", "value3");
        Set key2 = map2.keySet();
        assertTrue("keySet() is identical", key2 != keys);
        assertEquals("keySet() was not cloned", 
                "key2", key2.iterator().next());
        
        // regresion test for HARMONY-4603
        HashMap hashmap = new HashMap();
        MockClonable mock = new MockClonable(1);
        hashmap.put(1, mock);
        assertEquals(1, ((MockClonable) hashmap.get(1)).i);
        HashMap hm3 = (HashMap)hashmap.clone();
        assertEquals(1, ((MockClonable) hm3.get(1)).i);
        mock.i = 0;
        assertEquals(0, ((MockClonable) hashmap.get(1)).i);
        assertEquals(0, ((MockClonable) hm3.get(1)).i);
    }

    /**
     * @tests java.util.HashMap#containsKey(java.lang.Object)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "containsKey",
        args = {java.lang.Object.class}
    )
    public void test_containsKeyLjava_lang_Object() {
        // Test for method boolean
        // java.util.HashMap.containsKey(java.lang.Object)
        assertTrue("Returned false for valid key", hm.containsKey(new Integer(
                876).toString()));
        assertTrue("Returned true for invalid key", !hm.containsKey("KKDKDKD"));

        HashMap m = new HashMap();
        m.put(null, "test");
        assertTrue("Failed with null key", m.containsKey(null));
        assertTrue("Failed with missing key matching null hash", !m
                .containsKey(new Integer(0)));
    }

    /**
     * @tests java.util.HashMap#containsValue(java.lang.Object)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "containsValue",
        args = {java.lang.Object.class}
    )
    public void test_containsValueLjava_lang_Object() {
        // Test for method boolean
        // java.util.HashMap.containsValue(java.lang.Object)
        assertTrue("Returned false for valid value", hm
                .containsValue(new Integer(875)));
        assertTrue("Returned true for invalid valie", !hm
                .containsValue(new Integer(-9)));
    }

    /**
     * @tests java.util.HashMap#entrySet()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "entrySet",
        args = {}
    )
    public void test_entrySet() {
        // Test for method java.util.Set java.util.HashMap.entrySet()
        Set s = hm.entrySet();
        Iterator i = s.iterator();
        assertTrue("Returned set of incorrect size", hm.size() == s.size());
        while (i.hasNext()) {
            Map.Entry m = (Map.Entry) i.next();
            assertTrue("Returned incorrect entry set", hm.containsKey(m
                    .getKey())
                    && hm.containsValue(m.getValue()));
        }
    }

    /**
     * @tests java.util.HashMap#get(java.lang.Object)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "get",
        args = {java.lang.Object.class}
    )
    public void test_getLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.HashMap.get(java.lang.Object)
        assertNull("Get returned non-null for non existent key",
                hm.get("T"));
        hm.put("T", "HELLO");
        assertEquals("Get returned incorrect value for existing key", "HELLO", hm.get("T")
                );

        HashMap m = new HashMap();
        m.put(null, "test");
        assertEquals("Failed with null key", "test", m.get(null));
        assertNull("Failed with missing key matching null hash", m
                .get(new Integer(0)));
        
        // Regression for HARMONY-206
        ReusableKey k = new ReusableKey();
        HashMap map = new HashMap();
        k.setKey(1);
        map.put(k, "value1");

        k.setKey(18);
        assertNull(map.get(k));

        k.setKey(17);
        assertNull(map.get(k));
    }

    /**
     * @tests java.util.HashMap#isEmpty()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "isEmpty",
        args = {}
    )
    public void test_isEmpty() {
        // Test for method boolean java.util.HashMap.isEmpty()
        assertTrue("Returned false for new map", new HashMap().isEmpty());
        assertTrue("Returned true for non-empty", !hm.isEmpty());
    }

    /**
     * @tests java.util.HashMap#keySet()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "keySet",
        args = {}
    )
    public void test_keySet() {
        // Test for method java.util.Set java.util.HashMap.keySet()
        Set s = hm.keySet();
        assertTrue("Returned set of incorrect size()", s.size() == hm.size());
        for (int i = 0; i < objArray.length; i++)
            assertTrue("Returned set does not contain all keys", s
                    .contains(objArray[i].toString()));

        HashMap m = new HashMap();
        m.put(null, "test");
        assertTrue("Failed with null key", m.keySet().contains(null));
        assertNull("Failed with null key", m.keySet().iterator().next());

        Map map = new HashMap(101);
        map.put(new Integer(1), "1");
        map.put(new Integer(102), "102");
        map.put(new Integer(203), "203");
        Iterator it = map.keySet().iterator();
        Integer remove1 = (Integer) it.next();
        it.hasNext();
        it.remove();
        Integer remove2 = (Integer) it.next();
        it.remove();
        ArrayList list = new ArrayList(Arrays.asList(new Integer[] {
                new Integer(1), new Integer(102), new Integer(203) }));
        list.remove(remove1);
        list.remove(remove2);
        assertTrue("Wrong result", it.next().equals(list.get(0)));
        assertEquals("Wrong size", 1, map.size());
        assertTrue("Wrong contents", map.keySet().iterator().next().equals(
                list.get(0)));

        Map map2 = new HashMap(101);
        map2.put(new Integer(1), "1");
        map2.put(new Integer(4), "4");
        Iterator it2 = map2.keySet().iterator();
        Integer remove3 = (Integer) it2.next();
        Integer next;
        if (remove3.intValue() == 1)
            next = new Integer(4);
        else
            next = new Integer(1);
        it2.hasNext();
        it2.remove();
        assertTrue("Wrong result 2", it2.next().equals(next));
        assertEquals("Wrong size 2", 1, map2.size());
        assertTrue("Wrong contents 2", map2.keySet().iterator().next().equals(
                next));
    }

    /**
     * @tests java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "put",
        args = {java.lang.Object.class, java.lang.Object.class}
    )
    public void test_putLjava_lang_ObjectLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.HashMap.put(java.lang.Object, java.lang.Object)
        hm.put("KEY", "VALUE");
        assertEquals("Failed to install key/value pair", 
                "VALUE", hm.get("KEY"));

        HashMap m = new HashMap();
        m.put(new Short((short) 0), "short");
        m.put(null, "test");
        m.put(new Integer(0), "int");
        assertEquals("Failed adding to bucket containing null", "short", m.get(
                new Short((short) 0)));
        assertEquals("Failed adding to bucket containing null2", "int", m.get(
                new Integer(0)));
    }

    /**
     * @tests java.util.HashMap#putAll(java.util.Map)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Doesn't verify NullPointerException.",
        method = "putAll",
        args = {java.util.Map.class}
    )
    public void test_putAllLjava_util_Map() {
        // Test for method void java.util.HashMap.putAll(java.util.Map)
        HashMap hm2 = new HashMap();
        hm2.putAll(hm);
        for (int i = 0; i < 1000; i++)
            assertTrue("Failed to clear all elements", hm2.get(
                    new Integer(i).toString()).equals((new Integer(i))));
        
        Map mockMap = new MockMap();
        hm2 = new HashMap();
        hm2.putAll(mockMap);
        assertEquals("Size should be 0", 0, hm2.size());
    }
    
    /**
     * @tests java.util.HashMap#putAll(java.util.Map)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies NullPointerException.",
        method = "putAll",
        args = {java.util.Map.class}
    )
    public void test_putAllLjava_util_Map_Null() {
        HashMap hashMap = new HashMap();
        try {
            hashMap.putAll(new MockMapNull());
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected.
        }

        try {
            hashMap = new HashMap(new MockMapNull());
            fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected.
        }
    } 

    /**
     * @tests java.util.HashMap#remove(java.lang.Object)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "remove",
        args = {java.lang.Object.class}
    )
    public void test_removeLjava_lang_Object() {
        // Test for method java.lang.Object
        // java.util.HashMap.remove(java.lang.Object)
        int size = hm.size();
        Integer y = new Integer(9);
        Integer x = ((Integer) hm.remove(y.toString()));
        assertTrue("Remove returned incorrect value", x.equals(new Integer(9)));
        assertNull("Failed to remove given key", hm.get(new Integer(9)));
        assertTrue("Failed to decrement size", hm.size() == (size - 1));
        assertNull("Remove of non-existent key returned non-null", hm
                .remove("LCLCLC"));

        HashMap m = new HashMap();
        m.put(null, "test");
        assertNull("Failed with same hash as null",
                m.remove(new Integer(0)));
        assertEquals("Failed with null key", "test", m.remove(null));
    }

    /**
     * @tests java.util.HashMap#size()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "size",
        args = {}
    )
    public void test_size() {
        // Test for method int java.util.HashMap.size()
        assertTrue("Returned incorrect size",
                hm.size() == (objArray.length + 2));
    }

    /**
     * @tests java.util.HashMap#values()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "values",
        args = {}
    )
    public void test_values() {
        // Test for method java.util.Collection java.util.HashMap.values()
        Collection c = hm.values();
        assertTrue("Returned collection of incorrect size()", c.size() == hm
                .size());
        for (int i = 0; i < objArray.length; i++)
            assertTrue("Returned collection does not contain all keys", c
                    .contains(objArray[i]));

        HashMap myHashMap = new HashMap();
        for (int i = 0; i < 100; i++)
            myHashMap.put(objArray2[i], objArray[i]);
        Collection values = myHashMap.values();
        new Support_UnmodifiableCollectionTest(
                "Test Returned Collection From HashMap.values()", values)
                .runTest();
        values.remove(new Integer(0));
        assertTrue(
                "Removing from the values collection should remove from the original map",
                !myHashMap.containsValue(new Integer(0)));

    }

    static class ReusableKey {
        private int key = 0;

        public void setKey(int key) {
            this.key = key;
        }

        public int hashCode() {
            return key;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ReusableKey)) {
                return false;
            }
            return key == ((ReusableKey) o).key;
        }
    }
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "hashCode",
        args = {}
    )
    public void test_Map_Entry_hashCode() {
        //Related to HARMONY-403
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(10);
        Integer key = new Integer(1);
        Integer val = new Integer(2);
        map.put(key, val);
        int expected = key.hashCode() ^ val.hashCode();
        assertEquals(expected, map.hashCode());
        key = new Integer(4);
        val = new Integer(8);
        map.put(key, val);
        expected += key.hashCode() ^ val.hashCode();
        assertEquals(expected, map.hashCode());
    } 
    
    class MockClonable implements Cloneable{
        public int i;
        
        public MockClonable(int i) {
            this.i = i;
        }
        
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return new MockClonable(i);
        }
    }
    
    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() {
        objArray = new Object[hmSize];
        objArray2 = new Object[hmSize];
        for (int i = 0; i < objArray.length; i++) {
            objArray[i] = new Integer(i);
            objArray2[i] = objArray[i].toString();
        }

        hm = new HashMap();
        for (int i = 0; i < objArray.length; i++)
            hm.put(objArray2[i], objArray[i]);
        hm.put("test", null);
        hm.put(null, "test");
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     */
    protected void tearDown() {
        hm = null;
        objArray = null;
        objArray2 = null;
    }
}
