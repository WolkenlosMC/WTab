package de.theskyscout.wtab.commands

import de.theskyscout.wtab.config.Config
import de.theskyscout.wtab.config.MessagesConfig
import de.theskyscout.wtab.manager.GroupManager
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class WTabCommand: CommandExecutor, TabCompleter {

    private val mm = MiniMessage.miniMessage()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(sender is Player) {
            if(!sender.hasPermission("wtab.edit")) {
                sender.sendMessage(mm.deserialize(Config.prefix() + "<red> You don't have the permission to do this"))
                return true
            }
            if(args?.size!! < 1) {
                sender.sendMessage(mm.deserialize(Config.prefix() + "<red> /wtab [ create | delete | set | list | exists | messages ]"))
                return true
            }
            when(args[0]) {
                "list" -> {
                    if(args.size == 2) {
                        if(args[1] == "-inv" || args[1] == "-i") {
                            sender.openInventory(GroupManager.getGroupListInventory())
                            return true
                        } else sender.sendMessage(mm.deserialize(Config.prefix() + "<red> /wtab list [-inv]"))
                    }
                    sender.sendMessage(mm.deserialize(Config.prefix() + "<gray>All Groups:"))
                    GroupManager.getAllGroups().forEach {
                        sender.sendMessage(mm.deserialize("<gray>${it["_id"]}:"))
                        sender.sendMessage(mm.deserialize("<#2f3136>  |- <gray>Prefix: ${it["prefix"]}"))
                        sender.sendMessage(mm.deserialize("<#2f3136>  |- <gray>Order: ${it["order"]}"))
                    }
                }
                "exists" -> {
                    if(args.size != 2) {
                        sender.sendMessage(mm.deserialize(Config.prefix() + "<red> /wtab exists [name]"))
                        return true
                    }
                    if(GroupManager.existGroup(args[1])) {
                        sender.sendMessage(mm.deserialize(Config.prefix() + "<green> The group ${args[1]} exists"))
                    } else sender.sendMessage(mm.deserialize(Config.prefix() + "<red> The group ${args[1]} does not exist"))
                }
                "set" -> {
                    if(args.size != 4) {
                        sender.sendMessage(mm.deserialize(Config.prefix() + "<red> /wtab set [name] [ prefix | order ] [ value ]"))
                        return true
                    }
                    if(!GroupManager.existGroup(args[1])) {
                        sender.sendMessage(mm.deserialize(Config.prefix() + "<red> There is no group with this name"))
                        return true
                    }
                    when(args[2]) {
                        "prefix" -> {
                            GroupManager.setGroupPrefix(args[1], args[3])
                            sender.sendMessage(mm.deserialize(Config.prefix() + "<gray> The prefix of <green>${args[1]}<gray> is now <green>${args[3]}"))
                            sender.playSound(sender.location, "minecraft:entity.player.levelup", 5F, 2F)
                        }
                        "order" -> {
                            if(args[3].toIntOrNull() != null) {
                                GroupManager.setGroupOrder(args[1], args[3].toInt())
                                sender.sendMessage(mm.deserialize(Config.prefix() + "<gray> The order of <green>${args[1]}<gray> is now <green>${args[3]}"))
                                sender.playSound(sender.location, "minecraft:entity.player.levelup", 5F, 2F)
                            } else sender.sendMessage(mm.deserialize(Config.prefix() + "<red> Order must be a even number"))
                        }
                    }
                }
                "create" -> {
                    if(Config.isLuckperms()) {
                        sender.sendMessage(mm.deserialize(Config.prefix() + "<red> You can't create a group with this save method"))
                        return true
                    } else if(Config.isPerms()) {
                        if(args.size != 4) {
                            sender.sendMessage(mm.deserialize(Config.prefix() + "<red> /wtab create [name] [prefix] [order]"))
                                return true
                        }
                        if(GroupManager.existGroup(args[1])) {
                            sender.sendMessage(mm.deserialize(Config.prefix() + "<red> There is already a group with this name"))
                            return true
                        }
                        if(args[3].toIntOrNull() == null) {
                            sender.sendMessage(mm.deserialize(Config.prefix() + "<red> Order must be a even number"))
                            return true
                        }
                        GroupManager.createGroup(args[1], args[2], args[3].toInt())
                        sender.sendMessage(mm.deserialize(Config.prefix() + "<gray> The group <green>${args[1]}<gray> was created"))
                        sender.playSound(sender.location, "minecraft:entity.player.levelup", 5F, 2F)
                    }
                }
                "delete" -> {
                    if(Config.isLuckperms()) {
                        sender.sendMessage(mm.deserialize(Config.prefix() + "<red> You can't delete a group with this save method"))
                        return true
                    }
                        if(args.size != 2) {
                        sender.sendMessage(mm.deserialize(Config.prefix() + "<red> /wtab delete [name]"))
                        return true
                    }
                    if(!GroupManager.existGroup(args[1])) {
                        sender.sendMessage(mm.deserialize(Config.prefix() + "<red> There is no group with this name"))
                        return true
                    }
                    GroupManager.removeGroup(args[1])
                    sender.sendMessage(mm.deserialize(Config.prefix() + "<gray> The group <green>${args[1]}<gray> was deleted"))
                    sender.playSound(sender.location, "minecraft:entity.player.levelup", 5F, 2F)
                }
                "header" -> {
                    if(args.size < 2) {
                        sender.sendMessage(mm.deserialize(Config.prefix() + "<red> /wtab header [text]"))
                        return true
                    }
                    var header = ""
                    for (i in 1 until args.size) {
                        header += args[i] + " "
                    }
                    GroupManager.setHeader(header)
                    sender.sendMessage(mm.deserialize(Config.prefix() + "<gray> The header was set to <green>${header}"))
                    sender.playSound(sender.location, "minecraft:entity.player.levelup", 5F, 2F)
                }
                "footer" -> {
                    if(args.size < 2) {
                        sender.sendMessage(mm.deserialize(Config.prefix() + "<red> /wtab footer [text]"))
                        return true
                    }
                    var footer = ""
                    for (i in 1 until args.size) {
                        footer += args[i] + " "
                    }
                    GroupManager.setFooter(footer)
                    sender.sendMessage(mm.deserialize(Config.prefix() + "<gray> The footer was set to <green>${footer}"))
                    sender.playSound(sender.location, "minecraft:entity.player.levelup", 5F, 2F)
                }
                "messages" -> {
                    if(args.size < 2 || args[1] != "upload") {
                        sender.sendMessage(mm.deserialize(Config.prefix() + "<red> /wtab messages upload"))
                        return true
                    }
                    sender.sendMessage(mm.deserialize(Config.prefix() + "<gray> Uploading messages..."))
                    MessagesConfig.uploadToMongo()
                    sender.sendMessage(mm.deserialize(Config.prefix() + "<gray> Messages uploaded!"))
                }
            }

        } else sender.sendMessage(mm.deserialize(Config.prefix() + "<red>Console compatibility is not yet available :C"))

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String>? {
        val tabComplete = mutableListOf<String>()
        if(args?.size!! == 1) {
            val prelist = mutableListOf("create", "delete", "set", "list", "exists", "header", "footer", "messages")
            tabComplete.addAll(prelist.filter { it.startsWith(args[0])})
        }
        if(args.size == 2) {
            if(args[0] == "messages") {
                val prelist = mutableListOf("upload")
                tabComplete.addAll(prelist.filter { it.startsWith(args[1])})
            }
        }

        if(args.size == 2) {
            if(args[0] == "set" || args[0] == "delete") {
                val prelist = mutableListOf<String>()
                prelist.addAll(GroupManager.getAllGroups().map { it["_id"].toString() })
                tabComplete.addAll(prelist.filter { it.startsWith(args[1])})
            }
        }
        if(args.size == 3) {
            if(args[0] == "set") {
                tabComplete.add("prefix")
                tabComplete.add("order")
            }
        }
        return tabComplete
    }
}