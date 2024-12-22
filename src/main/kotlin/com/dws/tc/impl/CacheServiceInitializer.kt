package com.dws.tc.impl

import java.util.Properties
import kotlin.system.exitProcess

class CacheServiceInitializer {

    fun getCachingService(config:Properties)
    {
        if(config.getProperty("cache.type","ehcache")=="ehcache")
        {
            return EHCacheService()
        }
        else
        {
            //this is for redis
        }
    }
}