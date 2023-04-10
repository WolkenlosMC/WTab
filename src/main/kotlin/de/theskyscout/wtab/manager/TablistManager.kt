package de.theskyscout.wtab.manager

import de.theskyscout.wtab.WTab
import de.theskyscout.wtab.config.Config
import de.theskyscout.wtab.utils.TablistSortUtil
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object TablistManager {
    private val mm = MiniMessage.miniMessage()

    fun setTablist(player: Player) {
        TODO()
    }


    fun registerTeams(player: Player) {
        val scoreboard = player.scoreboard
        GroupManager.getAllGroups().forEach {
            var team = scoreboard.getTeam(TablistSortUtil.orderToSort(it))
            if(team == null) team = scoreboard.registerNewTeam(TablistSortUtil.orderToSort(it))
            team.prefix((mm.deserialize("${it["prefix"]}<gray> | <gray>")))
            team.color(NamedTextColor.GRAY)
        }
    }

    fun setPlayerTeam(player: Player) {
        val scoreboard = player.scoreboard
        if(Config.isLuckperms()) {
            val api = LuckPermsProvider.get()
            val user = api.userManager.getUser(player.uniqueId)
            val group = user?.primaryGroup ?: return
            if(!GroupManager.existGroup(group)) return
            val team = scoreboard.getTeam(TablistSortUtil.orderToSort(GroupManager.getGroup(group)!!))
            team?.addEntry(player.name)
        } else {
            GroupManager.getAllGroups().forEach {
                val team = scoreboard.getTeam(TablistSortUtil.orderToSort(it))
                if(player.hasPermission("wtab." + it["_id"].toString())) {
                    team?.addEntry(player.name)
                }
            }
        }
    }

    fun setAllPlayerTeams() {
        Bukkit.getOnlinePlayers().forEach {
            registerTeams(it)
            setPlayerTeam(it)
        }
    }

    fun updateTablist() {
        WTab.instance.logger.info("Starting Tablist")
       object : BukkitRunnable() {
           override fun run() {
               setAllPlayerTeams()
           }
       }.runTaskTimer(WTab.instance, 0L, 20L)
    }
}