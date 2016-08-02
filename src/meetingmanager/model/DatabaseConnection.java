package meetingmanager.model;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DatabaseConnection {

	public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	public static final String USER = "test";
	public static final String DATABASE = "test";
	public static final String HOST = "localhost";
	
	public static final void registerSQLDriver() throws ClassNotFoundException {
		Class.forName(MYSQL_DRIVER);
	}
	
	public Connection connect() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://" + HOST + "/" + DATABASE + "?user=" + USER);
	}
	
	public void change(String modStatement) throws SQLException {
		Connection connection = connect();
		Statement statement = connection.createStatement();
		statement.executeUpdate(modStatement);
		connection.close();
	}
}
