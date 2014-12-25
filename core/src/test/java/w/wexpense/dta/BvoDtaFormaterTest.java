package w.wexpense.dta;

import static w.wexpense.dta.DtaCommonTestData.bvo;
import static w.wexpense.dta.DtaCommonTestData.chf;
import static w.wexpense.dta.DtaCommonTestData.createDate;
import static w.wexpense.dta.DtaCommonTestData.createPaymentData;
import static w.wexpense.dta.DtaCommonTestData.nyon;
import static w.wexpense.model.enums.AccountEnum.ASSET;
import static w.wexpense.model.enums.AccountEnum.EXPENSE;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import w.wexpense.model.Account;
import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.Payment;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;

public class BvoDtaFormaterTest {

	final String[] expected = {
			"01130301            00000120215228    WEX010000182600WEX01ID000001234CH650022822851333340B         CHF22,50                     ",
			"02WILLIAM KEYSER      11 CH DU GRAND NOYER                    1197 PRANGINS (CH)  VEHICLE:GAS                                   ",
			"03/C/170012884GARAGE DE L'ETRAZ   CASE POSTALE 1359                       1260 NYON (CH)      000000056124638130000010314       "
	};
	
	@Test
	public void testPaddedAccountNumber() {
		Assert.assertEquals("011234561", BvoDtaFormater.paddedAccountNumber("1-123456-1"));		
		Assert.assertEquals("120000011", BvoDtaFormater.paddedAccountNumber("12-1-1"));		
	}

	@Test
	public void testBvoExpense() throws DtaException {
		Payment payment = createPaymentData(15,2,2012,"test.dta", getBvoExpense());
		List<String> l = new BvoDtaFormater().format(payment, 1, payment.getExpenses().get(0));
		Assert.assertEquals(3, l.size());

		// check length
		for(int i=0;i<3;i++) {
			Assert.assertEquals("line "+i+"'s length is not 128",128, l.get(i).length());
		}
		// check content
		for(int i=0;i<3;i++) {
			Assert.assertEquals("Line "+i+" is wrong", expected[i], l.get(i).toUpperCase());
		}
	}
	
	public static Expense getBvoExpense() {	
		Account assetAcc = new Account(null, 1, "asset", ASSET, null);						
		Account ecAcc = new Account(assetAcc, 2, "courant", ASSET, chf);
		ecAcc.setOwner(DtaHelperTest.getWilliamsBankDetails());
		
		Account vehicleAcc = new Account(null, 4, "vehicle", EXPENSE, null);
		Account gasAcc = new Account(vehicleAcc, 1, "gas", EXPENSE, chf);			
		
		Payee garageDeLEtraz = new Payee();
		garageDeLEtraz.setPrefix("Garage de l'");
		garageDeLEtraz.setName("Etraz");
		garageDeLEtraz.setAddress1("Case postale 1359");
		garageDeLEtraz.setCity(nyon);
		garageDeLEtraz.setPostalAccount("17-1288-4");
		
		// === Expense 1 ===
		BigDecimal amount = new BigDecimal("22.50");
		Expense expense = new Expense();
		expense.setId(1234L);		
		expense.setType(bvo);
		expense.setAmount(amount);
		expense.setCurrency(chf);
		expense.setPayee(garageDeLEtraz);
		expense.setDate(createDate(01,03,2013));
		expense.setExternalReference("56124 63813 00000 10314");

		TransactionLine line1 = new TransactionLine();
		line1.setExpense(expense);
		line1.setAccount(ecAcc);
		line1.setFactor(TransactionLineEnum.OUT);
		line1.setAmount(amount);
		line1.updateValue();
		
		TransactionLine line2 = new TransactionLine();
		line2.setExpense(expense);
		line2.setAccount(gasAcc);
		line2.setFactor(TransactionLineEnum.IN);
		line2.setAmount(amount);
		line2.updateValue();
		
		expense.setTransactions(Arrays.asList(line1, line2));
		
		return expense;
	}
}
