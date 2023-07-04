package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

public class Calculator {
    private String workflowType;
    private int Nw;
    private int Dw;
    private int criticalPathLenght = 0;
    private Stack<Task> tmpPath = new Stack<>();
    private ArrayList<Task> tempT = new ArrayList<>();

    public Calculator(String workflowType) throws Exception {
        this.workflowType = workflowType;
        this.calcNwDw(this.workflowType);
    }

    public int getCriticalPathLenght() {
        return this.criticalPathLenght;
    }

    public void setCriticalPathLenght(int criticalPathLenght){
        this.criticalPathLenght=criticalPathLenght;
    }

    private void calcNwDw(String type) throws Exception {    
        switch (type.toLowerCase()) {
            case "cybershake":
                this.Dw = 4;
                this.Nw = 500;
                break;
            case "ligo":
                this.Dw = 6;
                this.Nw = 500;
                break;
            case "montage":
                this.Dw = 10;
                this.Nw = 500;
                break;
            case "sipht":
                this.Dw = 6;
                this.Nw = 484;
                break;
            default:
                throw new Exception("Invalid workflow type for Calculator Class.");
        }
    }

    public int calculateVMsCount() {
        return (int) Math.ceil(this.Nw / (14 * this.Dw));
    }

    /**
     *
     * @param task        last task of workflow for trigering.
     * @param currentPath zero for trigering.
     */
    public void caclCP(Task task, int currentPath) {
        tempT.add(task);
        currentPath += task.getCloudletLength();
        if (task.getParents().size() == 0) {
            if (this.criticalPathLenght < currentPath) {
                this.criticalPathLenght = currentPath;
            }
            return;
        }
        for (int parentID : task.getParents()) {
            Task t=TaskPool.getTaskPool().getTaskByID(parentID);
            caclCP(t, currentPath);
            tempT.remove(t);
        }
    }

    /**
     *
     * @param task last task of workflow for trigering.
     */
    public void calcPCP(Task task) {
        //root - start of scheduling
        if (task.getParents().size() == 0) {
            int assignedDeadlines = 0, notAssignedRuntimes = 0;
            //check if it is finished or not
            for (Task task2 : tmpPath) {
                if (task2.getDeadline() == -1) {
                    notAssignedRuntimes += task2.getCloudletLength();
                } else {
                    assignedDeadlines += task2.getDeadline();
                }
            }
            //divide by zero prevention
            if (notAssignedRuntimes != 0) {
                double coeff = (((criticalPathLenght * 2.5) - assignedDeadlines) / notAssignedRuntimes);
                for (Task task2 : tmpPath) {
                    if (task2.getDeadline() == -1) {
                        task2.setDeadline((int) Math.floor(coeff * task2.getCloudletLength()));
                        task2.setSlackTime(task2.getDeadline() - (int) (task2.getCloudletLength()));
                    }
                }
            }
            return;
        }
        ArrayList<Task> parents=getParents(task.getCloudletId());
        for (Task parent : parents) {
            tmpPath.push(parent);
            calcPCP(parent);
            tmpPath.pop();
        }
    }

    private ArrayList<Task> getParents(int childID){
        ArrayList<Task> parents=new ArrayList<>();
        for (int parentID : TaskPool.getTaskPool().getTaskByID(childID).getParents()) {
            parents.add(TaskPool.getTaskPool().getTaskByID(parentID));
        }
        parents.sort(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return (int) (o1.getCloudletLength() - o2.getCloudletLength());
            }

        });
        Collections.reverse(parents);
        return parents;
    }

    public int getLastTaskID(){
        switch(this.workflowType){
            case "cybershake":
                return 0;
            case "genome":
                return 496;
            case "montage":
                return 499;
            case "sipht":
                return 88;
            default:
                return -1;
        }
    }
}
