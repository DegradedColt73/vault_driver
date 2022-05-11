package vault;

import crypt.SQLiteHandlerResponse;
import exceptions.EmptyResponseException;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLiteHandler {
    final private String SQLITE_PREFIX = "jdbc:sqlite:";
    final private String DB_INITIAL_STATEMENT_1 = "CREATE TABLE IF NOT EXISTS entries (\n"
            + " id INTEGER PRIMARY KEY,\n"
            + " iv STRING NOT NULL,\n"
            + " name TEXT NOT NULL,\n"
            + " data TEXT\n"
            + ");";
    final private String DB_INITIAL_STATEMENT_2 = "CREATE TABLE IF NOT EXISTS config (\n"
            + " id TEXT PRIMARY KEY,\n"
            + " checkMessage TEXT NOT NULL,\n"
            + " iv TEXT NOT NULL,\n"
            + " vk TEXT NOT NULL\n"
            + ");";
    private Connection connection;
    private String databaseId;
    private String databaseUrl;
    private Statement statement;

    public SQLiteHandler(){
        this.connection = null;
        this.databaseUrl = null;
        this.statement = null;
        this.databaseId = null;
    }

    public void initializeNewDatabase(String databaseId, String databaseUrl, String iv, String checkMessage, String vk) throws SQLException {
        this.databaseId = databaseId;
        String configStatement = "INSERT INTO config (id, checkMessage, iv, vk)"
                + "VALUES ('"
                + this.databaseId + "', '"
                + checkMessage + "', '"
                + iv + "', '"
                + vk + "')";
        this.databaseUrl = databaseUrl;
        this.connection = DriverManager.getConnection(this.SQLITE_PREFIX + this.databaseUrl);
        this.statement = this.connection.createStatement();
        this.statement.execute(this.DB_INITIAL_STATEMENT_1);
        this.statement.execute(this.DB_INITIAL_STATEMENT_2);
        this.statement.execute(configStatement);
    }

    public void connectToDatabase(String databaseUrl) throws VaultDoesNotExistException, SQLException, EmptyResponseException {
        this.databaseUrl = databaseUrl;
        File file = new File(this.databaseUrl);
        if(!file.exists() || file.isDirectory()){
            throw new VaultDoesNotExistException();
        }
        this.connection = DriverManager.getConnection(this.SQLITE_PREFIX + this.databaseUrl);
        this.statement = this.connection.createStatement();
        this.databaseId = this.retrieveId();
    }

    public void updateConfig(String checkMessage, String vk) throws SQLException {
        String configStatement = "UPDATE config SET "
                + "checkMessage = '" + checkMessage + "',"
                + "vk = '" + vk +"'"
                + "WHERE id = '" + this.databaseUrl + "';";
        this.statement.execute(configStatement);
    }

    public String retrieveId() throws SQLException, EmptyResponseException {
        String selectStatement = "SELECT id FROM config;";
        ResultSet resultSet = statement.executeQuery(selectStatement);
        if(!resultSet.next()) throw new EmptyResponseException();
        return resultSet.getString("id");
    }

    public String retrieveIv() throws SQLException, EmptyResponseException {
        String selectStatement = "SELECT iv FROM config;";
        ResultSet resultSet = this.statement.executeQuery(selectStatement);
        if(!resultSet.next()) throw new EmptyResponseException();
        return resultSet.getString("iv");
    }

    public String retrieveCheckMessage() throws SQLException, EmptyResponseException {
        String selectStatement = "SELECT checkMessage FROM config;";
        ResultSet resultSet = statement.executeQuery(selectStatement);
        if(!resultSet.next()) throw new EmptyResponseException();
        return resultSet.getString("checkMessage");
    }

    public String retrieveKey() throws SQLException, EmptyResponseException {
        String selectStatement = "SELECT vk FROM config;";
        ResultSet resultSet = statement.executeQuery(selectStatement);
        if(!resultSet.next()) throw new EmptyResponseException();
        return resultSet.getString("vk");
    }

    public Map<Integer, SQLiteHandlerResponse> getEntityNames() throws SQLException {
        Map<Integer, SQLiteHandlerResponse> map = new HashMap<Integer, SQLiteHandlerResponse>();
        String selectStatement = "SELECT id, iv, name FROM entries";
        ResultSet resultSet = statement.executeQuery(selectStatement);
        SQLiteHandlerResponse sqLiteHandlerResponse = null;
        while(resultSet.next()){
            sqLiteHandlerResponse = new SQLiteHandlerResponse();
            sqLiteHandlerResponse.setResponse(resultSet.getString("name"));
            sqLiteHandlerResponse.setIv(resultSet.getString("iv"));
            map.put(resultSet.getInt("id"), sqLiteHandlerResponse);
        }
        return map;
    }

    public void insertNewEntity(String iv, String name, String data) throws SQLException {
        String insertStatement = "INSERT INTO entries (iv, name, data)"
                + "VALUES ('"
                + iv + "', '"
                + name + "', '"
                + data + "')";
        this.statement.execute(insertStatement);
    }

    public String getEntityIv(int id) throws SQLException, EmptyResponseException {
        String selectStatement = "SELECT iv FROM entries WHERE id=" + id;
        ResultSet resultSet = statement.executeQuery(selectStatement);
        if(!resultSet.next()) throw new EmptyResponseException();
        return resultSet.getString("iv");

    }

    public String getEntityName(int id) throws SQLException, EmptyResponseException {
        String selectStatement = "SELECT name FROM entries WHERE id=" + id;
        ResultSet resultSet = statement.executeQuery(selectStatement);
        if(!resultSet.next()) throw new EmptyResponseException();
        return resultSet.getString("name");
    }

    public String getEntityData(int id) throws SQLException, EmptyResponseException {
        String selectStatement = "SELECT data FROM entries WHERE id =" + id;
        ResultSet resultSet = statement.executeQuery(selectStatement);
        if(!resultSet.next()) throw new EmptyResponseException();
        return resultSet.getString("data");
    }

    public void updateEntityName(int id, String newName) throws SQLException {
        String updateStatement = "UPDATE entries SET name = '"
                + newName + "' WHERE id=" + id;
        this.statement.execute(updateStatement);
    }

    public void updateEntityData(int id, String newData) throws SQLException {
        String updateStatement = "UPDATE entries SET data = '"
                + newData + "' WHERE id=" + id;
        this.statement.execute(updateStatement);
    }

    public void deleteEntity(int id) throws SQLException {
        String deleteStatement = "DELETE FROM entries WHERE id=" + id;
        this.statement.execute(deleteStatement);
    }
}
