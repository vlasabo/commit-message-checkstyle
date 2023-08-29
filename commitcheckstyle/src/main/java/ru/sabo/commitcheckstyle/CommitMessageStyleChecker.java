package ru.sabo.commitcheckstyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by sabo on 28.08.2023
 * description:
 */

public class CommitMessageStyleChecker {
    private final String commitMessage;
    private final String branchName;
    private final List<String> commitErrors = new ArrayList<>();

    public CommitMessageStyleChecker(String commitMessage, String branchName) {
        this.commitMessage = commitMessage;
        this.branchName = branchName;
    }

    public void check() {
        if (commitMessage == null || commitMessage.isEmpty()) {
            commitErrors.add("������ ��������� �������!");
            return;
        }

        if (!commitMessage.startsWith("fixes #") && !commitMessage.startsWith("refs #")) {
            commitErrors.add("��������� �� �������� ������� �������!");
            return;
        }

        int positionNumberStart = commitMessage.indexOf(" #") + 2;
        int positionNumberEnd = positionNumberStart + 6;
        if (positionNumberEnd > commitMessage.length()) {
            commitErrors.add("��������� �� �������� ����� ������, ���� �� ������ �����������!");
            return;
        }

        String issueNumberString = commitMessage.substring(positionNumberStart, positionNumberEnd);
        boolean isNumeric = issueNumberString.chars().allMatch(Character::isDigit);
        if (!isNumeric) {
            commitErrors.add("��������� �� �������� ����� ������, ���� �� ������ �����������!");
        } else {
            if (!branchName.contains("_") || branchName.indexOf("_") + 1 > branchName.length() - 1) {
                commitErrors.add("������������ ��� �����!");
                return;
            }
            String issueNumberFromBranch = branchName.substring(branchName.indexOf("_") + 1);
            if (!Objects.equals(issueNumberString, issueNumberFromBranch)) {
                commitErrors.add("����� ������ �� ������� �� ��������� � ������� ������!");
            }
        }

        //todo �������� ��� ���������� ������������ ������ �� ��������

        if (commitMessage.substring(positionNumberEnd).isBlank() || positionNumberEnd + 2 > commitMessage.length()) {
            commitErrors.add("������ ���� �������!");
            return;
        }

        if (!" ".equals(commitMessage.substring(positionNumberEnd, positionNumberEnd + 1))) {
            commitErrors.add("����� ������ ������ ������ ���� ������!");
        }

        if (!commitMessage
                .substring(positionNumberEnd + 1)
                .trim()
                .substring(0, 1)
                .matches("[a-zA-Z�-��-�]")) {
            commitErrors.add("��������� ������� ������ ���������� � �����!");
        }
    }

    public List<String> getCommitErrors() {
        return commitErrors;
    }
}
