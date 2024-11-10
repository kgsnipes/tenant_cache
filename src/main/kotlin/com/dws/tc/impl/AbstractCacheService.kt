package com.dws.tc.impl

import com.dws.tc.Cache
import com.dws.tc.impl.EHTenantCacheService.Companion
import com.dws.tc.impl.EHTenantCacheService.Companion.log
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.concurrent.thread
import kotlin.random.Random

abstract class AbstractCacheService {
    companion object
    {
        private var cacheService:Cache<Any>?= null
        private val addEntitiesfromQueries= ConcurrentSkipListSet<Pair<String,Any>>()
        private val entitiesForQueryUpdate= ConcurrentSkipListSet<Triple<String,String,String>>() //tenant,(entitytype,entityid)
        private val removeEntitiesFromQueries= ConcurrentSkipListSet<Triple<String,String,String>>() //tenant,(entitytype,entityid)
        private var maintenanceThread:Thread?=null
        private val log=LoggerFactory.getLogger(AbstractCacheService::class.java)

    }

    protected fun setCacheService(service: Cache<Any>)
    {
        cacheService=service
    }
    protected fun getCacheService():Cache<Any>
    {
        return cacheService!!
    }

    private fun isThreadDead(thread: Thread?): Boolean {
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

                    EHTenantCacheService.log.info("Background thread taking a nap!!")
                    sleep(Random(100).nextLong(1000))
                }catch (e:Exception)
                {
                    EHTenantCacheService.log.error(e.message,e)
                }
            }

        })
    }

    private fun performUpdateToEntitiesInQueries()
    {
        //check if the list is not empty
        if(com.dws.tc.impl.EHTenantCacheService.entitiesForQueryUpdate.isNotEmpty())
        {
            //iterate over each element
            com.dws.tc.impl.EHTenantCacheService.entitiesForQueryUpdate.forEach { t->
                updateQueryResultWithUpdatedEntity(t)
            }
        }
    }
}