package com.dws.tc.impl

import com.dws.tc.Cache
import com.dws.tc.TCache
import com.dws.tc.annotation.CacheEntity
import com.dws.tc.annotation.CacheId
import com.dws.tc.dto.CacheObject
import com.dws.tc.dto.CacheQueryObject
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.hasAnnotation

class TenantCacheService(cacheService: Cache<Any>):TCache<Any> {

    companion object
    {
        private var cacheService:Cache<Any>?= null
        private val addEntitiesFromQueries= ConcurrentSkipListSet<Pair<String,Any>>()
        private val entitiesForQueryUpdate= ConcurrentSkipListSet<Triple<String,String,String>>() //tenant,(entitytype,entityid)
        private val removeEntitiesFromQueries= ConcurrentSkipListSet<Triple<String,String,String>>() //tenant,(entitytype,entityid)
        private var maintenanceThread:Thread?=null

        private val log=LoggerFactory.getLogger(TenantCacheService::class.java)
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

    override fun hasTenant(name: String) {
        TODO("Not yet implemented")
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

    protected fun removeEntitiesFromQueriesOnRemoveAction(triple:Triple<String,String,String>)
    {
        removeEntitiesFromQueries.add(triple)
    }

    protected fun addEntitiesForQueryUpdate(triple:Triple<String,String,String>)
    {
        entitiesForQueryUpdate.add(triple)
    }

    protected fun addEntitiesfromQueriesToCache(pair:Pair<String,Any>)
    {
        addEntitiesFromQueries.add(pair)
    }

    protected fun setCacheService(service: Cache<Any>)
    {
        cacheService=service
    }
    protected fun getCacheService():Cache<Any>
    {
        return cacheService!!
    }

    protected fun isThreadDead(thread: Thread?): Boolean {
        return thread ==null || !thread.isAlive || thread.isInterrupted
    }

    protected fun initiateMaintenanceThread() {
        if(isThreadDead(maintenanceThread))
        {
            log.info("Starting the background thread")
            maintenanceThread =createMaintenanceThread()
            maintenanceThread?.start()
        }
    }

    protected fun createMaintenanceThread(): Thread {
        return thread(false,true,null,"CacheMaintenanceThread",1,{
            while (true)
            {
                try {
                    //updating queries with updated entities
                    performUpdateToEntitiesInQueries()
                    //adding entities to cache from query result
                    performCacheEntriesFromQueryResult()
                    //removing query results when entities are being removed from persistence.
                    performRemovalOfEntitiesFromQueries()

                    log.info("Background thread taking a nap!!")
                    sleep(Random(100).nextLong(1000))
                }catch (e:Exception)
                {
                    log.error(e.message,e)
                }
            }

        })
    }

    protected fun performUpdateToEntitiesInQueries()
    {
        //check if the list is not empty
        if(entitiesForQueryUpdate.isNotEmpty())
        {
            //iterate over each element
            entitiesForQueryUpdate.forEach { t->
                updateQueryResultWithUpdatedEntity(t)
            }
        }
    }

    protected fun performCacheEntriesFromQueryResult()
    {
        if(addEntitiesFromQueries.isNotEmpty())
        {
            addEntitiesFromQueries.forEach { e->
                _put(e.first,e.second,false,false)
            }
        }
    }

    protected fun performRemovalOfEntitiesFromQueries()
    {
        if(removeEntitiesFromQueries.isNotEmpty())
        {
            removeEntitiesFromQueries.forEach { u ->
                performRemovalOfEntitiesFromQuery(u.first,u.second,u.third)
                removeEntitiesFromQueries.remove(u)
            }
        }

    }

    protected fun performRemovalOfEntitiesFromQuery(tenant:String,entity:String,id:String)
    {
        val cacheKey="${entity}_${id}_queries"
        if(getCacheService().hasKey(tenant,cacheKey)) {
            val entityQueries = getCacheService().get(tenant, cacheKey)
            if(entityQueries!=null && entityQueries is Iterable<*>)
            {
                entityQueries.forEach { k->
                    if(k is String)
                        getCacheService().remove(tenant,k)
                }
            }
        }
    }

    protected fun updateQueryResultWithUpdatedEntity(t:Triple<String,String,String>)
    {
        //fetch the actual entity
        val wrappedEntity= getCacheService().get(t.first,"${t.second}_${t.third}")
        //fetch the list of queries it is associated to
        val queryList= getCacheService().get(t.first,"${t.second}_${t.third}_queries")
        //if the entity and list of queries is not null
        if(wrappedEntity!=null && queryList!=null && queryList is Iterable<*>)
        {
            //filter and iterate over all non null query key
            queryList.filterNotNull().forEach{qk->
                //fetch the query result object
                val cacheQueryObject= getCacheService().get(t.first,qk as String) as CacheQueryObject
                //get the name of the listing attribute for the results
                val listAttrName=cacheQueryObject.queryListAttr
                //get list attribute
                val listAttr=cacheQueryObject.obj::class.members.firstOrNull { e->e.name==listAttrName }
                //check if the list attribute is mutable
                if(listAttr!=null && listAttr is KMutableProperty<*>)
                {
                    //setup a modified list of results
                    val modifiedList= mutableListOf<Any>()
                    (listAttr as List<*>).forEach { e->
                        if(e!=null)
                        {
                            // get the id of the object
                            val id=getIdForObject(e)
                            //if the id matches that of the entity cached then add the new entity in the new result list
                            if(id!=null && id==(wrappedEntity as CacheObject).id)
                            {
                                modifiedList.add(wrappedEntity.obj)
                            }
                            else
                            {
                                //else add the object already available
                                modifiedList.add(e)
                            }
                        }

                    }
                    //updated the new list in the query result
                    (listAttr as KMutableProperty<*>).setter.call(cacheQueryObject.obj,modifiedList)
                }
                else
                {
                    // if list attribute is not mutable then clear the query key from the cache
                    getCacheService().remove(t.first,qk)
                }

            }
        }
    }

    protected fun getIdForObject(value: Any): String? {
        val idAttribute=value::class.members.firstOrNull { m->m.hasAnnotation<CacheId>()}
        return if(idAttribute!=null && idAttribute is KProperty<*>)
        {
            idAttribute.getter.call(value).toString()
        }
        else
        {
            null
        }
    }

    protected fun _put(tenant: String, value: Any,updateAsync: Boolean,performAsyncUpdates:Boolean)
    {
        if(cacheAnnotationsAvailable(value))
        {
            val entityName=getEntityName(value)
            val id=getIdForObject(value)
            if(entityName!=null && id!=null)
            {
                val cacheKey="${entityName}_${id}"
                getCacheService().put(tenant,cacheKey,CacheObject(id,value))
                if(performAsyncUpdates)
                {
                    if(updateAsync) {
                        entitiesForQueryUpdate.add(Triple(tenant,entityName, id))
                    }
                    else
                    {
                        updateQueryResultWithUpdatedEntity(Triple(tenant,entityName, id))
                    }
                }

            }
        }
    }

    protected fun cacheAnnotationsAvailable(value: Any): Boolean {
        return value::class.hasAnnotation<CacheEntity>() && value::class.members.any { e->e.hasAnnotation<CacheId>() }
    }

    protected fun getEntityName(cls: KClass<*>):String?
    {
        return if(cls.hasAnnotation<CacheEntity>())
        {
            cls.simpleName
        }
        else
        {
            null
        }
    }

    protected fun getEntityName(value: Any): String? {
        return getEntityName(value::class)
    }

}