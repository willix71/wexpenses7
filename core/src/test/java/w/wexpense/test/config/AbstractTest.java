package w.wexpense.test.config;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;

import w.junit.extras.OrderedSpringJUnit4ClassRunner;

@RunWith(OrderedSpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = false)
@ContextConfiguration(classes = { TestPersistenceConfiguration.class, TestServiceConfiguration.class })
public abstract class AbstractTest { }

