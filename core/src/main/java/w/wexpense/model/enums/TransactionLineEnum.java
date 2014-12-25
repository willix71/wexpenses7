package w.wexpense.model.enums;

public enum TransactionLineEnum {

	IN(1), SUM(0), OUT(-1);
	
	private int factor;
	
	private TransactionLineEnum(int factor) {
		this.factor = factor;
	}

	public int getFactor() {
		return factor;
	}

}
