/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.craftlin.client.gui.drawable

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftlin.client.gui.IGuiScreen
import net.pearx.craftlin.client.gui.drawItemStackGUI
import net.pearx.craftlin.client.translate


@SideOnly(Side.CLIENT)
abstract class ItemDrawable(
    private val scale: Float = 1F
) : IGuiDrawable {
    abstract val stack: ItemStack

    override val width: Int
        get() = (16 * scale).toInt()

    override val height: Int
        get() = (16 * scale).toInt()

    override fun draw(screen: IGuiScreen, x: Int, y: Int) {
        drawItemStackGUI(stack, x, y, scale, screen.renderItem, screen.fontRenderer)
    }

    fun drawTooltip(screen: IGuiScreen, x: Int, y: Int, mouseX: Int = screen.mouseX, mouseY: Int = screen.mouseY, offsetX: Int = 0, offsetY: Int = 0) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            pushMatrix()
            translate(-offsetX, -offsetY, 0)
            screen.drawHoveringText(stack, mouseX + offsetX, mouseY + offsetY)
            popMatrix()
        }
    }

    fun drawWithTooltip(screen: IGuiScreen, x: Int, y: Int, mouseX: Int, mouseY: Int, screenX: Int, screenY: Int) {
        draw(screen, x, y)
        drawTooltip(screen, x, y, mouseX, mouseY, screenX, screenY)
    }
}

@SideOnly(Side.CLIENT)
open class SimpleItemDrawable(override val stack: ItemStack, scale: Float = 1F) : ItemDrawable(scale)

@SideOnly(Side.CLIENT)
class MultiItemDrawable(
    val stacks: List<ItemStack>,
    scale: Float = 1F,
    val msDivider: Int = 1000
) : ItemDrawable(scale) {
    constructor(vararg stacks: ItemStack, scale: Float = 1F, msDivider: Int = 1000) : this(listOf(*stacks), scale, msDivider)

    override val stack: ItemStack
        get() = stacks[(System.currentTimeMillis() / msDivider % stacks.size).toInt()]
}