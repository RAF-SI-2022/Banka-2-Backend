package com.raf.si.Banka2Backend.repositories;

import com.raf.si.Banka2Backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
