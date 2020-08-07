package com.deflatedpickle.quiver.filepanel.event

import com.deflatedpickle.haruhi.api.event.AbstractEvent
import org.jdesktop.swingx.JXPanel
import java.io.File

object EventChangeViewWidget : AbstractEvent<Pair<File, JXPanel>>()