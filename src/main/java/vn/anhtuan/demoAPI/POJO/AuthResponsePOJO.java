package vn.anhtuan.demoAPI.POJO;

public class AuthResponsePOJO {
    private boolean success;
    private String message;
    private Long userId;
    private String username;

    public AuthResponsePOJO() {
    }

    public AuthResponsePOJO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public AuthResponsePOJO(boolean success, String message, Long userId, String username) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.username = username;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}