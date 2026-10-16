package com.veterinaria.demo.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

// import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CrearMascotaTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:4200";

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        // options.addArguments("--headless");
        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /* Caso de prueba 1 */
    @Test
    public void systemTest_CrearMascota_MostrarMascota() {
        // Paso 1: Configuración inicial del navegador
        driver.manage().window().maximize();
        driver.get(BASE_URL);

        // Paso 2: Intento fallido de login (vet2/pass1)
        WebElement botonLogin = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-btn")));
        botonLogin.click();

        WebElement campoUsuario = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        campoUsuario.clear();
        campoUsuario.sendKeys("vet2");

        WebElement campoContrasena = driver.findElement(By.id("password"));
        campoContrasena.clear();
        campoContrasena.sendKeys("pass1");

        WebElement botonEnviarLogin = driver.findElement(By.id("loginSubmit"));
        botonEnviarLogin.click();

        // Paso 3: Verificación de mensaje de error
        WebElement mensajeError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        assertTrue(mensajeError.isDisplayed(), "El mensaje de error debería estar visible");
        assertEquals("Usuario o contraseña incorrectos.", mensajeError.getText(), "El mensaje de error no coincide");

        // Paso 4: Intento exitoso de login (vet1/pass1)
        campoUsuario.clear();
        campoUsuario.sendKeys("vet1");

        campoContrasena.clear();
        campoContrasena.sendKeys("pass1");

        botonEnviarLogin.click();

        // Paso 5: Navegación a sección de clientes
        WebElement enlaceClientes = wait.until(ExpectedConditions.elementToBeClickable(By.className("clientes-link")));
        enlaceClientes.click();

        // Paso 6: Inicio de registro de nuevo cliente
        WebElement botonAgregarCliente = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-agregar")));
        botonAgregarCliente.click();

        // Paso 7: Llenado de formulario de cliente
        WebElement campoNombreCliente = driver.findElement(By.id("nombre"));
        campoNombreCliente.clear();
        campoNombreCliente.sendKeys("Sebastián Angarita");

        WebElement campoCorreoCliente = driver.findElement(By.id("correo"));
        campoCorreoCliente.clear();
        campoCorreoCliente.sendKeys("angaritat.js@javeriana.edu.co");

        WebElement campoCelularCliente = driver.findElement(By.id("celular"));
        campoCelularCliente.clear();
        campoCelularCliente.sendKeys("3194955734");

        WebElement campoCedulaCliente = driver.findElement(By.id("cedula"));
        campoCedulaCliente.clear();
        campoCedulaCliente.sendKeys("31415926335");

        WebElement campoNombreUsuarioCliente = driver.findElement(By.id("nombreUsuario"));
        campoNombreUsuarioCliente.clear();
        campoNombreUsuarioCliente.sendKeys("Ing. Angarita");

        WebElement campoFotoCliente = driver.findElement(By.id("foto"));
        campoFotoCliente.clear();
        campoFotoCliente.sendKeys("https://i.pinimg.com/236x/48/12/96/48129664d8dc5b068eb4d68a381c6c81.jpg");

        WebElement campoContrasenaCliente = driver.findElement(By.id("contrasena"));
        campoContrasenaCliente.clear();
        campoContrasenaCliente.sendKeys("1234");

        // Paso 8: Error en confirmación de contraseña (12345)
        WebElement campoConfirmarContrasena = driver.findElement(By.id("confirm_password"));
        campoConfirmarContrasena.clear();
        campoConfirmarContrasena.sendKeys("12345");

        WebElement botonCrearCliente = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("crear_cliente")));
        try {
            botonCrearCliente.click();
        } catch (Exception e) {
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", botonCrearCliente);
        }

        // Paso 9: Corrección de contraseña (1234)
        campoConfirmarContrasena.clear();
        campoConfirmarContrasena.sendKeys("1234");

        WebElement botonCrearClienteConfirmado = wait.until(ExpectedConditions.elementToBeClickable(By.className("crear_cliente")));
        try {
            botonCrearClienteConfirmado.click();
        } catch (Exception e) {
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", botonCrearCliente);
        }
        
        // Paso 10: Navegación a sección de mascotas
        WebElement enlaceMascotas = wait.until(ExpectedConditions.elementToBeClickable(By.className("mascotas-link")));
        enlaceMascotas.click();

        // Paso 11: Inicio de registro de nueva mascota
        WebElement botonAgregarMascota = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-agregar")));
        botonAgregarMascota.click();

        // Paso 12: Llenado correcto de formulario de mascota
        WebElement campoNombreMascota = driver.findElement(By.id("nombre"));
        campoNombreMascota.clear();
        campoNombreMascota.sendKeys("Scooby-Doo");

        WebElement campoRazaMascota = driver.findElement(By.id("raza"));
        campoRazaMascota.clear();
        campoRazaMascota.sendKeys("Gran Danés Cobarde");

        WebElement campoEdadMascota = driver.findElement(By.id("edad"));
        campoEdadMascota.clear();
        campoEdadMascota.sendKeys("53");

        WebElement campoPesoMascota = driver.findElement(By.id("peso"));
        campoPesoMascota.clear();
        campoPesoMascota.sendKeys("70.5");

        WebElement campoEnfermedadMascota = driver.findElement(By.id("enfermedad"));
        campoEnfermedadMascota.clear();
        campoEnfermedadMascota.sendKeys("Miedo crónico a fantasmas");

        Select selectorCliente = new Select(driver.findElement(By.id("cliente")));
        selectorCliente.selectByVisibleText("Sebastián Angarita");

        WebElement campoFotoMascota = driver.findElement(By.id("foto"));
        campoFotoMascota.clear();
        campoFotoMascota.sendKeys("https://i.pinimg.com/1200x/ee/3c/16/ee3c16658a352467a8e727d300fe314f.jpg");

        WebElement campoFechaNacimiento = driver.findElement(By.id("fechaNacimiento"));
        campoFechaNacimiento.clear();
        campoFechaNacimiento.sendKeys("09131969");

        WebElement campoFechaIngreso = driver.findElement(By.id("fechaIngreso"));
        campoFechaIngreso.clear();
        campoFechaIngreso.sendKeys("06062025");

        WebElement campoFechaSalida = driver.findElement(By.id("fechaSalida"));
        campoFechaSalida.clear();
        campoFechaSalida.sendKeys("07072025");

        Select selectorEstado = new Select(driver.findElement(By.id("estado")));
        selectorEstado.selectByValue("1");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            WebElement botonCrearMascota = wait.until(ExpectedConditions.elementToBeClickable(By.className("crear_mascota")));
            botonCrearMascota.click();
        } catch (StaleElementReferenceException e) {
            // Volver a buscar el botón si está stale
            WebElement botonCrearMascota = wait.until(ExpectedConditions.elementToBeClickable(By.className("crear_mascota")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonCrearMascota);
        }


        // Paso 13: Cierre de sesión del veterinario
        WebElement botonCerrarSesion = wait.until(ExpectedConditions.elementToBeClickable(By.className("logout")));
        botonCerrarSesion.click();

        // Paso 14: Inicio de sesión del cliente (Ing. Angarita/1234)
        WebElement botonLoginNuevo = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-btn")));
        botonLoginNuevo.click();

        WebElement campoUsuarioNuevo = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        campoUsuarioNuevo.clear();
        campoUsuarioNuevo.sendKeys("Ing. Angarita");

        WebElement campoContrasenaNuevo = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("password")));
        campoContrasenaNuevo.clear();
        campoContrasenaNuevo.sendKeys("1234");

        WebElement botonEnviarLoginNuevo = wait.until(ExpectedConditions.elementToBeClickable(By.id("loginSubmit")));
        botonEnviarLoginNuevo.click();
        botonEnviarLoginNuevo.click();

        // Paso 15: Verificación de datos de mascota en tabla
        WebElement tablaMascotas = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("recentOrders")));
        assertTrue(tablaMascotas.isDisplayed(), "La tabla de mascotas debería estar visible");

        List<WebElement> filasMascotas = driver.findElements(By.className("fila-mascota"));
        assertTrue(filasMascotas.size() > 0, "Debería haber al menos una mascota en la tabla");

        WebElement primeraFilaMascota = filasMascotas.get(0);
        WebElement celdaNombreMascota = primeraFilaMascota.findElement(By.className("celda-nombre"));
        WebElement celdaRazaMascota = primeraFilaMascota.findElement(By.className("celda-raza"));
        WebElement celdaEstadoMascota = primeraFilaMascota.findElement(By.className("status"));
        WebElement imagenMascota = primeraFilaMascota.findElement(By.className("img-mascota"));

        // Paso 16: Verificación con datos
        assertEquals("Scooby-Doo", celdaNombreMascota.getText(), "El nombre de la mascota no coincide");
        assertEquals("Gran Danés Cobarde", celdaRazaMascota.getText(), "La raza de la mascota no coincide");
        assertEquals("Inactivo", celdaEstadoMascota.getText(), "El estado de la mascota no coincide");
        assertEquals("https://i.pinimg.com/1200x/ee/3c/16/ee3c16658a352467a8e727d300fe314f.jpg", 
            imagenMascota.getAttribute("src"), "La URL de la foto no coincide");
    }

    /* @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    } */
}