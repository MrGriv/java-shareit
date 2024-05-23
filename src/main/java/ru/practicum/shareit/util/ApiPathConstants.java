package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiPathConstants {
    public static final String BY_ID_PATH = "{id}";
    public static final String SEARCH_ITEMS_PATH = "search";
    public static final String OWNER_PATH = "owner";
    public static final String ADD_COMMENT_PATH = BY_ID_PATH + "/comment";
}
