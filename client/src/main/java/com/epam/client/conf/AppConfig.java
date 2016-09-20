package com.epam.client.conf;

import com.epam.common.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.remoting.support.RemoteInvocationResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static com.epam.common.constants.TenantConstants.TENANT_ID;
import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

/**
 * Created by Ahmed_Khan on 4/22/2016.
 */
@Configuration
@ComponentScan(basePackages = {"com.epam.client.gui.swing"})
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("application.properties")
    private String projectPropertiesFilepath;
    @Value("${server.info.url}")
    private String serverInfoUrl;
    @Value("${server.customer.url}")
    private String customerServiceUrl;
    @Value("${server.fleet.url}")
    private String fleetServiceUrl;
    @Value("${server.rentcar.url}")
    private String rentCarServiceUrl;
    @Value("${server.rentalclass.url}")
    private String rentalClassServiceUrl;
    @Autowired
    Environment environment;

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    HttpInvokerProxyFactoryBean serverInfoService() {
        return getHttpInvokerProxyFactoryBean(ServerInfoService.class, serverInfoUrl);
    }

    @Bean
    HttpInvokerProxyFactoryBean customerService() {
        return getHttpInvokerProxyFactoryBean(CustomerService.class, customerServiceUrl);
    }

    @Bean
    HttpInvokerProxyFactoryBean fleetService() {
        return getHttpInvokerProxyFactoryBean(FleetService.class, fleetServiceUrl);
    }

    @Bean
    HttpInvokerProxyFactoryBean rentCarService() {
        return getHttpInvokerProxyFactoryBean(RentCarService.class, rentCarServiceUrl);
    }

    @Bean
    HttpInvokerProxyFactoryBean rentalClassService() {
        return getHttpInvokerProxyFactoryBean(RentalClassService.class, rentalClassServiceUrl);
    }

    private <T> HttpInvokerProxyFactoryBean getHttpInvokerProxyFactoryBean(Class<T> clazz, String serviceUrl) {
        HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();

        httpInvokerProxyFactoryBean.setServiceInterface(clazz);
        httpInvokerProxyFactoryBean.setServiceUrl(serviceUrl);
        httpInvokerProxyFactoryBean.setHttpInvokerRequestExecutor( new SimpleHttpInvokerRequestExecutor() {
            @Override
            protected void prepareConnection(HttpURLConnection con, int contentLength) throws IOException {
                super.prepareConnection(con, contentLength);
                con.setRequestProperty(TENANT_ID, environment.getProperty(TENANT_ID));
            }
        });

        return httpInvokerProxyFactoryBean;
    }

    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true)
                .setFieldAccessLevel(PRIVATE);
        return modelMapper;
    }
}
