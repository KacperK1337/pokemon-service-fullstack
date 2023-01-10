package pl.kacperk.pokemonservicefullstack.api.pokemon.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

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

    @Id
    @SequenceGenerator(
            name = "pokemon_sequence",
            sequenceName = "pokemon_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pokemon_sequence"
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
    private Set<String> possibleEvolutions = new LinkedHashSet<>();

    @Column(
            name = "types"
    )
    @ElementCollection
    private Set<Type> types = new LinkedHashSet<>();

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

    public Pokemon(String name, Set<String> possibleEvolutions, Set<Type> types, String photoUrl) {
        this.name = name;
        this.possibleEvolutions = possibleEvolutions;
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
