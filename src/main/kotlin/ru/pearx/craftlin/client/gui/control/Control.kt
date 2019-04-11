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
import ru.pearx.craftlin.client.gui.IGuiScreen
import ru.pearx.craftlin.client.gui.IGuiScreenProvider


@SideOnly(Side.CLIENT)
open class Control {
    //region Children and Parent
    val controls = controlCollection(this)

    var parent: Control? = null
        private set(value) {
            field = value
            if (parent != null) {
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

    private var width: Int = 0
    private var height: Int = 0
    private var x: Int = 0
    private var y: Int = 0
    var isVisible = true
    var isFocused: Boolean = false
        private set
    var isSelected: Boolean = false
        private set
    var lastMouseX: Int = 0
        private set
    var lastMouseY: Int = 0
        private set
    private var initialized: Boolean = false


    val transformedX: Int
        get() = getX() + (if (parent == null) 0 else parent!!.offsetX) + ownOffsetX

    val transformedY: Int
        get() = getY() + (if (parent == null) 0 else parent!!.offsetY) + ownOffsetY

    val offsetX: Int
        get() = 0

    val offsetY: Int
        get() = 0

    val ownOffsetX: Int
        get() = 0

    val ownOffsetY: Int
        get() = 0

    val overlay: GuiControlContainer.OverlayContainer?
        get() {
            val c = root
            return if (c is IOverlayProvider) c!!.overlay else null
        }

    val posOnScreen: Point
        get() {
            var x = transformedX
            var y = transformedY
            var parent = parent
            while (parent != null) {
                x += parent.transformedY
                y += parent.transformedY
                parent = parent.parent
            }
            return Point(x, y)
        }

    fun getWidth(): Int {
        return width
    }

    @JvmOverloads
    fun setWidth(width: Int, triggerMove: Boolean = true) {
        val prev = this.width
        this.width = width
        if (parent != null)
            parent!!.invokeChildWidthChanged(this, prev, width)
        if (triggerMove)
            triggerMove()
    }

    fun getHeight(): Int {
        return height
    }

    @JvmOverloads
    fun setHeight(height: Int, triggerMove: Boolean = true) {
        val prev = this.height
        this.height = height
        if (parent != null)
            parent!!.invokeChildHeightChanged(this, prev, height)
        if (triggerMove)
            triggerMove()
    }

    fun triggerMove() {
        if (initialized) {
            if (guiScreen != null && root != null) {
                val main = root
                main!!.invokeMouseMove(guiScreen!!.mouseX, guiScreen!!.mouseY, 0, 0)
            }
        }
    }

    fun getX(): Int {
        return x
    }

    @JvmOverloads
    fun setX(x: Int, triggerMove: Boolean = true) {
        val prev = this.x
        this.x = x
        if (parent != null)
            parent!!.invokeChildXChanged(this, prev, x)
        if (triggerMove)
            triggerMove()
    }

    fun getY(): Int {
        return y
    }

    @JvmOverloads
    fun setY(y: Int, triggerMove: Boolean = true) {
        val prev = this.y
        this.y = y
        if (parent != null)
            parent!!.invokeChildYChanged(this, prev, y)
        if (triggerMove)
            triggerMove()
    }


    @JvmOverloads
    fun setPos(x: Int, y: Int, triggerMove: Boolean = true) {
        val prevX = this.x
        val prevY = this.y
        this.x = x
        this.y = y
        if (parent != null) {
            parent!!.invokeChildXChanged(this, prevX, x)
            parent!!.invokeChildYChanged(this, prevY, y)
        }
        if (triggerMove)
            triggerMove()
    }

    @JvmOverloads
    fun setSize(w: Int, h: Int, triggerMove: Boolean = true) {
        val prevW = this.width
        val prevH = this.height
        this.width = w
        this.height = h
        if (parent != null) {
            parent!!.invokeChildWidthChanged(this, prevW, w)
            parent!!.invokeChildHeightChanged(this, prevH, h)
        }
        if (triggerMove)
            triggerMove()
    }

    fun select() {
        root!!.select(this)
    }

    private fun select(toSelect: Control) {
        isSelected = this === toSelect
        for (child in controls) {
            child.select(toSelect)
        }
    }

    fun shouldStencil(): Boolean {
        return false
    }

    //EVENTS


    fun render() {

    }

    fun render2() {

    }

    fun postRender() {

    }

    fun postRender2() {

    }

    fun keyDown(keycode: Int) {

    }

    fun keyUp(keycode: Int) {

    }

    fun keyPress(key: Char, keycode: Int) {

    }

    fun mouseDown(button: Int, x: Int, y: Int) {

    }

    fun mouseUp(button: Int, x: Int, y: Int) {

    }

    fun mouseMove(x: Int, y: Int, dx: Int, dy: Int) {

    }

    fun mouseEnter() {

    }

    fun mouseLeave() {

    }

    fun mouseWheel(delta: Int) {

    }

    fun init() {

    }

    fun close() {

    }

    fun update() {

    }

    fun childXChanged(c: Control, prevX: Int, newX: Int) {

    }

    fun childYChanged(c: Control, prevY: Int, newY: Int) {

    }

    fun childWidthChanged(c: Control, prevW: Int, newW: Int) {

    }

    fun childHeightChanged(c: Control, prevH: Int, newH: Int) {

    }

    //EVENT INVOKES


    fun invokeRender(stencilLevel: Int) {
        if (!initialized)
            return
        if (isVisible) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(transformedX.toFloat(), transformedY.toFloat(), 0f)
            val stenc = shouldStencil()
            val flag = stencilLevel + 1
            if (stenc) {
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
            if (stenc) {
                GL11.glStencilFunc(GL11.GL_EQUAL, flag - 1, 0xFF)
            }
            if (stenc && stencilLevel == 0)
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

    fun invokeInit() {
        if (!initialized) {
            initialized = true
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
        for (cont in controls)
            cont.invokeClose()
        close()
    }

    fun drawHoveringText(s: String, x: Int, y: Int) {
        GlStateManager.pushMatrix()
        val pos = posOnScreen
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