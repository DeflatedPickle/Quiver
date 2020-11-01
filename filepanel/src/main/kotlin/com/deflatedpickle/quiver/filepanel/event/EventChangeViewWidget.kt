/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filepanel.event

import com.deflatedpickle.haruhi.api.event.AbstractEvent
import java.io.File
import org.jdesktop.swingx.JXPanel

object EventChangeViewWidget : AbstractEvent<Pair<File, JXPanel>>()
