package com.dws.tc

import com.dws.tc.annotation.CacheEntity
import com.dws.tc.annotation.CacheId
import com.dws.tc.eh.TCache
import com.dws.tc.eh.impl.EHTenantCacheService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EHTCTest {

    @Test
    fun test01()
    {
        val tenant="tenant01"
        val tc=getEHTC()
        tc.createTenantCache(tenant, mapOf(Pair("maxItems","1000"), Pair("ttlHours","24")))
        tc.put(tenant,EntityData().also {
            it.id="123456"
            it.name="name 123456"
        },false)
        tc.put(tenant,EntityData().also {
            it.id="1234567"
            it.name="name 1234567"
        },false)
        tc.put(tenant,EntityData().also {
            it.id="123456"
            it.name="name lastname"
        },false)

        Assertions.assertEquals("name lastname",(tc.get(tenant,EntityData::class.simpleName!!,"123456",false) as EntityData).name)
    }

    fun getEHTC():TCache<Any>
    {
        return EHTenantCacheService()
    }

    @CacheEntity
    class EntityData(){
        @CacheId
        var id:String?=null
        var name:String?=null
    }

    class PagedResult(var results:List<String>,val page:Int,val limit:Int)
}