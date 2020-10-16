package com.deflatedpickle.quiver.filepanel.lang

import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.quiver.backend.api.Lang
import com.deflatedpickle.quiver.config.QuiverSettings

object FilePanelLang : Lang(
    "filepanel",
    ConfigUtil.getSettings<QuiverSettings>("deflatedpickle@quiver#1.2.0").language
)