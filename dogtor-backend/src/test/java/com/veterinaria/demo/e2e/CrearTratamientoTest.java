package com.veterinaria.demo.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
public class CrearTratamientoTest {
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

    /* Caso de prueba 2 */
    @Test
    public void SystemTest_CrearTratamiento_MostrarTratamiento() {
        // Paso 1: Iniciar navegador
        driver.manage().window().maximize();
        driver.get(BASE_URL);
        
        // Abrir segunda pestaña para tener sesiones paralelas
        ((JavascriptExecutor) driver).executeScript("window.open()");
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        
        // === PESTAÑA 1: ADMINISTRADOR ===
        // Paso 2: Iniciar sesión como administrador para obtener datos iniciales
        driver.switchTo().window(tabs.get(0));
        driver.get(BASE_URL);
        
        // Login como admin
        WebElement botonLogin = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-btn")));
        botonLogin.click();

        WebElement campoUsuario = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        campoUsuario.clear();
        campoUsuario.sendKeys("admin");

        WebElement campoContrasena = driver.findElement(By.id("password"));
        campoContrasena.clear();
        campoContrasena.sendKeys("123");

        WebElement botonEnviarLogin = driver.findElement(By.id("loginSubmit"));
        botonEnviarLogin.click();

        // Paso 3: Registrar métricas iniciales del dashboard
        // Esperar a que se carguen las méticas
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.id("tratamientos-mes"), "0")));
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.id("ventas-totales"), "0")));
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.id("ganancias-totales"), "0")));

        // Cantidad inicial de tratamientos
        WebElement tratamientosInicialesElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tratamientos-mes")));
        int tratamientosIniciales = Integer.parseInt(tratamientosInicialesElement.getText());

        // Ventas y ganancias iniciales
        WebElement ventasInicialesElement = driver.findElement(By.id("ventas-totales"));
        BigDecimal ventasIniciales = pesos(ventasInicialesElement.getText());

        WebElement gananciasInicialesElement = driver.findElement(By.id("ganancias-totales"));
        BigDecimal gananciasIniciales = pesos(gananciasInicialesElement.getText());

        // Paso 4: Obtener inventario inicial de Advantix desde la sección de medicamentos
        WebElement enlaceMedicamentos = wait.until(ExpectedConditions.elementToBeClickable(By.className("medicamentos-link")));
        enlaceMedicamentos.click();
        
        Map<String, String> filaAdvantix = buscarFilaMedicamento("Advantix");

        // Obtener unidades disponibles y vendidas de Advantix
        int advantixDisponiblesInicial = entero(filaAdvantix.get("medicamento-disponibles"));
        int advantixVendidosInicial = entero(filaAdvantix.get("medicamento-vendidos"));

        // Obtener precio de venta de Advantix
        BigDecimal precioAdvantix = pesos(filaAdvantix.get("medicamento-precio-venta"));

        // Obtener precio de compra de Advantix
        BigDecimal precioCompraAdvantix = pesos(filaAdvantix.get("medicamento-precio-compra"));


        // Obtener precio de Cirugía
        WebElement enlaceServicios = wait.until(ExpectedConditions.elementToBeClickable(By.className("servicios-link")));
        enlaceServicios.click();
        
        List<WebElement> filasServicios = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("servicio-fila")));
        BigDecimal precioCirugia = BigDecimal.ZERO;
        for (WebElement fila : filasServicios) {
            WebElement nombreServicio = fila.findElement(By.className("servicio-nombre"));
            if (nombreServicio.getText().equals("Cirugía")) {
                WebElement precioElement = fila.findElement(By.className("servicio-precio"));
                precioCirugia = pesos(precioElement.getText());
                break;
            }
        }
        
        // Volver al dashboard
        WebElement enlaceDashboard = wait.until(ExpectedConditions.elementToBeClickable(By.className("admin-link")));
        enlaceDashboard.click();

        // === PESTAÑA 2: VETERINARIO ===
        // Paso 5: Iniciar sesión como veterinario para crear tratamiento
        driver.switchTo().window(tabs.get(1));
        driver.get(BASE_URL);

        // La sesión se guarda en localStorage y se comparte entre pestañas: esta
        // pestaña abre ya con la sesión del admin. Se cierra desde el menú del
        // header (logout navega a /home y recarga) para poder entrar como veterinario.
        WebElement menuUsuario = wait.until(ExpectedConditions.elementToBeClickable(By.className("user-btn")));
        menuUsuario.click();
        WebElement cerrarSesionHeader = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'dropdown-menu')]//a[normalize-space()='Cerrar sesión']")));
        cerrarSesionHeader.click();

        // Login como veterinario
        botonLogin = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-btn")));
        botonLogin.click();

        campoUsuario = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        campoUsuario.clear();
        campoUsuario.sendKeys("vet1");

        campoContrasena = driver.findElement(By.id("password"));
        campoContrasena.clear();
        campoContrasena.sendKeys("pass1");

        botonEnviarLogin = driver.findElement(By.id("loginSubmit"));
        botonEnviarLogin.click();

        // Paso 6: Navegar a la sección de tratamientos
        WebElement enlaceHistorial = wait.until(ExpectedConditions.elementToBeClickable(By.className("historial-link")));
        enlaceHistorial.click();

        // Paso 7: Crear nuevo tratamiento
        WebElement botonAgregarTratamiento = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-agregar")));
        botonAgregarTratamiento.click();

        // Rellenar formulario de tratamiento
        WebElement campoCodigo = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("codigo")));
        campoCodigo.clear();
        campoCodigo.sendKeys("TRAT100");

        WebElement selectServicio = driver.findElement(By.id("idServicio"));
        Select servicioSelect = new Select(selectServicio);
        servicioSelect.selectByVisibleText("Cirugía");

        WebElement selectMascota = driver.findElement(By.id("idMascota"));
        Select mascotaSelect = new Select(selectMascota);
        mascotaSelect.selectByVisibleText("Firulais - Carlos Gómez");

        WebElement campoDetalles = driver.findElement(By.id("detalles"));
        campoDetalles.clear();
        campoDetalles.sendKeys("Tratamiento postoperatorio para recuperación completa");

        WebElement selectMedicamento = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("select-medicamento")));
        Select medicamentoSelect = new Select(selectMedicamento);
        medicamentoSelect.selectByVisibleText("Advantix");

        WebElement campoDosis = driver.findElement(By.className("input-dosis"));
        campoDosis.clear();
        campoDosis.sendKeys("1");

        // Guardar tratamiento (con manejo de posibles excepciones)
        WebElement botonGuardar = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-guardar")));
        try {
            botonGuardar.click();
        } catch (Exception e) {
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", botonGuardar);
        }

        // === VERIFICACIÓN EN LISTADO DE TRATAMIENTOS ===
        // Paso 8: Buscar el tratamiento recién creado en la tabla
        WebElement tablaTratamientos = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tabla-tratamientos")));
        List<WebElement> filasTratamientos = tablaTratamientos.findElements(By.tagName("tr"));

        WebElement tratamientoCreado = null;
        for (WebElement fila : filasTratamientos) {
            List<WebElement> celdas = fila.findElements(By.tagName("td"));
            if (!celdas.isEmpty() && celdas.get(0).getText().equals("TRAT100")) {
                tratamientoCreado = fila;
                break;
            }
        }
        assertNotNull(tratamientoCreado, "El tratamiento creado no aparece en la lista");

        // Paso 9: Verificar datos básicos del tratamiento en el listado
        List<WebElement> celdas = tratamientoCreado.findElements(By.tagName("td"));
        assertFalse(celdas.get(1).getText().isEmpty(), "La fecha del tratamiento no debe estar vacía");
        assertEquals("Firulais", celdas.get(2).getText(), "El nombre de la mascota no coincide");
        assertEquals("Cirugía", celdas.get(3).getText(), "El nombre del servicio no coincide");

        // Paso 10: Ver detalles completos del tratamiento
        WebElement enlaceDetalles = celdas.get(4).findElement(By.className("btn-detalles"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", enlaceDetalles);
        try {
            enlaceDetalles.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", enlaceDetalles);
        }

        // Verificar todos los campos en la vista de detalles
        WebElement codigoDetalle = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tratamiento-codigo")));
        assertTrue(codigoDetalle.getText().contains("TRAT100"), "El código del tratamiento no coincide");

        WebElement fechaDetalle = driver.findElement(By.id("tratamiento-fecha"));
        assertFalse(fechaDetalle.getText().isEmpty(), "La fecha del tratamiento no debe estar vacía");

        WebElement detallesDetalle = driver.findElement(By.id("tratamiento-detalles"));
        assertEquals("Tratamiento postoperatorio para recuperación completa", detallesDetalle.getText(), 
            "Los detalles del tratamiento no coinciden");

        WebElement servicioDetalle = driver.findElement(By.id("tratamiento-servicio"));
        assertEquals("Cirugía", servicioDetalle.getText(), "El servicio del tratamiento no coincide");

        WebElement mascotaDetalle = driver.findElement(By.id("tratamiento-mascota"));
        assertEquals("Firulais", mascotaDetalle.getText(), "La mascota del tratamiento no coincide");

        WebElement medicamentoDetalle = driver.findElement(By.id("tratamiento-medicamento"));
        assertEquals("Advantix", medicamentoDetalle.getText(), "El medicamento del tratamiento no coincide");

        // === VERIFICACIÓN EN PESTAÑA DE ADMINISTRADOR ===
        // Paso 11: Calcular valores esperados después de crear el tratamiento
        int tratamientosEsperados = tratamientosIniciales + 1;
        int advantixDisponiblesEsperado = advantixDisponiblesInicial - 1;
        int advantixVendidosEsperado = advantixVendidosInicial + 1;
        BigDecimal cantidadUsada = new BigDecimal(1);
        BigDecimal gananciaMedicamento = precioAdvantix.subtract(precioCompraAdvantix).multiply(cantidadUsada);

        BigDecimal ventasEsperadas = ventasIniciales
                .add(precioCirugia)
                .add(precioAdvantix.multiply(cantidadUsada));

        BigDecimal gananciasEsperadas = gananciasIniciales
                .add(precioCirugia)
                .add(gananciaMedicamento);

        // Paso 12: Volver a pestaña de admin, cerrar sesión y volver a ingresar
        driver.switchTo().window(tabs.get(0));
        
        // Cerrar sesión        
        WebElement botonCerrarSesion = wait.until(ExpectedConditions.elementToBeClickable(By.className("logout")));
        botonCerrarSesion.click();
        
        // Volver a iniciar sesión como admin
        WebElement botonLoginAdmin = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-btn")));
        botonLoginAdmin.click();

        WebElement campoUsuarioAdmin = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
        campoUsuarioAdmin.clear();
        campoUsuarioAdmin.sendKeys("admin");

        WebElement campoContrasenaAdmin = driver.findElement(By.id("password"));
        campoContrasenaAdmin.clear();
        campoContrasenaAdmin.sendKeys("123");

        WebElement botonEnviarLoginAdmin = driver.findElement(By.id("loginSubmit"));
        botonEnviarLoginAdmin.click();

        // Esperar a que se carguen las métricas
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.id("tratamientos-mes"), "0")));
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.id("ventas-totales"), "0")));
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.id("ganancias-totales"), "0")));
        
        // Verificar actualización de tratamientos
        WebElement tratamientosActualesElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("tratamientos-mes")));
        int tratamientosActuales = Integer.parseInt(tratamientosActualesElement.getText());
        assertEquals(tratamientosEsperados, tratamientosActuales, "La cantidad de tratamientos no coincide");

        // Verificar actualización de ventas y ganancias
        WebElement ventasActualesElement = driver.findElement(By.id("ventas-totales"));
        BigDecimal ventasActuales = pesos(ventasActualesElement.getText());
        assertEquals(ventasEsperadas.setScale(2), ventasActuales.setScale(2), "Las ventas totales no coinciden");

        WebElement gananciasActualesElement = driver.findElement(By.id("ganancias-totales"));
        BigDecimal gananciasActuales = pesos(gananciasActualesElement.getText());
        assertEquals(gananciasEsperadas.setScale(2), gananciasActuales.setScale(2), "Las ganancias totales no coinciden");

        // Paso 13: Verificar actualización de inventario de Advantix en la sección de medicamentos
        WebElement nuevoEnlaceMedicamentos = wait.until(ExpectedConditions.elementToBeClickable(By.className("medicamentos-link")));
        nuevoEnlaceMedicamentos.click();
        
        filaAdvantix = buscarFilaMedicamento("Advantix");

        // Verificar unidades disponibles actualizadas
        int advantixDisponiblesActual = entero(filaAdvantix.get("medicamento-disponibles"));
        assertEquals(advantixDisponiblesEsperado, advantixDisponiblesActual,
            "Las unidades disponibles de Advantix no coinciden");

        // Verificar unidades vendidas actualizadas
        int advantixVendidosActual = entero(filaAdvantix.get("medicamento-vendidos"));
        assertEquals(advantixVendidosEsperado, advantixVendidosActual,
            "Las unidades vendidas de Advantix no coinciden");
    }

    /**
     * Busca un medicamento en la tabla de inventario y devuelve el texto de las
     * celdas de su fila. Espera a que la tabla muestre el resultado de la
     * búsqueda (todas las filas coinciden con el nombre) y lee las celdas dentro
     * de la misma espera, porque Angular vuelve a renderizar las filas al
     * cargar y al filtrar (si no, StaleElementReferenceException).
     */
    private Map<String, String> buscarFilaMedicamento(String nombre) {
        WebElement campoBusqueda = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("input-busqueda-medicamento")));
        campoBusqueda.clear();
        campoBusqueda.sendKeys(nombre);
        driver.findElement(By.className("btn-buscar")).click();

        return wait.ignoring(StaleElementReferenceException.class).until(d -> {
            List<WebElement> filas = d.findElements(By.className("medicamento-fila"));
            if (filas.isEmpty()) {
                return null;
            }
            Map<String, String> encontrada = null;
            for (WebElement fila : filas) {
                String nombreFila = fila.findElement(By.className("medicamento-nombre")).getText().trim();
                if (!nombreFila.toLowerCase().contains(nombre.toLowerCase())) {
                    return null; // la tabla aún no muestra el resultado de la búsqueda
                }
                if (encontrada == null && nombreFila.equals(nombre)) {
                    encontrada = new HashMap<>();
                    for (String clase : List.of("medicamento-disponibles", "medicamento-vendidos",
                            "medicamento-precio-venta", "medicamento-precio-compra")) {
                        encontrada.put(clase, fila.findElement(By.className(clase)).getText());
                    }
                }
            }
            return encontrada;
        });
    }

    /**
     * Lee el número entero al inicio de la celda. La celda de unidades
     * disponibles puede traer además el badge "Bajo stock".
     */
    private static int entero(String texto) {
        return Integer.parseInt(texto.trim().split("\\s+")[0]);
    }

    /**
     * Convierte un valor mostrado con el pipe currency:'COP':'symbol-narrow':'1.0-0'
     * en locale es-CO (por ejemplo "$ 1.234.567", con espacio no separable)
     * a BigDecimal. Ese formato no tiene decimales, así que basta con
     * conservar los dígitos y el signo.
     */
    private static BigDecimal pesos(String texto) {
        return new BigDecimal(texto.replaceAll("[^0-9-]", ""));
    }

    /* @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    } */
}