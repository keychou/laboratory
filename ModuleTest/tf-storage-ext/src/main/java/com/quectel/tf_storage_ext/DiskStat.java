package com.quectel.tf_storage_ext;

/**
 * Created by klein on 18-9-6.
 */

public class DiskStat {
    private long free;
    private long total;

    public DiskStat(long free, long total) {
        this.free = free;
        this.total = total;
    }

    @Override
    public String toString() {
        String info = "total: " + total + ", free: " + free;
        return info;
    }

    public long getTotalCapacity(){
        return total;
    }

    public long getFreeCapacity(){
        return free;
    }

    public long getTotalCapacityByHuman(){
        return total/1024/1024;
    }

    public long getFreeCapacityByHuman(){
        return free/1024/1024;
    }
}
