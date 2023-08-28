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

        String issueNumber = commitMessage.substring(positionNumberStart, positionNumberEnd);
        boolean isNumeric = issueNumber.chars().allMatch(Character::isDigit);
        if (!isNumeric) {
            commitErrors.add("Сообщение не содержит номер задачи, либо он указан некорректно!");
        }

        //todo подумать как прикрутить подтягивание задачи из редмайна

        if (commitMessage.substring(positionNumberEnd).isBlank() || positionNumberEnd + 1 > commitMessage.length()) {
            commitErrors.add("Пустое тело коммита!");
            return;
        }

        if (!" ".equals(commitMessage.substring(positionNumberEnd, positionNumberEnd + 1))) {
            commitErrors.add("После номера задачи должен быть пробел!");
        }

        //todo проверить что строка после номера начинается с буквы
    }

    public List<String> getCommitErrors() {
        return commitErrors;
    }
}
