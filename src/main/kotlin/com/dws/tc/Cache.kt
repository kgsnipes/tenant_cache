package com.dws.tc

interface Cache<T> {
    fun createCache(bucket:String,config:Map<String,String>)
    fun removeCache(bucket:String)
    fun hasTenant(bucket:String):Boolean
    fun get(bucket:String,key:String):T?
    fun put(bucket:String,key:String, value:T)
    fun flushAll(bucket:String)
    fun remove(bucket:String,key:String)
    fun hasKey(bucket:String,key:String):Boolean
}