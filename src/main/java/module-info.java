module com.system.price_tracker {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.mail;
    requires java.sql;
    requires org.controlsfx.controls;
    requires jbcrypt;
    requires opencsv;
    requires org.apache.commons.text;
    requires mapbox.sdk.geojson;
    requires mapbox.sdk.services;
    requires retrofit2;


    opens com.system.price_tracker to javafx.fxml;
    exports com.system.price_tracker;
}