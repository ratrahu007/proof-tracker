# Proof Tracker - Architecture Analysis & Design Patterns

## 📋 Project Overview
**Type:** Spring Boot 4.1.0 Microservice (Java 21)  
**Domain:** Proof Tracking System with Notification & OTP Services  
**Architecture Style:** Layered MVC with Strategy & Factory Patterns

---

## 🏗️ Current Architecture & Communication Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION ENTRY POINT                  │
│              ProofTrackerApplication.java                   │
│                  (@SpringBootApplication)                   │
└──────────────────────────┬──────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                     EXCEPTION HANDLING                       │
│           GlobalExceptionHandler (@RestControllerAdvice)    │
│  - Catches AppException → ErrorResponse                      │
│  - Catches ValidationException → ValidationErrorResponse    │
└─────────────────────────────────────────────────────────────┘
                          │
              ┌───────────┼───────────┐
              ▼           ▼           ▼
        ┌─────────┐ ┌──────────┐ ┌────────┐
        │ COMMON  │ │ OTP      │ │NOTIF   │
        │ LAYER   │ │ SERVICE  │ │SERVICE │
        └─────────┘ └──────────┘ └────────┘
```

### **Layer Architecture**

#### **1. COMMON LAYER** (Cross-cutting concerns)
**Classes:**
- `AppException` - Custom exception with error codes
- `ErrorCode` - Enum with HTTP status mappings
- `ErrorResponse` - API error response DTO
- `ValidationErrorResponse` - Validation error details
- `GlobalExceptionHandler` - Centralized exception handling

**Communication Pattern:**
```
Any Layer throws AppException
        │
        ▼
GlobalExceptionHandler catches & converts
        │
        ▼
Returns standardized ErrorResponse (HTTP + JSON)
```

---

#### **2. NOTIFICATION SERVICE LAYER** 

**Key Classes & Their Roles:**

```
NotificationRequest (DTO)
    │ Input data from API
    │
    ▼
NotificationService (Interface)
    │
    ├─→ NotificationServiceImpl (Implementation)
    │       │
    │       ├─→ NotificationRepository (JPA)
    │       │       └─→ Notification (Entity)
    │       │
    │       └─→ NotificationProviderFactory
    │           │
    │           ▼
    │       providers.stream().filter()
    │           │
    │           ├─→ EmailNotificationProvider
    │           ├─→ [SMS Provider - not implemented]
    │           └─→ [PUSH Provider - not implemented]
    │
    └─→ Creates notification record in DB
        with status: PENDING → SENT/FAILED
```

**Communication Flow (Step-by-Step):**

1. **Request Arrives**: `NotificationRequest` DTO received
2. **Service Layer**: `NotificationServiceImpl.send(request)` called
3. **Entity Creation**: New `Notification` entity created with PENDING status
4. **Provider Selection**: `NotificationProviderFactory` selects provider
   - Factory uses Stream API with filter on `getChannel()`
   - Throws `AppException` if provider not found
5. **Send Execution**: Selected provider sends message via external service
6. **Status Update**: 
   - Success → SENT + timestamp
   - Failure → FAILED + saved anyway (audit trail)
7. **Persistence**: `NotificationRepository.save()` persists to DB

---

#### **3. OTP SERVICE LAYER**

**Classes Identified:**
- `OtpVerification` (Entity) - Stores OTP records
- `NotificationService` - Dependency injection (sends OTP via email)

**Potential Communication:**
```
OTP Generation Service
    │ (creates OTP)
    │
    ├─→ Saves to OtpVerification entity
    │
    ├─→ Creates NotificationRequest
    │   (subject, message with OTP)
    │
    └─→ Calls NotificationService.send()
        (sends OTP email to user)
```

---

## 🎯 Design Patterns Currently Used

### ✅ **1. Factory Pattern** (WELL IMPLEMENTED)
**Location:** `NotificationProviderFactory`
```java
public NotificationProvider getProvider(NotificationChannel channel) {
    return providers.stream()
            .filter(provider -> provider.getChannel() == channel)
            .findFirst()
            .orElseThrow(() -> new AppException(...));
}
```
**Benefits:**
- Decouples provider creation from usage
- Automatically discovers all `NotificationProvider` implementations via Spring
- Easy to add new providers (SMS, PUSH) without modifying factory

---

### ✅ **2. Strategy Pattern** (WELL IMPLEMENTED)
**Location:** `NotificationProvider` interface + implementations
```java
interface NotificationProvider {
    NotificationChannel getChannel();
    void send(NotificationRequest request);
}
```
**Implementations:**
- `EmailNotificationProvider` - Email strategy
- [SMS strategy to be added]
- [PUSH strategy to be added]

**Benefits:**
- Swappable notification channels
- New channels without modifying existing code (Open/Closed Principle)

---

### ✅ **3. Dependency Injection Pattern** (SPRING)
**Usage:**
```java
@Service
@RequiredArgsConstructor  // Constructor injection via Lombok
public class NotificationServiceImpl {
    private final NotificationRepository repository;
    private final NotificationProviderFactory providerFactory;
}
```
**Benefits:**
- Loose coupling
- Easy testing with mocks
- Spring manages bean lifecycle

---

### ✅ **4. Builder Pattern** (LOMBOK)
**Usage:**
```java
Notification notification = Notification.builder()
    .recipient(request.getRecipient())
    .subject(request.getSubject())
    .message(request.getMessage())
    .channel(request.getChannel())
    .type(request.getType())
    .status(NotificationStatus.PENDING)
    .build();
