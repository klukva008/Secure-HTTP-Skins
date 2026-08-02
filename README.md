# Secure HTTP Skins (Forge 1.20.1 / 47.4.20)

Мод принудительно переводит загрузку текстур скинов/плащей/элитр в HTTPS,
даже если Mojang session server отдал `http://` ссылку. Полезно там, где
провайдер/страна блокирует незашифрованный HTTP-трафик к серверам текстур
Mojang, но HTTPS проходит нормально — из-за этого скины у части игроков
просто не отображаются.

## Как это работает

Мод миксинит класс `net.minecraft.client.renderer.texture.HttpTexture` —
именно этот класс скачивает текстуры скинов/плащей по URL. Перехват
происходит на уровне `URL.openConnection()`: если URL начинается с
`http://`, мод на лету создаёт новый `URL` с `https://` и уже его
открывает.

Подход работает на уровне **скачивания**, а не на уровне данных профиля.
Это означает, что URL в `GameProfile` и его криптографическая подпись
(signature) остаются **нетронутыми** — Minecraft не отвергает текстуры
из-за "tampered" подписи. Мод совместим с любыми серверными плагинами
и модами, которые ставят скины через текстовые свойства профиля
(`/skin set` и т.п.).

## Настройка (опционально)

В `config/securehttpskins-common.toml` можно задать список regex-ов
для URL, которые НЕ нужно переводить в HTTPS (например, если у вас
свой сервер скинов, который отдаёт их только по HTTP):

```toml
[securehttpskins]
    excludedUrlPatterns = ["^http://my-skin-server\\.example\\.com/.*"]
```

По умолчанию список пуст — переводятся в HTTPS вообще все `http://` ссылки.

## Сборка

Нужны: JDK 17, доступ в интернет к Maven-репозиториям Forge/Sponge/Minecraft.

1. Соберите мод (Gradle wrapper уже в проекте):

   ```bash
   ./gradlew.bat build
   ```

   Готовый jar появится в `build/libs/securehttpskins-1.0.0.jar`.

2. Первая сборка скачает и обфусцирует Minecraft/Forge, это может занять
   несколько минут.

## Установка

Положите `securehttpskins-1.0.0.jar` в папку `mods` рядом с Forge 47.4.20
для Minecraft 1.20.1. Мод клиентский, конфликтов с другими модами скинов
быть не должно.

## Структура проекта

```
src/main/java/com/securehttpskins/forge/
├── SecureHttpSkins.java              — точка входа мода, регистрация конфига
├── Config.java                       — конфиг с исключениями (regex)
└── mixin/
    └── HttpTextureMixin.java         — перехват URL на уровне скачивания
src/main/resources/
├── META-INF/mods.toml
├── securehttpskins.mixins.json
└── pack.mcmeta
```
