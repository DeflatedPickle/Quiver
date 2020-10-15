package com.deflatedpickle.quiver.backend.lang

import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.quiver.backend.api.Lang
import com.deflatedpickle.quiver.config.QuiverSettings

object QuiverLang : Lang(
    "quiver",
    ConfigUtil.getSettings<QuiverSettings>("deflatedpickle@quiver#1.2.0").language
)