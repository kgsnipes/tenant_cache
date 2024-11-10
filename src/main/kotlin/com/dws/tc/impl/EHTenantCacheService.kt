package com.dws.tc.impl

import com.dws.tc.Cache
import com.dws.tc.TCache
import com.dws.tc.dto.CacheObject
import com.dws.tc.dto.CacheQueryObject
import org.slf4j.LoggerFactory

class EHTenantCacheService(cacheService: Cache<Any>): AbstractCacheService(), TCache<Any> {

    companion object
    {
        private val log=LoggerFactory.getLogger(EHTenantCacheService::class.java)
    }

    init {
        initiateMaintenanceThread()
        setCacheService(cacheService)
    }

    override fun createTenantCache(name: String, config: Map<String, String>) {
        getCacheService().createCache(name,config)
    }

    override fun removeTenantCache(name: String) {
        getCacheService().removeCache(name)
    }

    override fun get(tenant: String, entity: String, id: String, isQuery: Boolean): Any? {
        return when(isQuery)
        {
            false->{
                val cacheObject= getCacheService().get(tenant,"${entity}_${id}")
                if(cacheObject!=null)
                {
                    (cacheObject as CacheObject).obj
                }
                else
                {
                    null
                }
            }
            true->{
                val cacheObject= getCacheService().get(tenant,"${tenant}_query_${id}")
                if(cacheObject!=null)
                {
                    (cacheObject as CacheQueryObject).obj
                }
                else
                {
                    null
                }

            }
        }
    }

    override fun put(tenant: String, value: Any,updateAsync:Boolean) {
        _put(tenant,value,updateAsync,true)
        initiateMaintenanceThread()
    }


    override fun put(tenant: String, entity: String, id: String, value: Any,updateAsync:Boolean) {
        getCacheService().put(tenant,"${entity}_${id}",CacheObject(id,value))
        if(updateAsync) {
           // entitiesForQueryUpdate.add(Triple(tenant,entity, id))
            addEntitiesForQueryUpdate(Triple(tenant,entity, id))
        }
        else
        {
            TODO()
        }
        initiateMaintenanceThread()
    }

    override fun flushAll(tenant: String) {
        getCacheService().flushAll(tenant)
    }

    override fun remove(tenant: String, entity: String, id: String, isQuery: Boolean,updateAsync:Boolean) {
        when(isQuery)
        {
            false-> {
                // remove the entity from the cache
                getCacheService().remove(tenant,"${entity}_${id}")

                if(updateAsync) {
                    removeEntitiesFromQueriesOnRemoveAction(Triple(tenant,entity, id))
                }
                else
                {
                    performRemovalOfEntitiesFromQuery(tenant,entity, id)
                }


            }
            true-> {
                //remove the query where the entity was cached
                getCacheService().remove(tenant,"${entity}_query_${id}")
            }
        }
        initiateMaintenanceThread()
    }

    override fun hasKey(tenant: String, entity: String, id: String, isQuery: Boolean): Boolean {
        return when(isQuery)
        {
            false-> getCacheService().hasKey(tenant,"${entity}_${id}")
            true-> getCacheService().hasKey(tenant,"${tenant}_query_${id}")
        }
    }

    override fun putQueryResult(tenant: String,queryId:String,value: Any,listAttribute:String,updateAsync:Boolean) {
        getCacheService().put(tenant,"${tenant}_query_${queryId}",CacheQueryObject(queryId,value,listAttribute))
        val lAttribute=value::class.members.firstOrNull { e->e.name==listAttribute }
        if(lAttribute!=null && lAttribute is Iterable<*>)
        {
            lAttribute .filterNotNull().forEach { v->
                if(updateAsync) {
                    addEntitiesfromQueriesToCache(Pair(tenant,v))
                }
                else
                {
                    put(tenant,v,false)
                }
            }
        }
        initiateMaintenanceThread()
    }

}