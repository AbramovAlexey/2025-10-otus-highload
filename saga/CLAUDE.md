# CLAUDE.md — Saga / Transactional Outbox

---

## 1. Контекст и границы

### Назначение проекта

Домашнее задание курса **OTUS Highload Architect**, тема: надёжная доставка событий
между микросервисами через паттерн **Transactional Outbox + CDC**.

Цель — показать, как атомарно сохранять бизнес-операцию и исходящее событие в одной
БД-транзакции, а затем доставлять это событие потребителю без потерь, с идемпотентностью
и возможностью повтора. В данном случае это счётчик непрочитанных сообщений: каждое
новое сообщение увеличивает счётчик пользователя, прочтение — уменьшает.

### Основные модули и их ответственность

| Модуль | Путь | Ответственность |
|--------|------|-----------------|
| `message-service` | `saga/message-service/` | Хранит сообщения в PostgreSQL. Атомарно пишет сообщение и событие в таблицу Outbox. Экспонирует REST API. |
| `counter-service` | `saga/counter-service/` | Слушает Kafka, обновляет счётчики непрочитанных в Redis. Обеспечивает идемпотентность. |
| Debezium Server | `message-service/debezium-conf/` | CDC-коннектор: читает WAL PostgreSQL, публикует новые строки `outbox_events` в Kafka. |
| docker-compose | `message-service/docker-compose.yml` | Поднимает всю инфраструктуру: PostgreSQL, Zookeeper, Kafka, Debezium, Redis. |
| monitoring | `message-service/monitoring/` | Prometheus + Grafana для метрик message-service (Actuator + Micrometer). |

### Ключевые термины (глоссарий)

| Термин | Определение |
|--------|-------------|
| **Outbox** | Таблица `outbox_events` в PostgreSQL. Событие записывается туда в той же транзакции, что и бизнес-данные — это гарантирует атомарность. |
| **CDC (Change Data Capture)** | Механизм Debezium: отслеживает изменения в WAL PostgreSQL и публикует их в Kafka без опроса БД. |
| **WAL** | Write-Ahead Log PostgreSQL. Debezium требует `wal_level=logical`. |
| **Idempotency key** | Ключ `msg_processed:{outbox_event_id}` в Redis с TTL 24 ч. Защищает от двойной обработки при повторной доставке из Kafka. |
| **DLT (Dead Letter Topic)** | Kafka-топик для сообщений, которые не удалось обработать за 5 попыток. |
| **Safe decrement** | Lua-скрипт в Redis: декремент счётчика не уходит ниже 0. |
| **Outbox event key** | Поле `message_key` (UUID пользователя) — используется как ключ Kafka-сообщения для партиционирования по пользователю. |
| **EventType** | Enum: `CREATED` (новое сообщение) / `READED` (прочтено). Дублируется в обоих сервисах. |

### Архитектурная схема

```
┌─────────────────────────────────────────────────────────────┐
│  message-service (:8080)                                    │
│                                                             │
│  REST API ──► MessageServiceImpl                            │
│                    │                                        │
│                    ├── INSERT INTO messages          ┐      │
│                    └── INSERT INTO outbox_events     ┘ одна │
│                                                   PostgreSQL-транзакция
└─────────────────────────────────────────────────────────────┘
          │
          │  Debezium CDC читает WAL → публикует в Kafka
          ▼
    Kafka topic: my_server.public.outbox_events
          │
          │  @KafkaListener + @RetryableTopic (5 попыток, exponential backoff 2s×2^n)
          ▼
┌─────────────────────────────────────────────────────────────┐
│  counter-service (:8081)                                    │
│                                                             │
│  EventListener ──► CounterServiceImpl ──► Redis             │
│                    idempotency check      unread:{userId}   │
│                                           msg_processed:{}  │
└─────────────────────────────────────────────────────────────┘
```

### Что нельзя менять и почему

