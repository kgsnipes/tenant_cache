package com.dws.tc.eh.impl

import com.dws.tc.eh.Cache
import org.ehcache.CacheManager
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.EntryUnit
import java.time.Duration
import java.time.temporal.ChronoUnit


class EHCacheService: Cache<Any> {

    private var _cache:org.ehcache.Cache<String,Any>?=null

    init {
        val manager: CacheManager = CacheManagerBuilder.newCacheManagerBuilder().build()
        manager.init()
        _cache=manager.createCache("buckets",
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                String::class.java,Any::class.java,
                ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10000, EntryUnit.ENTRIES)).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(
                Duration.of(24,ChronoUnit.HOURS))).build())

    }
    override fun get(key: String): Any? {
        return _cache!!.get(key)
    }

    override fun clear() {
       _cache!!.clear()
    }

    override fun remove(key: String) {
        _cache!!.remove(key)
    }

    override fun hasKey(key: String): Boolean {
        return _cache!!.containsKey(key)
    }

    override fun size(): Int {
        throw RuntimeException("Not Supported")
    }

    override fun put(key: String, value: Any?, milli: Long) {
        _cache!!.put(key, value)
    }

    override fun put(key: String, value: Any?) {
        put(key, value,0)
    }
}