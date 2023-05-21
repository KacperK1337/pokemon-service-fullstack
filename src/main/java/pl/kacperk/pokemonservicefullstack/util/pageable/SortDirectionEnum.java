package pl.kacperk.pokemonservicefullstack.util.pageable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;

@AllArgsConstructor
@Getter
public enum SortDirectionEnum {

    ASC(
        Sort
            .Direction
            .ASC
    ),
    DESC(
        Sort
            .Direction
            .DESC
    );

    private final Sort.Direction sortDirection;

}
