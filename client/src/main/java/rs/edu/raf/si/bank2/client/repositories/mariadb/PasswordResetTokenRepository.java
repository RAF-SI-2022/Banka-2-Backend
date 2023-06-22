package rs.edu.raf.si.bank2.client.repositories.mariadb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.si.bank2.client.models.mariadb.PasswordResetToken;

import java.util.Optional;

// TODO nedostaje @Repository?
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findPasswordResetTokenByToken(String token);

    @Modifying
    @Transactional
    @Query("delete from PasswordResetToken prt where prt.token=:token")
    void deleteByToken(String token);
}
