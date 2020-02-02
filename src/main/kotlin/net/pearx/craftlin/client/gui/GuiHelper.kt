/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:SideOnly(Side.CLIENT)

package net.pearx.craftlin.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftlin.client.gui.control.ControlWrapper
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

fun GuiScreen.mouseX() = Mouse.getEventX() * width / mc.displayWidth
fun GuiScreen.mouseY() = Mouse.getEventY() * height / mc.displayHeight - 1

fun GuiScreen.closeGui() {
    with(Minecraft.getMinecraft()) {
        if (currentScreen == this)
            displayGuiScreen(null)
    }
}

fun ControlWrapper.handleKeyboardInput() {
    if (Keyboard.getEventKeyState())
        invokeKeyDown(Keyboard.getEventKey())
    else
        invokeKeyUp(Keyboard.getEventKey())
}

fun ControlWrapper.keyTyped(keyCode: Int, char: Char) {
    invokeKeyPress(keyCode, char)
}

fun ControlWrapper.handleMouseInput(gui: IGuiScreen) {
    val evX = gui.mouseX - x
    val evY = gui.mouseY - y
    if (Mouse.getEventButton() != -1) {
        if (Mouse.getEventButtonState())
            invokeMouseDown(Mouse.getEventButton(), evX, evY)
        else
            invokeMouseUp(Mouse.getEventButton(), evX, evY)
    }
    if (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0) {
        invokeMouseMove(evX, evY, evX - lastMouseX, evY - lastMouseY)
    }
    if (Mouse.getEventDWheel() != 0) {
        invokeMouseWheel(Mouse.getEventDWheel())
    }
}