module sn.clinique.sgcm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    requires static lombok;
    requires jbcrypt;


    requires java.sql;
    requires java.naming;
    requires java.xml;

    opens sn.clinique.sgcm to javafx.fxml, javafx.graphics;
    opens sn.clinique.sgcm.controller to javafx.fxml;
    opens sn.clinique.sgcm.model to javafx.fxml, org.hibernate.orm.core;
    opens sn.clinique.sgcm.enums to javafx.fxml, org.hibernate.orm.core;
    opens sn.clinique.sgcm.util.configs to javafx.fxml;
    opens sn.clinique.sgcm.util.security to javafx.fxml;

    exports sn.clinique.sgcm;
    exports sn.clinique.sgcm.controller;
    exports sn.clinique.sgcm.model;
    exports sn.clinique.sgcm.enums;
    exports sn.clinique.sgcm.service;
    exports sn.clinique.sgcm.repository;
    exports sn.clinique.sgcm.util.configs;
    exports sn.clinique.sgcm.util.security;
}