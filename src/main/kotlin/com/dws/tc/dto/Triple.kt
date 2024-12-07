package com.dws.tc.dto

class Triple(val first:String,val second:String,val third:String):Comparable<Triple>
{
    override fun compareTo(other: Triple): Int {
        return if(this.hashCode()==other.hashCode())
        {
            0
        }
        else if(this.hashCode()>=other.hashCode())
        {
             1
        }
        else
        {
             -1
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Triple

        if (first != other.first) return false
        if (second != other.second) return false
        if (third != other.third) return false

        return true
    }

    override fun hashCode(): Int {
        var result = first.hashCode()
        result = 31 * result + second.hashCode()
        result = 31 * result + third.hashCode()
        return result
    }

}
