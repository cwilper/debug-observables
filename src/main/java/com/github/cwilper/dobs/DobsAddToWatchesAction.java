package com.github.cwilper.dobs;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.frame.XWatchesView;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.intellij.xdebugger.impl.ui.XDebugSessionTab;
import com.intellij.xdebugger.impl.ui.tree.actions.XAddToWatchesTreeAction;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import org.jetbrains.annotations.NotNull;

/**
 *
 * A modified copy of {@link XAddToWatchesTreeAction}that only shows and enables if an Observable expression is
 * selected, and when performed, adds a watch expression that subscribes and returns the first value.
 */
public class DobsAddToWatchesAction extends XDebuggerTreeActionBase {
    @Override
    protected boolean isEnabled(@NotNull final XValueNodeImpl node, @NotNull AnActionEvent event) {
        return Util.isObservableSelected(event)
                && super.isEnabled(node, event)
                && DebuggerUIUtil.hasEvaluationExpression(node.getValueContainer()) && getWatchesView(event) != null;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(Util.isObservableSelected(event));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        for (XValueNodeImpl node : getSelectedNodes(event.getDataContext())) {
            if (node != null) {
                String nodeName = node.getName();
                if (nodeName != null) {
                    perform(node, nodeName, event);
                }
            }
        }
    }

    @Override
    protected void perform(final XValueNodeImpl node, @NotNull final String nodeName, final AnActionEvent e) {
        final XWatchesView watchesView = getWatchesView(e);
        if (watchesView != null) {
            addToWatches(watchesView, node);
        }
    }

    private static void addToWatches(@NotNull XWatchesView watchesView, @NotNull XValueNodeImpl node) {
        node.getValueContainer().calculateEvaluationExpression().onSuccess(expression -> {
            if (expression != null) {
                XExpression modifiedExpression = new XExpressionImpl(Util.getObservedExpression(
                        expression.getExpression()), expression.getLanguage(), expression.getCustomInfo(),
                        expression.getMode());
                DebuggerUIUtil.invokeLater(() -> watchesView.addWatchExpression(modifiedExpression, -1, false));
            }
        });
    }

    private static XWatchesView getWatchesView(@NotNull AnActionEvent event) {
        XWatchesView view = event.getData(XWatchesView.DATA_KEY);
        Project project = event.getProject();
        if (view == null && project != null) {
            XDebugSession session = XDebuggerManager.getInstance(project).getCurrentSession();
            if (session != null) {
                XDebugSessionTab tab = ((XDebugSessionImpl)session).getSessionTab();
                if (tab != null) {
                    return tab.getWatchesView();
                }
            }
        }
        return view;
    }
}
