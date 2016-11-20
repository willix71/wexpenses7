package w.wexpense.dta;

import static w.wexpense.dta.DtaCommonTestData.bursins;
import static w.wexpense.dta.DtaCommonTestData.bvr;
import static w.wexpense.dta.DtaCommonTestData.chf;
import static w.wexpense.dta.DtaCommonTestData.createDate;
import static w.wexpense.dta.DtaCommonTestData.createPaymentData;
import static w.wexpense.dta.DtaCommonTestData.nyon;
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
import w.wexpense.model.Expense;
import w.wexpense.model.Payee;
import w.wexpense.model.Payment;
import w.wexpense.model.TransactionLine;
import w.wexpense.model.enums.TransactionLineEnum;

public class BvrDtaFormaterTest {

	final String[] expected = {
			"01121202            00000121218228    WEX010000182700WEX01ID000000007CH650022822851333340B         CHF260,00                    ",
			"02WILLIAM KEYSER          11 CH DU GRAND NOYER                            1197 PRANGINS (CH)      SPORTS:FOOTBALL               ",
			"03/C/12-756431-0                FOOTBALL-CLUB           BURSINS-ROLLE-PERROY                            1183 BURSINS (CH)       ",
			"04WILLIAM KEYSER              COTISATION 2012-2013                                                                              " };

	@Test
	public void testBvr() throws DtaException {
		Payment payment = createPaymentData(18, 12, 2012, "test.dta", getBvrExpense());
		List<String> l = new BvrDtaFormater().format(payment, 1, payment.getExpenses().get(0));

		// use SoftAssertions instead of direct assertThat methods
		SoftAssertions softly = new SoftAssertions();
		softly.assertThat(l.size()).isEqualTo(4);

		// check length
		for (int i = 0; i < 4; i++) {
			softly.assertThat(l.get(i).length()).as("line " + i).isEqualTo(128);
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

	public static Expense getBvrExpense() {
		Account assetAcc = new Account(null, 1, "asset", ASSET, null);
		Account ecAcc = new Account(assetAcc, 2, "courant", ASSET, chf);
		ecAcc.setOwner(williamKeyser);

		Account sportsAcc = new Account(null, 4, "sports", EXPENSE, null);
		Account foot = new Account(sportsAcc, 1, "football", EXPENSE, chf);

		Payee ubs = new Payee();
		ubs.setCity(nyon);
		ubs.setPostalAccount("12-756431-0");

		Payee brp = new Payee();
		brp.setName("FOOTBALL-CLUB");
		brp.setAddress1("BURSINS-ROLLE-PERROY");
		brp.setCity(bursins);
		brp.setBankDetails(ubs);

		// === Expense 1 ===
		BigDecimal amount = new BigDecimal("260.00");
		Expense expense = new Expense();
		expense.setId(7L);
		expense.setType(bvr);
		expense.setAmount(amount);
		expense.setCurrency(chf);
		expense.setDate(createDate(02, 12, 2012));
		expense.setPayee(brp);
		expense.setExternalReference("William Keyser;Cotisation 2012-2013");

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
