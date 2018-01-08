package com.gwax.scryfall

import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.Serializer
import java.io.File

class ScryfallCache(cacheDir: File) {
    companion object {
        private const val DB_START_SIZE = 100 * 1024 * 1024L
        private const val DB_INCREMENT_SIZE = 10 * 1024 * 1024L
    }
    private enum class DBCollection {
        METADATA;

        fun createOrOpen(db: DB) = db.hashMap(name)
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.JAVA)
                .createOrOpen()
    }

    private val dbFile = cacheDir.resolve("scrycache.db")

    private fun db() =
            DBMaker.fileDB(dbFile)
                    .allocateStartSize(DB_START_SIZE)
                    .allocateIncrement(DB_INCREMENT_SIZE)
                    .make()

    fun clear() = db().use { db ->
        enumValues<DBCollection>().forEach { coll ->
            coll.createOrOpen(db).clearWithExpire()
        }
    }

    var changelog: ScryfallChangelog
        get() = db().use { db ->
            val metaMap = DBCollection.METADATA.createOrOpen(db)
            val cl = metaMap["changelog"] as? ScryfallChangelog
            return cl ?: ScryfallChangelog()
        }
        set(value) = db().use { db ->
            DBCollection.METADATA.createOrOpen(db)
                    .put("changelog", value)
        }
}

fun main(args: Array<String>) {
    val cacheDir = createTempDir()
    cacheDir.deleteOnExit()

    val cache1 = ScryfallCache(cacheDir)
    println(cache1.changelog)
    cache1.changelog = cache1.changelog.fetchLatest()
    println(cache1.changelog)

    val cache2 = ScryfallCache(cacheDir)
    println(cache2.changelog)
    println(cache2.changelog == cache2.changelog.fetchLatest())
    cache2.clear()
    println(cache2.changelog)
}