/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.craftlin.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderItem
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.client.config.GuiUtils
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Mouse


/**
 * A GUI Screen interface.
 */
@SideOnly(Side.CLIENT)
interface IGuiScreen {
    /**
     * The width of the screen.
     */
    val guiWidth: Int

    /**
     * The height of the screen.
     */
    val guiHeight: Int

    /**
     * The X position of the mouse pointer.
     */
    val mouseX: Int

    /**
     * The Y position of the mouse pointer.
     */
    val mouseY: Int

    /**
     * The RenderItem instance.
     */
    val renderItem: RenderItem

    fun drawHoveringText(stack: ItemStack, x: Int, y: Int)

    fun drawHoveringText(text: String, x: Int, y: Int)

    fun close()
}

@SideOnly(Side.CLIENT)
object OverlayGui : IGuiScreen {

    override val guiWidth: Int
        get() = Minecraft.getMinecraft().displayWidth

    override val guiHeight: Int
        get() = Minecraft.getMinecraft().displayHeight

    override val mouseX: Int
        get() = Mouse.getEventX()

    override val mouseY: Int
        get() = guiHeight - Mouse.getEventY() - 1

    override val renderItem: RenderItem
        get() = Minecraft.getMinecraft().renderItem

    override fun drawHoveringText(stack: ItemStack, x: Int, y: Int) {
        GuiUtils.drawHoveringText(stack, listOf(), x, y, guiWidth, guiHeight, 300, Minecraft.getMinecraft().fontRenderer)
    }

    override fun drawHoveringText(text: String, x: Int, y: Int) {
        GuiUtils.drawHoveringText(text.lines(), x, y, guiWidth, guiHeight, 300, Minecraft.getMinecraft().fontRenderer)
    }

    override fun close() {
    }
}