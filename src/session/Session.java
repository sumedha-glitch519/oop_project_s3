package session;

public class Session {
    public static int userId;
    public static String username;
    public static String role;

    public static void clear() {
        userId = 0;
        username = null;
        role = null;
    }
}
