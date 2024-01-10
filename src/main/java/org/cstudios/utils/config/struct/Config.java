package org.cstudios.utils.config.struct;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Config {


    CONFIG("config"),
    LANGUAGE("messages");

    @Getter private final String name;

}
