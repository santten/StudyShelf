package domain.model;

public enum PermissionType {
    // The following permissions are about resources
    CREATE_RESOURCE, // Create a new resource by a student, a teacher or an admin
    UPDATE_OWN_RESOURCE, // Update a resource created by a student, a teacher or an admin themselves
    READ_RESOURCES, // Read all resources by a student, a teacher or an admin
    DELETE_OWN_RESOURCE, // Delete a resource created by a student, a teacher or an admin themselves
    DELETE_COURSE_RESOURCE, // Delete a resource by a teacher for a course she/he teaches
    DELETE_ANY_RESOURCE, // Delete any resource by an admin

    // The following permissions are about tags
    CREATE_TAG, // Create a new tag by a student, a teacher or an admin
    UPDATE_OWN_TAG, // Update a tag created by a student, a teacher or an admin themselves
    UPDATE_COURSE_TAG, // Update a tag by a teacher for a course she/he teaches
    UPDATE_ANY_TAG, // Update any tag by an admin
    READ_TAGS, // Read all tags by a student, a teacher or an admin
    DELETE_COURSE_TAG, // Delete a tag by a teacher for a course she/he teaches
    DELETE_ANY_TAG, // Delete any tag created by an admin

    // The following permissions are about categories
    CREATE_CATEGORY, // Create a new category by a teacher or an admin
    UPDATE_COURSE_CATEGORY, // Update a category by a teacher for a course she/he teaches
    UPDATE_ANY_CATEGORY, // Update a category by an admin
    READ_CATEGORIES, // Read all categories by a student, a teacher, or an admin
    DELETE_COURSE_CATEGORY, // Delete a category by a teacher for a course she/he teaches
    DELETE_ANY_CATEGORY, // Delete a category by an admin

    // The following permissions are about reviews
    CREATE_REVIEW, // Create a new review by a student, a teacher or an admin
    UPDATE_OWN_REVIEW, // Update a review created by a student, a teacher or an admin themselves
    READ_REVIEWS, // Read all reviews by a student, a teacher or an admin
    DELETE_OWN_REVIEW, // Delete a review created by a student, a teacher, or an admin themselves
    DELETE_ANY_REVIEW, // Delete any review by an admin

    // The following permissions are about ratings
    CREATE_RATING, // Create a new rating by a student, a teacher or an admin
    UPDATE_OWN_RATING, // Update a rating created by a student, a teacher or an admin themselves
    READ_RATINGS, // Read all ratings by a student, a teacher or an admin
    DELETE_OWN_RATING,  // Delete a rating created by a student, a teacher, or an admin themselves
    DELETE_ANY_RATING, // Delete any rating by an admin

    // The following permissions are about users
    CREATE_USER, // Create a new user by a student, a teacher or an admin
    UPDATE_OWN_USER, // Update a user created by a student, a teacher or an admin themselves
    READ_OWN_USER, // Read a user created by a student, a teacher or an admin themselves
    READ_ALL_USERS, // Read all users by an admin
    DELETE_OWN_USER, // Delete a user created by a student, a teacher, or an admin themselves
    DELETE_ANY_USER, // Delete any user by an admin
}
