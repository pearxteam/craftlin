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


@SideOnly(Side.CLIENT)
interface IGuiScreenProvider {
    val providedGuiScreen: IGuiScreen
}

@SideOnly(Side.CLIENT)
interface IOverlayProvider {
    val providedOverlay: ControlWrapper.OverlayContainer
}

@SideOnly(Side.CLIENT)
class ControlWrapper(private val cont: Control, override val providedGuiScreen: IGuiScreen) : Control(), IGuiScreenProvider, IOverlayProvider {
    @SideOnly(Side.CLIENT)
    inner class OverlayContainer : Control(), IGuiScreenProvider, IOverlayProvider {
        init {
            mouseUp = { _, _, _ -> controls.clear() }
        }

        override val providedGuiScreen: IGuiScreen
            get() = this@ControlWrapper.providedGuiScreen

        override val providedOverlay: OverlayContainer
            get() = this

        override var width: Int
            get() = this@ControlWrapper.width
            set(value) { /* not supported */  }

        override var height: Int
            get() = this@ControlWrapper.height
            set(value) { /* not supported */ }

        val isActive: Boolean
            get() = controls.isNotEmpty()
    }

    override val providedOverlay: OverlayContainer = OverlayContainer()

    init {
        init = { controls.add(cont) }
    }

    override var width: Int
        get() = providedGuiScreen.guiWidth
        set(value) { /* not supported */ }

    override var height: Int
        get() = providedGuiScreen.guiHeight
        set(value) { /* not supported */ }


    override fun invokeClose() {
        super.invokeClose()
        providedOverlay.invokeClose()
    }

    override fun invokeKeyDown(keycode: Int) {
        if (providedOverlay.isActive)
            providedOverlay.invokeKeyDown(keycode)
        else
            super.invokeKeyDown(keycode)
    }

    override fun invokeKeyUp(keycode: Int) {
        if (providedOverlay.isActive)
            providedOverlay.invokeKeyUp(keycode)
        else
            super.invokeKeyUp(keycode)
    }

    override fun invokeKeyPress(keycode: Int, char: Char) {
        if (providedOverlay.isActive)
            providedOverlay.invokeKeyPress(char.toInt(), keycode.toChar())
        else
            super.invokeKeyPress(char.toInt(), keycode.toChar())
    }

    override fun invokeMouseDown(button: Int, x: Int, y: Int) {
        if (providedOverlay.isActive)
            providedOverlay.invokeMouseDown(button, x, y)
        else
            super.invokeMouseDown(button, x, y)
    }

    override fun invokeMouseUp(button: Int, x: Int, y: Int) {
        if (providedOverlay.isActive)
            providedOverlay.invokeMouseUp(button, x, y)
        else
            super.invokeMouseUp(button, x, y)
    }

    override fun invokeMouseMove(x: Int, y: Int, dx: Int, dy: Int) {
        if (providedOverlay.isActive)
            providedOverlay.invokeMouseMove(x, y, dx, dy)
        else
            super.invokeMouseMove(x, y, dx, dy)
    }

    override fun invokeMouseWheel(delta: Int) {
        if (providedOverlay.isActive)
            providedOverlay.invokeMouseWheel(delta)
        else
            super.invokeMouseWheel(delta)
    }

    override fun invokeRender(stencilLevel: Int) {
        if (providedOverlay.isActive) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(0f, 0f, 150f)
            providedOverlay.invokeRender(stencilLevel)
            GlStateManager.popMatrix()
        }
        super.invokeRender(stencilLevel)
    }

    override fun invokeRenderSecondary() {
        if (providedOverlay.isActive) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(0f, 0f, 150f)
            providedOverlay.invokeRenderSecondary()
            GlStateManager.popMatrix()
        }
        super.invokeRenderSecondary()
    }

    fun invokeInit() = invokeInit(null)

    override fun invokeInit(parent: Control?) {
        providedOverlay.invokeInit(parent)
        super.invokeInit(parent)
    }

    override fun invokeUpdate() {
        providedOverlay.invokeUpdate()
        super.invokeUpdate()
    }
}
