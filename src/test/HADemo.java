package test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.uorm.dao.common.CommonDaoXmlExtImpl;
import org.uorm.dao.common.DatasourceConfig;
import org.uorm.dao.common.DefaultConnectionFactory;
import org.uorm.dao.common.ICommonDaoXmlExt;
import org.uorm.ha.FailoverHotSwapHAConnectionFactory;
import org.uorm.ha.HADataSourceDescriptor;

public class HADemo {

	public static void main(String[] args) throws InterruptedException {
		String driver = "com.mysql.jdbc.Driver";
		DatasourceConfig mainconfig = new DatasourceConfig();
		mainconfig.setDriverClass(driver);
		mainconfig.setJdbcUrl("jdbc:mysql://127.0.0.1/simdb");
		mainconfig.setUsername("root");
		mainconfig.setPassword("root");
		Map<String, String> poolPerperties = new HashMap<String, String>();
		poolPerperties.put("___POOL_TYPE_", "DBCP");
		poolPerperties.put("initialSize", "2");
		poolPerperties.put("maxActive", "10");
		poolPerperties.put("maxIdle", "5");
		poolPerperties.put("minIdle", "2");
		poolPerperties.put("maxWait", "60000");
		poolPerperties.put("connectionProperties", "useUnicode=true;characterEncoding=UTF8");
		mainconfig.setPoolPerperties(poolPerperties);
		
		DatasourceConfig standbyconfig = new DatasourceConfig();
		standbyconfig.setDriverClass(driver);
		standbyconfig.setJdbcUrl("jdbc:mysql://198.9.1.122/mgr");
		standbyconfig.setUsername("root");
		standbyconfig.setPassword("password");
		Map<String, String> spoolPerperties = new HashMap<String, String>();
		spoolPerperties.put("___POOL_TYPE_", "DBCP");
		spoolPerperties.put("initialSize", "2");
		spoolPerperties.put("maxActive", "10");
		spoolPerperties.put("maxIdle", "5");
		spoolPerperties.put("minIdle", "2");
		spoolPerperties.put("maxWait", "60000");
		spoolPerperties.put("connectionProperties", "useUnicode=true;characterEncoding=UTF8");
		standbyconfig.setPoolPerperties(spoolPerperties);
		
		HADataSourceDescriptor haDataSourceDescriptor = new HADataSourceDescriptor();
		haDataSourceDescriptor.setIdentity("ha");
		haDataSourceDescriptor.setMainDataSource(new DefaultConnectionFactory(mainconfig));
		haDataSourceDescriptor.setStandbyDataSource(new DefaultConnectionFactory(standbyconfig));
		
		FailoverHotSwapHAConnectionFactory connectionFactory = new FailoverHotSwapHAConnectionFactory(haDataSourceDescriptor);
		connectionFactory.setDetectingSQL("select 1");
		connectionFactory.setDetectingRequestTimeout(15000);
		connectionFactory.setMonitorPeriod(15000);
		connectionFactory.setRecheckInterval(5000);
		connectionFactory.setRecheckTimes(3);
		connectionFactory.init();
		ICommonDaoXmlExt dao = new CommonDaoXmlExtImpl(connectionFactory);
		while (true) {
			try {
				System.out.println(dao.fillJson("select * from UUM_USER", 0, 20));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Thread.sleep(1000);
		}
		
	}

}
