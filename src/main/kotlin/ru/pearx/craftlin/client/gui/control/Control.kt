/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.craftlin.client.gui.control

import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import org.lwjgl.util.Point
import ru.pearx.craftlin.client.gui.IGuiScreen
import ru.pearx.craftlin.client.gui.IGuiScreenProvider

@SideOnly(Side.CLIENT)
open class Control {
    //region Children and Parent
    val controls = controlCollection(this)

    var parent: Control? = null
        private set(value) {
            field = value
            if (value != null) {
                root = retrieveRoot()
                guiScreen = retrieveGuiScreen()
            }
            else {
                guiScreen = null
                root = null
            }
        }

    var guiScreen: IGuiScreen? = null
        private set(value) {
            if (field != value) {
                field = value
                for (child in controls)
                    child.guiScreen = value
            }
        }

    var root: Control? = null
        private set(value) {
            if (field != value) {
                field = value
                for (child in controls)
                    child.root = value
            }
        }

    private fun retrieveRoot(): Control? {
        var control = this
        while (control.parent != null) {
            control = control.parent!!
        }
        return control
    }

    private fun retrieveGuiScreen(): IGuiScreen? = if (root is IGuiScreenProvider) (root as IGuiScreenProvider).providedGuiScreen else null
    //endregion

    //region Position & Size
    open var width: Int = 0
        set(value) {
            val prev = field
            field = value
            parent?.invokeChildWidthChanged(this, prev, value)
            triggerMove()
        }
    open var height: Int = 0
        set(value) {
            val prev = field
            field = value
            parent?.invokeChildHeightChanged(this, prev, value)
            triggerMove()
        }
    open var x: Int = 0
        set(value) {
            val prev = field
            field = value
            parent?.invokeChildXChanged(this, prev, value)
            triggerMove()
        }
    open var y: Int = 0
        set(value) {
            val prev = field
            field = value
            parent?.invokeChildYChanged(this, prev, value)
            triggerMove()
        }

    //todo val postition

    private fun triggerMove() {
        if (initialized) {
            if (guiScreen != null && root != null) {
                root!!.invokeMouseMove(guiScreen!!.mouseX, guiScreen!!.mouseY, 0, 0)
            }
        }
    }

    var offsetXChildren: Int = 0

    var offsetYChildren: Int = 0

    var offsetXOwn: Int = 0

    var offsetYOwn: Int = 0

    val transformedX: Int
        get() = x + (parent?.offsetXChildren ?: 0) + offsetXOwn

    val transformedY: Int
        get() = y + (parent?.offsetYChildren ?: 0) + offsetYOwn

    val positionOnScreen: Point
        get() {
            var x = transformedX
            var y = transformedY

            var parent = parent
            while (parent != null) {
                parent = parent.run {
                    x += this.transformedX
                    y += this.transformedY
                    this.parent
                }
            }
            return Point(x, y)
        }
    //endregion

    //region Misc Properties
    var isVisible = true

    var shouldStencil = false

    var isFocused: Boolean = false
        private set

    var lastMouseX: Int = 0
        private set

    var lastMouseY: Int = 0
        private set

    private var initialized: Boolean = false
    //endregion

    //region Overlay
    val overlay: GuiControlContainer.OverlayContainer?
        get() = if (root is IOverlayProvider) root?.overlay else null
    //endregion

    //region Selecting
    var isSelected: Boolean = false
        private set

    fun select() {
        root!!.select(this)
    }

    private fun select(toSelect: Control) {
        isSelected = this == toSelect
        for (child in controls) {
            child.select(toSelect)
        }
    }
    //endregion


    //region Events
    open fun render() {

    }

    open fun render2() {

    }

    open fun postRender() {

    }

    open fun postRender2() {

    }

    open fun keyDown(keycode: Int) {

    }

    open fun keyUp(keycode: Int) {

    }

    open fun keyPress(key: Char, keycode: Int) {

    }

    open fun mouseDown(button: Int, x: Int, y: Int) {

    }

    open fun mouseUp(button: Int, x: Int, y: Int) {

    }

    open fun mouseMove(x: Int, y: Int, dx: Int, dy: Int) {

    }

    open fun mouseEnter() {

    }

    open fun mouseLeave() {

    }

    open fun mouseWheel(delta: Int) {

    }

    open fun init() {

    }

    open fun close() {

    }

    open fun update() {

    }

    open fun childXChanged(c: Control, prevX: Int, newX: Int) {

    }

    open fun childYChanged(c: Control, prevY: Int, newY: Int) {

    }

    open fun childWidthChanged(c: Control, prevW: Int, newW: Int) {

    }

    open fun childHeightChanged(c: Control, prevH: Int, newH: Int) {

    }
    //endregion

