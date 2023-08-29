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
            commitErrors.add("Пустое сообщение коммита!");
            return;
        }

        if (!commitMessage.startsWith("fixes #") && !commitMessage.startsWith("refs #")) {
            commitErrors.add("Сообщение не содержит статуса коммита!");
            return;
        }

        int positionNumberStart = commitMessage.indexOf(" #") + 2;
        int positionNumberEnd = positionNumberStart + 6;
        if (positionNumberEnd > commitMessage.length()) {
            commitErrors.add("Сообщение не содержит номер задачи, либо он указан некорректно!");
            return;
        }

        String issueNumberString = commitMessage.substring(positionNumberStart, positionNumberEnd);
        boolean isNumeric = issueNumberString.chars().allMatch(Character::isDigit);
        if (!isNumeric) {
            commitErrors.add("Сообщение не содержит номер задачи, либо он указан некорректно!");
        } else {
            if (!branchName.contains("_") || branchName.indexOf("_") + 1 > branchName.length() - 1) {
                commitErrors.add("Некорректное имя ветки!");
                return;
            }
            String issueNumberFromBranch = branchName.substring(branchName.indexOf("_") + 1);
            if (!Objects.equals(issueNumberString, issueNumberFromBranch)) {
                commitErrors.add("Номер задачи из коммита не совпадает с текущей веткой!");
            }
        }

        //todo подумать как прикрутить подтягивание задачи из редмайна

        if (commitMessage.substring(positionNumberEnd).isBlank() || positionNumberEnd + 2 > commitMessage.length()) {
            commitErrors.add("Пустое тело коммита!");
            return;
        }

        if (!" ".equals(commitMessage.substring(positionNumberEnd, positionNumberEnd + 1))) {
            commitErrors.add("После номера задачи должен быть пробел!");
        }

        if (!commitMessage
                .substring(positionNumberEnd + 1)
                .trim()
                .substring(0, 1)
                .matches("[a-zA-Zа-яА-Я]")) {
            commitErrors.add("Сообщение коммита должно начинаться с буквы!");
        }
    }

    public List<String> getCommitErrors() {
        return commitErrors;
    }
}
