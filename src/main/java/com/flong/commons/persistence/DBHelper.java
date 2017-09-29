package com.flong.commons.persistence;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 
 * @author liangjilong
 * @Description：数据库帮助类..
 */

@SuppressWarnings("all")
public class DBHelper {
	
	private static DBHelper instance = null;
	private static Connection conn = null;
	private static Statement stmt = null;
	private static PreparedStatement preparedStatement = null;
	private static CallableStatement callableStatement = null;
	private static ResultSet rs = null;
	private Map<String,String> columnTypeMap = new HashMap<String, String>();	//存放字段名和数据类型
	private static String Driver = "com.mysql.jdbc.Driver";// ConfigUtils.getProperty("jdbc.driver");
	private static String Url = "jdbc:mysql://localhost:3306/flong?characterEncoding=utf8";// ConfigUtils.getProperty("jdbc.url");
	private static String UserName = "root";// ConfigUtils.getProperty("jdbc.userName");
	private static String PassWord = "root";// ConfigUtils.getProperty("jdbc.passWord");
	
	public static boolean isServerFlag =false;//部署到百度云环境的时候设置为true，默认让不要执行本地的数据库。

	/**建立单例模式
	 * Single
	 * @return
	 */
	public static DBHelper getInstance() {
		if (instance == null) {
			synchronized (DBHelper.class) {
				instance = new DBHelper();
			}
		}
		return instance;
	}

