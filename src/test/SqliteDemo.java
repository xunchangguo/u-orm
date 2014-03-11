package test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uorm.dao.common.CommonDaoFactory;
import org.uorm.dao.common.DatasourceConfig;
import org.uorm.dao.common.ICommonDaoXmlExt;

public class SqliteDemo {

	public static void main(String[] args) throws Exception {
		String driver = "org.sqlite.JDBC";//"oracle.jdbc.driver.OracleDriver";//"com.mysql.jdbc.Driver";//"com.microsoft.sqlserver.jdbc.SQLServerDriver";//
		/**连接字串 */
//		String url = "jdbc:sqlite:F:/tmp/sample.db";//"jdbc:oracle:thin:@192.168.1.91:1521:jdvncctv7";//"jdbc:mysql://127.0.0.1/simdb";//"jdbc:oracle:thin:@127.0.0.1:1521:orcl";//"jdbc:sqlserver://192.168.213.220:1433;DatabaseName=CastMain";//
		String url = "jdbc:sqlite:C:/Users/guoxunchang/Desktop/Avid Projects/test/SearchData/_SearchDB_";
		/** 数据库用户名 */
		String username = "";//"jmtc1";//"root";//"cctv7";//"cctv";//"root";
		/** 数据库密码 */
		String password = "";//"password";//"root";//"password";//"1";//"root";
		long start = System.currentTimeMillis();
		DatasourceConfig config = new DatasourceConfig();
//		config.setDatabasetype(DataBaseType.MYSQL);
		config.setDriverClass(driver);
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		Map<String, String> poolPerperties = new HashMap<String, String>();
		//c3p0
//		poolPerperties.put("___POOL_TYPE_", "c3p0");
//		poolPerperties.put("initialPoolSize", "2");//initialPoolSize
//		poolPerperties.put("maxIdleTime", "600");
//		poolPerperties.put("idleConnectionTestPeriod", "600");
		//DBCP
//		poolPerperties.put("___POOL_TYPE_", "DBCP");
//		poolPerperties.put("initialSize", "2");
//		poolPerperties.put("maxActive", "10");
//		poolPerperties.put("maxIdle", "5");
//		poolPerperties.put("minIdle", "2");
//		poolPerperties.put("maxWait", "60000");
//		poolPerperties.put("connectionProperties", "useUnicode=true;characterEncoding=UTF8");
		//jdbc-pool jdbc-pool连接池有点问题，不太稳定，不建议使用
//		poolPerperties.put("___POOL_TYPE_", "jdbc-pool");
//		poolPerperties.put("initialSize", "2");
//		poolPerperties.put("maxActive", "10");
//		poolPerperties.put("maxIdle", "5");
//		poolPerperties.put("minIdle", "2");
//		poolPerperties.put("maxWait", "60000");
//		poolPerperties.put("connectionProperties", "useUnicode=true;characterEncoding=UTF8");
		//druid 
		poolPerperties.put("___POOL_TYPE_", "druid");
		poolPerperties.put("initialSize", "2");
		poolPerperties.put("maxActive", "10");
		poolPerperties.put("minIdle", "2");
		poolPerperties.put("maxWait", "60000");
		config.setPoolPerperties(poolPerperties);
//		ConnectionFactory connectionFactory = new DefaultConnectionFactory(config);
//		ICommonDao dao = new CommonDaoImpl(connectionFactory );
//		ICommonDao dao = CommonDaoFactory.createOneThreadMultiCommonDao(config, true);//CommonDaoFactory.createCommonDao(config, true);
		ICommonDaoXmlExt dao = CommonDaoFactory.createCommonDaoXmlExt(config);
//		String[] sqls = new String[3];
//		sqls[0] = "create table person (id integer, name string)";
//		sqls[1] = "insert into person values(1, 'leo')";
//		sqls[2] = "insert into person values(2, 'yui')";
//		dao.batchUpdate(sqls);
//		List<Map<String, Object>> persons = dao.queryForListMap("select * from person");
//		for (Map<String, Object> map : persons) {
//			System.out.println(map);
//		}
		List<Mob> mobs = dao.queryBusinessObjs(Mob.class, "SELECT * FROM MOB", 2, 2);
		for (Mob mob : mobs) {
			System.out.println(mob.getName());
		}
		System.out.println("耗时：" + (System.currentTimeMillis() - start) + " ms");
	}

}
