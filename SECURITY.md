# 🔐 Security Configuration

## Spring Security Implementation

Il progetto utilizza **Spring Security** per implementare autenticazione e autorizzazione.

## 👥 Utenti Predefiniti

### Credenziali di Test (In-Memory)

| Username | Password | Ruolo | Permessi |
|----------|----------|-------|----------|
| `admin` | `admin123` | ADMIN | Accesso completo a tutte le funzionalità |
| `reception` | `reception123` | RECEPTION | Accesso solo alla pagina di verifica biglietti |
| `user` | `user123` | USER | Accesso limitato (funzionalità future) |

## 🛡️ Autorizzazioni per Endpoint

### Endpoint Pubblici (Accesso Senza Autenticazione)

- **Homepage**: `/`, `/index`
- **Creazione Biglietti**: 
  - `POST /tickets` (form)
  - `POST /api/tickets` (API REST)
- **Visualizzazione Biglietto**: `/ticket/{id}`
- **QR Code**: `/qrcode/{id}`
- **Risorse Statiche**: `/css/**`, `/js/**`, `/img/**`
- **Swagger UI**: `/swagger-ui/**`, `/v3/api-docs/**`
- **H2 Console** (solo dev): `/h2-console/**`

### Endpoint Protetti

#### Reception (Richiede Ruolo RECEPTION o ADMIN)
- **Scanner QR Code**: `/reception`, `/reception/**`
- **Verifica Biglietto**: `POST /reception/verify/{ticketId}`

> **Nota**: Gli utenti con ruolo `RECEPTION` o `ADMIN` possono accedere alla reception per verificare i biglietti.

## 🔑 Funzionalità di Sicurezza

### 1. Password Encoding
- Utilizzo di **BCryptPasswordEncoder** per la cifratura delle password
- Le password non vengono mai salvate in chiaro

### 2. Form Login
- Pagina di login personalizzata: `/login`
- Redirect automatico a `/reception` dopo login riuscito
- Messaggi di errore per credenziali non valide

### 3. Logout
- Endpoint logout: `POST /logout`
- Redirect a homepage (`/`) dopo logout

### 4. CSRF Protection
- Abilitata per tutte le richieste POST tramite form
- Disabilitata per:
  - API REST (`/api/**`) - da proteggere con JWT in produzione
  - H2 Console (`/h2-console/**`)

### 5. Session Management
- Gestione automatica delle sessioni da Spring Security
- Cookie sicuri per le sessioni

## 🚀 Come Usare

### Accesso alla Reception

1. Naviga su `http://localhost:8080/reception`
2. Verrai reindirizzato alla pagina di login
3. Usa le credenziali:
   - **Username**: `reception` (o `admin`)
   - **Password**: `reception123` (o `admin123`)
4. Dopo il login, accederai allo scanner QR

### Logout dalla Reception

Clicca sul pulsante **"🔓 Logout"** nell'angolo in alto a destra della pagina reception.

## 📝 Note di Sviluppo

### In-Memory Users
Attualmente gli utenti sono definiti in memoria tramite `InMemoryUserDetailsManager`. 

**Per produzione**, è consigliato:
1. Creare un'entità `User` con JPA
2. Implementare un `UserDetailsService` personalizzato
3. Salvare gli utenti in database
4. Implementare registrazione e gestione profili

### Esempio di UserDetailsService Personalizzato

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRoles().toArray(new String[0]))
            .build();
    }
}
```

## 🔒 Miglioramenti Futuri per Produzione

### Alta Priorità
- [ ] **JWT Authentication** per API REST
- [ ] **Database Users** invece di in-memory
- [ ] **Email Verification** per nuovi utenti
- [ ] **Password Reset** via email
- [ ] **Rate Limiting** per prevenire brute force
- [ ] **HTTPS** obbligatorio

### Media Priorità
- [ ] **2FA (Two-Factor Authentication)**
- [ ] **OAuth2/Social Login** (Google, GitHub, etc.)
- [ ] **Audit Logging** per tracciare accessi
- [ ] **Session Timeout** configurabile
- [ ] **Password Policy** (lunghezza, complessità)

### Bassa Priorità
- [ ] **Remember Me** functionality
- [ ] **Account Lockout** dopo tentativi falliti
- [ ] **IP Whitelisting** per admin
- [ ] **Security Headers** (CSP, XSS Protection)

## 🧪 Testing della Sicurezza

### Test degli Endpoint Protetti

```bash
# Tentativo di accesso senza autenticazione (deve essere reindirizzato a /login)
curl -i http://localhost:8080/reception

# Login con credenziali valide
curl -i -X POST http://localhost:8080/login \
  -d "username=reception&password=reception123" \
  -c cookies.txt

# Accesso a reception con cookie di sessione
curl -i http://localhost:8080/reception -b cookies.txt
```

### Test con Spring Security Test

Gli unit test possono usare `@WithMockUser` per simulare utenti autenticati:

```java
@Test
@WithMockUser(roles = "RECEPTION")
void reception_shouldBeAccessible_withReceptionRole() throws Exception {
    mockMvc.perform(get("/reception"))
           .andExpect(status().isOk());
}

@Test
@WithAnonymousUser
void reception_shouldRedirectToLogin_withoutAuthentication() throws Exception {
    mockMvc.perform(get("/reception"))
           .andExpect(status().is3xxRedirection());
}
```

## 📚 Documentazione

- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Spring Boot Security](https://docs.spring.io/spring-boot/reference/web/spring-security.html)
- [OWASP Security Guidelines](https://owasp.org/www-project-web-security-testing-guide/)

## ⚠️ Disclaimer

Questa configurazione è pensata per **sviluppo e testing**. Per un ambiente di produzione:
1. Non usare password hardcoded
2. Implementare un sistema di gestione utenti completo
3. Usare HTTPS
4. Implementare logging e monitoring
5. Eseguire penetration testing
6. Seguire le best practice OWASP
