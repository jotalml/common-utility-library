package com.jotalml.commonutilitylibrary.model.dao.impl;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Component
public class SQLUtils {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateById(String table, String columnCondition, String column, Object value, Object condition) {
        String sql;
        if (value instanceof String)
            sql = "UPDATE " + table + " set " + column + " = " + "'" + value + "'" + " where " + columnCondition + " = " + condition;
        else
            sql = "UPDATE " + table + " set " + column + " = " + value + " where " + columnCondition + " = " + condition;
        Query query = entityManager.createNativeQuery(sql);
        query.executeUpdate();
    }
}
