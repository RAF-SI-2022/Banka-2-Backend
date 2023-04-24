package com.raf.si.Banka2Backend.repositories.mariadb;

import com.raf.si.Banka2Backend.models.mariadb.UserOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOptionRepository extends JpaRepository<UserOption, Long> {

    List<UserOption> getUserOptionsByUserId(Long userId);
}
