package SSTF;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

import utils.Calculator;
import utils.Constants;
import utils.Task;
import utils.TaskPool;
import utils.VM;
import utils.VMPool;

public class SSTFBroker extends DatacenterBroker {

	private int datacenterId = 2;
	private Calculator calculator;
	private List<Task> readyCloudlet;

	public SSTFBroker(String name,Calculator calculator) throws Exception {
		super(name);
		this.calculator=calculator;
	}

	@Override
	public void startEntity() {
		int vmEachTypeCount = calculator.calculateVMsCount();
		calculator.caclCP(TaskPool.getTaskPool().getTaskByID(calculator.getLastTaskID()), 0);
		System.out.println("CRITICALPATH: "+calculator.getCriticalPathLenght());
		calculator.calcPCP(TaskPool.getTaskPool().getTaskByID(calculator.getLastTaskID()));
		readyCloudlet = new ArrayList<>();
		for (int i = 0; i < TaskPool.getTaskPool().getSize(); i++) {
			TaskPool.getTaskPool().getTaskByID(i).setUserId(getId());
		}
		updateReadyCloudletList();
		sendNow(getId(), Constants.SCHDULE);
		int mips;
		long size = 10000;
		int ram = 512;
		long bw = 1000;
		int pesNumber = 1;
		String vmm = "Xen";
		for (int i = 0; i < vmEachTypeCount * 3; i++) {
			int newVmId = i;
			if (i < vmEachTypeCount) {
				mips = 2000;
			} else if (i >= vmEachTypeCount && i < vmEachTypeCount * 2) {
				mips = 1500;
			} else if (i >= vmEachTypeCount * 2 && i < vmEachTypeCount * 3) {
				mips = 1000;
			} else {
				mips = 500;
			}
			VM vm = new VM(newVmId, getId(), mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
			VMPool.getVMPool().addVm(vm);
			sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
		}
	}

	@Override
	public void processEvent(SimEvent ev) {
		switch (ev.getTag()) {

			// VM Creation answer
			case CloudSimTags.VM_CREATE_ACK:
				processVmCreate(ev);
				break;
			// A finished cloudlet returned
			case CloudSimTags.CLOUDLET_RETURN:
				processCloudletReturn(ev);
				break;
			// if the simulation finishes
			case CloudSimTags.END_OF_SIMULATION:
				shutdownEntity();
				break;
			case Constants.SCHDULE:
				schdule();
				break;
			case Constants.ASSIGN_CLOUDLET_TO_VM:
				assignCloudletsToVms();
				break;
			// other unknown tags are processed by this method
			default:
				processOtherEvent(ev);
				break;
		}
	}

	@Override
	protected void processCloudletReturn(SimEvent ev) {
		Task cloudlet = (Task) ev.getData();
		Log.printConcatLine(CloudSim.clock(), ": ", getName(),
				": Cloudlet ", cloudlet.getCloudletId(), " is received from " + cloudlet.getVmId());
		VMPool.getVMPool().getVmById(cloudlet.getVmId()).setFree(true);
		cloudlet.setRunning(false);
		updateReadyCloudletList();
		sendNow(getId(), Constants.ASSIGN_CLOUDLET_TO_VM);
	}

	protected void processVmCreate(SimEvent ev) {
		int[] data = (int[]) ev.getData();
		int datacenterId = data[0];
		int vmId = data[1];
		int result = data[2];

		if (result == CloudSimTags.TRUE) {
			Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": VM #", vmId,
					" has been created in Datacenter #",
					datacenterId);
			VMPool.getVMPool().getVmById(vmId).setFree(true);
			sendNow(getId(), Constants.ASSIGN_CLOUDLET_TO_VM);
		} else {
			Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Creation of VM #", vmId,
					" failed in Datacenter #", datacenterId);
		}
	}

	@Override
	public void shutdownEntity() {
		// TODO Auto-generated method stub
		super.shutdownEntity();
	}

	private void updateReadyCloudletList() {
		for (int i = 0; i < TaskPool.getTaskPool().getSize(); i++) {
			Task task = TaskPool.getTaskPool().getTaskByID(i);
			if (!task.isFinished() && getReadyCloudletById(task.getCloudletId()) == null
					&& TaskPool.getTaskPool().allParentsDone(task.getCloudletId()) && !task.isRunning()) {
				readyCloudlet.add(task);
			}
		}
	}

	private void schdule() {
		if(readyCloudlet.size()!=0){
			readyCloudlet.sort(new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    return (int) (o1.getSlackTime() - o2.getSlackTime());
                }

            });
			for (int i = 0; i < TaskPool.getTaskPool().getSize(); i++) {
				TaskPool.getTaskPool().getTaskByID(i).setDeadline(-1);;
			}
			this.calculator.setCriticalPathLenght(this.calculator.getCriticalPathLenght()-60);
			this.calculator.calcPCP(TaskPool.getTaskPool().getTaskByID(calculator.getLastTaskID()));
			send(getId(), 120.0, 100);
		}
	}

	private void assignCloudletsToVms() {
		while (readyCloudlet.size() != 0 && VMPool.getVMPool().getFastestFreeVm() != null) {
			Task nextCloudlet = readyCloudlet.remove(0);
			nextCloudlet.setVmId(VMPool.getVMPool().getFastestFreeVm().getId());
			VMPool.getVMPool().getVmById(nextCloudlet.getVmId()).setFree(false);
			nextCloudlet.setRunning(true);
			sendNow(datacenterId, CloudSimTags.CLOUDLET_SUBMIT, nextCloudlet);
		}
	}

	private Cloudlet getReadyCloudletById(int id){
		for (Task cloudlet : readyCloudlet) {
			if (cloudlet.getCloudletId()==id) {
				return cloudlet;
			}
		}
		return null;
	}

}
