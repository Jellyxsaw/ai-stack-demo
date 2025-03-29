package com.example.demo.repository;

import com.example.demo.model.UserConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserConfigurationRepository extends JpaRepository<UserConfiguration, Long> {
    List<UserConfiguration> findByUsername(String username);
    UserConfiguration findByUsernameAndConfigName(String username, String configName);
    boolean existsByUsernameAndConfigName(String username, String configName);
}
