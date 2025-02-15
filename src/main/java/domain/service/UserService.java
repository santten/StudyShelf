//package service;
//
//import domain.model.Role;
//import domain.model.RoleType;
//import domain.model.User;
//import infrastructure.repository.RoleRepository;
//import infrastructure.repository.UserRepository;
//import java.util.Optional;
//
//public class UserService {
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//
//    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
//        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
//    }
//
//    public User registerUser(String firstName, String lastName, String email, String password, RoleType roleType) {
//        Optional<Role> existingRole = roleRepository.findByName(roleType);
//        Role role = existingRole.orElseGet(() -> roleRepository.save(new Role(roleType)));
//
//        User user = new User(firstName, lastName, email, password, role);
//        return userRepository.save(user);
//    }
//}
