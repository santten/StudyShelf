package domain.model;

public enum PermissionType {
    CREATE_TAGS,
    UPDATE_TAGS,
    DELETE_TAGS,

    CREATE_CATEGORY,
    UPDATE_CATEGORY,
    DELETE_CATEGORY,

    CREATE_RESOURCE,
    UPDATE_OWN_RESOURCE,
    DELETE_OWN_RESOURCE,
    DELETE_ANY_RESOURCE,

    READ_RESOURCES,
    READ_OWN_RESOURCE,
    READ_USERS
}
