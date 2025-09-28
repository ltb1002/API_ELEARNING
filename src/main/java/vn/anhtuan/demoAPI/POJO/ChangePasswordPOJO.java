package vn.anhtuan.demoAPI.POJO;

// DTO/ChangePasswordDTO.java  (nếu bạn chưa có dạng này)
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordPOJO {
    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;
}
