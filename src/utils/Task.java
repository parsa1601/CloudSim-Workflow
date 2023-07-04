package utils;

import java.util.ArrayList;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;

public class Task extends Cloudlet {

    private int deadline=-1;
    private int slackTime;
    private boolean running;
    private ArrayList<Integer> parents = new ArrayList<>();

    public Task(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize,
            UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam,
            UtilizationModel utilizationModelBw) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu,
                utilizationModelRam, utilizationModelBw);
        this.setRunning(false);
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getSlackTime() {
        return slackTime;
    }

    public void setSlackTime(int slackTime) {
        this.slackTime = slackTime;
    }

    public void addParent(int parentID){
        parents.add(parentID);
    }

    public ArrayList<Integer> getParents(){
        return parents;
    }

    @Override
    public String toString() {
        String parents = "";
        boolean isFirstParent=true;
        for (Integer integer : this.parents) {
            if (isFirstParent) {
                parents=parents+integer;
                isFirstParent=false;
            }else{
                parents=parents+","+integer;
            }
        }
        return "[ id:" + getCloudletId() + " done:" + isFinished() +" slackTime:"+slackTime+" runtime:"+getCloudletLength() +" parents:{" + parents + "} ]";
    }

    
}
