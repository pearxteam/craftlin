/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.craftlin.client.gui

import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Mouse
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.client.config.GuiUtils


val GuiScreen.mouseX
    get() = Mouse.getEventX() * width / mc.displayWidth

val GuiScreen.mouseY
    get() = Mouse.getEventY() * height / mc.displayHeight - 1