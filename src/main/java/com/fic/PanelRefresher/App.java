package com.fic.PanelRefresher;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fic.PanelRefresher.constant.Api;
import com.fic.PanelRefresher.util.StringUtils;

public class App {
	public static String directory;
	public static String password;
	public static String username;
	
	public static Map<String, Integer> deadlines;
	public static Map<String, WebDriver> drivers;
	public static Map<String, String> names;
	public static Map<String, Integer> panels;
	public static Map<String, String> zooms;
	
	/**
	 * Construtor da classe.
	 * @author Gugatb
	 * @date 01/10/2018
	 * @param pArgs os argumentos
	 */
	public static void main(String[] pArgs) {
		deadlines = new HashMap<String, Integer>();
		drivers = new HashMap<String, WebDriver>();
		names = new HashMap<String, String>();
		panels = new HashMap<String, Integer>();
		zooms = new HashMap<String, String>();
		directory = "";
		password = "";
		username = "";
		execute();
	}
	
	/**
	 * Verificar se contém.
	 * @author Gugatb
	 * @date 02/10/2018
	 * @param pDriver o driver
	 * @param pId o id
	 * @param pTag a etiqueta
	 * @return true se contém, caso contrário false
	 * 
	 */
	private static boolean contains(WebDriver pDriver, String pId, String pTag) {
		boolean contains = false;
		
		for (WebElement element : pDriver.findElements(By.tagName(pTag))) {
			String control = element.getAttribute("id");
			
			if (control != null && control.trim().equalsIgnoreCase(pId)) {
				contains = true;
			}
		}
		return contains;
	}
	
	/**
	 * Executar a aplicação.
	 * @author Gugatb
	 * @date 01/10/2018
	 */
	private static void execute() {
		StringUtils util = new StringUtils();
		
		try {
			// Definir o leitor.
			Ini reader = new Ini();
			reader.load(new FileReader(Api.DATA.getValue()));
			
			// Ler as seções.
			Set<String> sectionNames = reader.keySet();
			for (String name : sectionNames) {
				Map<String, String> map = reader.get(name);
				
				if (name.equals("Propriedades")) {
					directory = map.get("Diretorio");
					password = map.get("Senha");
					username = map.get("Usuario");
				}
				else {
					String time = map.get("Minutos");
					String url = map.get("Url");
					String zoom = map.get("Zoom");
					
					if (util.isNumber(time) && Integer.valueOf(time) > 0) {
						// Configurar o prazo.
						deadlines.put(url, Integer.valueOf(time));
						
						// Configurar a página.
						WebDriver driver = setUp(directory, url);
						drivers.put(url, driver);
						
						// Configurar os nomes.
						names.put(url, name);
						
						// Configurar o painel.
						panels.put(url, Integer.valueOf(time));
						
						// Configurar os zooms.
						zooms.put(url, zoom);
					}
				}
			}
			
			while (true) {
				int counter = 0;
				
				System.out.println("[" + new Date() + "]: Aguardando todas as páginas logarem.");
				
				// Executar a aplicação a cada 10 segundos.
				sleep(1000 * 10);
				
				// Verificar quais páginas logaram.
				for (WebDriver driver : drivers.values()) {
					if (contains(driver, "AppBodyHeader", "div")) {
						counter++;
					}
					else if (username != null && !username.isEmpty() &&
						password != null && !password.isEmpty()) {
						login(driver, username, password);
					}
				}
				
				// Todas as páginas logaram.
				if (counter >= panels.size()) {
					break;
				}
			}
			
			System.out.println("[" + new Date() + "]: As páginas foram logadas.");
			
			// Remover os elementos e definir o Zoom das páginas.
			System.out.println("[" + new Date() + "]: Remover componentes das páginas.");
			System.out.println("[" + new Date() + "]: Definir o Zoom das páginas.");
			for (Entry<String, WebDriver> entry : drivers.entrySet()) {
				removeElements(entry.getValue());
				setZoom(entry.getValue(), zooms.get(entry.getKey()));
			}
			
			while (true) {
				// Executar a aplicação a cada 1 minuto.
				sleep(1000 * 60);
				
				for (Entry<String, Integer> entry : deadlines.entrySet()) {
					String key = entry.getKey();
					Integer time = entry.getValue();
					
					if (time - 1 > 0) {
						deadlines.put(key, time - 1);
					}
					else {
						WebDriver driver = drivers.get(key);
						String name = names.get(key);
						String zoom = zooms.get(key);
						deadlines.put(key, panels.get(key));
						refresh(driver, name);
						setZoom(driver, zoom);
					}
				}
			}
		}
		catch (InvalidFileFormatException exception) {
			exception.printStackTrace();
		}
		catch (FileNotFoundException exception) {
			exception.printStackTrace();
		}
		catch (IOException exception) {
			exception.printStackTrace();
		}
		catch (Exception expcetion) {
			expcetion.printStackTrace();
		}
		finally {
			for (WebDriver driver : drivers.values()) {
				if (driver != null) {
					driver.close();
					driver.quit();
				}
			}
		}
	}
	
