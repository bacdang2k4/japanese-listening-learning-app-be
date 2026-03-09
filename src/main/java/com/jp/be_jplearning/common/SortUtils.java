package com.jp.be_jplearning.common;

import org.springframework.data.domain.Sort;

import java.util.Set;

public final class SortUtils {

    private SortUtils() {}

    public static Sort parseSort(String sortStr, Set<String> allowedColumns, String defaultColumn) {
        if (sortStr == null || sortStr.isBlank()) {
            return Sort.by(Sort.Direction.DESC, defaultColumn);
        }

        String[] parts = sortStr.split(",");
        String column = parts[0].trim();
        Sort.Direction direction = parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        if (!allowedColumns.contains(column)) {
            throw new BusinessException("Invalid sort column: " + column
                    + ". Allowed: " + String.join(", ", allowedColumns));
        }

        return Sort.by(direction, column);
    }
}
