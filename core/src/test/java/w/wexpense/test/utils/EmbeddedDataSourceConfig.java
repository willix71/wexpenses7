package w.wexpense.test.utils;

import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

public class EmbeddedDataSourceConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedDataSourceConfig.class);
	
	private final ResourceDatabasePopulator databasePopulator;
	
	private final EmbeddedDatabaseFactory databaseFactory;
	
	private final ResourceLoader resourceLoader;
	
	private DataSource datasource;
	
	private static String[] getDefaultScript() {
		return new String[] {};
	}
	
	public EmbeddedDataSourceConfig(String ...scripts) {
		String databaseName = UUID.randomUUID().toString();
		
		LOGGER.info("Generating new datasource named {}", databaseName);
		
		this.resourceLoader = new DefaultResourceLoader();
		
		this.databasePopulator = new ResourceDatabasePopulator();
		for(String script: scripts) {
			this.databasePopulator.addScript(this.resourceLoader.getResource(script));
		}
		
		this.databaseFactory = new EmbeddedDatabaseFactory();
		this.databaseFactory.setDatabasePopulator(this.databasePopulator);
		this.databaseFactory.setDatabaseName(databaseName);
		this.databaseFactory.setDatabaseType(EmbeddedDatabaseType.H2);		
	}
	
	public EmbeddedDataSourceConfig() {
		this(getDefaultScript());
	}
	
	public EmbeddedDataSourceConfig setStatments(String statments) {
		Resource rsrc = new ByteArrayResource(statments.getBytes());
		this.databasePopulator.addScript(rsrc);
		return this;
	}
	
	public EmbeddedDataSourceConfig setScripts(String script) {
		this.databasePopulator.addScript(this.resourceLoader.getResource(script));
		return this;
	}
	
	public EmbeddedDataSourceConfig setScripts(Resource rsrc) {
		this.databasePopulator.addScript(rsrc);
		return this;
	}
	
	public DataSource getDataSource() {
		LOGGER.info("getting datasource");
		if (this.datasource == null) {
			this.datasource = this.databaseFactory.getDatabase();
		}
		return datasource;
//		return this.databaseFactory.getDatabase();
	}
	
}
