package com.fukun.order.config.db;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * druid数据源相关的配置类
 *
 * @author tangyifei
 * @since 2019-5-24 14:07:29
 */
@Configuration
@EnableConfigurationProperties(DruidConfig.DruidProperties.class)
@ConditionalOnClass(DruidDataSource.class)
@ConditionalOnProperty(prefix = "druid", name = "url")
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@EnableTransactionManagement
public class DruidConfig {

    @Autowired
    private DruidProperties properties;

    /**
     * 注册druid数据源相关的bean到spring容器中
     *
     * @return druid数据源
     */
    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        if (properties.getInitialSize() > 0) {
            dataSource.setInitialSize(properties.getInitialSize());
        }
        if (properties.getMinIdle() > 0) {
            dataSource.setMinIdle(properties.getMinIdle());
        }
        if (properties.getMaxActive() > 0) {
            dataSource.setMaxActive(properties.getMaxActive());
        }
        dataSource.setTestOnBorrow(properties.isTestOnBorrow());
        try {
            dataSource.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dataSource;
    }

    /**
     * 创建SqlSessionFactory
     *
     * @return SqlSessionFactory对象
     * @throws Exception
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource());
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*Mapper.xml"));
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setLazyLoadingEnabled(true);
        configuration.setMapUnderscoreToCamelCase(true);
        bean.setConfiguration(configuration);
        bean.setTypeAliasesPackage("com.fukun.order.model.po");
        // 工程上默认使用的是Mybatis的DefaultVFS进行扫描，
        // 但是在SpringBoot的环境下，Mybatis的DefaultVFS这个扫包会出现问题，所以只能修改VFS
        bean.setVfs(SpringBootVFS.class);
        return bean.getObject();
    }

    /**
     * 配置事务管理器
     *
     * @return 数据源相关的事务管理器
     */
    @Bean
    public DataSourceTransactionManager testTransactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    /**
     * 本地事务配置
     *
     * @return
     */
//    @Bean
//    @ConditionalOnMissingBean
//    public TransactionInterceptor transactionInterceptor() {
//        Properties properties = new Properties();
//        properties.setProperty("*", "PROPAGATION_REQUIRED,-Throwable");
//        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
//        transactionInterceptor.setTransactionManager(testTransactionManager());
//        transactionInterceptor.setTransactionAttributes(properties);
//        return transactionInterceptor;
//    }

    /**
     * 分布式事务配置 设置为LCN模式
     *
     * @param dtxLogicWeaver
     * @return
     */
//    @ConditionalOnBean(DTXLogicWeaver.class)
//    @Bean
//    public TxLcnInterceptor txLcnInterceptor(DTXLogicWeaver dtxLogicWeaver) {
//        TxLcnInterceptor txLcnInterceptor = new TxLcnInterceptor(dtxLogicWeaver);
//        Properties properties = new Properties();
//        properties.setProperty(Transactions.DTX_TYPE, Transactions.LCN);
//        properties.setProperty(Transactions.DTX_PROPAGATION, "REQUIRED");
//        txLcnInterceptor.setTransactionAttributes(properties);
//        return txLcnInterceptor;
//    }

//    @Bean
//    public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
//        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
//        //需要调整优先级，分布式事务在前，本地事务在后。
//        beanNameAutoProxyCreator.setInterceptorNames("txLcnInterceptor", "transactionInterceptor");
//        beanNameAutoProxyCreator.setBeanNames("*Impl");
//        return beanNameAutoProxyCreator;
//    }

    /**
     * 读取druid数据源相关的属性的类
     *
     * @author tangyifei
     * @since 2019-5-24 14:09:20
     */
    @ConfigurationProperties(prefix = "druid")
    @Getter
    @Setter
    public class DruidProperties {
        private String url;
        private String username;
        private String password;
        private String driverClass;

        private int maxActive;
        private int minIdle;
        private int initialSize;
        private boolean testOnBorrow;

    }
}
