package de.theskyscout.wtab.manager

import de.theskyscout.wtab.WTab
import de.theskyscout.wtab.config.Config
import de.theskyscout.wtab.config.MessagesConfig
import de.theskyscout.wtab.utils.Placeholders
import de.theskyscout.wtab.utils.TablistSortUtil
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.DisplaySlot

object TablistManager {
    private val mm = MiniMessage.miniMessage()

    var tablistTask: BukkitTask? = null
    private fun setTablist(player: Player) {
        val header = mm.deserialize(Placeholders.replaceInString(GroupManager.getHeader(), player))
        val footer = mm.deserialize(Placeholders.replaceInString(GroupManager.getFooter(), player))
        player.sendPlayerListHeaderAndFooter(header, footer)
    }


    private fun registerTeams(player: Player) {
        val scoreboard = player.scoreboard
        GroupManager.getAllGroups().forEach {
            var team = scoreboard.getTeam(TablistSortUtil.orderToSort(it))
            if(team == null) {
                team = scoreboard.registerNewTeam(TablistSortUtil.orderToSort(it))
            }
            val prefix = MessagesConfig.getTabPrefix(it)
            if(team.prefix() != mm.deserialize(prefix)) team.prefix(mm.deserialize(prefix))
            team.color(NamedTextColor.GRAY)
        }
    }

    private fun setPlayerTeam(player: Player) {
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
        WTab.instance.logger.info("Starting Tablist")
        tablistTask = object : BukkitRunnable() {
           override fun run() {
               if(!WTab.enabled) return
               setAllPlayerTeams()
           }
       }.runTaskTimer(WTab.instance, 0L, 20L)
    }

    fun resetTablist() {
        tablistTask?.cancel()
        tablistTask = null
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        scoreboard.teams.forEach {
            it.unregister()
        }
        scoreboard.entries.forEach {
            scoreboard.resetScores(it)
        }
        scoreboard.clearSlot(DisplaySlot.PLAYER_LIST)
        scoreboard.entries.forEach {
            scoreboard.resetScores(it)
        }
        Bukkit.getOnlinePlayers().forEach {
            it.sendPlayerListHeaderAndFooter(mm.deserialize(""), mm.deserialize(""))
        }
    }
}