package com.dws.tc.eh.impl

import com.dws.tc.eh.TCache
import com.dws.tc.eh.dto.TenantCache
import org.ehcache.Cache

import org.ehcache.CacheManager
import org.ehcache.config.builders.CacheManagerBuilder


class EHTenantCacheService: TCache<Any> {

    companion object
    {
        private val tenantCacheMap= mutableMapOf<String, Cache<String, *>>()
        private val manager: CacheManager = CacheManagerBuilder.newCacheManagerBuilder().build()
    }

    init {
        manager.init()
    }

    override fun getCacheManager(): CacheManager {
        TODO("Not yet implemented")
    }

    override fun createTenantCache(name: String, config: Map<String, String>): TenantCache {
        TODO("Not yet implemented")
    }

    override fun get(tenant: String, entity: String, id: String, isQuery: Boolean): Any? {
        TODO("Not yet implemented")
    }

    override fun flushAll(tenant: String) {
        TODO("Not yet implemented")
    }

    override fun remove(tenant: String, entity: String, id: String, isQuery: Boolean) {
        TODO("Not yet implemented")
    }

    override fun hasKey(tenant: String, entity: String, id: String, isQuery: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun size(): Int {
        TODO("Not yet implemented")
    }

    override fun put(tenant: String, entity: String, id: String, value: Any, isQuery: Boolean) {
        TODO("Not yet implemented")
    }


}