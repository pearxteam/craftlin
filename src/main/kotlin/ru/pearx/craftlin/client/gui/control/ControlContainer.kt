/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.craftlin.client.gui.control

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import ru.pearx.craftlin.client.gui.IGuiScreen
import net.minecraft.client.renderer.GlStateManager
import ru.pearx.craftlin.client.gui.IGuiScreen
import ru.pearx.craftlin.client.gui.IGuiScreen


@SideOnly(Side.CLIENT)
interface IGuiScreenProvider {
    val providedGuiScreen: IGuiScreen
}

@SideOnly(Side.CLIENT)
interface IOverlayProvider {
    val providedOverlay: GuiControlContainer.OverlayContainer
}

@SideOnly(Side.CLIENT)
class GuiControlContainer(private val cont: Control, gs: IGuiScreen) : Control(), IGuiScreenProvider, IOverlayProvider {
    @SideOnly(Side.CLIENT)
    inner class OverlayContainer : Control(), IGuiScreenProvider, IOverlayProvider {
        override val providedGuiScreen: IGuiScreen
            get() = this@GuiControlContainer.providedGuiScreen

        override val providedOverlay: OverlayContainer
            get() = this

        override var width: Int
            get() = this@GuiControlContainer.width
            set(value) { /* not supported */  }

        override var height: Int
            get() = this@GuiControlContainer.height
            set(value) { /* not supported */ }

        val isActive: Boolean
            get() = controls.isNotEmpty()

        override fun mouseUp(button: Int, x: Int, y: Int) {
            controls.clear()
        }
    }
}
