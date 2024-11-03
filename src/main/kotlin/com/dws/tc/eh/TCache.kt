package com.dws.tc.eh

interface TCache<T> {

    fun createTenantCache(name:String,config:Map<String,String>)
    fun removeTenantCache(name:String)

    fun get(tenant:String,entity:String,id:String,isQuery:Boolean):T?

    fun put(tenant:String,value:T)
    fun put(tenant:String,entity:String,id:String,value:T)

    fun put(tenant:String,entity:String,id:String,value:T,entityIds:List<String>)

    fun flushAll(tenant:String)

    fun remove(tenant:String,entity:String,id:String,isQuery:Boolean)

    fun hasKey(tenant:String,entity:String,id:String,isQuery:Boolean):Boolean

}