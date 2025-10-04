package br.com.postech.auth.domain.ports.out;

import br.com.postech.auth.adapter.out.persistence.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoryPortOut extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByLogin(String login);
}
