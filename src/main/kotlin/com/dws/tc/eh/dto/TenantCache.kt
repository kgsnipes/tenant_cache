package com.dws.tc.eh.dto

import org.ehcache.Cache

data class TenantCache(val tenantId:String,val entities:List<String>,val cacheMap:MutableMap<String, Cache<String, *>>)
