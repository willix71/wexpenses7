package w.wexpense.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;

import w.junit.extras.OrderedSpringJUnit4ClassRunner;

@RunWith(OrderedSpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = false)
@ContextConfiguration(locations={"classpath:persistence-test-context.xml"})
@Ignore
public class DatabasePopulatorTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DatabasePopulator populator;
    
    @Test
    @Order(1)
    public void setup() {
        Assert.assertNotNull(entityManager);
        Assert.assertNotNull(populator);
        populator.populate();
    }
}
