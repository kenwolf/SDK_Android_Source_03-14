/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.android.dx.cf.cst;

import com.android.dx.cf.iface.ParseException;
import com.android.dx.cf.iface.ParseObserver;
import com.android.dx.rop.cst.Constant;
import com.android.dx.rop.cst.CstDouble;
import com.android.dx.rop.cst.CstFieldRef;
import com.android.dx.rop.cst.CstFloat;
import com.android.dx.rop.cst.CstInteger;
import com.android.dx.rop.cst.CstInterfaceMethodRef;
import com.android.dx.rop.cst.CstLong;
import com.android.dx.rop.cst.CstMethodRef;
import com.android.dx.rop.cst.CstNat;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.cst.CstUtf8;
import com.android.dx.rop.cst.StdConstantPool;
import com.android.dx.rop.type.Type;
import com.android.dx.util.ByteArray;
import com.android.dx.util.Hex;

import static com.android.dx.cf.cst.ConstantTags.*;

/**
 * Parser for a constant pool embedded in a class file.
 */
public final class ConstantPoolParser {
    /** {@code non-null;} the bytes of the constant pool */
    private final ByteArray bytes;

    /** {@code non-null;} actual parsed constant pool contents */
    private final StdConstantPool pool;

    /** {@code non-null;} byte offsets to each cst */
    private final int[] offsets;

    /**
     * -1 || &gt;= 10; the end offset of this constant pool in the
     * {@code byte[]} which it came from or {@code -1} if not
     * yet parsed
     */
    private int endOffset;

    /** {@code null-ok;} parse observer, if any */
    private ParseObserver observer;

    /**
     * Constructs an instance.
     *
     * @param bytes {@code non-null;} the bytes of the file
     */
    public ConstantPoolParser(ByteArray bytes) {
        int size = bytes.getUnsignedShort(8); // constant_pool_count

        this.bytes = bytes;
        this.pool = new StdConstantPool(size);
        this.offsets = new int[size];
        this.endOffset = -1;
    }

    /**
     * Sets the parse observer for this instance.
     *
     * @param observer {@code null-ok;} the observer
     */
    public void setObserver(ParseObserver observer) {
        this.observer = observer;
    }

    /**
     * Gets the end offset of this constant pool in the {@code byte[]}
     * which it came from.
     *
     * @return {@code >= 10;} the end offset
     */
    public int getEndOffset() {
        parseIfNecessary();
        return endOffset;
    }

    /**
     * Gets the actual constant pool.
     *
     * @return {@code non-null;} the constant pool
     */
    public StdConstantPool getPool() {
        parseIfNecessary();
        return pool;
    }

    /**
     * Runs {@link #parse} if it has not yet been run successfully.
     */
    private void parseIfNecessary() {
        if (endOffset < 0) {
            parse();
        }
    }

    /**
     * Does the actual parsing.
     */
    private void parse() {
        determineOffsets();

        if (observer != null) {
            observer.parsed(bytes, 8, 2,
                            "constant_pool_count: " + Hex.u2(offsets.length));
            observer.parsed(bytes, 10, 0, "\nconstant_pool:");
            observer.changeIndent(1);
        }

        for (int i = 1; i < offsets.length; i++) {
            int offset = offsets[i];
            if ((offset != 0) && (pool.getOrNull(i) == null)) {
                parse0(i);
            }
        }

        if (observer != null) {
            for (int i = 1; i < offsets.length; i++) {
                Constant cst = pool.getOrNull(i);
                if (cst == null) {
                    continue;
                }
                int offset = offsets[i];
                int nextOffset = endOffset;
                for (int j = i + 1; j < offsets.length; j++) {
                    int off = offsets[j];
                    if (off != 0) {
                        nextOffset = off;
                        break;
                    }
                }
                observer.parsed(bytes, offset, nextOffset - offset,
                                Hex.u2(i) + ": " + cst.toString());
            }

            observer.changeIndent(-1);
            observer.parsed(bytes, endOffset, 0, "end constant_pool");
        }
    }

    /**
     * Populates {@link #offsets} and also completely parse utf8 constants.
     */
    private void determineOffsets() {
        int at = 10; // offset from the start of the file to the first cst
        int lastCategory;

        for (int i = 1; i < offsets.length; i += lastCategory) {
            offsets[i] = at;
            int tag = bytes.getUnsignedByte(at);
            switch (tag) {
                case CONSTANT_Integer:
                case CONSTANT_Float:
                case CONSTANT_Fieldref:
                case CONSTANT_Methodref:
                case CONSTANT_InterfaceMethodref:
                case CONSTANT_NameAndType: {
                    lastCategory = 1;
                    at += 5;
                    break;
                }
                case CONSTANT_Long:
                case CONSTANT_Double: {
                    lastCategory = 2;
                    at += 9;
                    break;
                }
                case CONSTANT_Class:
                case CONSTANT_String: {
                    lastCategory = 1;
                    at += 3;
                    break;
                }
                case CONSTANT_Utf8: {
                    lastCategory = 1;
                    at += bytes.getUnsignedShort(at + 1) + 3;
                    break;
                }
                default: {
                    ParseException ex =
                        new ParseException("unknown tag byte: " + Hex.u1(tag));
                    ex.addContext("...while preparsing cst " + Hex.u2(i) +
                                  " at offset " + Hex.u4(at));
                    throw ex;
                }
            }
        }

        endOffset = at;
    }

