package de.theskyscout.wtab.manager

import de.theskyscout.wtab.config.Config
import de.theskyscout.wtab.database.MongoDB
import de.theskyscout.wtab.utils.ConfigUtil
import de.theskyscout.wtab.utils.ItemBuilder
import de.theskyscout.wtab.utils.TablistSortUtil
import net.luckperms.api.LuckPermsProvider
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object GroupManager {

    fun setGroupPrefix(name: String, prefix: String) {
        if(Config.saveMethodIsMongoDB()) {
            val set = "$" +"set"
            if(!existGroupData(name)) {
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
            if(!existGroupData(name)) {
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
        if(Config.isLuckperms()) {
            val luckPermsAPI = LuckPermsProvider.get()
            val result = mutableListOf<Document>()
            for (group in luckPermsAPI.groupManager.loadedGroups) {
                result.add(Document().append("_id", group.name).append("prefix", getGroup(group.name)?.get("prefix")).append("order", getGroup(group.name)?.get("order")))
            }
            return result.sortedBy { it["order"] as Int? ?: 0  }
        }
        if(Config.saveMethodIsMongoDB()) {
            val result = MongoDB.collection.find().sort(Document().append("order", 1)).into(mutableListOf())
            return result.sortedBy { it["order"] as Int? ?: 0  }
        }else if (Config.saveMethodIsFile()) {
            val config = ConfigUtil("groups.yml")
            val result = mutableListOf<Document>()
            for (group in config.config.getKeys(false)) {
                result.add(Document().append("_id", group).append("prefix", config.config.getString("$group.prefix")).append("order", config.config.getInt("$group.order")))
            }
            return result.sortedBy { it["order"] as Int? ?: 0  }
        }
        return mutableListOf()

    }

    fun existGroupData(name: String) : Boolean{
        if(Config.saveMethodIsMongoDB()) {
            val result = MongoDB.collection.find(Document().append("_id", name)).first() ?: return false
            return true
        }
        if (Config.saveMethodIsFile()) {
            return ConfigUtil("groups.yml").config.contains(name)
        }
        return false
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

    fun getGroupListAsItemList(): List<ItemStack> {
        val result = mutableListOf<ItemStack>()
        for (group in getAllGroups()) {
            result.add(ItemBuilder(Material.NAME_TAG)
                .setDisplayName("<#34ebde>${group["_id"].toString().capitalize()}")
                .addLore("<gray>------------------")
                .addLore("<gray>Prefix: ${group["prefix"]}")
                .addLore("<gray>Order: <green>${group["order"]}")
                .toItemStack()
            )
        }
        return result
    }

    fun getGroupListInventory() : Inventory {
        val inventory = Bukkit.createInventory(null, 54, "Groups")
        for (item in getGroupListAsItemList()) {
            inventory.addItem(item)
        }
        return inventory
    }

    fun getPrefix(player: Player): String {
        if(Config.isLuckperms()) {
            val api = LuckPermsProvider.get()
            val user = api.userManager.getUser(player.uniqueId)
            val group = user?.primaryGroup ?: return ""
            if(!GroupManager.existGroup(group)) return ""
            return GroupManager.getGroup(group)?.get("prefix").toString()
        } else {
            GroupManager.getAllGroups().forEach {
                if(player.hasPermission("wtab." + it["_id"].toString())) {
                    return it["prefix"].toString()
                }
            }
        }
        return ""
    }

}