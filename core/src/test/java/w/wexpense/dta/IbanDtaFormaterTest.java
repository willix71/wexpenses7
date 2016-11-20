package w.wexpense.dta;

import static w.wexpense.dta.DtaCommonTestData.ch;
import static w.wexpense.dta.DtaCommonTestData.chf;
import static w.wexpense.dta.DtaCommonTestData.createDate;
import static w.wexpense.dta.DtaCommonTestData.createPaymentData;
import static w.wexpense.dta.DtaCommonTestData.iban;
import static w.wexpense.dta.DtaCommonTestData.williamKeyser;
import static w.wexpense.model.enums.AccountEnum.ASSET;
import static w.wexpense.model.enums.AccountEnum.EXPENSE;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.iterable.Extractor;
import org.junit.Test;

import w.wexpense.model.Account;
import w.wexpense.model.City;
import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.Payment;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;

public class IbanDtaFormaterTest {

	String[] expected = {
			"01000000            00000121218228    WEX010000583600WEX01ID234567890CH650022822851333340B   121218CHF150,00                    ",
			"02            WILLIAM KEYSER                     11 CH DU GRAND NOYER               1197 PRANGINS (CH)                 SPORTS:FO",
			"03DUBS SA                             1196 GLAND (CH)                    CH2500243243G76735570                                  ",
			"04F.C. BURSINS 'VETERANS'                                               1183 BURSINS (CH)                                       ",
			"05UWILLIAM KEYSER SAISON 2012-2013                                                                          2                   "
	};
	
	@Test
	public void testIbanExpense() throws DtaException {
		Payment payment = createPaymentData(18,12,2012,"test.dta", getIbanExpense());
		List<String> l = new IbanDtaFormater().format(payment, 5, payment.getExpenses().get(0));
		
		// use SoftAssertions instead of direct assertThat methods
		SoftAssertions softly = new SoftAssertions();
		softly.assertThat(l.size()).isEqualTo(5);
		
		// check length
		for(int i=0;i<3;i++) {
			softly.assertThat(l.get(i).length()).as("line "+i).isEqualTo(128);
		}
		
		// check content
		softly.assertThat(l).extracting(new Extractor<String, String>() {
			  public String extract(String input) {
				  return input.toUpperCase();
			  }
		}).containsOnly(expected);

		// Don't forget to call SoftAssertions global verification !
		softly.assertAll();
	}
	
	public static Expense getIbanExpense() {	
		Account assetAcc = new Account(null, 1, "asset", ASSET, null);						
		Account ecAcc = new Account(assetAcc, 2, "courant", ASSET, chf);
		ecAcc.setOwner(williamKeyser);
		
		Account sportsAcc = new Account(null, 4, "sports", EXPENSE, null);
		Account foot = new Account(sportsAcc, 1, "football", EXPENSE, chf);			
			
		Payee brp = new Payee();
		brp.setName("F.C. Bursins 'Veterans'");
		brp.setCity(new City("1183", "Bursins", ch));
		brp.setIban("CH25 0024 3243 G767 3557 0");
		
		Payee ubs = new Payee();
		ubs.setName("UBS SA");
		ubs.setCity(new City("1196","Gland",ch));
		ubs.setPostalAccount("80-2-2");
		brp.setBankDetails(ubs);
		
		// === Expense 1 ===
		BigDecimal amount = new BigDecimal("150");
		Expense expense = new Expense();
		expense.setId(1234567890L);
		expense.setType(iban);
		expense.setAmount(amount);
		expense.setCurrency(chf);
		expense.setDate(createDate(18,12,2012));
		expense.setPayee(brp);
		expense.setExternalReference("William Keyser Saison 2012-2013");
		
		TransactionLine line1 = new TransactionLine();
		line1.setExpense(expense);
		line1.setAccount(ecAcc);
		line1.setFactor(TransactionLineEnum.OUT);
		line1.setAmount(amount);
		line1.updateValue();
		
		TransactionLine line2 = new TransactionLine();
		line2.setExpense(expense);
		line2.setAccount(foot);
		line2.setFactor(TransactionLineEnum.IN);
		line2.setAmount(amount);
		line2.updateValue();
		
		expense.setTransactions(Arrays.asList(line1, line2));
		
		return expense;
	}
}
