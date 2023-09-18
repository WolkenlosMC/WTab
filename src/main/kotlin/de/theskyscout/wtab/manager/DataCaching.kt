package de.theskyscout.wtab.manager

import de.theskyscout.wtab.config.Config
import de.theskyscout.wtab.database.MongoDB
import de.theskyscout.wtab.utils.ConfigUtil
import org.bson.Document
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable


object DataCaching {

    val cache = HashMap<String, Document>()

    fun refreshCache(plugin: JavaPlugin) {
        plugin.logger.info("------------------------------------")
        plugin.logger.info("Enabled DataCaching! ")
        plugin.logger.info("This is a new feature and is still in beta! It should use less resources than before.")
        plugin.logger.info("If this cause any problems, please contact me on Discord: TheSkyScout#0001")
        plugin.logger.info("------------------------------------")
        object : BukkitRunnable() {
            override fun run() {
            cache.clear()
                if(Config.saveMethodIsMongoDB()) {
                    if(!MongoDB.connected) return
                    MongoDB.collection.find().forEach {
                        cache[it["_id"] as String] = it
                    }
                } else {
                    var config = ConfigUtil("groups.yml")
                    config.config.getConfigurationSection("")?.getKeys(false)?.forEach {
                        cache[it] = Document().append("_id", it).append("prefix", config.config.getString("$it.prefix")).append("order", config.config.getInt("$it.order"))
                    }
                    config = ConfigUtil("config.yml")
                    cache["settings"] = Document().append("_id", "settings").append("header", config.config.getString("header")).append("footer", config.config.getString("footer"))
                }
            }

        }.runTaskTimer(plugin, 0, 20)
    }
}