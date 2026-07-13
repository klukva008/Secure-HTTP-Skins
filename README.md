# Secure HTTP Skins

## 🇬🇧 Description

**Secure HTTP Skins** is a tiny but handy mod that fixes invisible skins.

### 🤔 Why do you need it?
Sometimes your ISP or country blocks unencrypted **HTTP traffic** to Mojang's texture servers. As a result, skins, capes, and elytras of some players simply **don't load** — even though HTTPS works just fine.

### ⚙️ How does it work?
The mod intercepts texture downloads. If the game tries to fetch a skin via `http://`, the mod swaps the link to `https://` on the fly and opens that instead.

The important thing: the mod works **only at the download level**. Profile data and their cryptographic signatures stay untouched — Minecraft won't reject textures as "tampered."

### ✅ Compatibility
- Works with any server plugins and skin mods (`/skin set`, etc.)
- Doesn't break GameProfile signatures

### 🛠️ Configuration
In `config/securehttpskins-common.toml` you can specify regex patterns for URLs that should **NOT** be upgraded to HTTPS (e.g., for your own HTTP skin server):

```toml
[securehttpskins]
    excludedUrlPatterns = ["^http://my-skin-server\\.example\\.com/.*"]
```

By default the list is empty — **all** `http://` links get upgraded to `https://`.

---
