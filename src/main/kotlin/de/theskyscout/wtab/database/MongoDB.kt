package de.theskyscout.wtab.database

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import de.theskyscout.wtab.WTab
import de.theskyscout.wtab.config.Config
import org.bson.Document

object MongoDB {

    lateinit var client: MongoClient
    lateinit var database: MongoDatabase
    lateinit var collection: MongoCollection<Document>
    var connected = false

    fun connect() {
        if(Config.get("save-method") != "MONGODB") return
        try {
            client = MongoClients.create(Config.mongoURI())
            database = client.getDatabase(Config.get("database") as String)
            if(database.listCollectionNames().none { it == "wtab"}) {
                database.createCollection("wtab")
            }
            collection = database.getCollection("wtab")
            WTab.instance.logger.info("Connected to MongoDB!")
            connected = true
        } catch (e: Exception) {
            WTab.instance.logger.warning("Could not connect to MongoDB!")
            e.printStackTrace()
        }
    }
}