	/**
	 * 返回一个:Connection
	 * @return:Connection
	 */
	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName(Driver);
			conn = DriverManager.getConnection(Url, UserName, PassWord);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 加载链接数据库.
	 */
	public void loadConnection(){
		try{
			 Class.forName(Driver); 
			 conn = DriverManager.getConnection(Url, UserName, PassWord);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**构造方法---*/
	public  DBHelper(){
		try {
			loadConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * method3: 专门用于发送增删改语句的方法
	 * @param pstmt
	 * @return
	 */
    public static int execOther(PreparedStatement pstmt){
        try {
            //1、使用Statement对象发送SQL语句
            int affectedRows = pstmt.executeUpdate();
            //2、返回结果
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    /***
     * method4: 专门用于发送查询语句
     * @param pstmt
     * @return
     */
    public static ResultSet execQuery(PreparedStatement pstmt){
        try {
            //1、使用Statement对象发送SQL语句
            rs = pstmt.executeQuery();
            //2、返回结果
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
	

	/**
	 * 支持批量处理delete update insert
	 * 
	 * @param sqls
	 * @return
	 */
	public int supportsBatch(Object[] sqls) {
		try {
			conn = DBHelper.getInstance().getConnection();
			conn.setAutoCommit(false);
			DatabaseMetaData dma = conn.getMetaData();
			if (dma.supportsBatchUpdates()) {
				int bufferSize = 0;
				stmt = conn.createStatement();
				for (int i = 0; i < sqls.length; i++) {
					bufferSize++;
					if ((bufferSize + 1) % 100 == 0) {
						stmt.addBatch(sqls[i].toString());
						conn.commit();
						stmt.clearBatch();
					}
				}
				int[] rows = stmt.executeBatch();
				conn.commit();
				return rows.length;
			} else {
				return sqls.length;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ReleaseUtils.releaseAll(null, stmt, conn);
		}
		return 0;
	}

	 
	/**
	 * 执行(增删改)
	 * 
	 * @param sql
	 * @param args
	 * @return boolean
	 */
	public static boolean executeUpdates(String sql, Object[] args) {
		boolean sign = false;
		Connection conn = null;
		PreparedStatement state = null;
		try {
			conn = DBHelper.getInstance().getConnection();
			state = conn.prepareStatement(sql);
			if (args != null && args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					state.setObject(i + 1, args[i]);
				}
			}
			int rows = state.executeUpdate();
			if (rows > 0){
				sign = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ReleaseUtils.releaseAll(null, state, conn);
		}
		return sign;
	}

	public static Integer executeUpdatex(String sql, Object[] args) {
		Connection conn = null;
		PreparedStatement state = null;
		try {
			conn = DBHelper.getInstance().getConnection();
			state = conn.prepareStatement(sql);
			if (args != null && args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					state.setObject(i + 1, args[i]);
				}
			}
			int rows = state.executeUpdate();
			if (rows > 0){
				return rows;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ReleaseUtils.releaseAll(null, state, conn);
		}
		return 0;
	}

	
	
	 /**
     * 用于查询，返回结果集
     *
     * @param sql
     *            sql语句
     * @return 结果集
     * @throws SQLException
     */
    public static List query(String sql) throws SQLException {
        ResultSet rs = null;
        try {
            getPreparedStatement(sql);
            rs = preparedStatement.executeQuery();
            return ResultToListMap(rs);
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
        	ReleaseUtils.free(rs);
        }
    }
 
    /**
     * 用于带参数的查询，返回结果集
     *
     * @param sql
     *            sql语句
     * @param paramters
     *            参数集合
     * @return 结果集
     * @throws SQLException
     */
    @SuppressWarnings("rawtypes")
    public static List query(String sql, Object... paramters) throws SQLException {
        ResultSet rs = null;
        try {
            getPreparedStatement(sql);
            for (int i = 0; i < paramters.length; i++) {
                preparedStatement.setObject(i + 1, paramters[i]);
            }
            rs = preparedStatement.executeQuery();
            return ResultToListMap(rs);
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
        	ReleaseUtils.free(rs);
        }
    }
 
    /**
     * 返回单个结果的值，如count\min\max等等
     *
     * @param sql
     *            sql语句
     * @return 结果集
     * @throws SQLException
     */
    public static Object getSingle(String sql) throws SQLException {
        Object result = null;
        ResultSet rs = null;
        try {
            getPreparedStatement(sql);
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                result = rs.getObject(1);
            }
            return result;
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
        	ReleaseUtils.free(rs);
        }
    }
 
    /**
     * 返回单个结果值，如count\min\max等
     *
     * @param sql
     *            sql语句
     * @param paramters
     *            参数列表
     * @return 结果
     * @throws SQLException
     */
    public static Object getSingle(String sql, Object... paramters) throws SQLException {
        Object result = null;
        ResultSet rs = null;
        try {
            getPreparedStatement(sql);
            for (int i = 0; i < paramters.length; i++) {
                preparedStatement.setObject(i + 1, paramters[i]);
            }
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                result = rs.getObject(1);
            }
            return result;
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
        	ReleaseUtils.free(rs);
        }
    }
 
    /**
     * 用于增删改
     *
     * @param sql
     *            sql语句
     * @return 影响行数
     * @throws SQLException
     */
    public static int update(String sql) throws SQLException {
        try {
            getPreparedStatement(sql);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
        	ReleaseUtils.free();
        }
    }
 
    /**
     * 用于增删改（带参数）
     *
     * @param sql
     *            sql语句
     * @param paramters
     *            sql语句
     * @return 影响行数
     * @throws SQLException
     */
    public static int update(String sql, Object... paramters) throws SQLException {
        try {
            getPreparedStatement(sql);
            for (int i = 0; i < paramters.length; i++) {
                preparedStatement.setObject(i + 1, paramters[i]);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
        	ReleaseUtils.free();
        }
    }
 
    /**
     * 插入值后返回主键值
     *
     * @param sql
     *            插入sql语句
     * @return 返回结果
     * @throws Exception
     */
    public static Object insertWithReturnPrimeKey(String sql) throws SQLException {
        ResultSet rs = null;
        Object result = null;
        try {
        	conn = DBHelper.getInstance().getConnection();
            preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.execute();
            rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                result = rs.getObject(1);
            }
            return result;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
 
    /**
     * 插入值后返回主键值
     *
     * @param sql
     *            插入sql语句
     * @param paramters
     *            参数列表
     * @return 返回结果
     * @throws SQLException
     */
    public static Object insertWithReturnPrimeKey(String sql, Object... paramters) throws SQLException {
        ResultSet rs = null;
        Object result = null;
        try {
        	conn = DBHelper.getInstance().getConnection();
            preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < paramters.length; i++) {
                preparedStatement.setObject(i + 1, paramters[i]);
            }
            preparedStatement.execute();
            rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                result = rs.getObject(1);
            }
            return result;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
 
    }
 
    /**
     * 调用存储过程执行查询
     *
     * @param procedureSql
     *            存储过程
     * @return
     * @throws SQLException
     */
    public static List callableQuery(String procedureSql) throws SQLException {
        ResultSet rs = null;
        try {
            getCallableStatement(procedureSql);
            rs = callableStatement.executeQuery();
            return ResultToListMap(rs);
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
        	ReleaseUtils.free(rs);
        }
    }
 
    /**
     * 调用存储过程（带参数）,执行查询
     *
     * @param procedureSql
     *            存储过程
     * @param paramters
     *            参数表
     * @return
     * @throws SQLException
     */
    @SuppressWarnings("rawtypes")
    public static List callableQuery(String procedureSql, Object... paramters) throws SQLException {
        ResultSet rs = null;
        try {
            getCallableStatement(procedureSql);
            for (int i = 0; i < paramters.length; i++) {
                callableStatement.setObject(i + 1, paramters[i]);
            }
            rs = callableStatement.executeQuery();
            return ResultToListMap(rs);
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
        	ReleaseUtils.free(rs);
        }
    }
 
    /**
     * 调用存储过程，查询单个值
     * @param procedureSql
     * @return
     * @throws SQLException
     */
    public static Object callableGetSingle(String procedureSql) throws SQLException {
        Object result = null;
        ResultSet rs = null;
        try {
            getCallableStatement(procedureSql);
            rs = callableStatement.executeQuery();
            while (rs.next()) {
                result = rs.getObject(1);
            }
            return result;
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
        	ReleaseUtils.free(rs);
        }
    }
 
    /**
     * 调用存储过程(带参数)，查询单个值
     * @param procedureSql
     * @param parameters
     * @return
     * @throws SQLException
     */
    public static Object callableGetSingle(String procedureSql, Object... paramters) throws SQLException {
        Object result = null;
        ResultSet rs = null;
        try {
            getCallableStatement(procedureSql);
            for (int i = 0; i < paramters.length; i++) {
                callableStatement.setObject(i + 1, paramters[i]);
            }
            rs = callableStatement.executeQuery();
            while (rs.next()) {
                result = rs.getObject(1);
            }
            return result;
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
        	ReleaseUtils.free(rs);
        }
    }
 
    public static Object callableWithParamters(String procedureSql) throws SQLException {
        try {
            getCallableStatement(procedureSql);
            callableStatement.registerOutParameter(0, Types.OTHER);
            callableStatement.execute();
            return callableStatement.getObject(0);
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
        	ReleaseUtils.free();
        }
    }
 
    /**
     * 调用存储过程，执行增删改
     * @param procedureSql
     *            存储过程
     * @return 影响行数
     * @throws SQLException
     */
    public static int callableUpdate(String procedureSql) throws SQLException {
        try {
            getCallableStatement(procedureSql);
            return callableStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
        	ReleaseUtils.free();
        }
    }
 
    /**
     * 调用存储过程（带参数），执行增删改
     * @param procedureSql
     *            存储过程
     * @param parameters
     * @return 影响行数
     * @throws SQLException
     */
    public static int callableUpdate(String procedureSql, Object... parameters) throws SQLException {
        try {
            getCallableStatement(procedureSql);
            for (int i = 0; i < parameters.length; i++) {
                callableStatement.setObject(i + 1, parameters[i]);
            }
            return callableStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
        	ReleaseUtils.free();
        }
    }
 
    /**
     * 批量更新数据
     * @param sqlList
     *            一组sql
     * @return
     */
    public static int[] batchUpdate(List<String> sqlList) {
        int[] result = new int[] {};
        Statement statenent = null;
        try {
        	conn = DBHelper.getInstance().getConnection();
            conn.setAutoCommit(false);
            statenent = conn.createStatement();
            for (String sql : sqlList) {
                statenent.addBatch(sql);
            }
            result = statenent.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                throw new ExceptionInInitializerError(e1);
            }
            throw new ExceptionInInitializerError(e);
        } finally {
        	ReleaseUtils.free(statenent, null);
        }
        return result;
    }
 
    private static List ResultToListMap(ResultSet rs) throws SQLException {
        List list = new ArrayList();
        while (rs.next()) {
            ResultSetMetaData md = rs.getMetaData();
            Map map = new HashMap();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                map.put(md.getColumnLabel(i), rs.getObject(i));
            }
            list.add(map);
        }
        return list;
    }
 
    /**
     * 获取PreparedStatement
     * @param sql
     * @throws SQLException
     */
    private static void getPreparedStatement(String sql) throws SQLException {
    	conn = DBHelper.getInstance().getConnection();
        preparedStatement = conn.prepareStatement(sql);
    }
 
    /**
     * 获取CallableStatement
     * @param procedureSql
     * @throws SQLException
     */
    private static void getCallableStatement(String procedureSql) throws SQLException {
    	boolean isServerFlag=DBHelper.isServerFlag;
    	conn = DBHelper.getInstance().getConnection();
        callableStatement = conn.prepareCall(procedureSql);
    }
  
    
    
    
    /**
	 * 关闭连接
	 * @param conn
	 * @param statement
	 * @param rs
	 */
	public void close(Connection conn,Statement statement,ResultSet rs){
		try{
			if(rs!=null){
				rs.close();
			}if(statement!=null){
				statement.close();
			}if(conn!=null){
				conn.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取结果集列数
	 * @param rs
	 * @return
	 */
	public int findMaxCount(ResultSet rs){
		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();
			return rsmd.getColumnCount();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return 0;
	}
	
	/**
	 * 返回一维数组，不管结果集是一条还是多条数据，只返回第一条
	 * @param sql
	 * @return String[]
	 */
	public String[] findOneArray(String sql) {
		String[] strs = null;
		try {
			if (conn == null) {
				try {
					loadConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			int columnCount = findMaxCount(rs);
			strs = new String[columnCount];
			if (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					strs[i - 1] = rs.getString(i);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}
		return strs;
	}
	
	/**
	 * 返回二维数组
	 * @param sql
	 * @return String[][]
	 */
	public String[][] findTwoArray(String sql) {
		String[][] strs = null;
		try {
			if (conn == null) {
				try {
					loadConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 创建结果集可以移动的statement对象,Concurrency并发resultSetConcurrency
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			// 执行sql
			rs = stmt.executeQuery(sql);
			// 得到列数
			int columnCount = findMaxCount(rs);
			// 将结果集游标移到最后
			rs.last();
			// 获取行数
			int length = rs.getRow();
			// 根据查询结果行数和列数创建二维数组
			strs = new String[length][columnCount];
			// 将结果集游标移到最前面
			rs.beforeFirst();
			// 将结果集封装到数组
			for (int j = 0; j < length; j++) {
				if (rs.next()) {
					for (int i = 1; i <= columnCount; i++) {
						strs[j][i - 1] = rs.getString(i);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
		}
		return strs;
	}
	
	/**
	 * 返回list，list里面装字符数组
	 * @param sql
	 * @return
	 */
	public List<String[]> findList(String sql) {
		List<String[]> list = new ArrayList<String[]>();
		String[] strs = null;
		try {
			if(conn==null){
				try {
					loadConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			stmt = conn.createStatement();
			//执行sql
			rs = stmt.executeQuery(sql);
			//得到列数
			int columnCount = findMaxCount(rs);
			strs = new String[columnCount];
			 //将结果集封装到list
				 while(rs.next()){
					 for(int i=1;i<=columnCount;i++){
						 strs[i-1] = rs.getString(i);
					 }
					 list.add(strs);
					 strs = new String[columnCount];
				 }
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
		}
		return list;
	}
	/**
	 * 返回一条数据，数据格式为map直接通过数据库字段名获取值
	 * @param sql
	 * @return
	 * 格式 {p_name=多琳纳, ppid=24}
	 */
	public Map<String, String> findOneMap(String sql) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			if(conn==null){
				try {
					loadConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			int columnCount = findMaxCount(rs);
			ResultSetMetaData metaData = rs.getMetaData();
			if(rs.next()){
				for(int i=1;i<=columnCount;i++){
					map.put(metaData.getColumnName(i), rs.getString(i));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
		}
		return map;
	}
	
	/**
	 * 返回list，list里面装map数据
	 * @param sql
	 * @return
	 */
	public List<Map<String,String>> findListMap(String sql) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = null;
		try {
			if(conn==null){
				try {
					loadConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			int columnCount = findMaxCount(rs);
			ResultSetMetaData metaData = rs.getMetaData();
			while(rs.next()){
				map = new HashMap<String, String>();
				for(int i=1;i<=columnCount;i++){
					map.put(metaData.getColumnName(i), rs.getString(i));
				}
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
		}
		return list;
	}
	
	public <T> List<T> findEntity(Class<T> t,String sql){
		List<T> list = new ArrayList<T>();
		Object obj = null;
		//将实体类set方法名和类型存起来
		 Field[] field = t.getDeclaredFields();
	        for (int i = 0; i < field.length; i++) {
	            // 权限修饰符
	            // 属性类型
	            Class<?> type = field[i].getType();
	            columnTypeMap.put(field[i].getName(), type.getName());
	        }
		try {
			if(conn==null){
				try {
					loadConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			int columnCount = findMaxCount(rs);
			ResultSetMetaData metaData = rs.getMetaData();
			
			String columnName = "";
			String setName = "";
			//	Class<?> type = null;
			while(rs.next()){
				obj = t.newInstance();
				for(int i = 1;i<=columnCount;i++){
					//得到数据库字段名
					columnName = metaData.getColumnName(i);
					//拼接set方法字符串
					setName = "set" + columnName.substring(0, 1).toUpperCase()+columnName.substring(1, columnName.length());
					//		type = field[i-1].getType();
					//将数据库的字段值封装到对象里
					doSetColumn(obj,setName,columnName,rs.getString(i));
				}
				
				list.add((T)obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
		}
		return list;
	}
	/**
	 * 封装,暂时只做了int和String类型的处理，其他的需要再写上
	 * @param obj
	 * @param setName
	 * @param type
	 * @param v
	 */
	private void doSetColumn(Object obj, String setName,String columnName,String v) {
		Method method;
		String tp = columnTypeMap.get(columnName);
		try {
			if("int".equals(tp)){
				method = obj.getClass().getMethod(setName,int.class);
				method.invoke(obj,Integer.valueOf(v));
			}else if("java.lang.Integer".equals(tp)){
				method = obj.getClass().getMethod(setName,Integer.class);
				method.invoke(obj,v);
			}else if("java.lang.String".equals(tp)){
				method = obj.getClass().getMethod(setName,String.class);
				method.invoke(obj,v);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
