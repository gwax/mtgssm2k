package com.gwax.scryfall

import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import org.mapdb.Serializer
import java.io.File
import java.net.URL

class ScryfallCache(cacheDir: File) {
    companion object {
        private const val DB_START_SIZE = 100 * 1024 * 1024L
        private const val DB_INCREMENT_SIZE = 10 * 1024 * 1024L
    }

    private enum class DBCollection {
        METADATA,
        URLS;

        fun createOrOpen(db: DB) =
            db.hashMap(name)
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

    var changelog: ScryfallChangelog?
        get() = db().use { db ->
            val metaMap = DBCollection.METADATA.createOrOpen(db)
            return metaMap["changelog"] as? ScryfallChangelog
        }
        set(value) = db().use { db ->
            DBCollection.METADATA.createOrOpen(db)
                .put("changelog", value)
        }

    val urls = DbUrlProxy(::db)

    class DbUrlProxy(private val db: () -> DB) : MutableMap<URL, String> {
        private fun <T> proxy(op: HTreeMap<String, Any>.() -> T): T = db().use { db ->
            DBCollection.URLS.createOrOpen(db).let(op)
        }

        override fun get(key: URL) = proxy { this[key.toString()] as String? }
        override fun put(key: URL, value: String) = proxy { this.put(key.toString(), value) as String? }
        override fun remove(key: URL) = proxy { this.remove(key.toString()) as String?}
        override fun clear() = proxy { this.clear() }
        override fun isEmpty() = proxy { this.isEmpty() }
        override val size: Int
            get() = proxy { this.size }
        override val entries: MutableSet<MutableMap.MutableEntry<URL, String>>
            get() = proxy { this.entries }
                .map { (k, v) -> URL(k) to v as String}
                .toMap()
                .toMutableMap()
                .entries
                .toMutableSet()

        override fun containsKey(key: URL) = proxy { this.containsKey(key.toString()) }
        override fun containsValue(value: String) = proxy { this.containsValue(value) }
        override val keys: MutableSet<URL>
            get() = proxy { this.keys.map { URL(it) }.toMutableSet() }
        override val values: MutableCollection<String>
            get() = proxy { this.values.map { it as String }.toMutableList() }

        override fun putAll(from: Map<out URL, String>) = proxy {
            this.putAll(from.entries.map { (k, v) -> k.toString() to v})
        }

    }
}

fun main(args: Array<String>) {
    val cacheDir = createTempDir()
    cacheDir.deleteOnExit()

    val cache1 = ScryfallCache(cacheDir)
    println(cache1.changelog)
    cache1.changelog = ScryfallChangelog.fetch(cache1.changelog)
    println(cache1.changelog)

    val cache2 = ScryfallCache(cacheDir)
    println(cache2.changelog)
    println(cache2.changelog == ScryfallChangelog.fetch(cache2.changelog))
    cache2.clear()
    println(cache2.changelog)
}
