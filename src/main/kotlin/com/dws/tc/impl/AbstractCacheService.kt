package com.dws.tc.impl

import com.dws.tc.Cache
import com.dws.tc.TCache
import com.dws.tc.annotation.CacheEntity
import com.dws.tc.annotation.CacheId
import com.dws.tc.dto.CacheObject
import com.dws.tc.dto.CacheQueryObject
import com.dws.tc.impl.EHTenantCacheService.Companion
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.hasAnnotation

abstract class AbstractCacheService: TCache<Any> {
    companion object
    {
        private val log=LoggerFactory.getLogger(AbstractCacheService::class.java)

    }




}