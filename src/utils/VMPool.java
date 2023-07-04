package utils;

import java.util.ArrayList;

public class VMPool {
    private static VMPool vmPool;
    private ArrayList<VM> vms=new ArrayList<>();

    public synchronized static VMPool getVMPool() {
        if (vmPool == null) {
            vmPool = new VMPool();
        }
        return vmPool;
    }

    private VMPool() {
    }

    public void addVm(VM vm){
        vms.add(vm);
    }

    public VM getFastestFreeVm(){
        VM res=null;
        for (VM vm : vms) {
            if ((res==null || res.getMips()<vm.getMips())&&vm.isFree()) {
                res=vm;
            }
        }
        return res;
    }

    public VM getVmById(int vmId){
        return vms.get(vmId);
    }

    @Override
    public String toString() {
        String s="";
        for (VM vm : vms) {
            s=s.concat(vm+"\n");
        }
        return s;
    }
    
}