	/**
	 * Logar na página.
	 * @author Gugatb
	 * @date 02/10/2018
	 * @param pDriver o driver
	 * @param pUsername o nome de usuário
	 * @param pPassword a senha
	 */
	private static void login(WebDriver pDriver, String pUsername, String pPassword) {
		try {
			for (WebElement element : pDriver.findElements(By.tagName("input"))) {
				String control = element.getAttribute("id");
				
				if (control != null && control.equalsIgnoreCase("username")) {
					element.sendKeys(pUsername);
					element.sendKeys(Keys.ESCAPE);
				}
				else if (control != null && control.equalsIgnoreCase("password")) {
					element.sendKeys(pPassword);
					element.sendKeys(Keys.ESCAPE);
				}
				else if (control != null && control.equalsIgnoreCase("Login")) {
					Actions actions = new Actions(pDriver);
					actions.moveToElement(element).click().perform();
					wait(pDriver, 30L);
				}
			}
		}
		catch (Exception exception) { }
	}
	
	/**
	 * Atualizar o painel.
	 * @author Gugatb
	 * @date 02/10/2018
	 * @param pDriver o driver
	 * @param pName o nome
	 */
	private static void refresh(WebDriver pDriver, String pName) {
		for (WebElement element : pDriver.findElements(By.tagName("span"))) {
			String control = element.getAttribute("class");
			String text = element.getText();
			
			if (control != null && text != null &&
				control.trim().equalsIgnoreCase("menuButtonLabel") &&
				text.trim().equalsIgnoreCase("Atualizar")) {
				setZoom(pDriver, "1.0");
				Actions actions = new Actions(pDriver);
				actions.moveToElement(element).click().perform();
				wait(pDriver, 30L);
				removeElements(pDriver);
				
				System.out.println("[" + new Date() + "]: Página atualizada = " + pName);
				break;
			}
		}
	}
	
	/**
	 * Remover o elemento.
	 * @author Gugatb
	 * @date 02/10/2018
	 * @param pDriver o driver
	 * @param pId o id
	 */
	private static void removeElement(WebDriver pDriver, String pId) {
		try {
			JavascriptExecutor js;
			if (pDriver instanceof JavascriptExecutor) {
				js = (JavascriptExecutor)pDriver;
				js.executeScript("return document.getElementById('" + pId + "').remove();");
			}
		}
		catch (Exception exception) {
//			exception.printStackTrace();
		}
	}
	
	/**
	 * Remover os elementos.
	 * @author Gugatb
	 * @date 02/10/2018
	 * @param pDriver o driver
	 */
	private static void removeElements(WebDriver pDriver) {
		// Remover os elementos.
		removeElement(pDriver, "AppBodyHeader");
		removeElement(pDriver, "section_header");
	}
	
	/**
	 * Remover o elemento.
	 * @author Gugatb
	 * @date 04/10/2018
	 * @param pDriver o driver
	 * @param pZoom o zoom
	 */
	private static void setZoom(WebDriver pDriver, String pZoom) {
		try {
			JavascriptExecutor js;
			if (pDriver instanceof JavascriptExecutor) {
				js = (JavascriptExecutor)pDriver;
				js.executeScript("document.body.style.zoom = '" + pZoom +"'");
			}
		}
		catch (Exception exception) {
//			exception.printStackTrace();
		}
	}
	
	/**
	 * Dormir por um período.
	 * @author Gugatb
	 * @date 01/10/2018
	 * @param pTime o tempo
	 */
	private static void sleep(long pTime) {
		try {
			Thread.sleep(pTime);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Configurar o driver.
	 * @author Gugatb
	 * @date 01/10/2018
	 * @param pDiverDirectory o diretório do driver
	 * @param pUrl a url
	 * @return driver o driver
	 */
	private static WebDriver setUp(String pDiverDirectory, String pUrl) {
		System.setProperty("webdriver.chrome.driver", pDiverDirectory);
		
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().window().fullscreen();
		driver.navigate().to(pUrl);
		driver.get(pUrl);
		wait(driver, 30L);
		return driver;
	}
	
	/**
	 * Aguardar o carregamento.
	 * @author Gugatb
	 * @date 01/10/2018
	 * @param pDriver o driver
	 * @param pTime o tempo
	 */
	public static void wait(WebDriver pDriver, long pTime) {
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver pDriver) {
				return ((JavascriptExecutor)pDriver).executeScript("return document.readyState").equals("complete");
			}
		};
		WebDriverWait wait = new WebDriverWait(pDriver, pTime);
		wait.until(pageLoadCondition);
	}
}
