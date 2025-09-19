package vn.anhtuan.demoAPI.Service;


import org.springframework.stereotype.Service;
import vn.anhtuan.demoAPI.Entity.User;
import vn.anhtuan.demoAPI.Repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByEmailIgnoreCase(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        Optional<User> existingUser = userRepository.findByEmailIgnoreCase(user.getEmail());
        if (existingUser.isPresent()) {
            // Nếu email đã tồn tại, bạn có thể ném exception hoặc xử lý khác
            throw new IllegalArgumentException("Email đã tồn tại: " + user.getEmail());
        }
        return userRepository.save(user);
    }

}