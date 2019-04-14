/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.craftlin.client

import net.minecraft.client.renderer.GlStateManager

inline fun scale(x: Int, y: Int, z: Int) {
    GlStateManager.scale(x.toFloat(), y.toFloat(), z.toFloat())
}

inline fun scale(x: Long, y: Long, z: Long) {
    GlStateManager.scale(x.toDouble(), y.toDouble(), z.toDouble())
}

inline fun translate(x: Int, y: Int, z: Int) {
    GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
}

inline fun translate(x: Long, y: Long, z: Long) {
    GlStateManager.translate(x.toDouble(), y.toDouble(), z.toDouble())
}