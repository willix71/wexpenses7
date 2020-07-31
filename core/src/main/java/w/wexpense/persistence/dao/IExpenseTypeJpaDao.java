package w.wexpense.persistence.dao;

import w.wexpense.model.ExpenseType;
import w.wexpense.persistence.IGenericDao;
import w.wexpense.persistence.ITypeDao;
import w.wexpense.persistence.IUidableDao;

public interface IExpenseTypeJpaDao extends IGenericDao< ExpenseType, Long >, IUidableDao<ExpenseType>, ITypeDao<ExpenseType>  { }
