package pl.kacperk.pokemonservicefullstack.util.pageable;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class PageableCreatorTest {

    @Test
    void getPageable_exampleParamsAscSort_pageableParamsEqualToProvided() {
        // given
        final var pageNumber = 2;
        final var pageSize = 10;
        final var fieldToSortBy = "someField";
        final var sortDirectionName = "ASC";

        final var expectedSortDirection = SortDirectionEnum
                .valueOf(sortDirectionName)
                .getSortDirection();
        final var expectedSort = Sort.by(expectedSortDirection, fieldToSortBy);

        // when
        final var pageable = PageableCreator.getPageable(
                pageNumber, pageSize, sortDirectionName, fieldToSortBy
        );

        // then
        assertThat(pageable.getPageNumber())
                .isEqualTo(pageNumber);
        assertThat(pageable.getPageSize())
                .isEqualTo(pageSize);
        assertThat(pageable.getSort())
                .isEqualTo(expectedSort);
    }

    @Test
    void getPageable_exampleParamsDescSort_pageableParamsEqualToProvided() {
        // given
        final var pageNumber = 2;
        final var pageSize = 10;
        final var fieldToSortBy = "someField";
        final var sortDirectionName = "DESC";

        final var expectedSortDirection = SortDirectionEnum
                .valueOf(sortDirectionName)
                .getSortDirection();
        final var expectedSort = Sort.by(expectedSortDirection, fieldToSortBy);

        // when
        final var pageable = PageableCreator.getPageable(
                pageNumber, pageSize, sortDirectionName, fieldToSortBy
        );

        // then
        assertThat(pageable.getPageNumber())
                .isEqualTo(pageNumber);
        assertThat(pageable.getPageSize())
                .isEqualTo(pageSize);
        assertThat(pageable.getSort())
                .isEqualTo(expectedSort);
    }

}
