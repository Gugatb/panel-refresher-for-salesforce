package com.fic.PanelRefresher.util;

public class StringUtils {
	/**
	 * Obter os números.
	 * @author Gugatb
	 * @date 02/10/2018
	 * @param pText o texto
	 * @return numbers os números
	 */
	public String getNumbers(String pText) {
		return pText != null ? pText.replaceAll("\\D+", "") : "";
	}
	
	/**
	 * Verificar se é número.
	 * @author Gugatb
	 * @date 02/10/2018
	 * @param pNumber o número
	 * @return true se é número, caso contrário false
	 */
	public boolean isNumber(String pNumber) {
		boolean isNumber = false;
		
		try {
			Integer.valueOf(pNumber);
			isNumber = true;
		}
		catch (Exception exception) {
			//exception.printStackTrace();
			isNumber = false;
		}
		return isNumber;
	}
}
