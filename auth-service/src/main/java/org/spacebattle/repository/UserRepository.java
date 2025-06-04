package org.spacebattle.repository;

import org.spacebattle.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий Spring Data JPA для сущности User.
 * Обеспечивает доступ к данным пользователей и позволяет выполнять стандартные CRUD-операции.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Поиск пользователя по имени.
     *
     * @param name имя пользователя
     * @return Optional с найденным пользователем (или пустой, если не найден)
     */
    Optional<User> findByName(String name);
}
