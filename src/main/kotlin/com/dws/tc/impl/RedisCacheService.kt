package com.dws.tc.impl

import com.dws.tc.RedisCache
import com.dws.tc.util.fromByteArray
import com.dws.tc.util.toByteArray
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulConnection
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.cluster.RedisClusterClient
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection
import io.lettuce.core.codec.ByteArrayCodec
import io.lettuce.core.support.ConnectionPoolSupport
import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig


class RedisCacheService(private val config:Map<String,String>): RedisCache<Any> {
    companion object
    {

        private var redisClient:RedisClient?=null
        private var redisClusterClient:RedisClusterClient?=null
        private var connectionPool:GenericObjectPool<StatefulConnection<ByteArray, ByteArray>>?=null
    }

    init {
        assert(config["mode"]!=null)
        assert(config["host"]!=null)
        assert(config["port"]!=null)
        assert(config["ttlHours"]!=null)
        when(isClusterMode())
        {
            true-> {
                redisClusterClient = RedisClusterClient.create(RedisURI.create(config["host"], config["port"]!!.toInt()))
//                connectionPool= ConnectionPoolSupport.createGenericObjectPool({ redisClusterClient!!.connect(
//                    ByteArrayCodec()
//                ) },
//                    GenericObjectPoolConfig()
//                )
            }
            false-> {
                redisClient = RedisClient.create(RedisURI.create(config["host"], config["port"]!!.toInt()))
                connectionPool= ConnectionPoolSupport.createGenericObjectPool({ redisClient!!.connect(ByteArrayCodec()) },GenericObjectPoolConfig())
            }
        }
    }

    private fun isClusterMode():Boolean
    {
        return config["mode"]=="cluster"
    }

    private fun getStatefulRedisConnection(): StatefulRedisConnection<ByteArray, ByteArray>?
    {
        return redisClient!!.connect(ByteArrayCodec())
    }

    private fun getStatefulRedisClusterConnection(): StatefulRedisClusterConnection<ByteArray, ByteArray>?
    {
        return redisClusterClient!!.connect(ByteArrayCodec())
    }

    override fun createCache(bucket: String, config: Map<String, String>) {
        if(!isClusterMode())
        {
            getStatefulRedisConnection()!!.sync().set("${bucket}_cache".encodeToByteArray(), config.toByteArray())
        }
        else
        {
            getStatefulRedisClusterConnection()!!.sync().set("${bucket}_cache".encodeToByteArray(), config.toByteArray())
        }
    }

    override fun removeCache(bucket: String) {
        if(!isClusterMode())
        {
            getStatefulRedisConnection()!!.sync().del("${bucket}_cache".encodeToByteArray())
        }
        else
        {
            getStatefulRedisClusterConnection()!!.sync().del("${bucket}_cache".encodeToByteArray())
        }

    }

    override fun getCache(bucket: String): Map<String,String>? {
        return if(!isClusterMode())
        {
            fromByteArray(getStatefulRedisConnection()!!.sync().get("${bucket}_cache".encodeToByteArray())) as Map<String, String>?
        }
        else
        {
            fromByteArray(getStatefulRedisClusterConnection()!!.sync().get("${bucket}_cache".encodeToByteArray())) as Map<String, String>?
        }
    }

    override fun get(bucket: String, key: String): Any? {
        return if(!isClusterMode())
            {
                fromByteArray(getStatefulRedisConnection()!!.sync().get("${bucket}_${key}".encodeToByteArray()))
            }
            else
            {
                fromByteArray(getStatefulRedisClusterConnection()!!.sync().get("${bucket}_${key}".encodeToByteArray()))
            }
    }

    override fun flushAll(bucket: String) {
        if(!isClusterMode())
        {
            getStatefulRedisConnection()!!.sync().del("${bucket}_cache".encodeToByteArray())
        }
        else
        {
            getStatefulRedisClusterConnection()!!.sync().del("${bucket}_cache".encodeToByteArray())
        }
    }

    override fun remove(bucket: String, key: String) {
        if(!isClusterMode())
        {
            getStatefulRedisConnection()!!.sync().del("${bucket}_${key}".encodeToByteArray())
        }
        else
        {
            getStatefulRedisClusterConnection()!!.sync().del("${bucket}_${key}".encodeToByteArray())
        }
    }

    override fun hasKey(bucket: String, key: String): Boolean {
        return if(!isClusterMode())
        {
            getStatefulRedisConnection()!!.sync().get("${bucket}_${key}".encodeToByteArray())!=null
        }
        else
        {
            getStatefulRedisClusterConnection()!!.sync().del("${bucket}_${key}".encodeToByteArray())!=null
        }
    }

    override fun put(bucket: String, key: String, value: Any) {
            if(!isClusterMode())
            {
                getStatefulRedisConnection()!!.sync().setex("${bucket}_${key}".encodeToByteArray(),config["ttlHours"]!!.toLong()*3600,value.toByteArray())
            }
            else
            {
                getStatefulRedisClusterConnection()!!.sync().setex("${bucket}_${key}".encodeToByteArray(),config["ttlHours"]!!.toLong()*3600,value.toByteArray())
            }
    }

    override fun hasTenant(bucket: String):Boolean {
    return if(!isClusterMode())
        {
            getStatefulRedisConnection()!!.sync().get("${bucket}_cache".encodeToByteArray())!=null
        }
        else
        {
            getStatefulRedisClusterConnection()!!.sync().del("${bucket}_cache".encodeToByteArray())!=null
        }
    }
}