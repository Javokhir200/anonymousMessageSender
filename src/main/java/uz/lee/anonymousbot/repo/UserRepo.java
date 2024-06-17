package uz.lee.anonymousbot.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.lee.anonymousbot.entity.User;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    Optional<User> findByChatId(Long chatId);
    Optional<User> findByUsername(String username);
}
