package utils;

import java.util.ArrayList;

public class TaskPool {
    private static TaskPool taskPool;
    private ArrayList<Task> tasks=new ArrayList<>();

    public synchronized static TaskPool getTaskPool() {
        if (taskPool == null) {
            taskPool = new TaskPool();
        }
        return taskPool;
    }

    private TaskPool() {
    }

    public void addTask(Task task){
        tasks.add(task);
    }

    public Task getTaskByID(int taskID){
        for (Task task : tasks) {
            if (task.getCloudletId()==taskID) {
                return task;
            }
        }
        return null;
    }

    public int getSize(){
        return tasks.size();
    }

    public boolean allParentsDone(int taskID){
        for (int parentID : getTaskByID(taskID).getParents()) {
            if (!getTaskByID(parentID).isFinished()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String s="";
        for (Task task : tasks) {
            s=s.concat(task+"\n");
        }
        return s;
    }
}
