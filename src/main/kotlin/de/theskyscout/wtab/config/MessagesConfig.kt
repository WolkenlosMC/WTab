package de.theskyscout.wtab.config

import de.theskyscout.wtab.database.MongoDB
import de.theskyscout.wtab.manager.DataCaching
import de.theskyscout.wtab.manager.GroupManager
import de.theskyscout.wtab.utils.ConfigUtil
import net.kyori.adventure.text.TextComponent
import org.bson.Document
import org.bukkit.entity.Player
import javax.print.Doc

object MessagesConfig {

    fun load() {
        ConfigUtil("messages.yml")
    }

    fun get(key: String): Any? {
        return DataCaching.messageCache[key]
    }

    fun saveMethodIsFile(): Boolean {
        return DataCaching.settingsCache["message-save-method"] == "FILE"
    }

    fun saveMethodIsMongoDB(): Boolean {
        return DataCaching.settingsCache["message-save-method"] == "MONGODB"
    }

    fun getMessage(key: String): String? {
        return get(key) as String?
    }

    fun getNameTag(player: Player): String {
        var chatName = getMessage("name-tag-format")
        val name = (player.displayName() as TextComponent).content()
        if(chatName == null) return GroupManager.getPrefix(player) + "<gray> | " + name
        chatName = chatName.replace("%rank%", GroupManager.getPrefix(player)).replace("%player%", name)
        return chatName
    }

    fun getTabPrefix(doc: Document): String {
        var tabPrefix = getMessage("tab-list-format")
        val prefix = (doc["prefix"] ?: " ").toString()

        if(tabPrefix == null) return "$prefix<gray> | "
        tabPrefix = tabPrefix.replace("%rank%", prefix)
        return tabPrefix
    }

    fun set(key: String, value: Any) {
        ConfigUtil("messages.yml").config.set(key, value)
    }

    fun uploadToMongo() {
        val document = Document().append("_id", "messages")
        ConfigUtil("messages.yml").config.getConfigurationSection("")?.getKeys(false)?.forEach {
            document.append(it, ConfigUtil("messages.yml").config.getString(it))
        }
        if(!MongoDB.connected) return
        if(MongoDB.collection.find(Document().append("_id", "messages")).first() == null) {
            MongoDB.collection.insertOne(document)
        } else {
            MongoDB.collection.updateOne(Document().append("_id", "messages"), Document().append("$" + "set", document))
        }
    }

}