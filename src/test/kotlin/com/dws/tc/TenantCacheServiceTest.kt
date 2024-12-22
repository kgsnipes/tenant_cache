package com.dws.tc

import com.dws.tc.impl.CacheServiceInitializer
import com.dws.tc.impl.TenantCacheService
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TenantCacheServiceTest {
    private var tc:TenantCacheService?=null

    @Test
    @Order(1)
    fun initializeCache()
    {
        assertDoesNotThrow {
            tc=TenantCacheService(CacheServiceInitializer().getCachingService(mapOf(Pair("cache.type","ehcache")).toProperties()))
            createTenantInCache()
            addObjectToTenant()
            getObjectFromTenant()
        }
    }

    @Test
    @Order(2)
    fun createTenantInCache()
    {
        assertDoesNotThrow {
            tc!!.createTenantCache("sample-tenant",mapOf(Pair("ehcache.maxentries","100"), Pair("ehcache.ttlminutes","24")).toProperties())

        }
    }

    @Test
    @Order(3)
    fun addObjectToTenant()
    {
        assertDoesNotThrow {
            tc!!.put("sample-tenant","SampleEntity","1234","hello",false)
        }
    }

    @Test
    @Order(4)
    fun getObjectFromTenant()
    {
        assertEquals("hello",tc!!.get("sample-tenant","SampleEntity","1234",false))
    }
}