```
**Benefits:**
- Readable object construction
- Handles optional fields elegantly

---

### ✅ **5. Repository Pattern** (JPA)
**Location:** `NotificationRepository extends JpaRepository`
**Benefits:**
- Data access layer abstraction
- CRUD operations out of box
- Easy to swap implementations (e.g., switch from JPA to MongoDB)

---

## 🔴 Issues & Improvement Opportunities

### **Issue 1: Missing Service Interface for OTP**
**Current State:** `OtpVerification` entity exists but no `OtpService` interface
**Problem:**
- Can't call OTP functionality from other services
- No abstraction for OTP operations
- Makes testing difficult

**Solution:**
```java
// Create interface
public interface OtpService {
    String generateOtp(String userId);
    boolean validateOtp(String userId, String otp);
    void sendOtpViaNotification(String userId, String email);
}

// Implementation
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final NotificationService notificationService;
    private final OtpVerificationRepository otpRepository;
    // ... implementation
}
```

---

### **Issue 2: No Exception Handling in Providers**
**Current State:** `EmailNotificationProvider.send()` throws uncaught exceptions
```java
mailSender.send(message);  // ← Can throw exception
```
**Problem:**
- Exceptions bubble up uncaught
- Service layer catches generic Exception (not specific)

**Solution:** Add custom provider exception:
```java
public class NotificationProviderException extends AppException {
    public NotificationProviderException(String message, Throwable cause) {
        super(ErrorCode.NOTIFICATION_PROVIDER_NOT_FOUND, message);
    }
}

// In provider:
try {
    mailSender.send(message);
} catch (MailException ex) {
    throw new NotificationProviderException("Email send failed: " + ex.getMessage(), ex);
}
```

---

### **Issue 3: Missing Template Pattern**
**Current State:** `OtpEmailTemplate` exists but not used
**Problem:**
- Email content is hardcoded in `EmailNotificationProvider`
- No support for different message templates
- Difficult to maintain email content

**Solution:** Implement Template Method Pattern:
```java
public abstract class NotificationTemplate {
    public final void sendNotification(NotificationRequest request) {
        String template = loadTemplate(request.getType());
        String message = populateTemplate(template, request);
        send(message, request.getRecipient());
    }
    
    protected abstract String loadTemplate(NotificationType type);
    protected abstract void send(String message, String recipient);
}

