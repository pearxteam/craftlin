/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.craftlin.client.gui.control

import net.minecraft.client.renderer.GlStateManager.*
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11.*
import org.lwjgl.util.Rectangle
import ru.pearx.carbidelin.collections.event.eventCollectionBy
import ru.pearx.carbidelin.math.IntPoint
import ru.pearx.craftlin.client.gui.IGuiScreen
import ru.pearx.craftlin.client.gui.drawRectangle
import ru.pearx.craftlin.client.translate

@SideOnly(Side.CLIENT)
fun controlCollection(parent: Control): MutableCollection<Control> {
    return eventCollectionBy(arrayListOf()) {
        add { element ->
            element.invokeInit(parent)
        }

        remove { element ->
            element.invokeClose()
        }

        clear { elements ->
            for (element in elements)
                element.invokeClose()
        }
    }
}

typealias ControlEvent = (() -> Unit)?
typealias ControlEventKey = ((keycode: Int) -> Unit)?
typealias ControlEventKeyPress = ((keycode: Int, char: Char) -> Unit)?
typealias ControlEventMouse = ((button: Int, x: Int, y: Int) -> Unit)?
typealias ControlEventMouseMove = ((x: Int, y: Int, dx: Int, dy: Int) -> Unit)?
typealias ControlEventMouseWheel = ((delta: Int) -> Unit)?
typealias ControlEventChildPosChanged = ((c: Control, prevX: Int, newX: Int) -> Unit)?
typealias ControlEventChildSizeChanged = ((c: Control, prevW: Int, newW: Int) -> Unit)?

@SideOnly(Side.CLIENT)
open class Control {
    private var initialized: Boolean = false

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