    //region Event Invokes
    fun invokeRender(stencilLevel: Int) {
        if (!initialized)
            return
        if (isVisible) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(transformedX.toFloat(), transformedY.toFloat(), 0f)
            val flag = stencilLevel + 1
            if (shouldStencil) {
                GL11.glDisable(GL11.GL_TEXTURE_2D)
                GL11.glEnable(GL11.GL_STENCIL_TEST)
                GL11.glStencilFunc(GL11.GL_EQUAL, flag - 1, 0xFF)
                GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_INCR)
                GL11.glStencilMask(0xFF)
                GL11.glColorMask(false, false, false, false)
                GL11.glDepthMask(false)
                if (stencilLevel == 0) {
                    GL11.glClearStencil(0)
                    GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT)
                }
                DrawingTools.drawRectangle(0, 0, getWidth(), getHeight())
                GL11.glEnable(GL11.GL_TEXTURE_2D)
                GL11.glStencilFunc(GL11.GL_EQUAL, flag, 0xFF)
                GL11.glStencilMask(0)
                GL11.glColorMask(true, true, true, true)
                GL11.glDepthMask(true)
            }
            render()
            for (cont in controls) {
                //todo check bounds
                cont.invokeRender(if (stenc) stencilLevel + 1 else stencilLevel)
            }
            postRender()
            if (shouldStencil) {
                GL11.glStencilFunc(GL11.GL_EQUAL, flag - 1, 0xFF)
            }
            if (shouldStencil && stencilLevel == 0)
                GL11.glDisable(GL11.GL_STENCIL_TEST)
            GlStateManager.popMatrix()
        }
    }

    fun invokeRender2() {
        if (!initialized)
            return
        if (isVisible) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(transformedX.toFloat(), transformedY.toFloat(), 0f)
            render2()
            for (cont in controls) {
                cont.invokeRender2()
            }
            postRender2()

            GlStateManager.popMatrix()
        }
    }

    fun invokeKeyDown(keycode: Int) {
        if (!initialized)
            return
        for (cont in controls)
            cont.invokeKeyDown(keycode)
        keyDown(keycode)
    }

    fun invokeKeyUp(keycode: Int) {
        if (!initialized)
            return
        for (cont in controls)
            cont.invokeKeyUp(keycode)
        keyUp(keycode)
    }

    fun invokeKeyPress(key: Char, keycode: Int) {
        if (!initialized)
            return
        for (cont in controls)
            cont.invokeKeyPress(key, keycode)
        keyPress(key, keycode)
    }

    fun invokeMouseDown(button: Int, x: Int, y: Int) {
        var last = true
        if (!initialized)
            return
        for (cont in controls) {
            if (Rectangle(cont.transformedX, cont.transformedY, cont.getWidth(), cont.getHeight()).contains(x, y)) {
                last = false
                cont.invokeMouseDown(button, x - cont.transformedX, y - cont.transformedY)
            }
        }
        if (last) {
            select()
            mouseDown(button, x, y)
        }
    }

    fun invokeMouseUp(button: Int, x: Int, y: Int) {
        var last = true
        if (!initialized)
            return
        for (cont in controls) {
            if (Rectangle(cont.transformedX, cont.transformedY, cont.getWidth(), cont.getHeight()).contains(x, y)) {
                last = false
                cont.invokeMouseUp(button, x - cont.transformedX, y - cont.transformedY)
            }
        }
        if (last)
            mouseUp(button, x, y)
    }

    fun invokeMouseMove(x: Int, y: Int, dx: Int, dy: Int) {
        if (!initialized)
            return
        var last = true
        for (cont in controls) {
            if (cont.transformedX <= x && cont.transformedX + cont.getWidth() >= x && cont.transformedY <= y && cont.transformedY + cont.getHeight() >= y) {
                cont.invokeMouseMove(x - cont.transformedX, y - cont.transformedY, dx, dy)
                last = false
            }
        }
        mouseMove(x, y, dx, dy)
        lastMouseX = x
        lastMouseY = y
        if (last)
            setFocused(root!!, this)
    }

    fun invokeMouseEnter() {
        if (!initialized)
            return
        mouseEnter()
    }

    fun invokeMouseLeave() {
        if (!initialized)
            return
        mouseLeave()
    }

    fun invokeMouseWheel(delta: Int) {
        if (!initialized)
            return
        for (cont in controls) {
            cont.invokeMouseWheel(delta)
        }
        mouseWheel(delta)
    }

    fun invokeInit(parent: Control) {
        if (!initialized) {
            initialized = true
            this.parent = parent
            init()
        }
        triggerMove()
    }

    //todo invokeChildAdd, invokeChildRemove, invokeChildClear with before and after

    fun invokeChildXChanged(c: Control, prevX: Int, newX: Int) {
        if (!initialized)
            return
        childXChanged(c, prevX, newX)
    }

    fun invokeChildYChanged(c: Control, prevY: Int, newY: Int) {
        if (!initialized)
            return
        childYChanged(c, prevY, newY)
    }

    fun invokeChildWidthChanged(c: Control, prevW: Int, newW: Int) {
        if (!initialized)
            return
        childWidthChanged(c, prevW, newW)
    }

    fun invokeChildHeightChanged(c: Control, prevH: Int, newH: Int) {
        if (!initialized)
            return
        childHeightChanged(c, prevH, newH)
    }

    fun invokeUpdate() {
        if (!initialized)
            return
        update()
        for (c in controls)
            c.invokeUpdate()
    }

    fun invokeClose() {
        if (!initialized)
            return
        parent = null
        invokeClosePrivate()
    }

    private fun invokeClosePrivate() {
        for (cont in controls)
            cont.invokeClosePrivate()
        close()
    }
    //endregion

    fun drawHoveringText(s: String, x: Int, y: Int) {
        GlStateManager.pushMatrix()
        val pos = positionOnScreen
        GlStateManager.translate(-pos.getX(), -pos.getY(), 0)
        guiScreen!!.drawHovering(s, x + pos.getX(), y + pos.getY())
        GlStateManager.popMatrix()
    }

    companion object {

        fun setFocused(c: Control, select: Control) {
            if (c.isFocused && c !== select) {
                c.isFocused = false
                c.invokeMouseLeave()
            }
            if (!c.isFocused && c === select) {
                c.isFocused = true
                c.invokeMouseEnter()
            }
            for (cont in c.controls)
                setFocused(cont, select)
        }
    }
}