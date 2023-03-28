package de.theskyscout.wtab.manager

import de.theskyscout.wtab.config.Config
import de.theskyscout.wtab.database.MongoDB
import de.theskyscout.wtab.utils.ConfigUtil
import net.luckperms.api.LuckPermsProvider
import org.bson.Document

object GroupManager {

    fun setGroupPrefix(name: String, prefix: String) {
        if(Config.saveMethodIsMongoDB()) {
            val set = "$" +"set"
            if(!existGroup(name)) {
                MongoDB.collection.insertOne(Document().append("_id", name).append("prefix", prefix).append("order", 0))
                return
            }
            MongoDB.collection.updateOne(
                Document().append("_id", name),
                Document().append(set,
                    Document().append("prefix", prefix)))
        }else if (Config.saveMethodIsFile()) {
            val config = ConfigUtil("groups.yml")
            config.config.set("$name.prefix", prefix)
            config.save()
        }
    }

    fun setGroupOrder(name: String, order: Int) {
        if(Config.saveMethodIsMongoDB()) {
            val set = "$" +"set"
            if(!existGroup(name)) {
                MongoDB.collection.insertOne(Document().append("_id", name).append("prefix", "").append("order", order))
                return
            }
            MongoDB.collection.updateOne(
                Document().append("_id", name),
                Document().append(set,
                    Document().append("order", order)))
        }else if (Config.saveMethodIsFile()) {
            val config = ConfigUtil("groups.yml")
            config.config.set("$name.order", order)
            config.save()
        }
    }

    fun getGroup(name: String) : Document? {
        if(Config.saveMethodIsMongoDB()) {
            return MongoDB.collection.find(Document().append("_id", name)).first() ?: Document()
        }else if (Config.saveMethodIsFile()) {
            val config = ConfigUtil("groups.yml")
            return Document().append("prefix", config.config.getString("$name.prefix")).append("order", config.config.getInt("$name.order"))
        }
        return null
    }

    fun removeGroup(name: String) {
        if(Config.saveMethodIsMongoDB()) {
            MongoDB.collection.findOneAndDelete(Document().append("_id", name))
        }else if (Config.saveMethodIsFile()) {
            val config = ConfigUtil("groups.yml")
            config.config.set(name, null)
            config.save()
        }
    }

    fun createGroup(name: String, prefix: String, order: Int) {
        if(Config.saveMethodIsMongoDB()) {
            MongoDB.collection.insertOne(Document().append("_id", name).append("prefix", prefix).append("order", order))
        }else if (Config.saveMethodIsFile()) {
            val config = ConfigUtil("groups.yml")
            config.config.set("$name.prefix", prefix)
            config.config.set("$name.order", order)
            config.save()
        }
    }

    fun getAllGroups() : List<Document> {
        if(Config.saveMethodIsMongoDB()) {
            val result = MongoDB.collection.find().sort(Document().append("order", 1)).into(mutableListOf())
            return result
        }else if (Config.saveMethodIsFile()) {
            val config = ConfigUtil("groups.yml")
            val result = mutableListOf<Document>()
            for (group in config.config.getKeys(false)) {
                result.add(Document().append("_id", group).append("prefix", config.config.getString("$group.prefix")).append("order", config.config.getInt("$group.order")))
            }
            return result
        }
        return mutableListOf()

    }

    fun existGroup(name: String) : Boolean{
        if(Config.isLuckperms()) {
            val luckPermsAPI = LuckPermsProvider.get()
            return luckPermsAPI.groupManager.getGroup(name) != null
        }
        if(Config.saveMethodIsMongoDB()) {
            val result = MongoDB.collection.find(Document().append("_id", name)).first() ?: return false
            return true
        }
        if (Config.saveMethodIsFile()) {
            return ConfigUtil("groups.yml").config.contains(name)
        }
        return false
    }

}