    val positionOnScreen: IntPoint
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
            return IntPoint(x, y)
        }
    //endregion

    //region Misc
    var isVisible = true

    var shouldStencil = false

    var lastMouseX: Int = 0
        private set

    var lastMouseY: Int = 0
        private set

    fun drawHoveringText(s: String, x: Int, y: Int) {
        pushMatrix()
        val screenPos = positionOnScreen
        translate(-screenPos.x, -screenPos.y, 0)
        guiScreen!!.drawHoveringText(s, x + screenPos.x, y + screenPos.y)
        popMatrix()
    }
    //endregion

    //region Overlay
    val overlay: ControlWrapper.OverlayContainer?
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

    //region Focusing
    var isFocused: Boolean = false
        private set

    fun setFocused() = root!!.setFocused(this)

    private fun setFocused(toSelect: Control) {
        if (isFocused && this != toSelect) {
            isFocused = false
            invokeMouseLeave()
        }
        if (!isFocused && this == toSelect) {
            isFocused = true
            invokeMouseEnter()
        }
        for (cont in controls)
            cont.setFocused(toSelect)
    }
    //endregion

    //region Events
    var render: ControlEvent = null
    var renderSecondary: ControlEvent = null
    var postRender: ControlEvent = null
    var postRenderSecondary: ControlEvent = null
    var keyDown: ControlEventKey = null
    var keyUp: ControlEventKey = null
    var keyPress: ControlEventKeyPress = null
    var mouseDown: ControlEventMouse = null
    var mouseUp: ControlEventMouse = null
    var mouseMove: ControlEventMouseMove = null
    var mouseEnter: ControlEvent = null
    var mouseLeave: ControlEvent = null
    var mouseWheel: ControlEventMouseWheel = null
    var init: ControlEvent = null
    var close: ControlEvent = null
    var update: ControlEvent = null
    var childXChanged: ControlEventChildPosChanged = null
    var childYChanged: ControlEventChildPosChanged = null
    var childWidthChanged: ControlEventChildSizeChanged = null
    var childHeightChanged: ControlEventChildSizeChanged = null
    //endregion

    //region Event Invocations
    open fun invokeRender(stencilLevel: Int) {
        if (!initialized)
            return
        if (isVisible) {
            pushMatrix()
            translate(transformedX, transformedY, 0)
            val flag = stencilLevel + 1
            if (shouldStencil) {
                glDisable(GL_TEXTURE_2D)
                glEnable(GL_STENCIL_TEST)
                glStencilFunc(GL_EQUAL, flag - 1, 0xFF)
                glStencilOp(GL_KEEP, GL_KEEP, GL_INCR)
                glStencilMask(0xFF)
                glColorMask(false, false, false, false)
                glDepthMask(false)
                if (stencilLevel == 0) {
                    glClearStencil(0)
                    glClear(GL_STENCIL_BUFFER_BIT)
                }
                drawRectangle(0, 0, width, height)
                glEnable(GL_TEXTURE_2D)
                glStencilFunc(GL_EQUAL, flag, 0xFF)
                glStencilMask(0)
                glColorMask(true, true, true, true)
                glDepthMask(true)
            }
            render?.invoke()
            for (cont in controls) {
                //todo check bounds
                cont.invokeRender(if (shouldStencil) stencilLevel + 1 else stencilLevel)
            }
            postRender?.invoke()
            if (shouldStencil) {
                glStencilFunc(GL_EQUAL, flag - 1, 0xFF)
            }
            if (shouldStencil && stencilLevel == 0)
                glDisable(GL_STENCIL_TEST)
            popMatrix()
        }
    }

    open fun invokeRenderSecondary() {
        if (!initialized)
            return
        if (isVisible) {
            pushMatrix()
            translate(transformedX, transformedY, 0)
            renderSecondary?.invoke()
            for (cont in controls) {
                cont.invokeRenderSecondary()
            }
            postRenderSecondary?.invoke()

            popMatrix()
        }
    }

    open fun invokeKeyDown(keycode: Int) {
        if (!initialized)
            return
        for (cont in controls)
            cont.invokeKeyDown(keycode)
        keyDown?.invoke(keycode)
    }

    open fun invokeKeyUp(keycode: Int) {
        if (!initialized)
            return
        for (cont in controls)
            cont.invokeKeyUp(keycode)
        keyUp?.invoke(keycode)
    }

    open fun invokeKeyPress(keycode: Int, char: Char) {
        if (!initialized)
            return
        for (cont in controls)
            cont.invokeKeyPress(keycode, char)
        keyPress?.invoke(keycode, char)
    }

    open fun invokeMouseDown(button: Int, x: Int, y: Int) {
        var last = true
        if (!initialized)
            return
        for (cont in controls) {
            if (Rectangle(cont.transformedX, cont.transformedY, cont.width, cont.height).contains(x, y)) {
                last = false
                cont.invokeMouseDown(button, x - cont.transformedX, y - cont.transformedY)
            }
        }
        if (last) {
            select()
            mouseDown?.invoke(button, x, y)
        }
    }

    open fun invokeMouseUp(button: Int, x: Int, y: Int) {
        var last = true
        if (!initialized)
            return
        for (cont in controls) {
            if (Rectangle(cont.transformedX, cont.transformedY, cont.width, cont.height).contains(x, y)) {
                last = false
                cont.invokeMouseUp(button, x - cont.transformedX, y - cont.transformedY)
            }
        }
        if (last)
            mouseUp?.invoke(button, x, y)
    }

    open fun invokeMouseMove(x: Int, y: Int, dx: Int, dy: Int) {
        if (!initialized)
            return
        var last = true
        for (cont in controls) {
            if (cont.transformedX <= x && cont.transformedX + cont.width >= x && cont.transformedY <= y && cont.transformedY + cont.height >= y) {
                cont.invokeMouseMove(x - cont.transformedX, y - cont.transformedY, dx, dy)
                last = false
            }
        }
        mouseMove?.invoke(x, y, dx, dy)
        lastMouseX = x
        lastMouseY = y
        if (last)
            setFocused()
    }

    open fun invokeMouseEnter() {
        if (!initialized)
            return
        mouseEnter?.invoke()
    }

    open fun invokeMouseLeave() {
        if (!initialized)
            return
        mouseLeave?.invoke()
    }

    open fun invokeMouseWheel(delta: Int) {
        if (!initialized)
            return
        for (cont in controls) {
            cont.invokeMouseWheel(delta)
        }
        mouseWheel?.invoke(delta)
    }

    open fun invokeInit(parent: Control?) {
        if (!initialized) {
            initialized = true
            this.parent = parent
            init?.invoke()
        }
        triggerMove()
    }

    //todo invokeChildAdd, invokeChildRemove, invokeChildClear with before and after

    open fun invokeChildXChanged(c: Control, prevX: Int, newX: Int) {
        if (!initialized)
            return
        childXChanged?.invoke(c, prevX, newX)
    }

    open fun invokeChildYChanged(c: Control, prevY: Int, newY: Int) {
        if (!initialized)
            return
        childYChanged?.invoke(c, prevY, newY)
    }

    open fun invokeChildWidthChanged(c: Control, prevW: Int, newW: Int) {
        if (!initialized)
            return
        childWidthChanged?.invoke(c, prevW, newW)
    }

    open fun invokeChildHeightChanged(c: Control, prevH: Int, newH: Int) {
        if (!initialized)
            return
        childHeightChanged?.invoke(c, prevH, newH)
    }

    open fun invokeUpdate() {
        if (!initialized)
            return
        update?.invoke()
        for (c in controls)
            c.invokeUpdate()
    }

    open fun invokeClose() {
        if (!initialized)
            return
        parent = null
        invokeClosePrivate()
    }

    private fun invokeClosePrivate() {
        for (cont in controls)
            cont.invokeClosePrivate()
        close?.invoke()
    }
    //endregion
}