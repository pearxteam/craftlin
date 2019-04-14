/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.craftlin.client.gui.drawable

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import ru.pearx.craftlin.client.gui.IGuiScreen


@SideOnly(Side.CLIENT)
interface IGuiDrawable {
    val width: Int
    val height: Int

    fun draw(screen: IGuiScreen, x: Int, y: Int)
}
