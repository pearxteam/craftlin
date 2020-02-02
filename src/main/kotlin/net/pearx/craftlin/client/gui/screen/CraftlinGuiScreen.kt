/*
 * Copyright © 2019-2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.craftlin.client.gui.screen

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.RenderItem
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftlin.client.gui.*
import net.pearx.craftlin.client.gui.control.Control
import net.pearx.craftlin.client.gui.control.ControlWrapper


@SideOnly(Side.CLIENT)
class CraftlinGuiScreen(control: Control) : GuiScreen(), IGuiScreen {
    val wrapper: ControlWrapper = ControlWrapper(control, this)

    override val guiWidth: Int
        get() = width

    override val guiHeight: Int
        get() = width

    override val mouseX: Int
        get() = mouseX()

    override val mouseY: Int
        get() = mouseY()

    override val renderItem: RenderItem
        get() = itemRender

    override val fontRenderer: FontRenderer
        get() = super.fontRenderer

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        wrapper.invokeRender(0)
        wrapper.invokeRenderSecondary()
    }

    override fun handleKeyboardInput() {
        super.handleKeyboardInput()
        wrapper.handleKeyboardInput()
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)
        wrapper.keyTyped(keyCode, typedChar)
    }

    override fun handleMouseInput() {
        wrapper.handleMouseInput(this)
    }

    override fun drawHoveringText(stack: ItemStack, x: Int, y: Int) {
        renderToolTip(stack, x, y)
    }

    override fun drawHoveringText(text: String, x: Int, y: Int) {
        drawHoveringText(text.lines(), x, y)
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun onGuiClosed() {
        wrapper.invokeClose()
    }

    override fun setWorldAndResolution(mc: Minecraft, width: Int, height: Int) {
        super.setWorldAndResolution(mc, width, height)
        wrapper.invokeInit()
    }

    override fun updateScreen() {
        wrapper.invokeUpdate()
    }

    override fun close() {
        closeGui()
    }
}
