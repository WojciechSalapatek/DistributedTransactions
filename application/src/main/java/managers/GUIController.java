package managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import managers.scenarios.correct.CorrectDatabasesAndFilesScenario;
import managers.scenarios.correct.SingleDatabaseScenario;
import managers.scenarios.fatal.ErrorDuringRollbackingScenario;
import managers.scenarios.rollbacks.DatabasesAndFilesInvalidQueryScenario;
import managers.scenarios.rollbacks.SingleDatabaseInvalidQuery;

import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GUIController {

    @FXML
    private TableView table1;

    @FXML
    private TableColumn col11;

    @FXML
    private TableColumn col12;

    @FXML
    private TableView table2;

    @FXML
    private TableColumn col21;

    @FXML
    private TableColumn col22;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final CorrectDatabasesAndFilesScenario correctDatabasesAndFilesScenario = new CorrectDatabasesAndFilesScenario();
    private final SingleDatabaseScenario singleDatabaseScenario = new SingleDatabaseScenario();
    private final ErrorDuringRollbackingScenario errorDuringRollbackingScenario = new ErrorDuringRollbackingScenario();
    private final SingleDatabaseInvalidQuery singleDatabaseInvalidQuery = new SingleDatabaseInvalidQuery();
    private final DatabasesAndFilesInvalidQueryScenario databasesAndFilesInvalidQueryScenario = new DatabasesAndFilesInvalidQueryScenario();

    private final String url1 = "jdbc:postgresql://localhost:5432/dp";
    private final String url2 = "jdbc:postgresql://localhost:5432/dp2";
    private final String user = "postgres";
    private final String password = "postgres";

    @FXML
    protected void runStrategy1() {
        correctDatabasesAndFilesScenario.execute();
    }

    @FXML
    protected void runStrategy2() {
        singleDatabaseScenario.execute();
    }

    @FXML
    protected void runStrategy3() {
        errorDuringRollbackingScenario.execute();
    }

    @FXML
    protected void runStrategy4() {
        singleDatabaseInvalidQuery.execute();
    }

    @FXML
    protected void runStrategy5() {
        databasesAndFilesInvalidQueryScenario.execute();
    }

    public void init() {

        col11.setCellValueFactory(
                new PropertyValueFactory<managers.scenarios.TableView, String>("col1")
        );
        col12.setCellValueFactory(
                new PropertyValueFactory<managers.scenarios.TableView, String>("col2")
        );

        col21.setCellValueFactory(
                new PropertyValueFactory<managers.scenarios.TableView, String>("col1")
        );
        col22.setCellValueFactory(
                new PropertyValueFactory<managers.scenarios.TableView, String>("col2")
        );

        Runnable runnable = () -> {
            scheduler.scheduleAtFixedRate(() -> {
                table1.setItems(getFromDatabase(url1));
                table2.setItems(getFromDatabase(url2));
                table1.refresh();
                table2.refresh();
            }, 8, 8, TimeUnit.MILLISECONDS);
        };
        runnable.run();

    }

    private ObservableList<managers.scenarios.TableView> getFromDatabase(String url) {
        Statement stmt = null;
        ObservableList<managers.scenarios.TableView> data = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(url, user, password);){
            Class.forName("org.postgresql.Driver");
            stmt = conn.createStatement();
            String sql = "SELECT col1, col2 FROM test_table";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String col1 = rs.getString("col1");
                String col2 = rs.getString("col2");

                data.add(new managers.scenarios.TableView(col1, col2));
            }
            rs.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

}
