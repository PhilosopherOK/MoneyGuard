package ua.bank.moneyguard.dtos.authorizationDTOs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDTO {
    private String newPassword;
    private String confirmPassword;
}
