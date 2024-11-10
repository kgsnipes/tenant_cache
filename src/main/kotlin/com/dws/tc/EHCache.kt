package com.dws.tc

import org.ehcache.Cache

interface EHCache<T>:com.dws.tc.Cache<T> {
    fun getCache(bucket:String):Cache<String,T>?
}