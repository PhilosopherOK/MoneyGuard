package ua.bank.moneyguard.models.tokens;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.bank.moneyguard.models.Client;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class PassToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(nullable = false, name = "client_id", referencedColumnName = "client_id")
    private Client client;

    public PassToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, Client client) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.client = client;
    }
}
