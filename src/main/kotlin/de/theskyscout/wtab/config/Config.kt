package de.theskyscout.wtab.config

import de.theskyscout.wtab.utils.ConfigUtil

object Config {

    fun load() {
        ConfigUtil("config.yml")
    }

    fun get(key: String): Any? {
        return ConfigUtil("config.yml").config.get(key)
    }

    fun set(key: String, value: Any) {
        ConfigUtil("config.yml").config.set(key, value)
    }

    fun prefix(): String {
        return ConfigUtil("config.yml").config.getString("prefix")!!
    }

    fun saveMethodIsFile(): Boolean {
        if(ConfigUtil("config.yml").config.getString("save-method") == "FILE") return true
        return false
    }

    fun saveMethodIsMongoDB(): Boolean {
        if(ConfigUtil("config.yml").config.getString("save-method") == "MONGODB") return true
        return false
    }

    fun isLuckperms(): Boolean {
        if(ConfigUtil("config.yml").config.getString("tablist-method") == "LUCKPERMS") return true
        return false
    }

    fun isPerms(): Boolean {
        if(ConfigUtil("config.yml").config.getString("tablist-method") == "PERMISSION") return true
        return false
    }

    fun mongoURI(): String {
        return ConfigUtil("config.yml").config.getString("mongo_uri")!!
    }

    fun save() {
        ConfigUtil("config.yml").save()
    }
}