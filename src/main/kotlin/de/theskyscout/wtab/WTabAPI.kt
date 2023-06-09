package de.theskyscout.wtab

import de.theskyscout.wtab.manager.GroupManager
import org.bukkit.entity.Player

class WTabAPI {

    fun getPluginVersion(): String {
        return WTab.instance.description.version
    }

    fun getPlayerPrefix(player: Player): String {
        return GroupManager.getPrefix(player)
    }

}