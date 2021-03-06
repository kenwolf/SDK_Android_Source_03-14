/* Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.api.java.nio.charset;

import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargets;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestLevel;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.UnmappableCharacterException;

/**
 * test case specific activity of iso-8859-1 charset encoder
 */
@TestTargetClass(java.nio.charset.CharsetEncoder.class)
public class ISOCharsetEncoderTest extends AbstractCharsetEncoderTestCase {

    // charset for iso-8859-1
    private static final Charset CS = Charset.forName("iso-8859-1");

    /*
     * @see CharsetEncoderTest#setUp()
     */
    protected void setUp() throws Exception {
        cs = CS;
        super.setUp();
    }

    /*
     * @see CharsetEncoderTest#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL,
        notes = "IllegalStateException checking missed.",
        method = "canEncode",
        args = {java.lang.CharSequence.class}
    )
    public void testCanEncodeCharSequence() {
        // normal case for isoCS
        assertTrue(encoder.canEncode("\u0077"));
        assertFalse(encoder.canEncode("\uc2a3"));
        assertFalse(encoder.canEncode("\ud800\udc00"));
        try {
            encoder.canEncode(null);
        } catch (NullPointerException e) {
        }
        assertTrue(encoder.canEncode(""));
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.PARTIAL,
            notes = "Regression test. IllegalStateException checking missed.",
            method = "canEncode",
            args = {char.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL,
            notes = "Regression test. IllegalStateException checking missed.",
            method = "canEncode",
            args = {java.lang.CharSequence.class}
        )
    })
    public void testCanEncodeICUBug() {
        assertFalse(encoder.canEncode((char) '\ud800'));
        assertFalse(encoder.canEncode((String) "\ud800"));
    }

    @TestTargetNew(
        level = TestLevel.PARTIAL,
        notes = "IllegalStateException checking missed.",
        method = "canEncode",
        args = {char.class}
    )
    public void testCanEncodechar() throws CharacterCodingException {
        assertTrue(encoder.canEncode('\u0077'));
        assertFalse(encoder.canEncode('\uc2a3'));
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "",
            method = "averageBytesPerChar",
            args = {}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "",
            method = "maxBytesPerChar",
            args = {}
        )
    })
    public void testSpecificDefaultValue() {
        assertEquals(1, encoder.averageBytesPerChar(), 0.001);
        assertEquals(1, encoder.maxBytesPerChar(), 0.001);
    }

    CharBuffer getMalformedCharBuffer() {
        return CharBuffer.wrap("\ud800 buffer");
    }

    CharBuffer getUnmapCharBuffer() {
        return CharBuffer.wrap("\ud800\udc00 buffer");
    }

    CharBuffer getExceptionCharBuffer() {
        return null;
    }

    protected byte[] getIllegalByteArray() {
        return null;
    }

    @TestTargets({
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Checks also: flush & encode, but not covers exceptions.",
            method = "onMalformedInput",
            args = {java.nio.charset.CodingErrorAction.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Checks also: flush & encode, but not covers exceptions.",
            method = "onUnmappableCharacter",
            args = {java.nio.charset.CodingErrorAction.class}
        ),
        @TestTargetNew(
            level = TestLevel.COMPLETE,
            notes = "Checks also: flush & encode, but not covers exceptions.",
            method = "reset",
            args = {}
        )
    })
    public void testMultiStepEncode() throws CharacterCodingException {
        encoder.onMalformedInput(CodingErrorAction.REPORT);
        encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            encoder.encode(CharBuffer.wrap("\ud800\udc00"));
            fail("should unmappable");
        } catch (UnmappableCharacterException e) {
        }
        encoder.reset();
        ByteBuffer out = ByteBuffer.allocate(10);
        assertTrue(encoder.encode(CharBuffer.wrap("\ud800"), out, true)
                .isMalformed());
        encoder.flush(out);
        encoder.reset();
        out = ByteBuffer.allocate(10);
        assertSame(CoderResult.UNDERFLOW, encoder.encode(CharBuffer
                .wrap("\ud800"), out, false));
        assertTrue(encoder.encode(CharBuffer.wrap("\udc00"), out, true)
                .isMalformed());
    }
}
