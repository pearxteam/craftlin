/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.craftlin.client.gui.drawable

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.pearx.craftlin.Craftlin
import net.pearx.craftlin.client.gui.IGuiScreen
import net.pearx.craftlin.client.gui.drawEntity
import net.pearx.craftlin.client.gui.drawTexture


@SideOnly(Side.CLIENT)
class SimpleDrawable(
    val texture: ResourceLocation,
    val textureWidth: Int,
    val textureHeight: Int,
    override val width: Int = textureWidth,
    override val height: Int = textureHeight,
    val u: Int = 0,
    val v: Int = 0,
    val transparent: Boolean = true
) : IGuiDrawable {

    override fun draw(screen: IGuiScreen, x: Int, y: Int) {
        drawTexture(texture, x, y, width, height, u, v, textureWidth, textureHeight, transparent)
    }
}

@SideOnly(Side.CLIENT)
class AnimatedDrawable(
    val texture: ResourceLocation,
    val textureWidth: Int,
    val textureHeight: Int,
    val textureElementWidth: Int,
    val textureElementHeight: Int,
    val msDivider: Int,
    val elementWidth: Int = textureElementWidth,
    val elementHeight: Int = textureElementHeight,
    val xOffset: Int = 0,
    val yOffset: Int = 0,
    val transparent: Boolean = true
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

@SideOnly(Side.CLIENT)
inline fun <reified T : Entity> EntityDrawable(scale: Float, yOffset: Double): EntityDrawable = EntityDrawable(T::class.java, scale, yOffset)

@SideOnly(Side.CLIENT)
class EntityDrawable(
    val entityClass: Class<out Entity>,
    val scale: Float,
    val yOffset: Double
) : IGuiDrawable {
    private var entity: Entity? = null
    override val width: Int
        get() = 0

    override val height: Int
        get() = 0

    override fun draw(screen: IGuiScreen, x: Int, y: Int) {
        if (entity == null) {
            try {
                entity = entityClass.getDeclaredConstructor(World::class.java).newInstance(Minecraft.getMinecraft().world)
            }
            catch (e: Exception) {
                Craftlin.log.error("An error occurred while drawing an EntityDrawable", e)
            }
        }

        if (entity != null) {
            pushMatrix()
            translate(0.0, yOffset, 0.0)
            drawEntity(entity!!, x.toFloat(), y.toFloat(), scale, 30F, -30F, 0F)
            popMatrix()
        }
    }
}

