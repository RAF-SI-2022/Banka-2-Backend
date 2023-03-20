package com.raf.si.Banka2Backend.repositories.users;

import com.raf.si.Banka2Backend.models.users.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  Optional<PasswordResetToken> findPasswordResetTokenByToken(String token);

  @Modifying
  @Transactional
  @Query("delete from PasswordResetToken prt where prt.token=:token")
  void deleteByToken(String token);
}
