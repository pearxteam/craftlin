/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.craftlin.client.gui.control.standard.containers

import net.minecraft.client.gui.inventory.GuiContainer
import net.pearx.craftlin.client.gui.control.Control


open class GuiOnScreen : Control() {
    override var x: Int
        get() = guiScreen?.let { (it.guiWidth - width) / 2 } ?: 0
        set(value) {}

    override var y: Int
        get() = guiScreen?.let { (it.guiHeight - height) / 2 } ?: 0
        set(value) {}
}

open class GuiOnScreenContainer(private val container: GuiContainer) : GuiOnScreen() {
    override var width: Int
        get() = container.xSize
        set(value) {}
    override var height: Int
        get() = container.ySize
        set(value) {}
}