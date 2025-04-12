# Базовый образ Keycloak
FROM quay.io/keycloak/keycloak:26.1.3

# Установка переменных окружения
ENV KC_HTTP_PORT=8082 \
    KC_BOOTSTRAP_ADMIN_USERNAME=admin \
    KC_BOOTSTRAP_ADMIN_PASSWORD=admin

# Порт, который будет использоваться внутри контейнера
EXPOSE 8082

# Команда для запуска Keycloak в режиме разработки
CMD ["start-dev"]