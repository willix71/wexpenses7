package w.wexpense.test.populator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import w.dao.populator.entity.EntityDatabasePopulator;

@Configuration
public class TestDatabasePopulator {

	@Autowired
	private DataSource dataSource;

	private List<Object> population = new ArrayList<Object>();

	@Bean
	public EntityDatabasePopulator populateDatabase() throws SQLException {
		EntityDatabasePopulator entityPopulator = new EntityDatabasePopulator();
		entityPopulator.addPopulatatorClasses(CodableFieldPopulator.class, DBableFieldPopulator.class);
		entityPopulator.addAllEntities(getPopulation());
		entityPopulator.populate(dataSource.getConnection());
		return entityPopulator;
	}

	public <T> T add(T t) {
		population.add(t);
		return t;
	}

	public List<Object> getPopulation() {
		return population;
	}
}
