package com.dws.tc.eh.impl

import com.dws.tc.eh.TCache
import com.dws.tc.dto.CacheObject


class EHTenantCacheService: TCache<Any> {

    companion object
    {
        private val cacheService=EHCacheService()
    }

    override fun createTenantCache(name: String, config: Map<String, String>) {
        cacheService.createCache(name,config)

    }

    override fun get(tenant: String, entity: String, id: String, isQuery: Boolean): Any? {
        return when(isQuery)
        {
            false->(cacheService.get(tenant,"${entity}_${id}") as CacheObject).obj
            true->(cacheService.get(tenant,"${tenant}_query_${id}")as CacheObject).obj
        }
    }

    override fun flushAll(tenant: String) {
        cacheService.flushAll(tenant)
    }

    override fun remove(tenant: String, entity: String, id: String, isQuery: Boolean) {

        when(isQuery)
        {
            false-> {
                val obj=cacheService.get(tenant, "${entity}_${id}") as CacheObject
                if(obj!=null)
                {
                    val list=cacheService.get(tenant, "${entity}_${id}_queries") as CacheObject
                    if(list!=null && (list.obj as List<*>).isNotEmpty())
                    {
                        list.obj.forEach { key->
                            cacheService.remove(tenant,key as String)
                        }

                    }
                    cacheService.remove(tenant,"${entity}_${id}")
                }

            }
            true-> {
                cacheService.remove(tenant,"${entity}_query_${id}")
            }
        }
    }

    override fun hasKey(tenant: String, entity: String, id: String, isQuery: Boolean): Boolean {
        return when(isQuery)
        {
            false->cacheService.hasKey(tenant,"${entity}_${id}")
            true->cacheService.hasKey(tenant,"${tenant}_query_${id}")
        }
    }

    override fun put(tenant: String, entity: String, id: String, value: Any, isQuery: Boolean,entityIds:List<String>) {
        when(isQuery)
        {
            false->{

            }
            true->{

            }
        }
    }


}