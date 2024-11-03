package com.dws.tc.eh.impl

import com.dws.tc.annotation.CacheEntity
import com.dws.tc.annotation.CacheId
import com.dws.tc.eh.TCache
import com.dws.tc.dto.CacheObject
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


class EHTenantCacheService: TCache<Any> {

    companion object
    {
        private val cacheService=EHCacheService()

        private val addEntitiesfromQueries=ConcurrentHashMap<String,Any>()
        private val entitiesForForUpdate=ConcurrentHashMap<String,Pair<String,String>>() //tenant,(entitytype,entityid)
        private val removeEntitiesFromQueries=ConcurrentHashMap<String,Pair<String,String>>() //tenant,(entitytype,entityid)
        private val queriesToDelete=ConcurrentHashMap<String,String>() // tenant,(queryid,entityid)

        private var maintenanceThread:Thread?=null
        private val log=LoggerFactory.getLogger(EHTenantCacheService::class.java)
    }

    init {
        initiateMaintenanceThread()
    }

    private fun initiateMaintenanceThread() {
        if(isThreadDead(maintenanceThread))
        {
            log.info("Starting the background thread")
            maintenanceThread =createMaintenanceThread()
            maintenanceThread?.start()
        }
    }

    private fun isThreadDead(thread: Thread?): Boolean {
        return thread ==null || !thread.isAlive || thread.isInterrupted
    }


    private fun createMaintenanceThread(): Thread {
        return thread(false,true,null,"CacheMaintenanceThread",1,{
            while (true)
            {
                try {
                    if(entitiesForForUpdate.isNotEmpty())
                    {

                    }

                    if(queriesToDelete.isNotEmpty())
                    {

                    }

                    if(addEntitiesfromQueries.isNotEmpty())
                    {

                    }

                    if(removeEntitiesFromQueries.isNotEmpty())
                    {

                    }

                    log.info("Background thread taking a nap!!")
                    sleep(Random(100).nextLong(1000))
                }catch (e:Exception)
                {
                    log.error(e.message,e)
                }
            }

        })
    }

    override fun createTenantCache(name: String, config: Map<String, String>) {
        cacheService.createCache(name,config)
    }

    override fun removeTenantCache(name: String) {
        cacheService.removeCache(name)
    }

    override fun get(tenant: String, entity: String, id: String, isQuery: Boolean): Any? {
        return when(isQuery)
        {
            false->(cacheService.get(tenant,"${entity}_${id}") as CacheObject).obj
            true->(cacheService.get(tenant,"${tenant}_query_${id}")as CacheObject).obj
        }
    }

    override fun put(tenant: String, value: Any) {
        if(cacheAnnotationsAvailable(value))
        {
            val entityName=getEntityName(value)
            val id=getIdForObject(value)
            if(entityName!=null && id!=null)
            {
                val cacheKey="${entityName}_${id}"
                cacheService.put(tenant,cacheKey,value)
                entitiesForForUpdate.put(tenant, Pair(entityName,id))
            }
            initiateMaintenanceThread()
        }
    }

    private fun getIdForObject(value: Any): String? {
        val idAttribute=value::class.members.any { m->m.annotations.any { e->e.annotationClass==CacheId::class} } as KProperty<*>
        return if(isAnnotationAvailable(value::class,CacheId::class))
        {
            idAttribute.getter.call(value).toString()
        }
        else
        {
            null
        }
    }

    private fun getEntityName(value: Any): String? {
       return getEntityName(value::class)
    }

    private fun cacheAnnotationsAvailable(value: Any): Boolean {
        return isAnnotationAvailable(value::class,CacheEntity::class) && isAnnotationAvailableOnMembers(value::class,CacheId::class)
    }

    private fun isAnnotationAvailableOnMembers(kClass: KClass<out Any>, kClass1: KClass<CacheId>): Boolean {
        return kClass.members.any { m->m.annotations.any { e->e.annotationClass==kClass1} }
    }

    private fun isAnnotationAvailable(cls: KClass<*>,annotation:KClass<*>):Boolean
    {
        return cls.annotations.any { e -> e.annotationClass==annotation::class }
    }

    private fun getEntityName(cls: KClass<*>):String?
    {
        return if(isAnnotationAvailable(cls,CacheEntity::class))
        {
            cls.simpleName
        }
        else
        {
            null
        }
    }

    override fun put(tenant: String, entity: String, id: String, value: Any) {
        cacheService.put(tenant,"${entity}_${id}",value)
        entitiesForForUpdate.put(tenant,Pair(entity,id))
        initiateMaintenanceThread()
    }

    override fun flushAll(tenant: String) {
        cacheService.flushAll(tenant)
    }

    override fun remove(tenant: String, entity: String, id: String, isQuery: Boolean) {
        when(isQuery)
        {
            false-> {
              //  val obj=cacheService.get(tenant, "${entity}_${id}") as CacheObject
//                if(obj!=null)
//                {
//                    val list=cacheService.get(tenant, "${entity}_${id}_queries") as CacheObject
//                    if(list!=null && (list.obj as List<*>).isNotEmpty())
//                    {
//                        list.obj.forEach { key->
//                            cacheService.remove(tenant,key as String)
//                        }
//
//                    }
                //}
                removeEntitiesFromQueries.put(tenant, Pair(entity,id))
                // remove the entity from the cache
                cacheService.remove(tenant,"${entity}_${id}")

            }
            true-> {
                //remove the query where the entity was cached
                cacheService.remove(tenant,"${entity}_query_${id}")
            }
        }
        initiateMaintenanceThread()
    }

    override fun hasKey(tenant: String, entity: String, id: String, isQuery: Boolean): Boolean {
        return when(isQuery)
        {
            false->cacheService.hasKey(tenant,"${entity}_${id}")
            true->cacheService.hasKey(tenant,"${tenant}_query_${id}")
        }
    }

    override fun putQueryResult(tenant: String,queryId:String,value: Any,listAttribute:String) {
        cacheService.put(tenant,"${tenant}_query_${queryId}",value)
        val lAttribute=value::class.members.firstOrNull { e->e.name==listAttribute }
        if(lAttribute!=null && lAttribute is Iterable<*>)
        {
            lAttribute .filterNotNull().forEach { v->
                addEntitiesfromQueries.put(tenant,v)
            }
        }
        initiateMaintenanceThread()
    }


}