    /**
     * Parses the constant for the given index if it hasn't already been
     * parsed, also storing it in the constant pool. This will also
     * have the side effect of parsing any entries the indicated one
     * depends on.
     *
     * @param idx which constant
     * @return {@code non-null;} the parsed constant
     */
    private Constant parse0(int idx) {
        Constant cst = pool.getOrNull(idx);
        if (cst != null) {
            return cst;
        }

        int at = offsets[idx];

        try {
            int tag = bytes.getUnsignedByte(at);
            switch (tag) {
                case CONSTANT_Utf8: {
                    cst = parseUtf8(at);
                    break;
                }
                case CONSTANT_Integer: {
                    int value = bytes.getInt(at + 1);
                    cst = CstInteger.make(value);
                    break;
                }
                case CONSTANT_Float: {
                    int bits = bytes.getInt(at + 1);
                    cst = CstFloat.make(bits);
                    break;
                }
                case CONSTANT_Long: {
                    long value = bytes.getLong(at + 1);
                    cst = CstLong.make(value);
                    break;
                }
                case CONSTANT_Double: {
                    long bits = bytes.getLong(at + 1);
                    cst = CstDouble.make(bits);
                    break;
                }
                case CONSTANT_Class: {
                    int nameIndex = bytes.getUnsignedShort(at + 1);
                    CstUtf8 name = (CstUtf8) parse0(nameIndex);
                    cst = new CstType(Type.internClassName(name.getString()));
                    break;
                }
                case CONSTANT_String: {
                    int stringIndex = bytes.getUnsignedShort(at + 1);
                    CstUtf8 string = (CstUtf8) parse0(stringIndex);
                    cst = new CstString(string);
                    break;
                }
                case CONSTANT_Fieldref: {
                    int classIndex = bytes.getUnsignedShort(at + 1);
                    CstType type = (CstType) parse0(classIndex);
                    int natIndex = bytes.getUnsignedShort(at + 3);
                    CstNat nat = (CstNat) parse0(natIndex);
                    cst = new CstFieldRef(type, nat);
                    break;
                }
                case CONSTANT_Methodref: {
                    int classIndex = bytes.getUnsignedShort(at + 1);
                    CstType type = (CstType) parse0(classIndex);
                    int natIndex = bytes.getUnsignedShort(at + 3);
                    CstNat nat = (CstNat) parse0(natIndex);
                    cst = new CstMethodRef(type, nat);
                    break;
                }
                case CONSTANT_InterfaceMethodref: {
                    int classIndex = bytes.getUnsignedShort(at + 1);
                    CstType type = (CstType) parse0(classIndex);
                    int natIndex = bytes.getUnsignedShort(at + 3);
                    CstNat nat = (CstNat) parse0(natIndex);
                    cst = new CstInterfaceMethodRef(type, nat);
                    break;
                }
                case CONSTANT_NameAndType: {
                    int nameIndex = bytes.getUnsignedShort(at + 1);
                    CstUtf8 name = (CstUtf8) parse0(nameIndex);
                    int descriptorIndex = bytes.getUnsignedShort(at + 3);
                    CstUtf8 descriptor = (CstUtf8) parse0(descriptorIndex);
                    cst = new CstNat(name, descriptor);
                    break;
                }
            }
        } catch (ParseException ex) {
            ex.addContext("...while parsing cst " + Hex.u2(idx) +
                          " at offset " + Hex.u4(at));
            throw ex;
        } catch (RuntimeException ex) {
            ParseException pe = new ParseException(ex);
            pe.addContext("...while parsing cst " + Hex.u2(idx) +
                          " at offset " + Hex.u4(at));
            throw pe;
        }

        pool.set(idx, cst);
        return cst;
    }

    /**
     * Parses a utf8 constant.
     *
     * @param at offset to the start of the constant (where the tag byte is)
     * @return {@code non-null;} the parsed value
     */
    private CstUtf8 parseUtf8(int at) {
        int length = bytes.getUnsignedShort(at + 1);

        at += 3; // Skip to the data.

        ByteArray ubytes = bytes.slice(at, at + length);

        try {
            return new CstUtf8(ubytes);
        } catch (IllegalArgumentException ex) {
            // Translate the exception
            throw new ParseException(ex);
        }
    }
}
