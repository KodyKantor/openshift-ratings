package com.ratings.kkantor.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;


public abstract class DatabaseService {
    private static final String dbUrl = System.getenv("OPENSHIFT_MYSQL_DB_URL") != null ?
            System.getenv("OPENSHIFT_MYSQL_DB_URL") : "mysql://localhost:3306/ratings?user=root&password=root";
    private static final Logger logger = LogManager.getLogger(DatabaseService.class);
    private static SessionFactory sessionFactory;

    private static void createSessionFactory() {
        Configuration configuration = new Configuration().configure();
        configuration.setProperty("hibernate.connection.url", "jdbc:" + dbUrl);
        logger.debug("Set hibernate url property");
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(builder.build());
        logger.debug("Created hibernate session factory");
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            logger.debug("Initializing session factory...");
            createSessionFactory();
        }
        return sessionFactory;
    }
}