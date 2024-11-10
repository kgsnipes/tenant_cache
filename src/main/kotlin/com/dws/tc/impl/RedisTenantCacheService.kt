package com.dws.tc.impl

import com.dws.tc.RedisCache
import com.dws.tc.TCache

class RedisTenantCacheService(private val config:Map<String,String>):TCache<Any> {

    companion object
    {
        private var redisCache:RedisCacheService?=null
    }

    init {
        redisCache=RedisCacheService(config)
    }

    override fun createTenantCache(name: String, config: Map<String, String>) {
        TODO("Not yet implemented")
    }

    override fun removeTenantCache(name: String) {
        TODO("Not yet implemented")
    }

    override fun get(tenant: String, entity: String, id: String, isQuery: Boolean): Any? {
        TODO("Not yet implemented")
    }

    override fun putQueryResult(
        tenant: String,
        queryId: String,
        value: Any,
        listAttribute: String,
        updateAsync: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun flushAll(tenant: String) {
        TODO("Not yet implemented")
    }

    override fun remove(tenant: String, entity: String, id: String, isQuery: Boolean, updateAsync: Boolean) {
        TODO("Not yet implemented")
    }

    override fun hasKey(tenant: String, entity: String, id: String, isQuery: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun put(tenant: String, entity: String, id: String, value: Any, updateAsync: Boolean) {
        TODO("Not yet implemented")
    }

    override fun put(tenant: String, value: Any, updateAsync: Boolean) {
        TODO("Not yet implemented")
    }
}