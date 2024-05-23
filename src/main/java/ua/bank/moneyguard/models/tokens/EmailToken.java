package ua.bank.moneyguard.models.tokens;


import lombok.Data;
import lombok.NoArgsConstructor;
import ua.bank.moneyguard.models.Client;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class EmailToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "client_id", referencedColumnName = "client_id")
    private Client client;

    public EmailToken(String token,
                      LocalDateTime createdAt,
                      LocalDateTime expiresAt,
                      Client client) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.client = client;
    }
}
