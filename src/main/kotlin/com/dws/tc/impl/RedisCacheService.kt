package com.dws.tc.impl

import com.dws.tc.Cache

class RedisCacheService: Cache<Any> {
    override fun createCache(bucket: String, config: Map<String, String>) {
        TODO("Not yet implemented")
    }

    override fun removeCache(bucket: String) {
        TODO("Not yet implemented")
    }

    override fun getCache(bucket: String): org.ehcache.Cache<String, Any>? {
        TODO("Not yet implemented")
    }

    override fun get(bucket: String, key: String): Any? {
        TODO("Not yet implemented")
    }

    override fun flushAll(bucket: String) {
        TODO("Not yet implemented")
    }

    override fun remove(bucket: String, key: String) {
        TODO("Not yet implemented")
    }

    override fun hasKey(bucket: String, key: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun put(bucket: String, key: String, value: Any) {
        TODO("Not yet implemented")
    }
}