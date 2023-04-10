package de.theskyscout.wtab.utils

import org.bson.Document

object TablistSortUtil {

    fun orderToSort(it: Document): String {
        val order = it["order"] as Int? ?: return ""
        val letter = 'a' + (order - 1) / 10
        return "$letter$order${it["_id"]}"
    }
}