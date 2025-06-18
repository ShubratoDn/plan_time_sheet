package com.aim.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import com.aim.entity.Company;
import com.aim.repository.CompanyRepository;

@Component
public class TenantDataSource implements Serializable {

    private HashMap<String, DataSource> dataSources = new HashMap<>();

    @Value("${sub.db.url}")
	private String SUB_DB_URL;
    
    @Value("${spring.datasource.username}")
	private String DB_USERNAME;
	
	@Value("${spring.datasource.password}")
	private String DB_PASSWORD;
    
    @Autowired
    private CompanyRepository companyRepository;

    public DataSource getDataSource(String name) {
    	
    	if (dataSources.get(name) != null) {
    		return dataSources.get(name);
    	}
    	
    	DataSource dataSource = createDataSource(name);
    	if (dataSource != null) {
    		dataSources.put(name, dataSource);
    	}
    	return dataSource;
    }
    
    public DataSource getNewDataSource(String name) {
        	 DataSourceBuilder factory = DataSourceBuilder
                     .create().driverClassName("com.mysql.jdbc.Driver")
                     .username(DB_USERNAME)
                     .password(DB_PASSWORD)
                     .url(SUB_DB_URL + name);
             DataSource ds = factory.build();     
             return ds;
    }

    @PostConstruct
    public Map<String, DataSource> getAll() {
    	
        Iterable<Company> companyList = companyRepository.findAll();
        Map<String, DataSource> result = new HashMap<>();
        
        for (Company config : companyList) {
            DataSource dataSource = getDataSource(config.getDbName());
            result.put(config.getName(), dataSource);
        }
        return result;
    }

    private DataSource createDataSource(String name) {
    	Company config = companyRepository.findByUrlSlug(name);
        if (config != null) {
            DataSourceBuilder factory = DataSourceBuilder
                    .create().driverClassName("com.mysql.jdbc.Driver")
                    .username(DB_USERNAME)
                    .password(DB_PASSWORD)
                    .url(SUB_DB_URL + config.getDbName());
            DataSource ds = factory.build();     
            return ds;
        }
        return null;
    }   

}
