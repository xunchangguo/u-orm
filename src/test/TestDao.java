/**
 * Copyright 2010-2016 the original author or authors.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.uorm.dao.common.CommonDaoFactory;
import org.uorm.dao.common.CommonDaoImpl;
import org.uorm.dao.common.CommonDaoXmlExtImpl;
import org.uorm.dao.common.ConnectionFactory;
import org.uorm.dao.common.DatasourceConfig;
import org.uorm.dao.common.DefaultConnectionFactory;
import org.uorm.dao.common.ICommonDao;
import org.uorm.dao.common.ICommonDaoXmlExt;
import org.uorm.dao.common.MapResultSetExtractor;
import org.uorm.dao.common.OneThreadMultiConnectionCommonDaoImpl;
import org.uorm.dao.common.PaginationSupport;
import org.uorm.dao.common.SqlParameter;
import org.uorm.utils.Utils;

/**
 *
 * @author <a href="mailto:xunchangguo@gmail.com">郭训常</a>
 * @version 1.0.0
 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝<br/>
 * 修订日期                 修订人            描述<br/>
 * 2012-1-21       郭训常            创建<br/>
 */
public class TestDao {

	public static void main(String[] args) throws Exception {
		String driver = "com.mysql.jdbc.Driver";//"com.ibm.db2.jcc.DB2Driver";//"org.mariadb.jdbc.Driver";//"oracle.jdbc.driver.OracleDriver";//"sun.jdbc.odbc.JdbcOdbcDriver";//"oracle.jdbc.driver.OracleDriver";//"com.microsoft.sqlserver.jdbc.SQLServerDriver";//
		/**连接字串 */
		String url = "jdbc:mysql://127.0.0.1/jnmp_bj";//"jdbc:db2://198.9.1.130:50000/JUMMII";//"jdbc:mariadb://127.0.0.1:3308/jnmp_bj";//"jdbc:oracle:thin:@198.9.1.122:1521:orcl";//"jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=C:\\Users\\guoxunchang\\Desktop\\YJDBNew.mdb";//"jdbc:oracle:thin:@192.168.1.91:1521:jdvncctv7";//"jdbc:oracle:thin:@127.0.0.1:1521:orcl";//"jdbc:sqlserver://192.168.213.220:1433;DatabaseName=CastMain";//
		/** 数据库用户名 */
		String username = "root";//"db2inst1";//"root";//"GBMadmin";//"";//"jmtc1";//"cctv7";//"cctv";//"root";
		/** 数据库密码 */
		String password = "root";//"password";//"root";//"GBMadmin1";//"";//"password";//"password";//"1";//"root";
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
		poolPerperties.put("___POOL_TYPE_", "DBCP");
		poolPerperties.put("initialSize", "2");
		poolPerperties.put("maxActive", "10");
		poolPerperties.put("maxIdle", "5");
		poolPerperties.put("minIdle", "2");
		poolPerperties.put("maxWait", "60000");
		poolPerperties.put("connectionProperties", "useUnicode=true;characterEncoding=UTF8");
//		poolPerperties.put("charSet", "GBK");
		//jdbc-pool jdbc-pool连接池有点问题，不太稳定，不建议使用
//		poolPerperties.put("___POOL_TYPE_", "jdbc-pool");
//		poolPerperties.put("initialSize", "2");
//		poolPerperties.put("maxActive", "10");
//		poolPerperties.put("maxIdle", "5");
//		poolPerperties.put("minIdle", "2");
//		poolPerperties.put("maxWait", "60000");
//		poolPerperties.put("connectionProperties", "useUnicode=true;characterEncoding=UTF8");
		//druid 
//		poolPerperties.put("___POOL_TYPE_", "druid");
//		poolPerperties.put("initialSize", "2");
//		poolPerperties.put("maxActive", "10");
//		poolPerperties.put("minIdle", "2");
//		poolPerperties.put("maxWait", "60000");
//		config.setPoolPerperties(poolPerperties);
		//TODO
//		ConnectionFactory connectionFactory = new DefaultConnectionFactory(config);
//		ICommonDao dao = new CommonDaoImpl(connectionFactory );
//		ICommonDao dao = CommonDaoFactory.createOneThreadMultiCommonDao(config, true);//CommonDaoFactory.createCommonDao(config, true);
		ICommonDaoXmlExt dao = CommonDaoFactory.createCommonDaoXmlExt(config);
		
//		dao.beginTransation();
//		User user = dao.queryBusinessObjByPk(User.class, 2);
//		System.out.println(user.getLoginName());
//		System.out.println("耗时：" + (System.currentTimeMillis() - start) + " ms");
		start = System.currentTimeMillis();
//		List<String> unamses = dao.queryBusinessObjs(String.class, "SELECT USER_NAME FROM UUM_USER");
//		for (String unamse : unamses) {
//			System.out.println(unamse);
//		}
//		List<User> users = dao.queryBusinessObjs(User.class, "select t.*, 'test' from UUM_USER t");
//		System.out.println(users.size() + "," + users.get(0).getLoginName());
//		Long count = dao.querySingleObject(Long.class, "select count(0) from UUM_USER");
//		System.out.println(count);
//		List<Map<String, Object>> userListMap = dao.queryForListMap("select a.* from UUM_USER t, ns_info a where t.id = a.id and t.id=? and a.namespace=?", new SqlParameter(User.PROP_ID, 503), new SqlParameter("a.namespace", "\"/root/ibm\""));
//		for (Map<String, Object> map : userListMap) {
//			for (String key : map.keySet()) {
//				System.out.println(key + " = " + map.get(key));
//			}
//			System.out.println("----------------------");
//		}
//		Map<String, Object> u = dao.queryForMap("select * from UUM_USER");
//		for (String key : u.keySet()) {
//			System.out.println(key + " = " + u.get(key));
//		}
//		User uu = dao.querySingleObject(User.class, "select * from UUM_USER");
//		System.out.println(uu.getLoginName());
//		users = dao.queryBusinessObjs(User.class, "select * from UUM_USER where ID = ?", 0, 102, new SqlParameter(User.PROP_ID, 100));
//		users = dao.queryBusinessObjs(User.class, "select * from UUM_USER", 0, 102);
//		System.out.println("===============================");
//		System.out.println(users.size());
//		for(User ue : users){
//			ue.setState(2000.6D);
//			ue.setEmail("xunchangguo@gmail.com");
//			ue.setBirthday(new java.sql.Date(System.currentTimeMillis()));
//			System.out.println(ue.getLoginName());
//		}
//		System.out.println("update : " + dao.updateBusinessObjs(false, users));
//		PaginationSupport<User> upage = dao.queryByPagedQuery(User.class, "select * from UUM_USER where ID = ?", 0, 10, new SqlParameter(User.PROP_ID, 100));
//		PaginationSupport<User> upage = dao.queryByPagedQuery(User.class, "select * from UUM_USER", 1, 10);
//		PaginationSupport<User> upage = dao.queryByPagedQuery(User.class, "select * from (select * from UUM_USER order by id desc) A order by state", 1, 10);
//		
//		System.out.println("*************************************");
//		System.out.println("total = " + upage.getTotalCount() + ", page count = " + upage.getPageCount());
//		users = upage.getItems();
//		System.out.println(users.size());
//		for(User ue : users){
//			System.out.println(ue.getPassword());
//		}
//		System.out.println("---------- delete -------------------");
//		System.out.println(dao.deleteBusiness(users.get(0)));
//		System.out.println("---------- update -------------------");
//		user.setUserName(user.getLoginName());
//		user.setPassword("123456");
//		user.setBirthday(new Date());
//		System.out.println(dao.updateBusinessObjs(false, user));
//		System.out.println("---------- save -------------------");
		List<User> userss = new ArrayList<User>();
		for(int i = 0; i < 10; i ++){
			User uus = new User();//new User(i);
			uus.setLoginName("o-loginname-"+Utils.genRandomNum(6));
			uus.setUserName("o-着急啊看到username-"+ Utils.genRandomNum(6));
			uus.setPassword("o-着急啊看到password-"+i);
			uus.setDescription("o-着急啊看到desc-"+i);
			uus.setState(Double.valueOf(i));
			uus.setCreateTime(new Timestamp(System.currentTimeMillis()));
			uus.setUserCard("usercard-"+i);
			uus.setUserType(i);
			uus.setEmail("email-"+i);
			uus.setSex(i);
			uus.setAddress("address-"+i);
			uus.setBirthday(new java.sql.Date(System.currentTimeMillis()));
			userss.add(uus);
		}
//		System.out.println(dao.saveBusinessObjsCol(userss));
//		List<Map<String, Object>> models = new ArrayList<Map<String,Object>>();
//		for(int i = 0; i < 10; i ++){
//			Map<String, Object> model = new HashMap<String, Object>();
//			model.put(User.PROP_LOGIN_NAME, "loginname-"+Utils.genRandomNum(6));
//			model.put(User.PROP_USER_NAME, "着急啊看到username-"+ Utils.genRandomNum(6));
//			model.put(User.PROP_PASSWORD, "着急啊看到password-"+i);
//			model.put(User.PROP_DESCRIPTION, "着急啊看到desc-"+i);
//			model.put(User.PROP_STATE, Double.valueOf(i));
//			model.put(User.PROP_CREATE_TIME, new Timestamp(System.currentTimeMillis()));
//			model.put(User.PROP_USER_CARD, "usercard-"+i);
//			model.put(User.PROP_USER_TYPE, i);
//			model.put(User.PROP_EMAIL, "email-"+i);
//			model.put(User.PROP_SEX, i);
//			model.put(User.PROP_ADDRESS, "address-"+i);
//			model.put(User.PROP_BIRTHDAY, new Date());
//			models.add(model);
//		}
//		dao.saveModelData(User.class, models);
//		System.out.println(models.get(0).get(User.PROP_ID));
//		dao.commitTransation();
		
		/**test id generator*/
//		User us =  new User();
//		us.setLoginName("loginname-id");
//		us.setUserName("username-id");
//		us.setPassword("password-id");
//		us.setDescription("desc-id");
//		us.setState(1);
//		us.setCreateTime(new Date());
//		us.setUserCard("usercard-id");
//		us.setUserType(1);
//		us.setEmail("email-id");
//		us.setSex(1);
//		us.setAddress("address-id");
//		us.setBirthday(new Date());
//		System.out.println(dao.saveBusinessObjs(us));
//		System.out.println(us.getId());
//		TestUuid test = new TestUuid();
//		test.setSeqNum(10);
//		System.out.println(dao.saveBusinessObjs(test));
//		System.out.println(test.getId());
		
		/**test lob*/
//		CimData cimdata = dao.queryBusinessObjByPk(CimData.class, 2, 1);
//		System.out.println(cimdata.getCimClassId());
//		System.out.println(new String(cimdata.getCimOp()));
//		System.out.println(new String(cimdata.getCimInst()));
//		ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(cimdata.getCimInst()));
//		CimData cd = (CimData)oi.readObject();
//		System.out.println(cd.getCimClassId());
//		System.out.println(new String(cd.getCimOp()));
//		System.out.println(new String(cd.getCimInst()));
		
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ObjectOutputStream oos = new ObjectOutputStream(baos);
//		oos.writeObject(cimdata);
//		oos.flush();
//		baos.flush();
//		byte[] bytes = baos.toByteArray();
//		baos.close();
//		oos.close();
//		cimdata.setCimInst(bytes);
//		System.out.println( dao.updateBusinessObjs(false, cimdata) );
//		NewsStoryclip storyclip = new NewsStoryclip();
//		storyclip.setIconFile("ay绿卡就12323adfadf".getBytes());
//		storyclip.setClipUid("dd");
//		storyclip.setStoryId(1);
//		storyclip.setClipType(0);
//		storyclip.setDuration(0);
//		storyclip.setCreateUserid(0);
//		storyclip.setCreateTime(new Date());
//		System.out.println(dao.saveBusinessObjs(storyclip));
//		List<String> names = dao.queryBusinessObjs(String.class, "SELECT Name from LedGroup");
//		System.out.println(names);
//		Document doc = dao.fillByPagedQuery("select * from UUM_USER","root","item", 0, 20);
//		Document doc = dao.fillByPagedQuery("SELECT * from LedGroup", 0, 20);
//		Document doc = dao.fill("select * from LedGroup", 0, 20);
//		System.out.println(doc.asXML());
//		doc = dao.fillByPagedQuery("select count(0) from UUM_USER", "select * from UUM_USER","root","item", 0, 20);
//		System.out.println(doc.asXML());
//		System.out.println(dao.fillJson("SELECT * from LedGroup"));
//		System.out.println(dao.querySingleObject(String.class, "select newid()"));
//		System.out.println(dao.fillJson("select * from UUM_USER where id < 10"));
//		System.out.println(dao.fillJson("select * from UUM_USER", 0, 20));
//		System.out.println(dao.fillJsonByPagedQuery("select * from UUM_USER", 1, 20));
//		System.out.println(dao.fillByPagedQuery("SELECT * FROM BMP_KPI", 0, 20).asXML());
//		System.out.println(dao.fillJsonByPagedQuery("select * from BMP_KPI", 3, 20));
		
//		Map<Long, Object[]> result = dao.query("SELECT ID, USER_NAME, LOGIN_NAME FROM UUM_USER LIMIT 20", new MapResultSetExtractor<Long, Object[]>("ID", Long.class, Object[].class));
//		System.out.println("------------- size = " + result.size());
//		for (Long key : result.keySet()) {
//			System.out.println(key + " = " + result.get(key));
//		}
//		List<Object[]> listArr = dao.queryForListArray("SELECT ID, USER_NAME, LOGIN_NAME FROM UUM_USER LIMIT 20");
//		for (Object[] objects : listArr) {
//			System.out.println(Arrays.asList(objects));
//		}
//		Object[] arr = dao.queryForArray("SELECT ID, USER_NAME, LOGIN_NAME FROM UUM_USER LIMIT 2");
//		System.out.println("------");
//		System.out.println(Arrays.asList(arr));
//		String sql = "SELECT * FROM (SELECT sy.*,sc.CLASS_NAME FROM BMP_SYSTEM sy LEFT JOIN BMP_SYSCLASS sc ON sc.CLASS_CODE=sy.CLASS_CODE order by SYS_CODE ) alias_001 WHERE 1=1 AND PARENT_CODE='-1'";
//		System.out.println(dao.fillByPagedQuery(sql, 1, 5).asXML());
//		NewsStoryscript storyscript = dao.queryBusinessObjByPk(NewsStoryscript.class, 2804l);
//		System.out.println(storyscript.getContent());
//		System.out.println(storyscript.getContent().indexOf(System.getProperty("line.separator")));
//		System.out.println(breakline2enterbreakline(storyscript.getContent()));
//		storyscript.setContent(breakline2enterbreakline(storyscript.getContent()));
//		System.out.println(dao.updateBusinessObjs(true, storyscript));
		TestTypes types = new TestTypes();
		types.setAddress("a");
		types.setField1("洛克是否");
		types.setField2(new Timestamp(System.currentTimeMillis()));
		types.setField3("收到1");
		types.setName("dsdsdf撒旦发");
//		types.setField4("收到1".getBytes());
//		System.out.println(dao.saveBusinessObjs(types));
		NmpPerfdata perfdata = new NmpPerfdata();
		perfdata.setDevId(1);
		perfdata.setCollTime(new Date(0));
		perfdata.setKpiId(1);
		perfdata.setKpiValue("20");
		perfdata.setObjectId(1);
		perfdata.setTaskId(1l);
		dao.saveBusinessObjs(perfdata);
		System.out.println("耗时：" + (System.currentTimeMillis() - start) + " ms");
	}
	
	/**
	 * 换行(\n)转换为回车换行(\r\n)
	 * @param content
	 * @return
	 */
	private static String breakline2enterbreakline(String content) {
		String lineSeparator = System.getProperty("line.separator");
		if(content == null) {
			return "";
		}
		if(content.indexOf(lineSeparator) == -1) {
			return content.replaceAll("\n", lineSeparator);
		}
		return content;
	}

}
