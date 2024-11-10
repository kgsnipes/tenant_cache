package com.dws.tc

interface RedisCache<T>:Cache<T> {

    fun getCache(bucket:String): Map<String,String>?
}