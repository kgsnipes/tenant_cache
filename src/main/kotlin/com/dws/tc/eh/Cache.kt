package com.dws.tc.eh

interface Cache<T> {
    fun get(key:String):T?
    fun put(key:String, value:T?)
    fun put(key:String, value:T?,milli:Long)
    fun clear()
    fun remove(key:String)
    fun hasKey(key:String):Boolean
    fun size():Int
}