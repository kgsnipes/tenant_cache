package com.dws.tc.eh

import com.dws.tc.eh.dto.TenantCache
import org.ehcache.Cache
import org.ehcache.CacheManager


interface TCache<T> {

    fun getCacheManager(): CacheManager

    fun createTenantCache(name:String,config:Map<String,String>): TenantCache

    fun get(tenant:String,entity:String,id:String,isQuery:Boolean):T?

    fun put(tenant:String,entity:String,id:String,value:T,isQuery:Boolean)

    fun flushAll(tenant:String)

    fun remove(tenant:String,entity:String,id:String,isQuery:Boolean)

    fun hasKey(tenant:String,entity:String,id:String,isQuery:Boolean):Boolean

    fun size():Int
}