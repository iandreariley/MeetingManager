package meetingmanager.model;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Date;

import meetingmanager.exception.EntityNotFoundException;
import meetingmanager.exception.MissingPrimaryKeyException;

public abstract class DatabaseConnection<T> {

	public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	public static final String USER = "test";
	public static final String DATABASE = "test";
	public static final String HOST = "localhost";
	public static final String SETTINGS = "&allowMultiQueries=true&useSSL=false";
	protected static final String LINE_SEP = ", ";
        protected static final String AND = " AND ";
	
	public static final void registerSQLDriver() throws ClassNotFoundException {
		Class.forName(MYSQL_DRIVER);
	}
	
	public DatabaseConnection() throws SQLException {
		init();
	}
	
	protected abstract void init() throws SQLException;
	
	protected static Connection connect() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://" + HOST + "/" + DATABASE + "?user=" + USER + SETTINGS);
	}
	
	protected void updateDatabase(String modStatement) throws SQLException {
		Connection connection = connect();
		Statement statement = connection.createStatement();
		statement.executeUpdate(modStatement);
		connection.close();
	}
	
	protected List<T> queryDatabase(String queryStatement) throws SQLException {
		Connection connection = connect();
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(queryStatement);
		return toObject(rs);
	}
	
	protected abstract List<T> toObject(ResultSet rs) throws SQLException;
	
	protected abstract void checkPrimaryKey(T obj) throws MissingPrimaryKeyException;
	
	protected String keyValue(String key, String value) {
            return key + "=" + stringify(value);
	}
	
	protected String keyValue(String key, Boolean value) {
            return key + "=" + value.toString();
	}
	
	protected String keyValue(String key, Integer value) {
            return key + "=" + value.toString();
	}
        
        protected String keyValue(String key, Date date) {
            return key + "=" + date.getTime();
        }
        
        protected String columnEquals(String col1, String col2) {
            return col1 + "=" + col2;
        }
	
        protected String stringify(Date date) {
            return stringify(date.toString());
        }
        
	protected String stringify(String value) {
            return "'" + value + "'";
	}
	
	protected void checkResultsNotEmpty(List<T> results) throws EntityNotFoundException {
		if(results.isEmpty())
			throw new EntityNotFoundException();
	}
}
