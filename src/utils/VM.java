package utils;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;

public class VM extends Vm {
    private boolean isFree;

    public VM(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size, String vmm,
            CloudletScheduler cloudletScheduler) {
        super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
        this.setFree(false);
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean isFree) {
        this.isFree = isFree;
    }

    @Override
    public String toString() {
        
        return "[ id:"+getId()+" isFree:"+isFree+" mips:"+getMips()+" ]";
    }

    
    
}
