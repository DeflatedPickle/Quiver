package com.deflatedpickle.quiver.launcher.lang

import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.quiver.backend.api.Lang
import com.deflatedpickle.quiver.config.QuiverSettings

object LauncherLang : Lang(
    "launcher",
    ConfigUtil.getSettings<QuiverSettings>("deflatedpickle@quiver#1.2.0").language
)