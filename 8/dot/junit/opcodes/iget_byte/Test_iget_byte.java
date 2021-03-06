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

package dot.junit.opcodes.iget_byte;

import dot.junit.DxTestCase;
import dot.junit.DxUtil;
import dot.junit.opcodes.iget_byte.d.T_iget_byte_1;
import dot.junit.opcodes.iget_byte.d.T_iget_byte_11;
import dot.junit.opcodes.iget_byte.d.T_iget_byte_9;

public class Test_iget_byte extends DxTestCase {
    
    /**
     * @title get byte from field 
     */
    public void testN1() {
        T_iget_byte_1 t = new T_iget_byte_1();
        assertEquals(77, t.run());
    }


    /**
     * @title access protected field from subclass
     */
    public void testN3() {
        //@uses dot.junit.opcodes.iget_byte.d.T_iget_byte_1
        //@uses dot.junit.opcodes.iget_byte.d.T_iget_byte_11
        T_iget_byte_11 t = new T_iget_byte_11();
        assertEquals(77, t.run());
    }

    /**
     * @title expected NullPointerException
     */
    public void testE2() {
        T_iget_byte_9 t = new T_iget_byte_9();
        try {
            t.run();
            fail("expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }   

    /**
     * @constraint A11 
     * @title  constant pool index
     */
    public void testVFE1() {
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint A23 
     * @title number of registers
     */
    public void testVFE2() {
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint B13 
     * @title read byte from long field - only field with same name but 
     * different type exists
     */
    public void testVFE3() {
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_13");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint n/a
     * @title Attempt to read inaccessible field. Java throws IllegalAccessError 
     * on first access but Dalvik throws VerifyError on class loading.
     */
    public void testVFE4() {
        //@uses dot.junit.opcodes.iget_byte.d.T_iget_byte_6
        //@uses dot.junit.opcodes.iget_byte.TestStubs
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_6");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint n/a
     * @title Attempt to read field of undefined class. Java throws NoClassDefFoundError 
     * on first access but Dalvik throws VerifyError on class loading.
     */
    public void testVFE5() {
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_7");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint n/a
     * @title Attempt to read undefined field. Java throws NoSuchFieldError 
     * on first access but Dalvik throws VerifyError on class loading.
     */
    public void testVFE6() {
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_8");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint n/a
     * @title Attempt to read superclass' private field from subclass. Java 
     * throws IllegalAccessError on first access but Dalvik throws VerifyError on class loading.
     */
    public void testVFE7() {
        //@uses dot.junit.opcodes.iget_byte.d.T_iget_byte_12
        //@uses dot.junit.opcodes.iget_byte.d.T_iget_byte_1
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_12");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
   
    /**
     * @constraint B1 
     * @title iget_byte shall not work for reference fields
     */
    public void testVFE8() {
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_14");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint B1 
     * @title iget_byte shall not work for short fields
     */
    public void testVFE9() {
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_15");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint B1 
     * @title iget_byte shall not work for int fields
     */
    public void testVFE10() {
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_16");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint B1 
     * @title iget_byte shall not work for char fields
     */
    public void testVFE11() {
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_17");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint B1 
     * @title iget_byte shall not work for boolean fields
     */
    public void testVFE12() {
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_18");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }    
    
    /**
     * @constraint B1 
     * @title iget_byte shall not work for double fields
     */
    public void testVFE13() {
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_19");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    } 
    
    /**
     * @constraint B1 
     * @title iget_byte shall not work for long fields
     */
    public void testVFE14() {
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_20");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
    
    /**
     * @constraint B12
     * @title Attempt to read inaccessible protected field. Java throws IllegalAccessError 
     * on first access but Dalvik throws VerifyError on class loading.
     */
    public void testVFE15() {
        //@uses dot.junit.opcodes.iget_byte.d.T_iget_byte_21
        //@uses dot.junit.opcodes.iget_byte.TestStubs
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_21");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }


    /**
     * @constraint A11
     * @title Attempt to read static  field. Java throws IncompatibleClassChangeError 
     * on first access but Dalvik throws VerifyError on class loading.
     */
    public void testVFE16() {
        //@uses dot.junit.opcodes.iget_byte.d.T_iget_byte_5
        //@uses dot.junit.opcodes.iget_byte.TestStubs        
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint B6 
     * @title instance fields may only be accessed on already initialized instances. 
     */
    public void testVFE30() {
        try {
            Class.forName("dot.junit.opcodes.iget_byte.d.T_iget_byte_30");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
}

