package com.github.cwilper.dobs;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.impl.actions.handlers.XDebuggerEvaluateActionHandler;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import org.jetbrains.annotations.NotNull;

/**
 * A functional duplicate of {@code com.intellij.xdebugger.impl.actions.EvaluateAction} that uses a
 * modified copy of {@link XDebuggerEvaluateActionHandler} to launch an evaluate dialog based on the
 * selected expression. It differs in that it only shows and enables if an Observable expression is selected,
 * and when performed, launches the dialog pre-filled with an expression that subscribes and returns the first value.
 */
public class DobsEvalExpressionAction extends AnAction {
    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(Util.isObservableSelected(event));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        XValueNodeImpl node = XDebuggerTreeActionBase.getSelectedNode(event.getDataContext());
        node.getValueContainer().calculateEvaluationExpression()
                .onSuccess(expression -> {
                    if (expression != null) {
                        ModifiedExpressionDebuggerEvaluateActionHandler handler =
                                new ModifiedExpressionDebuggerEvaluateActionHandler(
                                        Util.getObservedExpression(expression.getExpression()));
                        handler.perform(event.getProject(), event);
                    }
                });
    }
}
