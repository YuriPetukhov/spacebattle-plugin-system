package org.spacebattle.controller;

import lombok.RequiredArgsConstructor;
import org.spacebattle.dto.LoginRequest;
import org.spacebattle.dto.RegistrationRequest;
import org.spacebattle.service.ITokenService;
import org.spacebattle.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST-контроллер для авторизации и аутентификации пользователей.
 * <p>Обрабатывает регистрацию, вход и верификацию JWT-токенов.</p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ITokenService tokenService;
    private final IUserService userService;

    /**
     * Обрабатывает регистрацию нового пользователя.
     *
     * @param request объект с именем пользователя и паролем
     * @return 200 OK, если регистрация успешна, или 409 CONFLICT, если пользователь уже существует
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegistrationRequest request) {
        if (userService.exists(request.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        userService.register(request.username(), request.password());
        return ResponseEntity.ok().build();
    }

    /**
     * Обрабатывает вход пользователя и возвращает JWT-токен.
     *
     * @param request объект с логином и паролем
     * @return 200 OK с токеном или 401 UNAUTHORIZED при неуспешной проверке
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        if (userService.validate(request.username(), request.password())) {
            String token = tokenService.generateToken(request.username());
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Проверяет валидность токена из заголовка Authorization.
     *
     * @param authHeader строка заголовка вида "Bearer ..."
     * @return 200 OK, если токен валиден; 401 UNAUTHORIZED — иначе
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        boolean valid = tokenService.isValid(token);
        return valid
                ? ResponseEntity.ok("Token is valid")
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
}
