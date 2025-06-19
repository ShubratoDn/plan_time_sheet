package com.aim.config;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {
   //https://medium.com/swlh/multi-tenancy-implementation-using-spring-boot-hibernate-6a8e3ecb251a
	private static final String DEFAULT_TENANT_ID = null;

	@Autowired
    private DataSource defaultDS;

    @Autowired
    private ApplicationContext context;

    private Map<String, DataSource> map = new HashMap<>();

    boolean init = false;

    @PostConstruct
    public void load() {
        map.put(DEFAULT_TENANT_ID, defaultDS);
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return map.get(DEFAULT_TENANT_ID);
    }

    private void clearDataSource() {
    	map.clear();
    	map.put(DEFAULT_TENANT_ID, defaultDS);
    }
    
    @Override
    protected DataSource selectDataSource(Object tenantIdentifierObj) {
        String tenantIdentifier = tenantIdentifierObj.toString();
        if (!init) {
            init = true;
//            TenantDataSource tenantDataSource = context.getBean(TenantDataSource.class);
//            map.putAll(tenantDataSource.getAll());
        }
//        	clearDataSource();
        if(map.get(tenantIdentifier) != null) {
        	return map.get(tenantIdentifier);
        } else {
        	
        	if(!tenantIdentifier.equals("public")) {
        		TenantDataSource tenantDataSource = context.getBean(TenantDataSource.class);
        		DataSource dataSource = tenantDataSource.getNewDataSource(tenantIdentifier);
        		if(dataSource == null) {
        			return map.get(DEFAULT_TENANT_ID);
        		} else {
        			map.put(tenantIdentifier,dataSource);
        			return map.get(tenantIdentifier);
        		}
        	}
        }
         
        return map.get(tenantIdentifier) != null ? map.get(tenantIdentifier):map.get(DEFAULT_TENANT_ID);
    }
}
