package de.theskyscout.wtab.utils

import de.theskyscout.wtab.WTab
import org.apache.commons.io.FileUtils
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ConfigUtil(fileName: String) {
    val fileName = fileName
    var file: File
    var config: YamlConfiguration

    init {
        file = File(WTab.instance.dataFolder, fileName)

        if(!file.exists()) {
            val stream = WTab.instance.getResource(fileName)
            if(stream != null) {
                FileUtils.copyInputStreamToFile(stream, File(WTab.instance.dataFolder, fileName))
            } else file.createNewFile()
        }

        config = YamlConfiguration.loadConfiguration(file)
    }

    fun save() {
        config.save(file)
    }
}