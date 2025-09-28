// DTO/UpdateProfileRequest.java
package vn.anhtuan.demoAPI.POJO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequestPOJO {
    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    // Nếu muốn đổi mật khẩu ngay trong profile:
    private String currentPassword; // optional: bắt buộc nếu có newPassword
    private String newPassword;     // optional
}

