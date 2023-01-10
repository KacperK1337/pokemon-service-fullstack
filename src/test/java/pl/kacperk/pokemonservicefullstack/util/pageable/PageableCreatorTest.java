package pl.kacperk.pokemonservicefullstack.util.pageable;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class PageableCreatorTest {

    @Test
    void getPageable_exampleParamsWithASCSort_pageableWithParamsEqualToProvided() {
        // given
        Integer pageNumber = 2;
        Integer pageSize = 10;
        String fieldToSortBy = "someField";
        String sortDirectionName = "ASC";

        Sort.Direction expectedSortDirection = SortDirectionEnum.valueOf(sortDirectionName).getSortDirection();
        Sort expectedSort = Sort.by(expectedSortDirection, fieldToSortBy);

        // when
        Pageable pageable = PageableCreator.getPageable(pageNumber, pageSize, sortDirectionName, fieldToSortBy);

        // then
        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);
        assertThat(pageable.getSort()).isEqualTo(expectedSort);
    }

    @Test
    void getPageable_exampleParamsWithDESCSort_pageableWithParamsEqualToProvided() {
        // given
        Integer pageNumber = 2;
        Integer pageSize = 10;
        String fieldToSortBy = "someField";
        String sortDirectionName = "DESC";

        Sort.Direction expectedSortDirection = SortDirectionEnum.valueOf(sortDirectionName).getSortDirection();
        Sort expectedSort = Sort.by(expectedSortDirection, fieldToSortBy);

        // when
        Pageable pageable = PageableCreator.getPageable(pageNumber, pageSize, sortDirectionName, fieldToSortBy);

        // then
        assertThat(pageable.getPageNumber()).isEqualTo(pageNumber);
        assertThat(pageable.getPageSize()).isEqualTo(pageSize);
        assertThat(pageable.getSort()).isEqualTo(expectedSort);
    }
}