public class EmailTemplate extends NotificationTemplate {
    @Override
    protected String loadTemplate(NotificationType type) {
        // Load from OtpEmailTemplate, etc.
    }
}
```

---

### **Issue 4: No Event-Driven Pattern**
**Current State:** Direct method calls (synchronous)
```
Request → Service → Provider → External API
```
**Problem:**
- Blocking calls to external email service
- If email fails, user waits for error
- No retry mechanism
- Can't decouple sender from notification logic

**Solution:** Implement Event Pattern with Spring Events:
```java
@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {
    private final ApplicationEventPublisher eventPublisher;
    
    public void publishNotificationEvent(NotificationRequest request) {
        eventPublisher.publishEvent(new NotificationSendEvent(request));
    }
}

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final NotificationProviderFactory factory;
    
    @Async
    @EventListener
    public void onNotificationEvent(NotificationSendEvent event) {
        // Send asynchronously
        NotificationProvider provider = factory.getProvider(event.getChannel());
        provider.send(event.getRequest());
    }
}
```

---

### **Issue 5: No Retry Mechanism**
**Current State:** Single attempt, no retry logic
**Problem:**
- Transient failures cause permanent failure
- Network glitches lose notifications

**Solution:** Use Spring Retry or Resilience4j:
```java
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    
    @Retryable(
        retryFor = {IOException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public void sendWithRetry(NotificationRequest request) {
        // Try up to 3 times with 1 second backoff
    }
}
```

---

### **Issue 6: Missing Caching**
**Current State:** No caching for provider lookups
**Problem:**
- Stream filter runs every send operation
- Repetitive provider lookups

**Solution:**
```java
@Component
@RequiredArgsConstructor
public class NotificationProviderFactory {
    private final Map<NotificationChannel, NotificationProvider> providerCache;
    
    public NotificationProviderFactory(List<NotificationProvider> providers) {
        this.providerCache = providers.stream()
            .collect(Collectors.toMap(
                NotificationProvider::getChannel,
                provider -> provider
            ));
    }
    
    public NotificationProvider getProvider(NotificationChannel channel) {
        return providerCache.getOrDefault(channel, 
            () -> { throw new AppException(...); });
    }
}
```

---

### **Issue 7: No Audit Logging**
**Current State:** Notification entity stores status but no detailed logs
**Problem:**
- Can't track who sent, when, why
- Difficult to debug failures
- No accountability trail

**Solution:** Add audit fields:
```java
@Entity
public class Notification {
    // ... existing fields
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private String createdBy;  // User who triggered
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    private String failureReason;  // Error message
    
    private Integer retryCount;
}
```

---

### **Issue 8: Mixed Concerns in Service Layer**
**Current State:** `NotificationServiceImpl` handles both business logic and status management
**Problem:**
- Hard to test
- Single Responsibility Principle violation

**Solution:** Split into separate classes:
```java
// Business logic
@Service
public class NotificationSender {
    public void send(NotificationRequest request) {
        NotificationProvider provider = providerFactory.getProvider(request.getChannel());
        provider.send(request);
    }
}

// Status management
@Service
public class NotificationStatusManager {
    public Notification createPending(NotificationRequest request) { }
    public void markSent(Notification notification) { }
    public void markFailed(Notification notification, String reason) { }
}
```

---

## 🚀 Recommended Design Pattern Improvements

### **Priority 1: Observer/Event Pattern**
```
RequestController
    │
    ├─→ NotificationEventPublisher.publish(request)
    │
    └─→ Response: "Processing..."
    
    [Async]
    NotificationEventListener
        ├─→ Send email
        ├─→ Update DB
        └─→ Log audit trail
```

### **Priority 2: Decorator Pattern** (for additional features)
```java
public interface NotificationProvider {
    void send(NotificationRequest request);
}

// Base implementation
public class EmailNotificationProvider implements NotificationProvider {
    public void send(NotificationRequest request) { }
}

// Decorated with logging
public class LoggingNotificationDecorator implements NotificationProvider {
    private NotificationProvider wrapped;
    public void send(NotificationRequest request) {
        log.info("Sending notification: " + request);
        wrapped.send(request);
        log.info("Notification sent successfully");
    }
}

// Decorated with retry
public class RetryableNotificationDecorator implements NotificationProvider {
    @Retryable(...)
    public void send(NotificationRequest request) {
        wrapped.send(request);
    }
}
```

### **Priority 3: Command Pattern** (for undo/redo)
```java
public interface NotificationCommand {
    void execute();
    void undo();
}

public class SendNotificationCommand implements NotificationCommand {
    private Notification notification;
    
    public void execute() {
        // Send
    }
    
    public void undo() {
        // Mark as revoked or delete
    }
}
```

### **Priority 4: Chain of Responsibility** (for validation)
```
ValidationChain
    ├─→ RecipientValidator (email format)
    ├─→ ChannelValidator (channel exists)
    ├─→ RateLimitValidator (user hasn't exceeded limit)
    └─→ ApprovalValidator (admin approval if needed)
```

---

## 📊 Communication Diagram Summary

```
┌──────────────────────────────────────────────────────┐
│ HTTP Request (POST /notifications)                   │
└──────────────┬───────────────────────────────────────┘
               │
               ▼
      ┌─────────────────────┐
      │ @RestController     │ ← [To implement]
      │ [Controller Layer]  │   NotificationController
      └────────────┬────────┘
                   │ NotificationRequest
                   ▼
      ┌─────────────────────────────────────┐
      │ NotificationServiceImpl              │
      │ ├─→ Create entity (PENDING)          │
      │ ├─→ Get provider from Factory        │
      │ ├─→ Try: provider.send()             │
      │ ├─→ Update status (SENT/FAILED)     │
      │ └─→ repository.save()                │
      └────────────┬────────────────────────┘
                   │
        ┌──────────┼──────────┐
        │          │          │
        ▼          ▼          ▼
   [Repository]  [Factory]  [Provider]
        │          │          │
        └──────────┼──────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
        ▼                     ▼
   [Database]          [External Service]
   (Notification)      (Mail Server)
   
   Status tracked in DB for audit & retry
```

---

## 🔧 Quick Implementation Checklist

- [ ] Create `NotificationController` (API endpoint)
- [ ] Create `OtpService` interface & implementation
- [ ] Add `NotificationEventListener` for async processing
- [ ] Implement `@Retryable` decorator for providers
- [ ] Add audit fields to `Notification` entity
- [ ] Create `NotificationTemplateEngine` for email templates
- [ ] Add logging interceptor (Decorator Pattern)
- [ ] Add rate limiting validator (Chain of Responsibility)
- [ ] Create unit tests with Mockito
- [ ] Add integration tests with `@SpringBootTest`

---

## 📚 Resources

**Design Patterns Used:**
- Factory Pattern → Provider instantiation
- Strategy Pattern → Multiple notification channels
- Dependency Injection → Spring @Autowired

**Recommended Patterns to Add:**
- Observer Pattern → Event-driven architecture
- Decorator Pattern → Logging, retry logic
- Command Pattern → Auditing & undo operations
- Chain of Responsibility → Multi-step validation

**Spring Annotations Key:**
- `@Service` - Business logic beans
- `@Component` - Generic Spring component
- `@RequiredArgsConstructor` - Constructor injection (Lombok)
- `@Async` - Async method execution
- `@EventListener` - Listen to events
- `@Retryable` - Automatic retry logic
