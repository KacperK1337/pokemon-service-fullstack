package pl.kacperk.pokemonservicefullstack.entity.appuser.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.SEQUENCE;

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

    private static final String USER_SEQUENCE = "app_user_sequence";

    @Id
    @SequenceGenerator(
        name = USER_SEQUENCE,
        sequenceName = USER_SEQUENCE,
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = SEQUENCE,
        generator = USER_SEQUENCE
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
    @Enumerated(STRING)
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

    public AppUser(
        final AppUserRole role, final String userName,
        final String password
    ) {
        this.role = role;
        this.userName = userName;
        this.password = password;
    }
}
