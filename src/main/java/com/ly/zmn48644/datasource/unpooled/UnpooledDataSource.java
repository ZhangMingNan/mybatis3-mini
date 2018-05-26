
package com.ly.zmn48644.datasource.unpooled;



import com.ly.zmn48644.io.Resources;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 非池化数据源实现类
 */
public class UnpooledDataSource implements DataSource {

    //驱动类加载器
    private ClassLoader driverClassLoader;
    //数据库连接驱动的相关配置属性,这些参数就是一般配置在URL 后面的那些参数.
    private Properties driverProperties;
    //已经注册的驱动
    private static Map<String, Driver> registeredDrivers = new ConcurrentHashMap<String, Driver>();

    //驱动名称
    private String driver;
    //数据库url
    private String url;
    //用户名
    private String username;
    //密码
    private String password;

    //是否自动提交事务
    private Boolean autoCommit;
    //默认事务隔离级别
    private Integer defaultTransactionIsolationLevel;


    /**
     * 通过静态代码块,将 DriverManager 中已经注册过的 数据库驱动
     * 放入 registeredDrivers 集合中去.
     */
    static {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            registeredDrivers.put(driver.getClass().getName(), driver);
        }
    }

    /**
     * 无参构造方法
     */
    public UnpooledDataSource() {
    }


    public UnpooledDataSource(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public UnpooledDataSource(String driver, String url, Properties driverProperties) {
        this.driver = driver;
        this.url = url;
        this.driverProperties = driverProperties;
    }

    public UnpooledDataSource(ClassLoader driverClassLoader, String driver, String url, String username, String password) {
        this.driverClassLoader = driverClassLoader;
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public UnpooledDataSource(ClassLoader driverClassLoader, String driver, String url, Properties driverProperties) {
        this.driverClassLoader = driverClassLoader;
        this.driver = driver;
        this.url = url;
        this.driverProperties = driverProperties;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return doGetConnection(username, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return doGetConnection(username, password);
    }

    @Override
    public void setLoginTimeout(int loginTimeout) throws SQLException {
        DriverManager.setLoginTimeout(loginTimeout);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) throws SQLException {
        DriverManager.setLogWriter(logWriter);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    public ClassLoader getDriverClassLoader() {
        return driverClassLoader;
    }

    public void setDriverClassLoader(ClassLoader driverClassLoader) {
        this.driverClassLoader = driverClassLoader;
    }

    public Properties getDriverProperties() {
        return driverProperties;
    }

    public void setDriverProperties(Properties driverProperties) {
        this.driverProperties = driverProperties;
    }

    public String getDriver() {
        return driver;
    }

    public synchronized void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public Integer getDefaultTransactionIsolationLevel() {
        return defaultTransactionIsolationLevel;
    }

    public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel) {
        this.defaultTransactionIsolationLevel = defaultTransactionIsolationLevel;
    }

    private Connection doGetConnection(String username, String password) throws SQLException {
        Properties props = new Properties();
        if (driverProperties != null) {
            props.putAll(driverProperties);
        }
        if (username != null) {
            props.setProperty("user", username);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        return doGetConnection(props);
    }


    /**
     * 所有的 getConnection 方法包含重载方法,最终都是调用此方法获取数据源
     *
     * @param properties 所有的连接配置项集合
     * @return 返回数据库连接
     * @throws SQLException
     */
    private Connection doGetConnection(Properties properties) throws SQLException {
        //初始化驱动,第一次调用内部才会执行
        initializeDriver();
        //从驱动管理器,根据url和配置参数,获取连接
        Connection connection = DriverManager.getConnection(url, properties);
        //配置数据库连接的autoCommit和隔离级别
        configureConnection(connection);
        return connection;
    }


    /**
     * 初始化驱动,注意这是个同步方法
     *
     * @throws SQLException
     */
    private synchronized void initializeDriver() throws SQLException {

        //首先判断 当前数据源所使用的 驱动(就是配置文件配置的)是否在  DriverManager 中注册过
        //如果判断条件成立 说明 配置文件中配置的驱动没有被 类加载器加载过.

        //如果不成立说明已经加载过了不用重复加载了.
        if (!registeredDrivers.containsKey(driver)) {
            Class<?> driverType;
            try {
                //如果类加载器不为空
                if (driverClassLoader != null) {
                    //使用类加载器去加载驱动
                    driverType = Class.forName(driver, true, driverClassLoader);
                } else {
                    //否则使用 Resources 中的一组加载器去加载驱动
                    driverType = Resources.classForName(driver);
                }
                // DriverManager requires the driver to be loaded via the system ClassLoader.
                // DriverManager  需要通过 system ClassLoader 来加载 driver .
                // http://www.kfu.com/~nsayer/Java/dyn-jdbc.html

                Driver driverInstance = (Driver) driverType.newInstance();
                //注册驱动
                DriverManager.registerDriver(new DriverProxy(driverInstance));
                //放入 registeredDrivers 集合中
                registeredDrivers.put(driver, driverInstance);
            } catch (Exception e) {
                throw new SQLException("Error setting driver on UnpooledDataSource. Cause: " + e);
            }
        }
    }

    private void configureConnection(Connection conn) throws SQLException {
        //配置是否是自动提交
        if (autoCommit != null && autoCommit != conn.getAutoCommit()) {
            conn.setAutoCommit(autoCommit);
        }
        //配置隔离级别
        if (defaultTransactionIsolationLevel != null) {
            conn.setTransactionIsolation(defaultTransactionIsolationLevel);
        }
    }


    /**
     * 这个代理类的作用在下面的文章中解释
     * http://www.kfu.com/~nsayer/Java/dyn-jdbc.html
     * 主要的意思就是
     * DriverManager 会拒绝不是使用 system ClassLoader 加载的 Driver,这样就不能实现在运行时加载配置好的驱动.
     * 解决方法就是文章中 使用一个  Shim(垫片类), 也就是这里的 DriverProxy 类,来实现运行时加载 Driver.
     * <p>
     * 这个内部逻辑很简单就是包装一下调用.
     */
    private static class DriverProxy implements Driver {
        private Driver driver;

        DriverProxy(Driver d) {
            this.driver = d;
        }

        @Override
        public boolean acceptsURL(String u) throws SQLException {
            return this.driver.acceptsURL(u);
        }

        @Override
        public Connection connect(String u, Properties p) throws SQLException {
            return this.driver.connect(u, p);
        }

        @Override
        public int getMajorVersion() {
            return this.driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return this.driver.getMinorVersion();
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
            return this.driver.getPropertyInfo(u, p);
        }

        @Override
        public boolean jdbcCompliant() {
            return this.driver.jdbcCompliant();
        }

        // @Override only valid jdk7+
        public Logger getParentLogger() {
            return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    // @Override only valid jdk7+
    public Logger getParentLogger() {
        // requires JDK version 1.6
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

}
