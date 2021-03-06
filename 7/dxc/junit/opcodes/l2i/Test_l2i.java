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

package dxc.junit.opcodes.l2i;

import dxc.junit.DxTestCase;
import dxc.junit.DxUtil;
import dxc.junit.opcodes.l2i.jm.T_l2i_1;

public class Test_l2i extends DxTestCase {

    /**
     * @title  Argument = 0xAAAAFFEEDDCCl
     */
    public void testN1() {
        T_l2i_1 t = new T_l2i_1();
        assertEquals(0xFFEEDDCC, t.run(0xAAAAFFEEDDCCl));
    }

    /**
     * @title  Argument = -123456789
     */
    public void testN2() {
        T_l2i_1 t = new T_l2i_1();
        assertEquals(-123456789, t.run(-123456789l));
    }

    /**
     * @title  Argument = 1
     */
    public void testN3() {
        T_l2i_1 t = new T_l2i_1();
        assertEquals(1, t.run(1l));
    }

    /**
     * @title  Argument = -1
     */
    public void testN4() {
        T_l2i_1 t = new T_l2i_1();
        assertEquals(-1, t.run(-1l));
    }

    /**
     * @title  Argument = Long.MAX_VALUE
     */
    public void testB1() {
        T_l2i_1 t = new T_l2i_1();
        assertEquals(-1, t.run(Long.MAX_VALUE));
    }

    /**
     * @title  Argument = Long.MIN_VALUE
     */
    public void testB2() {
        T_l2i_1 t = new T_l2i_1();
        assertEquals(0, t.run(Long.MIN_VALUE));
    }

    /**
     * @title  Argument = 0
     */
    public void testB3() {
        T_l2i_1 t = new T_l2i_1();
        assertEquals(0, t.run(0l));
    }

    /**
     * @constraint 4.8.2.1
     * @title number of arguments
     */
    public void testVFE1() {
        try {
            Class.forName("dxc.junit.opcodes.l2i.jm.T_l2i_2");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title type of argument - double
     */
    public void testVFE2() {
        try {
            Class.forName("dxc.junit.opcodes.l2i.jm.T_l2i_3");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title type of argument - float
     */
    public void testVFE3() {
        try {
            Class.forName("dxc.junit.opcodes.l2i.jm.T_l2i_4");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }

    /**
     * @constraint 4.8.2.1
     * @title type of argument - reference
     */
    public void testVFE4() {
        try {
            Class.forName("dxc.junit.opcodes.l2i.jm.T_l2i_5");
            fail("expected a verification exception");
        } catch (Throwable t) {
            DxUtil.checkVerifyException(t);
        }
    }
}
