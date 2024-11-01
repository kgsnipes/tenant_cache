package com.dws.tc.eh

import org.ehcache.Cache

interface Cache<T> {
    fun createCache(bucket:String,config:Map<String,String>)
    fun getCache(bucket:String):Cache<String,T>?
    fun get(bucket:String,key:String):T?
    fun put(bucket:String,key:String, value:T)
    fun flushAll(bucket:String)
    fun remove(bucket:String,key:String)
    fun hasKey(bucket:String,key:String):Boolean
}