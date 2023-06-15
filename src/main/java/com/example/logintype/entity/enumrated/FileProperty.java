package com.example.logintype.entity.enumrated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum FileProperty {

    BOOK_IMAGE("book" + File.separator, Set.of("png", "jpg"));

    private final String rootDir;
    private final Set<String> extensions;
}
