package com.gwax.scryfall

import com.gwax.scryfall.models.ScryfallCard
import com.gwax.scryfall.models.ScryfallSet
import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.HTreeMap
import org.mapdb.Serializer
import java.io.File
import java.net.URL

private inline fun <reified T> DB.transact(op: (DB) -> T): T =
    try {
        op(this)
    } catch (e: Exception) {
        this.rollback()
        throw e
    } finally {
        this.commit()
    }


class ScryfallCache(cacheDir: File) {
    companion object {
        private const val DB_START_SIZE = 100 * 1024 * 1024L
        private const val DB_INCREMENT_SIZE = 10 * 1024 * 1024L
    }

    private enum class DBCollection {
        METADATA,
        URLS,
        COLLECTIONS;

        fun createOrOpen(db: DB) =
            db.hashMap(name)
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.JAVA)
                .createOrOpen()
    }

    private val dbFile = cacheDir.resolve("scrycache.db")
    private val db =
        DBMaker.fileDB(dbFile)
            .allocateStartSize(DB_START_SIZE)
            .allocateIncrement(DB_INCREMENT_SIZE)
            .closeOnJvmShutdown()
            .transactionEnable()
            .make()

    fun close() = db.close()

    fun clear() = db.transact { db ->
        enumValues<DBCollection>().forEach { coll ->
            coll.createOrOpen(db).clearWithExpire()
        }
    }

    var changelog: ScryfallChangelog?
        get() {
            val metaMap = DBCollection.METADATA.createOrOpen(db)
            return metaMap["changelog"] as? ScryfallChangelog
        }
        set(value) = db.transact { db ->
            DBCollection.METADATA.createOrOpen(db)
                .put("changelog", value)
        }

    var allCards: List<ScryfallCard>?
        get() {
            val collectionMap = DBCollection.COLLECTIONS.createOrOpen(db)
            return collectionMap["all_cards"] as? List<ScryfallCard>
        }
        set(value) = db.transact { db ->
            DBCollection.COLLECTIONS.createOrOpen(db)
                .put("all_cards", value)
        }

    var allSets: List<ScryfallSet>?
        get() {
            val collectionMap = DBCollection.COLLECTIONS.createOrOpen(db)
            return collectionMap["all_sets"] as? List<ScryfallSet>
        }
        set(value) = db.transact { db ->
            DBCollection.COLLECTIONS.createOrOpen(db)
                .put("all_sets", value)
        }

    val urls = DbUrlProxy(db)

    class DbUrlProxy(private val db: DB) : MutableMap<URL, String> {
        private fun <T> readProxy(op: HTreeMap<String, Any>.() -> T): T =
            DBCollection.URLS.createOrOpen(db).let(op)

        inline private fun <reified T> writeProxy(op: HTreeMap<String, Any>.() -> T): T = db.transact { db ->
            DBCollection.URLS.createOrOpen(db).let(op)
        }

        override fun get(key: URL) = readProxy { this[key.toString()] as String? }
        override fun put(key: URL, value: String) = writeProxy { this.put(key.toString(), value) as String? }
        override fun remove(key: URL) = writeProxy { this.remove(key.toString()) as String?}
        override fun clear() = writeProxy { this.clear() }
        override fun isEmpty() = readProxy { this.isEmpty() }
        override val size: Int
            get() = readProxy { this.size }
        override val entries: MutableSet<MutableMap.MutableEntry<URL, String>>
            get() = readProxy { this.entries }
                .map { (k, v) -> URL(k) to v as String}
                .toMap()
                .toMutableMap()
                .entries
                .toMutableSet()

        override fun containsKey(key: URL) = readProxy { this.containsKey(key.toString()) }
        override fun containsValue(value: String) = readProxy { this.containsValue(value) }
        override val keys: MutableSet<URL>
            get() = readProxy { this.keys.map { URL(it) }.toMutableSet() }
        override val values: MutableCollection<String>
            get() = readProxy { this.values.map { it as String }.toMutableList() }

        override fun putAll(from: Map<out URL, String>) = writeProxy {
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
    cache1.close()

    val cache2 = ScryfallCache(cacheDir)
    println(cache2.changelog)
    println(cache2.changelog == ScryfallChangelog.fetch(cache2.changelog))
    cache2.clear()
    println(cache2.changelog)
}