| Ограничение | Причина |
|-------------|---------|
| `wal_level=logical` в PostgreSQL | Обязательное условие для Debezium CDC. Без него Debezium не может читать WAL. |
| Запись в `outbox_events` в той же транзакции, что и в `messages` | Суть паттерна Outbox: атомарность бизнес-операции и публикации события. Разрыв транзакции уничтожает гарантию. |
| Kafka manual ack (`ack-mode: manual_immediate`) | Подтверждение только после успешной обработки. Auto-commit скроет ошибки и приведёт к потере событий. |
| `setIfAbsent` для idempotency key | Атомарная проверка+запись. Замена на GET+SET создаёт race condition. |
| Дублирование `EventType` в обоих сервисах | Сервисы независимы и не должны иметь общих зависимостей. При изменении — синхронизировать вручную. |
| `wal_level=logical` требует перезапуска PostgreSQL | Изменение этого параметра в docker-compose требует пересоздания контейнера с данными. |

### Поведение агента: вопросы или допущения?

**Задавай уточняющий вопрос**, если:
- задача затрагивает Outbox-транзакцию или CDC-конфигурацию (высокий риск нарушить гарантии);
- нужно добавить новое хранилище или инфраструктурный компонент;
- требуется изменить схему `outbox_events` (Debezium конфигурирован на конкретную структуру);
- задача описана размыто и возможны принципиально разные реализации.

**Делай допущения** (и явно их фиксируй в ответе), если:
- задача касается только одного сервиса и не затрагивает контракт между ними;
- это добавление нового endpoint, теста или утилитарного метода;
- допущение очевидно из контекста (например, новый EventType добавляется в оба сервиса).

---

## 2. Требования к результату

### Стиль кода

- **Java 21**, **Spring Boot 3.5** — использовать возможности языка (records, switch expressions, text blocks).
- Lombok — только `@RequiredArgsConstructor`, `@Slf4j`, `@Builder`, `@Data`. Не использовать `@SneakyThrows` в новом коде (скрывает checked exceptions).
- Именование: классы — `PascalCase`, методы/поля — `camelCase`, константы — `UPPER_SNAKE_CASE`.
- Бизнес-логика — в `service`-слое. DAO-классы — только SQL. Controllers — только маршрутизация и валидация входа.
- Новые SQL-изменения оформлять как Liquibase changeset в `message-service/src/main/resources/db/changelog/`.
- Конфигурация сервисов — через `application.yml` / `application-docker.yml`. Секреты не хардкодить в коде.

### Требования к тестам

- Новая бизнес-логика покрывается **unit-тестами** (JUnit 5, Mockito).
- Интеграционные тесты (`@SpringBootTest`) — для проверки корректности SQL-запросов и Kafka-листенеров.
- Тесты идемпотентности — обязательны при изменении `CounterServiceImpl` или `EventListener`.
- Тест должен проверять **поведение**, а не реализацию: тестируй результат вызова метода, а не последовательность вызовов моков.

### Формат ответа агента

Каждый ответ на задачу должен содержать следующие разделы:

#### 1. План действий
Нумерованный список шагов до начала любого кода. Например:
```
1. Добавить значение DELETED в EventType (оба сервиса)
2. Добавить метод addDeleteEvent в EventDao/EventDaoImpl
3. Добавить вызов в MessageServiceImpl.deleteMessage
4. Добавить ветку case DELETED в EventListener
5. Реализовать decrease в CounterServiceImpl (переиспользует логику READED)
6. Написать unit-тест для нового кейса
```

#### 2. Изменения в коде
Для каждого изменяемого файла:
- путь к файлу относительно `saga/`
- полный изменённый фрагмент (не diff, а готовый код)
- если файл создаётся — полное содержимое

#### 3. Пояснение решений
Один абзац: почему выбран именно такой подход, какие альтернативы рассматривались и почему отклонены.

