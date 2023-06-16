package com.example.logintype.service.util;

import com.example.logintype.constant.Constants;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;

public final class PaginationUtil {
    private PaginationUtil() {
    }

    public static <T> HttpHeaders generatePaginationHttpHeaders(UriComponentsBuilder uriBuilder, Page<T> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.HEADER_X_TOTAL_COUNT, Long.toString(page.getTotalElements()));
        int pageNumber = page.getNumber();
        int pageSize = page.getSize();
        StringBuilder link = new StringBuilder();

        if (pageNumber < page.getTotalPages() - 1) {
            link.append(prepareLink(uriBuilder, pageNumber + 1, pageSize, "next")).append(",");
        }

        if (pageNumber > 0) {
            link.append(prepareLink(uriBuilder, pageNumber - 1, pageSize, "prev")).append(",");
        }

        link.append(prepareLink(uriBuilder, page.getTotalPages() - 1, pageSize, "last")).append(",").append(prepareLink(uriBuilder, 0, pageSize, "first"));
        headers.add(Constants.HEADER_LINK, link.toString());
        headers.add(Constants.HEADER_ACCESS_CONTROL_EXPOSE_HEADERS, "*");

        return headers;
    }

    private static String prepareLink(UriComponentsBuilder uriBuilder, int pageNumber, int pageSize, String relType) {
        return MessageFormat.format(
                Constants.HEADER_LINK_FORMAT,
                preparePageUri(uriBuilder, pageNumber, pageSize),
                relType
        );
    }

    private static String preparePageUri(UriComponentsBuilder uriBuilder, int pageNumber, int pageSize) {
        return uriBuilder.replaceQueryParam("page", new Object[]{Integer.toString(pageNumber)})
                .replaceQueryParam("size", new Object[]{Integer.toString(pageSize)})
                .toUriString()
                .replace(",", "%2C")
                .replace(";", "%3B");
    }
}