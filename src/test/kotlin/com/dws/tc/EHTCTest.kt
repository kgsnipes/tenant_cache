package com.dws.tc

import com.dws.tc.annotation.CacheEntity
import com.dws.tc.annotation.CacheId
import com.dws.tc.impl.EHCacheService
import com.dws.tc.impl.TenantCacheService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EHTCTest {

    var tc:TenantCacheService?=null

    @Test
    fun test01()
    {
        val tenant="tenant01"
        val tc=getEHTC()
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

    @Test
    fun test02()
    {
        val tenant="tenant01"
        val tc=getEHTC()

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

    @Test
    fun test03()
    {
        val tenant="tenant01"
        val tc=getEHTC()
        val itemsList= listOf(EntityData().also {
            it.id="123456"
            it.name="name 123456"
        },EntityData().also {
            it.id="1234567"
            it.name="name 1234567"
        },EntityData().also {
            it.id="123456"
            it.name="name lastname"
        })
        val pagedResult=PagedResult(itemsList,1,10)
        tc.putQueryResult(tenant,"123234",pagedResult,"results",true)

        Assertions.assertEquals("name lastname",((tc.get(tenant,PagedResult::class.simpleName!!,"123234",true) as PagedResult).results.last() as EntityData).name)
    }

    fun getEHTC(): TCache<Any>
    {
        val tenant="tenant01"
        if(tc==null)
        {
            tc=TenantCacheService(EHCacheService())
        }
        if(!tc!!.hasTenant(tenant))
        {
            tc!!.createTenantCache(tenant, mapOf(Pair("maxItems","100"), Pair("ttlHours","24")))
        }

        return tc!!
    }

    @CacheEntity
    class EntityData(){
        @CacheId
        var id:String?=null
        var name:String?=null
    }

    class PagedResult(var results:List<*>,val page:Int,val limit:Int)
}