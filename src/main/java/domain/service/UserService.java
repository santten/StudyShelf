package domain.service;

import domain.model.Role;
import domain.model.RoleType;
import domain.model.User;
import infrastructure.repository.RoleRepository;
import infrastructure.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    public User registerUser(String firstName, String lastName, String email, String password, RoleType roleType) {
        Role role = roleRepository.findByName(roleType);

        if (role == null) {
            role = new Role(roleType);
            role = roleRepository.save(role);
        }

        User user = new User(firstName, lastName, email, password, role);
        return userRepository.save(user);
    }
}
