package pl.kacperk.pokemonservicefullstack.api.appuser.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(
        name = "AppUser"
)
@Table(
        name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "app_user_username_unique", columnNames = "username")
        }
)
public class AppUser {

    @Id
    @SequenceGenerator(
            name = "app_user_sequence",
            sequenceName = "app_user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "app_user_sequence"
    )
    @Column(
            name = "id"
    )
    private Long id;

    @Column(
            name = "role",
            nullable = false,
            columnDefinition = "TEXT"
    )
    @Enumerated(EnumType.STRING)
    private AppUserRole role;

    @Column(
            name = "username",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String userName;

    @Column(
            name = "password",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String password;

    @Column(
            name = "account_non_expired",
            nullable = false
    )
    private boolean accountNonExpired = true;

    @Column(
            name = "account_non_locked",
            nullable = false
    )
    private boolean accountNonLocked = true;

    @Column(
            name = "credentials_non_expired",
            nullable = false
    )
    private boolean credentialsNonExpired = true;

    @Column(
            name = "enabled",
            nullable = false
    )
    private boolean enabled = true;

    @Column(
            name = "favourite_pokemon_name"
    )
    private String favouritePokemonName = null;

    public AppUser(AppUserRole role, String userName, String password) {
        this.role = role;
        this.userName = userName;
        this.password = password;
    }
}
