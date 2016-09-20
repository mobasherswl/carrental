package com.epam.server.conf;

import com.epam.common.dto.CarDto;
import com.epam.common.dto.RentCarDto;
import com.epam.common.service.*;
import com.epam.server.model.Car;
import com.epam.server.model.RentCar;
import com.epam.server.threadlocal.TenantIdThreadLocal;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerServiceExporter;
import org.springframework.remoting.support.SimpleHttpServerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.epam.common.constants.TenantConstants.TENANT_ID;
import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

/**
 * Created by Ahmed_Khan on 4/22/2016.
 */
@Configuration
@EnableJpaRepositories(basePackages = {"com.epam.server.repo"})
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.epam.server.service","com.epam.server.multitenancy"})
@PropertySource({"classpath:application.properties", "hibernate.properties"})
public class AppConfig {

    @Value("${server.port}")
    int port;
    @Value("${server.info.servicepath}")
    String serviceInfoPath;
    @Value("${server.customer.servicepath}")
    String customerServicePath;
    @Value("${server.carfleet.servicepath}")
    String fleetServicePath;
    @Value("${server.rentcar.servicepath}")
    String rentCarServicePath;
    @Value("${server.rentalclass.servicepath}")
    String rentalClassServicePath;
    @Value("${test.url}")
    String testUrl;

    @Lazy
    @Resource(name = "serverInfoServiceImpl")
    ServerInfoService serverInfoService;
    @Lazy
    @Resource(name = "customerServiceImpl")
    CustomerService customerServiceImpl;
    @Lazy
    @Resource(name = "fleetServiceImpl")
    FleetService fleetService;
    @Lazy
    @Resource(name = "rentCarServiceImpl")
    RentCarService rentCarService;
    @Lazy
    @Resource(name = "rentalClassServiceImpl")
    RentalClassService rentalClassService;
    @Resource
    Environment environment;

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public SimpleHttpServerFactoryBean httpServer() {
        SimpleHttpServerFactoryBean simpleHttpServerFactoryBean = new SimpleHttpServerFactoryBean();
        Map<String, HttpHandler> contexts = new HashMap<String, HttpHandler>();

        contexts.put(serviceInfoPath, serverInfoSimpleHttpInvokerServiceExporter());
        contexts.put(customerServicePath, customerSimpleHttpInvokerServiceExporter());
        contexts.put(fleetServicePath, fleetSimpleHttpInvokerServiceExporter());
        contexts.put(rentCarServicePath, rentCarSimpleHttpInvokerServiceExporter());
        contexts.put(rentalClassServicePath, rentalClassSimpleHttpInvokerServiceExporter());

        simpleHttpServerFactoryBean.setPort(port);
        simpleHttpServerFactoryBean.setContexts(contexts);
        simpleHttpServerFactoryBean.setExecutor(executor());

        return simpleHttpServerFactoryBean;
    }

    @Bean
    public Executor executor() {
        return Executors.newFixedThreadPool(4);
    }

    @Bean
    public SimpleHttpInvokerServiceExporter serverInfoSimpleHttpInvokerServiceExporter() {
        return getSimpleHttpInvokerServiceExporter(ServerInfoService.class, serverInfoService);
    }

    @Bean
    public SimpleHttpInvokerServiceExporter customerSimpleHttpInvokerServiceExporter() {
        return getSimpleHttpInvokerServiceExporter(CustomerService.class, customerServiceImpl);
    }

    @Bean
    public SimpleHttpInvokerServiceExporter fleetSimpleHttpInvokerServiceExporter() {
        return getSimpleHttpInvokerServiceExporter(FleetService.class, fleetService);
    }

    @Bean
    public SimpleHttpInvokerServiceExporter rentCarSimpleHttpInvokerServiceExporter() {
        return getSimpleHttpInvokerServiceExporter(RentCarService.class, rentCarService);
    }

    @Bean
    public SimpleHttpInvokerServiceExporter rentalClassSimpleHttpInvokerServiceExporter() {
        return getSimpleHttpInvokerServiceExporter(RentalClassService.class, rentalClassService);
    }

    private <T> SimpleHttpInvokerServiceExporter getSimpleHttpInvokerServiceExporter(Class<T> clazz, Object service) {
        SimpleHttpInvokerServiceExporter simpleHttpInvokerServiceExporter = new SimpleHttpInvokerServiceExporter() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                TenantIdThreadLocal.getInstance().set(exchange.getRequestHeaders().getFirst(TENANT_ID));
                super.handle(exchange);
            }
        };

        simpleHttpInvokerServiceExporter.setServiceInterface(clazz);
        simpleHttpInvokerServiceExporter.setService(service);

        return simpleHttpInvokerServiceExporter;
    }

    @Bean(destroyMethod = "close")
    DataSource dataSource(Environment env) {
        return connectionProvider().unwrap(DataSource.class);
/*
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setDriverClassName(env.getRequiredProperty("h2.jdbc.driver.classname"));
        dataSourceConfig.setJdbcUrl(env.getRequiredProperty("h2.db.url"));
        dataSourceConfig.setUsername(env.getProperty("db.username"));
        dataSourceConfig.setPassword(env.getProperty("db.password"));

        return new HikariDataSource(dataSourceConfig);
*/
    }

    @Bean(destroyMethod = "stop")
    ConnectionProvider connectionProvider() {
        HikariCPConnectionProvider connectionProvider = new HikariCPConnectionProvider();
        connectionProvider.configure(org.hibernate.cfg.Environment.getProperties());
        return connectionProvider;
    }

    @Bean(destroyMethod = "destroy")
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, MultiTenantConnectionProvider multiTenantConnectionProvider) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties properties = new Properties();
        properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        entityManagerFactoryBean.setJpaProperties(properties);
        entityManagerFactoryBean.setPackagesToScan("com.epam.server.model");

        return entityManagerFactoryBean;
    }

    @Bean
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    ModelMapper modelMapper() {
        class RentCarToRentCarDtoMap extends PropertyMap<RentCar, RentCarDto> {
            protected void configure() {
                map(source.getCar()).setCarDto(null);
                map(source.getCustomer()).setCustomerDto(null);
            }
        }

        class CarToCarDtoMap extends PropertyMap<Car, CarDto> {
            protected void configure() {
                map(source.getRentalClass()).setRentalClassDto(null);
            }
        }

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)
                .setFieldAccessLevel(PRIVATE);
        modelMapper.addMappings(new RentCarToRentCarDtoMap());
        modelMapper.addMappings(new CarToCarDtoMap());
        return modelMapper;
    }

    ;

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    InetAddress inetAddress() throws UnknownHostException {
        return Inet4Address.getLocalHost();
    }

    //Following beans are for test cases to test via remote calls
/*
    @Bean
    HttpInvokerProxyFactoryBean serverInfoService()
    {
        return getHttpInvokerProxyFactoryBean(ServerInfoService.class, String.format("%s%d%s", testUrl, port, serviceInfoPath));
    }

    @Bean(name = "customerService")
    HttpInvokerProxyFactoryBean customerService()
    {
        return getHttpInvokerProxyFactoryBean(CustomerService.class, String.format("%s%d%s", testUrl, port, customerServicePath));
    }

    private <T> HttpInvokerProxyFactoryBean getHttpInvokerProxyFactoryBean(Class<T> clazz, String serviceUrl)
    {
        HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();

        httpInvokerProxyFactoryBean.setServiceInterface(clazz);
        httpInvokerProxyFactoryBean.setServiceUrl(serviceUrl);

        return httpInvokerProxyFactoryBean;
    }
*/

}
