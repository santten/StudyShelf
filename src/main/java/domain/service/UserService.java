package domain.service;

import domain.model.Role;
import domain.model.RoleType;
import domain.model.User;
import infrastructure.repository.RoleRepository;
import infrastructure.repository.UserRepository;
import presentation.view.CurrentUserManager;

import java.util.List;

/**
 * Service responsible for managing user-related actions such as registration,
 * authentication, profile updates, and account deletion.
 */
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordService passwordService;
    private final JWTService jwtService;

    /**
     * Constructs a UserService with the required dependencies.
     */
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordService passwordService, JWTService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user with the provided information.
     *
     * @param firstName user's first name
     * @param lastName user's last name
     * @param email user's email
     * @param password raw password to hash
     * @param roleType role type (e.g., ADMIN, STUDENT)
     * @return the created User
     */
    public User registerUser(String firstName, String lastName, String email,   String password, RoleType roleType) {
        Role role = roleRepository.findByName(roleType);

        if (role == null) {
            role = new Role(roleType);
            role = roleRepository.save(role);
        }

        String hashedPassword = passwordService.hashPassword(password);
        User user = new User(firstName, lastName, email, hashedPassword, role);
        return userRepository.save(user);
    }

    /**
     * Authenticates a user using email and password.
     *
     * @param email the user's email
     * @param password the raw password
     * @return JWT token if valid, null otherwise
     */
    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordService.checkPassword(password, user.getPassword())) {
            return jwtService.createToken(email);
        }
        return null;
    }

    /**
     * Finds a user by email.
     *
     * @param email Email address
     * @return The user, or null if not found
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Validates a given JWT token.
     *
     * @param token JWT token
     * @return true if token is valid; false otherwise
     */
    public boolean isTokenValid(String token) {
        return jwtService.getEmailFromToken(token) != null;
    }

    /**
     * Retrieves all users from the repository.
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Updates user details (first name, last name, email).
     *
     * @param user      The user to update
     * @param firstName New first name
     * @param lastName  New last name
     * @param email     New email
     * @return Updated user object
     * @throws IllegalArgumentException if email is already taken
     */
    public User updateUser(User user, String firstName, String lastName, String email) {
        if (email != null && !email.equals(user.getEmail()) && userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already taken!");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        return userRepository.update(user);
    }

    /**
     * Deletes a user.
     *
     * @param user The user to delete
     */
    public void deleteUser(User user) {
        userRepository.delete(user);
    }


    /**
     * Deletes the currently logged-in user if permitted.
     *
     * @param user The user requesting deletion
     * @throws IllegalArgumentException if attempting to delete another user without admin rights
     */
    public void deleteOwnUser(User user){
        User curUser = CurrentUserManager.get();
        if (user.getUserId() != curUser.getUserId() && !curUser.isAdmin()) {
            throw new IllegalArgumentException("You can't delete someone else's user unless you're an admin.");
        }

        userRepository.deleteUser(curUser);
    }

    /**
     * Updates the first name of the user.
     *
     * @param user      The user whose name is being updated
     * @param firstName New first name
     * @throws IllegalArgumentException if not updating own profile or lacking admin rights
     */
    public void updateUserFirstName(User user, String firstName) {
        User curUser = CurrentUserManager.get();
        if (user.getUserId() != curUser.getUserId() && !curUser.isAdmin()) {
            throw new IllegalArgumentException("You can't change someone else's first name unless you're an admin.");
        }

        userRepository.updateUserFirstName(user.getUserId(), firstName);
        CurrentUserManager.get().setFirstName(firstName);
    }

    /**
     * Updates the last name of the user.
     *
     * @param user     The user whose name is being updated
     * @param lastName New last name
     * @throws IllegalArgumentException if not updating own profile or lacking admin rights
     */
    public void updateUserLastName(User user, String lastName) {
        User curUser = CurrentUserManager.get();
        if (user.getUserId() != curUser.getUserId() && !curUser.isAdmin()) {
            throw new IllegalArgumentException("You can't change someone else's last name unless you're an admin.");
        }

        userRepository.updateUserLastName(user.getUserId(), lastName);
        CurrentUserManager.get().setLastName(lastName);
    }

    /**
     * Updates the email of the user.
     *
     * @param user  The user whose email is being updated
     * @param email New email address
     * @throws IllegalArgumentException if not updating own profile or lacking admin rights
     */
    public void updateUserEmail(User user, String email) {
        User curUser = CurrentUserManager.get();
        if ((user.getUserId() != curUser.getUserId()) && !curUser.isAdmin()) {
            throw new IllegalArgumentException("You can't change someone else's email unless you're an admin.");
        }

        userRepository.updateUserEmail(user.getUserId(), email);
        CurrentUserManager.get().setEmail(email);
    }

    /**
     * Updates the user's password after verifying the old password.
     *
     * @param user        The user whose password is being changed
     * @param oldPassword Old password
     * @param newPassword New password
     * @return true if update succeeded; false otherwise
     * @throws IllegalArgumentException if trying to update someone else's password
     */
    public boolean updateUserPassword(User user, String oldPassword, String newPassword) {
        if (user.getUserId() != CurrentUserManager.get().getUserId()) {
            throw new IllegalArgumentException("You can only change your own password.");
        }

        return userRepository.changePassword(user.getUserId(), oldPassword, newPassword);
    }

    /**
     * Verifies a password against a hashed version.
     *
     * @param rawPassword    Raw text password
     * @param hashedPassword Hashed password
     * @return true if matches; false otherwise
     */
    public boolean checkPassword(String rawPassword, String hashedPassword) {
        return passwordService.checkPassword(rawPassword, hashedPassword);
    }
}