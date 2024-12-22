package com.dws.tc.impl

import com.dws.tc.Cache
import java.lang.IllegalArgumentException
import java.util.Properties

class CacheServiceInitializer {

    fun getCachingService(config:Properties):Cache<Any>
    {
        val cacheType=config.getProperty("cache.type")
        if( cacheType!=null && cacheType=="ehcache")
        {
            return EHCacheService()
        }
        else if( cacheType!=null && cacheType=="redis")
        {
            //this is for redis
            return RedisCacheService(config)
        }
        else
        {
            throw IllegalArgumentException("cache type is missing in the config")
        }
    }
}