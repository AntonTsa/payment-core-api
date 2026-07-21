package com.anton.tsarenko.payment.core.api.shared.api.response;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for paging elements.
 *
 * @param content - content
 * @param page - page number
 * @param size - size of elements on a page
 * @param totalElements - total amount of elements
 * @param totalPages - total amount of pages
 * @param <T> - type of content, covered with pages
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) implements Serializable {}
