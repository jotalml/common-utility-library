package com.jotalml.commonutilitylibrary.model.dao.impl;

import com.jotalml.commonutilitylibrary.enums.CompareEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class CriteriaUtil {

    private final MongoTemplate mongoTemplate;

    public void addCriteria(List<Criteria> criteria, String field, Object value, CompareEnum compare) {
        if (Objects.nonNull(value)) {
            switch (compare) {
                case EQUAL:
                    if (value instanceof String)
                        criteria.add(Criteria.where(field).is(String.valueOf(value).trim()));
                    else
                        criteria.add(Criteria.where(field).is(value));
                    break;
                case IN_NUMBER:
                    Stream<String> results = Arrays.stream(((String) value).split(","));
                    criteria.add(Criteria.where(field).in(results.map(Integer::parseInt).collect(Collectors.toList())));
                    break;
                case IN_STRING:
                    criteria.add(Criteria.where(field).in(
                            Arrays.stream((String.valueOf(value)).split(",")).map(String::trim).collect(Collectors.toList())));
                    break;
                case NOT_EQUAL:
                    criteria.add(Criteria.where(field).ne(value));
                    break;
                case LESS_THAN:
                    criteria.add(Criteria.where(field).lt(value));
                    break;
                case GREATER_THAN:
                    criteria.add(Criteria.where(field).gt(value));
                    break;
                case LESS_THAN_EQUAL:
                    criteria.add(Criteria.where(field).lte(value));
                    break;
                case GREATER_THAN_EQUAL:
                    criteria.add(Criteria.where(field).gte(value));
                    break;
                case EXIST:
                    criteria.add(Criteria.where(field).exists((Boolean) value));
                    break;
            }
        }
    }

    /**
     * Add a new criteria to the Query
     *
     * @param query    MongoDB Query
     * @param criteria Central class for creating queries
     */
    public static void addCriteriaQuery(Query query, List<Criteria> criteria) {
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
    }


    /**
     * Generic method to query in Mongodb
     *
     * @param query    MongoDB Query object
     * @param pageable Abstract interface for pagination information
     * @param clazz    Query class
     * @return Page<?>
     */
    public Page<?> query(Query query, Pageable pageable, Class clazz) {
        query.with(pageable)
                .skip((long) pageable.getPageSize() * pageable.getPageNumber())
                .limit(pageable.getPageSize());

        List<?> result = mongoTemplate.find(query, clazz);
        long count = mongoTemplate.count(query.skip(-1).limit(-1), clazz);
        return new PageImpl<>(result, pageable, count);
    }
}
