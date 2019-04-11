/*
 * Copyright © 2019, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package ru.pearx.craftlin.client.gui.control

import ru.pearx.carbidelin.collections.event.eventCollectionBy

private fun Control.setParentAndInit(parent: Control) {
    this.parent = parent
    this.invokeInit()
}

private fun Control.removeParentAndClose() {
    this.invokeClose()
    this.parent = null
}

fun controlCollection(parent: Control): Collection<Control> {
    return eventCollectionBy(arrayListOf()) {
        add { element ->
            element.setParentAndInit(parent)
        }

        remove { element ->
            element.removeParentAndClose()
        }

        clear { elements ->
            for (element in elements)
                element.removeParentAndClose()
        }
    }
}