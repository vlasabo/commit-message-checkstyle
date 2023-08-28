package ru.sabo.commitcheckstyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabo on 28.08.2023
 * description:
 */

public class CommitMessageStyleChecker {
    private final String commitMessage;
    private final List<String> commitErrors = new ArrayList<>();

    public CommitMessageStyleChecker(String commitMessage) {
        this.commitMessage = commitMessage;
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

        String issueNumber = commitMessage.substring(positionNumberStart, positionNumberEnd);
        boolean isNumeric = issueNumber.chars().allMatch(Character::isDigit);
        if (!isNumeric) {
            commitErrors.add("��������� �� �������� ����� ������, ���� �� ������ �����������!");
        }

        //todo �������� ��� ���������� ������������ ������ �� ��������

        if (commitMessage.substring(positionNumberEnd).isBlank() || positionNumberEnd + 1 > commitMessage.length()) {
            commitErrors.add("������ ���� �������!");
            return;
        }

        if (!" ".equals(commitMessage.substring(positionNumberEnd, positionNumberEnd + 1))) {
            commitErrors.add("����� ������ ������ ������ ���� ������!");
        }

        //todo ��������� ��� ������ ����� ������ ���������� � �����
    }

    public List<String> getCommitErrors() {
        return commitErrors;
    }
}
