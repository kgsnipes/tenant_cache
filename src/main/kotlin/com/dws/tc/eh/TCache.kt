package com.dws.tc.eh

interface TCache<T> {

    fun createTenantCache(name:String,config:Map<String,String>)
    fun removeTenantCache(name:String)

    fun get(tenant:String,entity:String,id:String,isQuery:Boolean):T?

    fun put(tenant:String,value:T,updateAsync:Boolean)
    fun put(tenant:String,entity:String,id:String,value:T,updateAsync:Boolean)

    fun putQueryResult(tenant:String,queryId:String,value: Any,listAttribute:String,updateAsync:Boolean)

    fun flushAll(tenant:String)

    fun remove(tenant:String,entity:String,id:String,isQuery:Boolean,updateAsync:Boolean)

    fun hasKey(tenant:String,entity:String,id:String,isQuery:Boolean):Boolean

}