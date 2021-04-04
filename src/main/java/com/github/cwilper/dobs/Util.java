package com.github.cwilper.dobs;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import org.jetbrains.annotations.NotNull;

final class Util {
    private Util() {}

    public static boolean isObservableSelected(@NotNull AnActionEvent event) {
        XValueNodeImpl node = XDebuggerTreeActionBase.getSelectedNode(event.getDataContext());
        return node.getRawValue().startsWith("Observable ");
    }

    public static String getObservedExpression(String observableExpression) {
        return observableExpression + ".subscribe(v=>o=v);o";
    }
}
