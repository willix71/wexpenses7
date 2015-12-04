package w.wexpense.utils;

import java.math.BigDecimal;
import java.util.Date;

import w.wexpense.model.Account;
import w.wexpense.model.Currency;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Expense;
import w.wexpense.model.ExpenseType;
import w.wexpense.model.Payee;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;

public class ExpenseUtils {

   public static Expense newExpense(Object... args) {
      return fillExpense(new Expense(), args);
   }

   public static Expense fillExpense(Expense x, Object... args) {
      for (Object arg : args) {
         if (arg instanceof Date) {
            x.setDate((Date) arg);
         } else if (arg instanceof BigDecimal) {
            x.setAmount((BigDecimal) arg);
         } else if (arg instanceof Float || arg instanceof Double) {
            x.setAmount(new BigDecimal(((Number) arg).doubleValue()));
         } else if (arg instanceof Number) {
            x.setAmount(new BigDecimal(((Number) arg).longValue()));
         } else if (arg instanceof ExpenseType) {
            x.setType((ExpenseType) arg);
         } else if (arg instanceof Payee) {
            Payee p = (Payee) arg;
            x.setPayee(p);
            if (x.getCurrency() == null) {
               try {
                  x.setCurrency(p.getCity().getCountry().getCurrency());
               } catch (NullPointerException e) {
               }
            }
         } else if (arg instanceof Currency) {
            x.setCurrency((Currency) arg);
         } else if (arg instanceof String) {
            x.setExternalReference((String) arg);
         } else if (arg instanceof TransactionLine) {
            TransactionLine tl = (TransactionLine) arg;
            x.addTransaction(tl);
            tl.setExpense(x);
         } else if (arg instanceof Account) {
            if (x.getTransactions() == null || x.getTransactions().size() % 2 == 0) {
               newTransactionLine(x, TransactionLineEnum.OUT, (Account) arg);
            } else {
               newTransactionLine(x, TransactionLineEnum.IN, (Account) arg);
            }
         } else {
            throw new IllegalArgumentException("Don't knoe what to do for a new expense with a " + args.getClass());
         }
      }
      return x;
   }

   public static TransactionLine newTransactionLine(Object... args) {
      return fillTransactionLine(new TransactionLine(), args);
   }

   public static TransactionLine fillTransactionLine(TransactionLine tl, Object... args) {
      for (Object arg : args) {
         if (arg instanceof Expense) {
            Expense x = (Expense) arg;
            tl.setExpense(x);
            if (tl.getDate() == null) {
               tl.setDate(x.getDate());
            }
            if (tl.getAmount() == null) {
               tl.setAmount(x.getAmount());
            }
            x.addTransaction(tl);
         } else if (arg instanceof Date) {
            tl.setDate((Date) arg);
         } else if (arg instanceof BigDecimal) {
            tl.setAmount((BigDecimal) arg);
         } else if (arg instanceof Float || arg instanceof Double) {
            tl.setAmount(new BigDecimal(((Number) arg).doubleValue()));
         } else if (arg instanceof Number) {
            tl.setAmount(new BigDecimal(((Number) arg).longValue()));
         } else if (arg instanceof ExchangeRate) {
            tl.setExchangeRate((ExchangeRate) arg);
         } else if (arg instanceof Account) {
            tl.setAccount((Account) arg);
         } else if (arg instanceof TransactionLineEnum) {
            tl.setFactor((TransactionLineEnum) arg);
         } else {
            throw new IllegalArgumentException(
                  "Don't knoe what to do for a new transaction line with a " + args.getClass());
         }
      }
      tl.updateValue();
      return tl;
   }

   public static Expense addTransactionLines(Expense x, TransactionLine... lines) {
      for (TransactionLine tl : lines) {
         x.addTransaction(tl);
         tl.setExpense(x);
         if (tl.getDate() == null) {
            tl.setDate(tl.getDate());
         }
         if (tl.getAmount() == null) {
            tl.setAmount(tl.getAmount());
         }
         tl.updateValue();
      }
      return x;
   }
}
