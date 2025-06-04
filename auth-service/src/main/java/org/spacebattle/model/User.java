package org.spacebattle.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * JPA-сущность, представляющая пользователя системы.
 *
 * Таблица в БД: users
 * Используется для аутентификации и авторизации.
 */
@Entity
@Table(name = "users")
@Data
public class User {

    /** Уникальный идентификатор пользователя */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Имя пользователя*/
    private String name;

    /** Пароль пользователя */
    private String password;
}
