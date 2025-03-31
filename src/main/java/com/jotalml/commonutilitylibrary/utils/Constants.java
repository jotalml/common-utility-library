package com.jotalml.commonutilitylibrary.utils;

public class Constants {

    public static final String EXCEEDED = "Exceeded the maximum number of pages";
    public static final String NO_RECORD_FOUND = "No records found";
    public static final int MAX_RECORD_PAGE = 10;
    public static final String INITIAL_PAGE = "0";
    public static final String VALID_PAGE = "page must be greater than 0";
    public static final String NOT_NULL_PAGE = "page cannot be null";
    public static final String OK = "200";
    public static final String DO_NOT_SHOW = "no_mostrar";
    public static final String IN_REVIEW = "revisi";
    public static final String ID = "id";

    // Filter descriptions
    public static final String ID_DESCRIPTION = "Mostrar registros en los cuales “id” es igual a este valor";
    public static final String FIELD_IN = "Múltiples valores separados por comas.";
    public static final String SEARCH = "Un término de búsqueda.";
    public static final String ORDERING = "Qué campo usar para ordenar los resultados.";
    public static final String PAGE = "Número de página.";
    public static final String SET = "set";
    public static final String DB = "postgres";
    public static final Integer PAGE_SIZE = -1;

    //Filter url
    public static final String PAGE_URL = "page=";
    public static final String HEADER = "request-original-uri";
    public static final int PAGE1 = 1;
    public static final int PAGE2 = 2;
    public static final String OPER_AMP = "&";
    public static final String OPER_INTERR = "?";
    public static final String REQUEST_NULL = "Request cannot be null";
    public static final String PAGERESPONSE2_NULL = "PagedResponse2 cannot be null";
    public static final String PAGE_RANGE = "page cannot be less than 1";
    public static final String PAGESIZE_RANGE = "pageSize cannot be less than 1";


}
