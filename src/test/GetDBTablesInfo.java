package test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class GetDBTablesInfo {

	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/jnmp_bj?user=root&password=root";
		Connection conn = DriverManager.getConnection(url);

		DatabaseMetaData databaseMetaData = conn.getMetaData();
		// 获取所有表
		ResultSet tableSet = databaseMetaData.getTables(null, "%", "device_model",
				new String[] { "TABLE" });
		while (tableSet.next()) {
			String tableName = tableSet.getString("TABLE_NAME");
			String tableComment = tableSet.getString("REMARKS");
			System.out.println("tableName=" + tableName);
			System.out.println("tableComment=" + tableComment);

			// 获取tableName表列信息
			ResultSet columnSet = databaseMetaData.getColumns(null, "%",
					tableName, "%");
			while (columnSet.next()) {
				String columnName = columnSet.getString("COLUMN_NAME");
				String columnComment = columnSet.getString("REMARKS");
				String sqlType = columnSet.getString("DATA_TYPE");
				System.out.println(columnName);
				System.out.println(columnComment);
				System.out.println(sqlType);
			}
		}
	}

}
