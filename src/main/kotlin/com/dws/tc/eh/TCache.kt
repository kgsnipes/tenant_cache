package com.dws.tc.eh

interface TCache<T> {

    fun createTenantCache(name:String,config:Map<String,String>)

    fun get(tenant:String,entity:String,id:String,isQuery:Boolean):T?

    fun put(tenant:String,entity:String,id:String,value:T,isQuery:Boolean)

    fun flushAll(tenant:String)

    fun remove(tenant:String,entity:String,id:String,isQuery:Boolean)

    fun hasKey(tenant:String,entity:String,id:String,isQuery:Boolean):Boolean

}