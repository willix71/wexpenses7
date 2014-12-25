package w.wexpense.utils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import w.wexpense.model.Account;
import w.wexpense.model.Currency;
import w.wexpense.model.ExchangeRate;
import w.wexpense.model.Expense;
import w.wexpense.model.ExpenseType;
import w.wexpense.model.Payee;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;

public class ExpenseUtils {

    public static Expense newExpense(Date date, BigDecimal amount, Currency currency, Payee payee, ExpenseType type, String externalReference) {
        Expense x = new Expense();
        x.setDate(date);
        x.setAmount(amount);
        x.setPayee(payee);
        x.setCurrency(currency != null ? currency : payee.getCity().getCountry().getCurrency());
        x.setType(type);
        x.setExternalReference(externalReference);
        return x;
    }

    public static TransactionLine addTransactionLine(Expense x, Account account, TransactionLineEnum factor, BigDecimal amount, ExchangeRate rate) {
        TransactionLine tx = new TransactionLine();
        tx.setExpense(x);
        tx.setAmount(amount == null? x.getAmount() : amount);
        tx.setExchangeRate(rate);        
        tx.setAccount(account);
        tx.setFactor(factor);
        tx.updateValue();
        
        List<TransactionLine> lines = x.getTransactions();
        if (lines == null) {
            lines = new ArrayList<TransactionLine>();
            x.setTransactions(lines);
        }
        lines.add(tx);

        return tx;
    }

    public static TransactionLine addTransactionLine(Expense x, Account account, TransactionLineEnum factor, BigDecimal amount) {
        return addTransactionLine(x, account, factor, amount, null);
    }

    public static TransactionLine addTransactionLine(Expense x, Account account, TransactionLineEnum factor) {
        return addTransactionLine(x, account, factor, null, null);
    }
    
    public static String toString(Expense expense) {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format("\n{0,date,dd.MM.yy} {1,number,0.00}{2} {3} {4}", expense.getDate(), expense.getAmount(), expense.getCurrency(),
                expense.getPayee(), expense.getType()));
        for (TransactionLine tr : expense.getTransactions()) {
            sb.append(MessageFormat.format("\n\t{0}.{1} {2,number,0.000} {3,number,0.000}", tr.getAccount(), tr.getDiscriminator(), tr.getAmount(),
                    tr.getValue()));
        }
        return sb.toString();
    }

}
