package com.jotalml.commonutilitylibrary.model.dao.impl;

import com.jotalml.commonutilitylibrary.enums.CompareEnum;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jotalml.commonutilitylibrary.utils.Constants.DB;

@Component
public class PredicateSpecification {

    private static final DecimalFormat df = new DecimalFormat("###,#####");

    /**
     * Add new predicate a list of predicates
     *
     * @param predicates      List of predicates
     * @param criteriaBuilder Instance of search criteria
     * @param field           Object as field
     * @param path            Path
     * @param compareEnum     Type of compare
     * @param database        Database (Default value Sql Server)
     */
    public static <T> void addPredicate(
            List<Predicate> predicates,
            CriteriaBuilder criteriaBuilder,
            Object field,
            Path<T> path,
            CompareEnum compareEnum,
            String database) {
        if (Objects.nonNull(field)) {
            switch (compareEnum) {
                case EQUAL:
                    if (field instanceof String)
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(path, ((String) field).trim())));
                    else
                        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(path, field)));
                    break;
                case IN_NUMBER:
                    Stream<String> results = Arrays.stream(((String) field).split(","));
                    predicates.add(
                            criteriaBuilder.and(
                                    path.in(results.map(Integer::parseInt).collect(Collectors.toList()))));
                    break;
                case IN_STRING:
                    predicates.add(
                            criteriaBuilder.and(
                                    path.in(
                                            Arrays.stream(((String) field).split(",")).map(String::trim).collect(Collectors.toList()))));
                    break;
                case CONTAIN:
                    if (database == DB)
                        predicates.add(criteriaBuilder
                                .and(criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)),
                                        "%" + field.toString().toLowerCase(
                                                Locale.ROOT) + "%")));
                    else
                        predicates.add(
                                criteriaBuilder.and(criteriaBuilder.like(path.as(String.class), "%" + field + "%")));
                    break;
                case START_WITH:
                    predicates.add(
                            criteriaBuilder.and(criteriaBuilder.like(path.as(String.class), field + "%")));
                    break;
                default:
                    break;
                case NO_CONTAIN:
                    predicates.add(
                            criteriaBuilder.or(criteriaBuilder.notLike(path.as(String.class), "%" + field + "%")));
                    break;
                case LESS_THAN:
                    if (field instanceof Double)
                        predicates.add(criteriaBuilder.and(
                                criteriaBuilder.lessThan(path.as(Double.class), (Double) field)));
                    else if (field instanceof BigDecimal)
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .lessThan(path.as(BigDecimal.class), (BigDecimal) field)));
                    else if (field instanceof LocalDate)
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .lessThan(path.as(LocalDate.class), (LocalDate) field)));
                    else
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .lessThan(path.as(Date.class), (Date) field)));
                    break;
                case LESS_THAN_EQUAL:
                    if (field instanceof Double)
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .lessThanOrEqualTo(path.as(Double.class), (Double) field)));
                    else if (field instanceof BigDecimal)
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .lessThanOrEqualTo(path.as(BigDecimal.class), (BigDecimal) field)));
                    else if (field instanceof LocalDate)
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .lessThanOrEqualTo(path.as(LocalDate.class), (LocalDate) field)));
                    else
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .lessThanOrEqualTo(path.as(Date.class), (Date) field)));
                    break;
                case GREATER_THAN_EQUAL:
                    if (field instanceof Double)
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .greaterThanOrEqualTo(path.as(Double.class), (Double) field)));
                    else if (field instanceof BigDecimal)
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .greaterThanOrEqualTo(path.as(BigDecimal.class), (BigDecimal) field)));
                    else if (field instanceof LocalDate)
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .greaterThanOrEqualTo(path.as(LocalDate.class), (LocalDate) field)));
                    else
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .greaterThanOrEqualTo(path.as(Date.class), (Date) field)));
                    break;
                case GREATER_THAN:
                    if (field instanceof Double)
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .greaterThan(path.as(Double.class), (Double) field)));
                    else if (field instanceof BigDecimal)
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .greaterThan(path.as(BigDecimal.class), (BigDecimal) field)));
                    else if (field instanceof LocalDate)
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .greaterThan(path.as(LocalDate.class), (LocalDate) field)));
                    else
                        predicates.add(criteriaBuilder.and(criteriaBuilder
                                .greaterThan(path.as(Date.class), (Date) field)));
                    break;
                case JSONB_ARRAY:
                    Arrays.stream(((String) field).split(","))
                            .forEach(
                                    id ->
                                            predicates.add(
                                                    criteriaBuilder.and(
                                                            criteriaBuilder.like(path.as(String.class), "%" + id + "%"))));
                    break;
                case EQUAL_DECIMAL:
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(path, df.format(field))));
                    break;
                case IN_DECIMAL:
                    predicates.add(
                            criteriaBuilder.and(
                                    path.in(
                                            Arrays.stream(((String) field).split(","))
                                                    .map(Double::parseDouble)
                                                    .map(df::format)
                                                    .collect(Collectors.toList()))));
                    break;
            }
        }
    }
}
