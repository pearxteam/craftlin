/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.craftlin.client.gui.container

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.RenderItem
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import ru.pearx.craftlin.client.gui.*
import ru.pearx.craftlin.client.gui.control.Control
import ru.pearx.craftlin.client.gui.control.ControlWrapper

class CraftlinGuiContainer(inventorySlotsIn: Container, control: Control) : GuiContainer(inventorySlotsIn), IGuiScreen {
    val wrapper: ControlWrapper = ControlWrapper(control, this)

    override val guiWidth: Int
        get() = width
    override val guiHeight: Int
        get() = height
    override val mouseX: Int
        get() = mouseX()
    override val mouseY: Int
        get() = mouseY()
    override val renderItem: RenderItem
        get() = itemRender

    override fun drawHoveringText(stack: ItemStack, x: Int, y: Int) {
        renderToolTip(stack, x, y)
    }

    override fun drawHoveringText(text: String, x: Int, y: Int) {
        drawHoveringText(text.lines(), x, y)
    }

    override fun close() {
        closeGui()
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        pushMatrix();
        translate(-guiLeft.toDouble(), -guiTop.toDouble(), 0.0);
        wrapper.invokeRender(0)
        wrapper.invokeRenderSecondary()
        popMatrix()
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
        super.handleMouseInput()
        wrapper.handleMouseInput(this)
    }

    override fun onGuiClosed() {
        super.onGuiClosed()
        wrapper.invokeClose()
    }

    override fun setWorldAndResolution(mc: Minecraft, width: Int, height: Int) {
        super.setWorldAndResolution(mc, width, height)
        wrapper.invokeInit()
    }

    override fun updateScreen() {
        super.updateScreen()
        wrapper.invokeUpdate()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
}