package com.rede.terminal_api.application.workflow;

public enum WorkflowResult {
    CONTINUE,
    STOP;

    public boolean shouldContinue() {
        return this == CONTINUE;
    }
}