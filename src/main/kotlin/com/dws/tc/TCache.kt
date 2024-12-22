package com.dws.tc

import java.util.Properties

interface TCache<T> {

    //tenant operations
    fun createTenantCache(name:String,config:Properties)
    fun removeTenantCache(name:String)
    fun hasTenant(name:String):Boolean

    //read operations
    fun get(tenant:String,entity:String,id:String,isQuery: Boolean):T?

    //write operations
    fun put(tenant:String,value:T,updateAsync:Boolean)
    fun put(tenant:String,entity:String,id:String,value:T,updateAsync:Boolean)
    fun putQueryResult(tenant:String,queryId:String,value: Any,listAttribute:String,updateAsync:Boolean)

    //delete operations
    fun flushAll(tenant:String)
    fun remove(tenant:String,entity:String,id:String,isQuery:Boolean,updateAsync:Boolean)

    fun hasKey(tenant:String,entity:String,id:String,isQuery:Boolean):Boolean
    fun hasKey(tenant:String,value:T, isQuery: Boolean):Boolean

}