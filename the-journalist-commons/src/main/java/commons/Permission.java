package commons;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_WRITE("admin:write"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin:delete"),
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),
    AUTHOR_READ("author:read"),
    AUTHOR_WRITE("author:write"),
    AUTHOR_UPDATE("author:update"),
    AUTHOR_DELETE("author:delete");

    @Getter
    private final String permission;

}
