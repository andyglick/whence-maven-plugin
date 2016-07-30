package com.atlassian.maven.whence.testing.api;

import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleParser;
import com.sun.syndication.io.impl.DCModuleParser;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;
import org.jdom.JDOMFactory;

@SuppressWarnings("unused")
public class Api {

    class QEntity extends RelationalPathBase<QEntity> {

        StringPath NAME = createString("NAME");
        StringPath TITLE = createString("TITLE");

        QEntity() {
            super(QEntity.class, "entity", null, null);
        }
    }

    public void apiMethod1() {
        PostgreSQLTemplates templates = PostgreSQLTemplates.DEFAULT;

        Configuration configuration = new Configuration(templates);

        SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, () -> {
            throw new UnsupportedOperationException("Not implemented");
        });

        QEntity TABLE = new QEntity();

        queryFactory.select(TABLE.NAME, TABLE.TITLE)
                .from(TABLE).fetch();


        ModuleParser moduleParser = new DCModuleParser();
        JDOMFactory jdomFactory = new DefaultJDOMFactory();
        Element element = jdomFactory.element("e");
        Module parse = moduleParser.parse(element);
    }
}
