package com.upmudoum.user.common.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BulkRequestDto<T> {

    private List<T> added = List.of();
    private List<T> updated = List.of();
    private List<T> deleted = List.of();

    public BulkRequestDto(List<T> added, List<T> updated, List<T> deleted) {
        this.added = added == null ? List.of() : List.copyOf(added);
        this.updated = updated == null ? List.of() : List.copyOf(updated);
        this.deleted = deleted == null ? List.of() : List.copyOf(deleted);
    }
}
