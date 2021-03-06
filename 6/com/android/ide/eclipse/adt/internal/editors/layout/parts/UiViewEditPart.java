/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.ide.eclipse.adt.internal.editors.layout.parts;

import com.android.ide.eclipse.adt.internal.editors.uimodel.UiElementNode;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;

/**
 * Graphical edit part for an {@link UiElementNode} that represents a View.
 *
 * @since GLE1
 */
public class UiViewEditPart extends UiElementEditPart {

    public UiViewEditPart(UiElementNode uiElementNode) {
        super(uiElementNode);
    }

    @Override
    protected IFigure createFigure() {
        IFigure f = new ElementFigure();
        f.setLayoutManager(new XYLayout());
        return f;
    }

    @Override
    protected void showSelection() {
        IFigure f = getFigure();
        if (f instanceof ElementFigure) {
            ((ElementFigure) f).setSelected(true);
        }
    }

    @Override
    protected void hideSelection() {
        IFigure f = getFigure();
        if (f instanceof ElementFigure) {
            ((ElementFigure) f).setSelected(false);
        }
    }
}
