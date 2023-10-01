package de.theskyscout.wtab.config

import de.theskyscout.wtab.utils.ConfigUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object MessagesConfig {

    fun load() {
        ConfigUtil("messages.yml")
    }

    fun get(key: String): Any? {
        return ConfigUtil("messages.yml").config.get(key)
    }

    fun getMessage(messageType: MessageType): Any? {
        return ConfigUtil("messages.yml").config.get(messageType.key)
    }

    fun test() {
        //erstelle eine MutableList mit dem Namen und einer Funktion
        val messages = mutableListOf<Pair<String, () -> Unit>>()
        messages.add(Pair("no-permission") { val players = Bukkit.getOnlinePlayers() })

    }

    fun set(key: String, value: Any) {
        ConfigUtil("messages.yml").config.set(key, value)
    }

}

enum class MessageType(val pair: Pair<String, () -> Unit>) {
    NO_PERMS(Pair("no-permission") {
        var variable = Bukkit.getOnlinePlayers()
    });

    var variable: List<Player> = listOf()
}