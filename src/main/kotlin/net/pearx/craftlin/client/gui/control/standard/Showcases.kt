/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.craftlin.client.gui.control.standard

import org.lwjgl.input.Mouse.isButtonDown
import net.pearx.craftlin.client.gui.control.Control
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.entity.Entity
import net.minecraft.world.World
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.fml.common.thread.SidedThreadGroups.CLIENT
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import ru.pearx.carbidelin.math.cycle
import net.pearx.craftlin.Craftlin
import net.pearx.craftlin.client.gui.drawEntity
import net.pearx.craftlin.client.translate


abstract class AbstractShowcase : Control() {
    protected var rotX: Int = 0
    protected var rotY: Int = 0
    protected var scale: Int = 0

    init {
        shouldStencil = true
        mouseMove = { x, y, dx, dy ->
            if (isButtonDown(0)) {
                val mX = x - lastMouseX
                val mY = y - lastMouseY
                rotY += mX
                rotX += mY
                rotX.cycle(0, 360)
                rotY.cycle(0, 360)
            }
        }

        mouseWheel = { delta ->
            if (isFocused) {
                scale += delta / 120
                scale.coerceAtLeast(0)
            }
        }
    }
}

@SideOnly(Side.CLIENT)
inline fun <reified T : Entity> EntityShowcase() = EntityShowcase(T::class.java)

@SideOnly(Side.CLIENT)
class EntityShowcase(var entityClass: Class<out Entity>) : AbstractShowcase() {
    private var entity: Entity? = null

    init {
        scale = 75
        rotX = 45
        rotY = 45

        render = {
            if (entity?.javaClass != entityClass)
                try {
                    entity = entityClass.getDeclaredConstructor(World::class.java).newInstance(Minecraft.getMinecraft().world)
                }
                catch (e: Exception) {
                    Craftlin.log.error("An error occurred while creating an Entity instance!", e)
                }

            if(entity != null) {
                pushMatrix()
                translate(0, scale / 5, 150)
                drawEntity(entity!!, width / 2F, height / 2F, scale.toFloat(), rotX.toFloat(), rotY.toFloat(), 0F)
                popMatrix()
            }
        }
    }
}
