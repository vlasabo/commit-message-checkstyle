package ru.sabo.commitcheckstyle;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.util.PairConsumer;
import com.intellij.util.ui.UIUtil;
import git4idea.GitLocalBranch;
import git4idea.branch.GitBranchUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by sabo on 28.08.2023
 * description:
 */
public class BeforeCommitPlugin extends CheckinHandlerFactory implements GitRepositoryChangeListener {

    private CheckinProjectPanel projectPanel;

    @Override
    public @NotNull CheckinHandler createHandler(@NotNull CheckinProjectPanel panel,
                                                 @NotNull CommitContext commitContext) {
        return new CheckinHandler() {

            @Override
            public @Nullable RefreshableOnComponent getBeforeCheckinConfigurationPanel() {
                return super.getBeforeCheckinConfigurationPanel();
            }

            @Override
            public @Nullable UnnamedConfigurable getBeforeCheckinSettings() {
                return super.getBeforeCheckinSettings();
            }

            @Override
            public @Nullable RefreshableOnComponent getAfterCheckinConfigurationPanel(Disposable parentDisposable) {
                return super.getAfterCheckinConfigurationPanel(parentDisposable);
            }

            @Override
            public ReturnResult beforeCheckin(@Nullable CommitExecutor executor, PairConsumer<Object, Object> additionalDataConsumer) {
                return super.beforeCheckin(executor, additionalDataConsumer);
            }

            @Override
            public ReturnResult beforeCheckin() {
                projectPanel = panel;
                String branchName = extractBranchName(panel.getProject());
                CommitMessageStyleChecker checker = new CommitMessageStyleChecker(panel.getCommitMessage(), branchName);
                checker.check();
                if (!checker.getCommitErrors().isEmpty()) {
                    return showYesNoCancel(String.join("\n", checker.getCommitErrors()));
                } else {
                    return ReturnResult.COMMIT;
                }
            }

            @Override
            public void checkinSuccessful() {
                super.checkinSuccessful();
            }

            @Override
            public void checkinFailed(List<VcsException> exception) {
                super.checkinFailed(exception);
            }

            @Override
            public void includedChangesChanged() {
                super.includedChangesChanged();
            }

            @Override
            public boolean acceptExecutor(CommitExecutor executor) {
                return super.acceptExecutor(executor);
            }

            private ReturnResult showYesNoCancel(String resultStr) {
                final var answer = Messages.showYesNoCancelDialog(panel.getProject(),
                        resultStr,
                        "Bad Commit Message!",
                        "Cancel Commit",
                        "Continue",
                        "Update Message",
                        UIUtil.getWarningIcon());

                if (answer == Messages.YES) {
                    return ReturnResult.CLOSE_WINDOW;
                } else if (answer == Messages.CANCEL) {
                    return ReturnResult.CANCEL;
                } else {
                    return ReturnResult.COMMIT;
                }
            }
        };
    }


    @Override
    public void repositoryChanged(@NotNull GitRepository repository) {
        updateCommitMessage(repository.getCurrentBranchName());
    }

    private void updateCommitMessage(String branchName) {
        ApplicationManager.getApplication().invokeLater(() -> projectPanel.setCommitMessage("refs #" + branchName));
    }

    private String extractBranchName(Project project) {

        String branch = "";
        ProjectLevelVcsManager instance = ProjectLevelVcsManagerImpl.getInstance(project);
        if (instance.checkVcsIsActive("Git")) {
            GitRepository currentRepository = GitBranchUtil.getCurrentRepository(project);
            if (currentRepository != null) {
                GitLocalBranch currentBranch = currentRepository.getCurrentBranch();

                if (currentBranch != null) {
                    branch = currentBranch.getName().trim();
                }
            }
        }

        return branch;
    }


}
