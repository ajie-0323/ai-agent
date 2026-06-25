package com.donggua.aiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Author: ajie
 * Date: 2026-06-18 10:57
 * Description: <描述>
 */
class TerminalOperationToolTest {

    @Test
    void executeTerminalCommand() {
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();

        String content = "dir";
        String command = terminalOperationTool.executeTerminalCommand(content);
        System.out.println(command);
    }
}