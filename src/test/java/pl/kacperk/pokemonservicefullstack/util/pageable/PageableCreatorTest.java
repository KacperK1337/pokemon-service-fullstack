package pl.kacperk.pokemonservicefullstack.util.pageable;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_FIELD_TO_SORT;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_PAGE_NUM;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_PAGE_SIZE;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.DEF_SORT;
import static pl.kacperk.pokemonservicefullstack.TestUtils.PageableUtils.NON_DEF_SORT;
import static pl.kacperk.pokemonservicefullstack.util.pageable.PageableCreator.getPageable;

class PageableCreatorTest {

    @ParameterizedTest
    @ValueSource(strings = {DEF_SORT, NON_DEF_SORT})
    void getPageable_differentSortDir_pageableParamsCorrect(final String sortDirection) {
        final var expectedSortDirection = SortDirectionEnum
            .valueOf(sortDirection)
            .getSortDirection();
        final var expectedSort = Sort.by(expectedSortDirection, DEF_FIELD_TO_SORT);

        final var pageable = getPageable(
            DEF_PAGE_NUM, DEF_PAGE_SIZE,
            sortDirection, DEF_FIELD_TO_SORT
        );

        assertThat(pageable.getPageNumber())
            .isEqualTo(DEF_PAGE_NUM);
        assertThat(pageable.getPageSize())
            .isEqualTo(DEF_PAGE_SIZE);
        assertThat(pageable.getSort())
            .isEqualTo(expectedSort);
    }

}
