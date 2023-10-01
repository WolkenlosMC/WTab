package de.theskyscout.wtab.manager

import de.theskyscout.wtab.config.Config
import de.theskyscout.wtab.config.MessagesConfig
import de.theskyscout.wtab.database.MongoDB
import de.theskyscout.wtab.utils.ConfigUtil
import org.bson.Document
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable


object DataCaching {

    val cache = HashMap<String, Document>()
    val messageCache = HashMap<String, String>()
    var settingsCache = Document()

    fun refreshCache(plugin: JavaPlugin) {
        plugin.logger.info("------------------------------------")
        plugin.logger.info("Enabled DataCaching! ")
        plugin.logger.info("This is a new feature and is still in beta! It should use less resources than before.")
        plugin.logger.info("If this cause any problems, please contact me on Discord: TheSkyScout#0000")
        plugin.logger.info("------------------------------------")

        val config = ConfigUtil("config.yml")
        val messageConfig = ConfigUtil("messages.yml")
        settingsCache = Document().append("save-method", config.config.getString("save-method"))
            .append("tablist-method", config.config.getString("tablist-method"))
            .append("message-save-method", messageConfig.config.getString("save-method"))

        object : BukkitRunnable() {
            override fun run() {
                // Refresh settings cache
                val settingsDoc = Document().append("save-method", config.config.getString("save-method"))
                    .append("tablist-method", config.config.getString("tablist-method"))
                    .append("message-save-method", messageConfig.config.getString("save-method"))

                if(settingsCache != settingsDoc) settingsCache = settingsDoc

                // Refresh group cache
                if(Config.saveMethodIsMongoDB()) {
                    if(!MongoDB.connected) return
                    MongoDB.collection.find().forEach {
                        if (cache[it["_id"] as String] != it) cache[it["_id"] as String] = it
                    }
                } else {
                    var file = ConfigUtil("groups.yml")
                    file.config.getConfigurationSection("")?.getKeys(false)?.forEach {
                        val doc = Document().append("_id", it).append("prefix", file.config.getString("$it.prefix")).append("order", file.config.getInt("$it.order"))
                        if (cache[it] != doc) cache[it] = doc
                    }
                    file = ConfigUtil("config.yml")
                    val tabDoc = Document().append("_id", "settings").append("header", file.config.getString("header")).append("footer", file.config.getString("footer"))
                    if (cache["settings"] != tabDoc) cache["settings"] = tabDoc
                }

                // Refresh message cache
                if(MessagesConfig.saveMethodIsMongoDB()) {
                    if(!MongoDB.connected) return
                    val document = MongoDB.collection.find(Document().append("_id", "messages")).first() ?: return
                    document.forEach {
                        if (messageCache[it.key] != it.value) messageCache[it.key] = it.value.toString()
                    }
                } else {
                    val file = ConfigUtil("messages.yml")
                    file.config.getConfigurationSection("")?.getKeys(false)?.forEach {
                        if (messageCache[it] != file.config.getString(it)) messageCache[it] = file.config.getString(it)!!
                    }
                }
            }

        }.runTaskTimer(plugin, 0, 20)
    }
}