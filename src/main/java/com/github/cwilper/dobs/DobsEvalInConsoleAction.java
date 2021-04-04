package com.github.cwilper.dobs;

import com.intellij.execution.console.ConsoleExecuteAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.xdebugger.impl.actions.handlers.XEvaluateInConsoleFromEditorActionHandler;
import com.intellij.xdebugger.impl.ui.tree.actions.XAddToWatchesTreeAction;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A modified copy of {@code com.intellij.xdebugger.impl.actions.EvaluateInConsoleAction}
 * that only shows and enables if an Observable expression is selected, and when performed,
 * executes an expression that subscribes and returns the first value.
 */
public class DobsEvalInConsoleAction extends XAddToWatchesTreeAction {
    @Override
    protected boolean isEnabled(@NotNull XValueNodeImpl node, @NotNull AnActionEvent event) {
        return Util.isObservableSelected(event)
                && super.isEnabled(node, event) && getConsoleExecuteAction(event) != null;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        if (Util.isObservableSelected(event) && getConsoleExecuteAction(event) != null) {
            event.getPresentation().setVisible(true);
            super.update(event);
        }
        else {
            event.getPresentation().setEnabledAndVisible(false);
        }
    }

    @Nullable
    private static ConsoleExecuteAction getConsoleExecuteAction(@NotNull AnActionEvent event) {
        return XEvaluateInConsoleFromEditorActionHandler.getConsoleExecuteAction(
                event.getData(LangDataKeys.CONSOLE_VIEW));
    }

    @Override
    protected void perform(XValueNodeImpl node, @NotNull String nodeName, AnActionEvent event) {
        final ConsoleExecuteAction action = getConsoleExecuteAction(event);
        if (action != null) {
            node.getValueContainer().calculateEvaluationExpression()
                    .onSuccess(expression -> {
                        if (expression != null) {
                            action.execute(null, Util.getObservedExpression(expression.getExpression()), null);
                        }
                    });
        }
    }
}
