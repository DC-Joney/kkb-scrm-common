package com.kkb.common.tools.assigner;

import com.kkb.plugins.uid.worker.WorkerNode;

public class WorkIdAssignException extends RuntimeException {

    private final WorkerNode workerNode;

    public WorkIdAssignException(WorkerNode workerNode, String message) {
        super(message);
        this.workerNode = workerNode;
    }

    public WorkIdAssignException(WorkerNode workerNode, String message, Throwable cause) {
        super(message, cause);
        this.workerNode = workerNode;
    }

    @Override
    public String toString() {
        return "{ " + super.toString() +
                ", workNode: " + workerNode + " }";
    }
}
