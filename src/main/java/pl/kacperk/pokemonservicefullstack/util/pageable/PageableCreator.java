package pl.kacperk.pokemonservicefullstack.util.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableCreator {

    public static Pageable getPageable(
        final int pageNumber, final int pageSize,
        final String sortDirectionName, final String fieldToSortBy
    ) {
        final Sort.Direction sortDirection = SortDirectionEnum
            .valueOf(sortDirectionName)
            .getSortDirection();
        return PageRequest.of(
            pageNumber, pageSize, Sort.by(sortDirection, fieldToSortBy)
        );
    }

}
