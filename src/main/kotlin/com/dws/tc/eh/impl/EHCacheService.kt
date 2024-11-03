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

    companion object
    {
        private val cacheMap= mutableMapOf<String,org.ehcache.Cache<String,Any>>()
        private val manager: CacheManager = CacheManagerBuilder.newCacheManagerBuilder().build()
    }

    init {
        manager.init()
    }

    override fun createCache(bucket: String, config: Map<String, String>) {
        val cache=manager.createCache(bucket,
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                String::class.java,Any::class.java,
                ResourcePoolsBuilder.newResourcePoolsBuilder().heap(config["maxItems"]!!.toLong(), EntryUnit.ENTRIES)).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(
                Duration.of(config["ttlHours"]!!.toLong(),ChronoUnit.HOURS))).build())
        cacheMap[bucket] = cache
    }

    override fun removeCache(bucket: String) {
        cacheMap.remove(bucket)
    }

    override fun getCache(bucket: String): org.ehcache.Cache<String, Any>? {
        return cacheMap[bucket]
    }

    override fun get(bucket:String,key: String): Any? {
        return getCache(bucket)!!.get(key)
    }

    override fun flushAll(bucket:String,) {
        getCache(bucket)!!.clear()
    }

    override fun remove(bucket:String,key: String) {
        getCache(bucket)!!.remove(key)
    }

    override fun hasKey(bucket:String,key: String): Boolean {
        return getCache(bucket)!!.containsKey(key)
    }

    override fun put(bucket:String,key: String, value: Any) {
        getCache(bucket)!!.put(key, value)
    }
}