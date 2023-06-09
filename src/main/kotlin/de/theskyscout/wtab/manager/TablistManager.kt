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
import org.bukkit.scheduler.BukkitTask

object TablistManager {
    private val mm = MiniMessage.miniMessage()

    var tablistTask: BukkitTask? = null
    fun setTablist(player: Player) {
        val scoreboard = player.scoreboard
        player.sendPlayerListHeaderAndFooter(mm.deserialize(GroupManager.getHeader()), mm.deserialize(GroupManager.getFooter()))
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
            if(!Config.checkLuckPerms())return
            val api = LuckPermsProvider.get()
            val user = api.userManager.getUser(player.uniqueId)
            val group = user?.primaryGroup ?: return
            if(!GroupManager.existGroup(group)) return
            val team = scoreboard.getTeam(TablistSortUtil.orderToSort(GroupManager.getGroup(group)!!))
            team?.addEntry(player.name)
        } else {
             for (it in GroupManager.getAllGroups()) {
                 val team = scoreboard.getTeam(TablistSortUtil.orderToSort(it))
                 if(player.hasPermission("wtab." + it["_id"].toString())) {
                     team?.addEntry(player.name)
                     break
                 }
             }
        }
    }

    fun setAllPlayerTeams() {
        Bukkit.getOnlinePlayers().forEach {
            registerTeams(it)
            setPlayerTeam(it)
            setTablist(it)
        }
    }

    fun updateTablist() {
        if(WTab.instance.server.onlinePlayers.isEmpty()) return
        WTab.instance.logger.info("Starting Tablist")
        tablistTask = object : BukkitRunnable() {
           override fun run() {
               if(!WTab.enabled) return
               setAllPlayerTeams()
           }
       }.runTaskTimer(WTab.instance, 0L, 20L)
    }
}