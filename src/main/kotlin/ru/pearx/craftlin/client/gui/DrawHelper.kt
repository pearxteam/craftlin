/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.craftlin.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.RenderHelper.*
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11.*
import ru.pearx.carbidelin.colors.Color
import ru.pearx.carbidelin.math.FloatPoint
import ru.pearx.carbidelin.math.calculateQuadraticBezierPoints

@SideOnly(Side.CLIENT)
fun drawTexture(tex: ResourceLocation, x: Int, y: Int, width: Int, height: Int, u: Float = 0F, v: Float = 0F, texWidth: Float = width.toFloat(), texHeight: Float = height.toFloat()) {
    Minecraft.getMinecraft().textureManager.bindTexture(tex)
    GuiScreen.drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, texWidth, texHeight)
}

@SideOnly(Side.CLIENT)
fun drawString(string: String, x: Int, y: Int, color: Color, width: Int = -1, shadow: Boolean = true, scale: Float = 1F, rend: FontRenderer = Minecraft.getMinecraft().fontRenderer) {
    pushMatrix()
    translate(x.toFloat(), y.toFloat(), 0F)
    scale(scale, scale, 0F)
    val shouldUseWidth = width >= 0
    if (shouldUseWidth) {
        enableAlpha()
        rend.resetStyles()
    }
    var yToRender = 0
    if (shouldUseWidth) {
        for (s in string.lineSequence()) {
            rend.drawString(s, 0F, yToRender.toFloat(), color.argb, shadow)
            yToRender += getFontHeight(rend)
        }
    }
    else {
        for (s in rend.wrapFormattedStringToWidth(string, width).lineSequence()) {
            rend.renderStringAligned(s, 0, yToRender, width, color.argb, shadow)
            yToRender += getFontHeight(rend)
        }
    }
    color(1F, 1F, 1F, 1F)
    popMatrix()
}

@SideOnly(Side.CLIENT)
fun measureString(string: String, rend: FontRenderer = Minecraft.getMinecraft().fontRenderer): Int {
    var maxWidth = 0
    for (s in string.lineSequence()) {
        val width = rend.getStringWidth(s)
        if (width > maxWidth)
            maxWidth = width
    }
    return maxWidth
}

@SideOnly(Side.CLIENT)
fun measureChar(char: Char, rend: FontRenderer = Minecraft.getMinecraft().fontRenderer): Int = rend.getCharWidth(char)

@SideOnly(Side.CLIENT)
fun measureStringHeight(string: String, width: Int = -1, rend: FontRenderer = Minecraft.getMinecraft().fontRenderer): Int =
    (if (width >= 0) rend.wrapFormattedStringToWidth(string, width) else string).lines().size * getFontHeight(rend)

@SideOnly(Side.CLIENT)
fun getFontHeight(rend: FontRenderer = Minecraft.getMinecraft().fontRenderer) = rend.FONT_HEIGHT

@SideOnly(Side.CLIENT)
fun drawHoveringText(string: String, x: Int, y: Int, color: Color, width: Int = -1, shadow: Boolean = true, scale: Float = 1F, rend: FontRenderer = Minecraft.getMinecraft().fontRenderer) {
    pushMatrix()
    translate(10F, 0F, 1F)
    drawString(string, x, y, color, width, shadow, scale, rend)
    popMatrix()
}

@SideOnly(Side.CLIENT)
inline fun Tessellator.tessellate(glMode: Int, format: VertexFormat, block: BufferBuilder.() -> Unit) {
        buffer.begin(glMode, format)
        buffer.block()
        draw()
}

@SideOnly(Side.CLIENT)
inline fun tessellate(glMode: Int, format: VertexFormat, block: BufferBuilder.() -> Unit) = Tessellator.getInstance().tessellate(glMode, format, block)

@SideOnly(Side.CLIENT)
private inline fun draw2D(block: () -> Unit) {
        disableTexture2D()
        enableBlend()
        disableAlpha()
        tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO)
        shadeModel(GL_SMOOTH)
        block()
        shadeModel(GL_FLAT)
        disableBlend()
        enableAlpha()
        enableTexture2D()
}

@SideOnly(Side.CLIENT)
private inline fun drawLine(width: Float, block: () -> Unit) {
    GlStateManager.glLineWidth(width)
    draw2D(block)
    GlStateManager.glLineWidth(1F)
}

