import java.util.*;

// === 1. Singleton ===
class SecurityManager {
    private static SecurityManager instance;

    private SecurityManager() {}

    public static SecurityManager getInstance() {
        if (instance == null) {
            instance = new SecurityManager();
        }
        return instance;
    }

    public void handleRequest(UserRequest request, AccessHandler handler) {
        handler.handle(request);
    }
}

// === 2. Chain of Responsibility ===
abstract class AccessHandler {
    protected AccessHandler next;

    public void setNext(AccessHandler next) {
        this.next = next;
    }

    public void handle(UserRequest request) {
        if (next != null) {
            next.handle(request);
        } else {
            System.out.println("✅ Доступ надано користувачу: " + request.getUsername());
        }
    }
}

class LoginHandler extends AccessHandler {
    public void handle(UserRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            System.out.println("❌ Помилка: немає логіна");
        } else {
            System.out.println("🔓 Логін підтверджено");
            super.handle(request);
        }
    }
}

class PermissionHandler extends AccessHandler {
    public void handle(UserRequest request) {
        if (!request.hasPermission()) {
            System.out.println("❌ Помилка: недостатньо прав доступу");
        } else {
            System.out.println("🛡️ Права доступу підтверджено");
            super.handle(request);
        }
    }
}

// === 3. Decorator ===
interface IRequest {
    void process();
}

class BasicRequest implements IRequest {
    private final String username;

    public BasicRequest(String username) {
        this.username = username;
    }

    public void process() {
        System.out.println("📦 Обробка запиту користувача: " + username);
    }
}

abstract class RequestDecorator implements IRequest {
    protected IRequest request;

    public RequestDecorator(IRequest request) {
        this.request = request;
    }

    public void process() {
        request.process();
    }
}

class IPLoggerDecorator extends RequestDecorator {
    public IPLoggerDecorator(IRequest request) {
        super(request);
    }

    public void process() {
        System.out.println("🌐 IP-логування...");
        super.process();
    }
}

class TimeLoggerDecorator extends RequestDecorator {
    public TimeLoggerDecorator(IRequest request) {
        super(request);
    }

    public void process() {
        System.out.println("⏰ Час запиту: " + new Date());
        super.process();
    }
}

// === Запит користувача ===
class UserRequest {
    private final String username;
    private final boolean permission;

    public UserRequest(String username, boolean permission) {
        this.username = username;
        this.permission = permission;
    }

    public String getUsername() {
        return username;
    }

    public boolean hasPermission() {
        return permission;
    }
}

// === Main ===
public class Main {
    public static void main(String[] args) {
        // === Створення запиту ===
        UserRequest userRequest = new UserRequest("admin", true);

        // === Обгортання в декоратори ===
        IRequest decoratedRequest = new TimeLoggerDecorator(
                                        new IPLoggerDecorator(
                                            new BasicRequest(userRequest.getUsername())));
        decoratedRequest.process();

        // === Створення ланцюжка перевірок ===
        AccessHandler login = new LoginHandler();
        AccessHandler permission = new PermissionHandler();
        login.setNext(permission);

        // === Використання Singleton для обробки ===
        SecurityManager.getInstance().handleRequest(userRequest, login);
    }
}
