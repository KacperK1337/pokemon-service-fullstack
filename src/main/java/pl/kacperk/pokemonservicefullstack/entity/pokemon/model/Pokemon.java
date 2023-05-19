package pl.kacperk.pokemonservicefullstack.entity.pokemon.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Getter
@Setter
@NoArgsConstructor
@Entity(
    name = "Pokemon"
)
@Table(
    name = "pokemon",
    uniqueConstraints = {
        @UniqueConstraint(name = "pokemon_name_unique", columnNames = "name"),
        @UniqueConstraint(name = "pokemon_photo_url_unique", columnNames = "photo_url")
    }
)
public class Pokemon {

    private static final String POKEMON_SEQUENCE = "pokemon_sequence";

    @Id
    @SequenceGenerator(
        name = POKEMON_SEQUENCE,
        sequenceName = POKEMON_SEQUENCE,
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = SEQUENCE,
        generator = POKEMON_SEQUENCE
    )
    @Column(
        name = "id"
    )
    private Long id;

    @Column(
        name = "name",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String name;

    @Column(
        name = "evolution"
    )
    @ElementCollection
    private Set<String> evolutions = new LinkedHashSet<>();

    @Column(
        name = "types"
    )
    @ElementCollection
    private Set<PokemonType> types = new LinkedHashSet<>();

    @Column(
        name = "photo_url",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String photoUrl;

    @Column(
        name = "number_of_likes",
        nullable = false
    )
    private int numberOfLikes = 0;

    public Pokemon(
        final String name, final Set<String> evolutions,
        final Set<PokemonType> types, final String photoUrl
    ) {
        this.name = name;
        this.evolutions = evolutions;
        this.types = types;
        this.photoUrl = photoUrl;
    }

    public void like() {
        numberOfLikes += 1;
    }

    public void unlike() {
        numberOfLikes -= 1;
    }

}