#### 4. Шаги проверки
Конкретные команды для ручной или автоматической проверки:
```bash
# Пример
curl -X POST http://localhost:8080/v1/messages/create \
  -H "Content-Type: application/json" \
  -d '{"userTo": "<UUID>", "content": "hello"}'
curl "http://localhost:8081/v1/counter/user?userId=<UUID>"
# Ожидаемый результат: 1
```

---

## 3. Примеры типовых задач

---

### Сценарий 1: Добавление нового endpoint

**Пример промпта:**
> Добавь в `message-service` endpoint `GET /v1/messages/unread?userId=<UUID>`,
> который возвращает список непрочитанных сообщений для пользователя.

**Ожидаемый формат ответа:**

```
## План действий
1. Добавить метод getUnread(UUID userId) в интерфейс MessageService
2. Реализовать запрос в MessageDaoImpl:
   SELECT * FROM messages WHERE user_to_id = :userId AND is_read = false
3. Реализовать метод в MessageServiceImpl
4. Добавить @GetMapping("unread") в MessageController
5. Написать unit-тест: mockito-заглушка на dao, проверка фильтрации

## Изменения в коде
[полные фрагменты каждого файла]

## Пояснение
Запрос через NamedParameterJdbcOperations — в духе существующего DAO-слоя.
JPA не используется (зависимость отсутствует).

## Шаги проверки
curl "http://localhost:8080/v1/messages/unread?userId=<UUID>"
# Ожидается: JSON-массив непрочитанных сообщений
```

**Критерии готовности:**
- Endpoint возвращает корректный JSON-список.
- Фильтрация по `is_read = false` и `user_to_id` работает.
- Написан unit-тест, покрывающий пустой список и непустой.
- Нет изменений в Outbox-логике.

---

### Сценарий 2: Покрытие модуля тестами

**Пример промпта:**
> Напиши тесты для `CounterServiceImpl`: проверь increase, decrease, идемпотентность
> и то, что счётчик не уходит ниже 0.

**Ожидаемый формат ответа:**

```
## План действий
1. Создать CounterServiceImplTest в counter-service/src/test/...
2. Мокировать RedisTemplate и RedisScript через Mockito
3. Тест increase: isNew=true → increment вызван; isNew=false → increment не вызван
4. Тест decrease: isNew=true → execute(script) вызван; isNew=false → нет
5. Тест getByUserId: val=null → 0, val="5" → 5

## Изменения в коде
[полный класс CounterServiceImplTest]

## Пояснение
RedisTemplate мокируется целиком, т.к. интеграционный тест потребовал бы
запущенного Redis — избыточно для проверки логики isNew.

## Шаги проверки
mvn test -pl counter-service -Dtest=CounterServiceImplTest
# Все тесты GREEN
```

**Критерии готовности:**
- Все 5+ тест-кейсов зелёные.
- Покрыты: happy path, идемпотентный повтор, нулевое значение в Redis.
- Тесты не зависят от запущенной инфраструктуры.

---

### Сценарий 3: Исправление бага по описанию

**Пример промпта:**
> После перезапуска counter-service счётчик всегда возвращает 0, хотя сообщения
> отправлялись до перезапуска. Redis при этом не перезапускался.

**Ожидаемый формат ответа:**

```
## Анализ
Гипотеза 1: при старте counter-service Kafka consumer читает offset с начала
(auto-offset-reset=earliest) и переобрабатывает уже обработанные события.
Но idempotency-ключи в Redis живы → повторная обработка блокируется → счётчик не меняется.

Гипотеза 2 (более вероятная): offset сохранён, повторной обработки нет,
но в Redis нет ключа unread:{userId} — значит счётчик никогда не был записан.
Проверить: redis-cli GET "unread:<UUID>"

## План действий
1. Проверить команду: redis-cli KEYS "unread:*"
2. Если ключей нет — воспроизвести отправку сообщения и проверить логи EventListener
3. Если ключи есть — проверить правильность userId в запросе к counter-service

## Шаги диагностики
docker exec -it <redis-container> redis-cli
> KEYS unread:*
> GET "unread:<userId-из-теста>"

## Ожидаемый результат диагностики
[описание того, что должно появиться при каждом варианте]
```

