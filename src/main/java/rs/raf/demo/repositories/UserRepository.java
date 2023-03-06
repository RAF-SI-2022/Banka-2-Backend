package rs.raf.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.raf.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByMail(String mail);

}