@SideOnly(Side.CLIENT)
fun drawGradientRectangle(x: Int, y: Int, width: Int, height: Int, color1: Color, color2: Color = color1, color3: Color = color1, color4: Color = color1) {
    draw2D {
        tessellate(GL_QUADS, DefaultVertexFormats.POSITION_COLOR) {
            val right = x + width
            val bottom = y + height
            begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR)
            pos(right.toDouble(), y.toDouble(), 0.0).color(color1.red, color1.green, color1.blue, color1.alpha).endVertex()
            pos(x.toDouble(), y.toDouble(), 0.0).color(color2.red, color2.green, color2.blue, color2.alpha).endVertex()
            pos(x.toDouble(), bottom.toDouble(), 0.0).color(color3.red, color3.green, color3.blue, color3.alpha).endVertex()
            pos(right.toDouble(), bottom.toDouble(), 0.0).color(color4.red, color4.green, color4.blue, color4.alpha).endVertex()
        }
    }
}

@SideOnly(Side.CLIENT)
fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int, width: Float, color1: Color, color2: Color) {
    drawLine(width) {
        tessellate(GL_LINES, DefaultVertexFormats.POSITION_COLOR) {
            pos(x1.toDouble(), y1.toDouble(), 0.0).color(color1.red, color1.green, color1.blue, color1.alpha).endVertex()
            pos(x2.toDouble(), y2.toDouble(), 0.0).color(color2.red, color2.green, color2.blue, color2.alpha).endVertex()
        }
    }
}

@SideOnly(Side.CLIENT)
fun drawBezier(deltaT: Float, width: Float, x0: Float, y0: Float, x1: Float, y1: Float, x2: Float, y2: Float, colorSupplier: (points: Array<FloatPoint>, point: FloatPoint, index: Int) -> Color) {
    drawLine(width) {
        tessellate(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR) {
            val bezierPoints = calculateQuadraticBezierPoints(deltaT, x0, y0, x1, y1, x2, y2)
            bezierPoints.forEachIndexed { index, point ->
                val color = colorSupplier(bezierPoints, point, index)
                pos(point.x.toDouble(), point.y.toDouble(), 0.0).color(color.red, color.green, color.blue, color.alpha).endVertex()
            }
        }
    }
}

@SideOnly(Side.CLIENT)
fun drawRectangle(x: Int, y: Int, width: Int, height: Int) {
    disableTexture2D()
    tessellate(GL_QUADS, DefaultVertexFormats.POSITION) {
        val bottom = y + height
        val right = x + width
        pos(x.toDouble(), bottom.toDouble(), 0.0).endVertex()
        pos(right.toDouble(), bottom.toDouble(), 0.0).endVertex()
        pos(right.toDouble(), y.toDouble(), 0.0).endVertex()
        pos(x.toDouble(), y.toDouble(), 0.0).endVertex()
    }
    enableTexture2D()
}

@SideOnly(Side.CLIENT)
fun drawEntity(ent: Entity, x: Float, y: Float, scale: Float, rotX: Float, rotY: Float, rotZ: Float) {
    enableColorMaterial()
    pushMatrix()
    translate(x, y, 50f)
    scale(-scale, scale, scale)
    rotate(180.0f, 0.0f, 0.0f, 1.0f)
    rotate(135.0f, 0.0f, 1.0f, 0.0f)
    enableStandardItemLighting()
    rotate(-135.0f, 0.0f, 1.0f, 0.0f)
    rotate(rotX, 1f, 0f, 0f)
    rotate(rotY, 0f, 1f, 0f)
    rotate(rotZ, 0f, 0f, 1f)
    with(Minecraft.getMinecraft().renderManager) {
        setPlayerViewY(180.0f)
        isRenderShadow = false
        renderEntity(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false)
        isRenderShadow = true
    }
    popMatrix()
    disableStandardItemLighting()
    disableRescaleNormal()
    setActiveTexture(OpenGlHelper.lightmapTexUnit)
    disableTexture2D()
    setActiveTexture(OpenGlHelper.defaultTexUnit)
}

@SideOnly(Side.CLIENT)
fun drawItemStackGUI(stack: ItemStack, itemRend: RenderItem = Minecraft.getMinecraft().renderItem, fontRend: FontRenderer = Minecraft.getMinecraft().fontRenderer, x: Int, y: Int, scale: Float) {
    pushMatrix()
    translate(x.toFloat(), y.toFloat(), 0f)
    scale(scale, scale, scale)
    enableGUIStandardItemLighting()
    with(itemRend) {
        renderItemAndEffectIntoGUI(Minecraft.getMinecraft().player, stack, 0, 0)
        renderItemOverlays(fontRend, stack, 0, 0)
    }
    disableStandardItemLighting()
    popMatrix()
}