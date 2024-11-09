module org.loudsheep.psio_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.loudsheep.psio_project to javafx.fxml;
    exports org.loudsheep.psio_project;
}