**Критерии готовности:**
- Баг воспроизведён и локализован (конкретный файл + строка).
- Предложен фикс с обоснованием.
- Написан тест, воспроизводящий сценарий (если применимо).
- После фикса счётчик корректно восстанавливается при повторной обработке.

---

### Сценарий 4: Рефакторинг без изменения поведения

**Пример промпта:**
> Логика идемпотентности в `CounterServiceImpl` дублируется в методах `increase`
> и `decrease`. Вынеси её в отдельный приватный метод.

**Ожидаемый формат ответа:**

```
## План действий
1. Выделить метод ifNew(Long idempotencyId, Runnable action) или
   вынести guard-check в начало обоих методов через общий приватный метод
2. Убедиться, что поведение isNew() не изменилось
3. Запустить существующие тесты — все должны остаться GREEN

## Изменения в коде
[только CounterServiceImpl — полный класс после рефакторинга]

## Пояснение
Выбран вариант с ifNew(id, action) — он явно выражает намерение
«выполни действие, если событие новое», не меняя логику блокировки.

## Шаги проверки
mvn test -pl counter-service
# Все тесты GREEN — поведение не изменилось
```

**Критерии готовности:**
- Дублирование устранено — логика `isNew` присутствует ровно в одном месте.
- Ни один существующий тест не сломан.
- Публичный контракт методов `increase` / `decrease` не изменился.
- Нет изменений в других модулях.

---

## Справочник

### REST API

**message-service** (`:8080`)

| Метод | Путь | Тело | Описание |
|-------|------|------|----------|
| `POST` | `/v1/messages/create` | `MessageDto` | Создать сообщение + событие CREATED в Outbox |
| `PUT` | `/v1/messages/mark-readed` | `MessageDto` | Отметить как прочитанное + событие READED в Outbox |
| `GET` | `/v1/broke` | — | Chaos endpoint: возвращает 500 |

**counter-service** (`:8081`)

| Метод | Путь | Параметры | Описание |
|-------|------|-----------|----------|
| `GET` | `/v1/counter/user` | `?userId=<UUID>` | Получить счётчик непрочитанных |

### Схема БД

```sql
CREATE TABLE messages (
  id         bigserial PRIMARY KEY,
  extId      UUID NOT NULL,
  user_to_id UUID NOT NULL,
  content    TEXT,
  is_read    BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE outbox_events (
  id          bigserial PRIMARY KEY,
  message_key UUID NOT NULL,   -- ключ Kafka = userId, партиционирование по пользователю
  payload     JSONB NOT NULL,  -- { "eventType": "CREATED"|"READED", "userId": "..." }
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Redis-ключи

| Ключ | TTL | Назначение |
|------|-----|------------|
| `unread:{userId}` | — | Счётчик непрочитанных сообщений пользователя |
| `msg_processed:{outboxEventId}` | 24 ч | Idempotency key: предотвращает двойную обработку |

### Запуск

```bash
cd saga/message-service
docker-compose up --build        # вся инфраструктура + оба сервиса

# Локальная разработка (IDE)
# message-service: profile=local → PostgreSQL localhost:5432
# counter-service: profile=local → Kafka localhost:9092, Redis localhost:6379
```

### Известные ограничения

- Таблица `outbox_events` не очищается — нужна периодическая задача при продакшн-использовании.
- DLT-обработчик только логирует; выравнивание счётчиков после DLT не реализовано.
- Idempotency TTL 24 ч: повторная доставка события старше суток будет обработана повторно.
- Redis не персистируется — при полном рестарте Redis счётчики обнуляются без возможности восстановления.
