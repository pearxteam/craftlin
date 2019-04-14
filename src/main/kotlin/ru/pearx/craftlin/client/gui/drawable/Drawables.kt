/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.craftlin.client.gui.drawable

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.Minecraft
import ru.pearx.craftlin.client.gui.IGuiScreen
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.thread.SidedThreadGroups.CLIENT
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import ru.pearx.craftlin.client.gui.drawTexture


@SideOnly(Side.CLIENT)
class SimpleDrawable(
    private val texture: ResourceLocation,
    private val textureWidth: Int,
    private val textureHeight: Int,
    override val width: Int = textureWidth,
    override val height: Int = textureHeight,
    private val u: Int = 0,
    private val v: Int = 0,
    private val transparent: Boolean = true
) : IGuiDrawable {

    override fun draw(screen: IGuiScreen, x: Int, y: Int) {
        drawTexture(texture, x, y, width, height, u, v, textureWidth, textureHeight, transparent)
    }
}

@SideOnly(Side.CLIENT)
class AnimatedDrawable(
    private val texture: ResourceLocation,
    private val textureWidth: Int,
    private val textureHeight: Int,
    private val textureElementWidth: Int,
    private val textureElementHeight: Int,
    private val msDivider: Int,
    private val elementWidth: Int = textureElementWidth,
    private val elementHeight: Int = textureElementHeight,
    private val xOffset: Int = 0,
    private val yOffset: Int = 0,
    private val transparent: Boolean = true
) : IGuiDrawable {
    private val cycleSize = textureHeight / textureElementHeight
    private var current = 0

    override val width: Int
        get() = elementWidth

    override val height: Int
        get() = elementHeight

    override fun draw(screen: IGuiScreen, x: Int, y: Int) {
        current = (System.currentTimeMillis() / msDivider % cycleSize).toInt();
        drawTexture(texture, x + xOffset, y + yOffset, elementWidth, elementHeight, 0, current * textureElementHeight, textureWidth, textureHeight, transparent)
    }
}