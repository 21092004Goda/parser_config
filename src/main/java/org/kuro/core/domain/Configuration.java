package org.kuro.core.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class Configuration {
    int id;  // Если возможно большое количество конфигураций, то лучше использовать long в зависимости от ситуации.
    ModeType mode;
    List<String> path;
    ActionType action;

    public Configuration(int id, ModeType mode, List<String> path, ActionType action) {
        this.id = id;
        this.mode = mode;
        this.path = path;
        this.action = action;
    }
}
