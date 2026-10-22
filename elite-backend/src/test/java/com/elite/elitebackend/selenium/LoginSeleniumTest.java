package com.elite.elitebackend.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginSeleniumTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver",
                "C:/Users/Jefferson/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.setBinary("C:/Program Files/Google/Chrome Beta/Application/chrome.exe");
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
    }

    // PRUEBA 3: RF05 - Listado de Productos (Versión Robusta)
    @Test
    void testListadoProductosRF05() {
        // 1 Navegar a la página principal (catálogo)
        driver.get("http://localhost:3000/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Más tiempo para cargar

        // 2️ Esperar a que AL MENOS UN producto esté visible (usando la clase que SÍ existe)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("producto-card")));

        // 3️ Contar productos usando la clase común "producto-card"
        List<WebElement> productos = driver.findElements(By.className("producto-card"));
        int cantidadProductos = productos.size();

        // 4️ Validar que hay productos cargados
        assertTrue(cantidadProductos > 0,
                "El catálogo debe mostrar al menos un producto disponible");

        // 5⃣ Validar que el primer producto tiene nombre, imagen y precio
        WebElement primerProducto = productos.get(0);

        // Validar nombre (usando la clase CSS)
        WebElement nombreElement = primerProducto.findElement(By.className("producto-nombre"));
        boolean tieneNombre = !nombreElement.getText().isEmpty();

        // Validar imagen (verificando que el src no esté vacío)
        WebElement imagen = primerProducto.findElement(By.className("producto-imagen"));
        String srcImagen = imagen.getAttribute("src");
        boolean tieneImagen = srcImagen != null && !srcImagen.isEmpty() && !srcImagen.contains("placeholder");

        // Validar precio (buscando elemento con clase producto-precio que contenga "$")
        WebElement precioElement = primerProducto.findElement(By.className("producto-precio"));
        boolean tienePrecio = precioElement.getText().contains("$");

        // Validar stock visible
        WebElement stockElement = primerProducto.findElement(By.className("producto-stock"));
        boolean tieneStock = stockElement.getText().contains("Stock:");

        // Aserciones finales
        assertTrue(tieneNombre, "El producto debe tener nombre visible");
        assertTrue(tieneImagen, "El producto debe tener imagen cargada (no placeholder)");
        assertTrue(tienePrecio, "El producto debe mostrar precio con formato $");
        assertTrue(tieneStock, "El producto debe mostrar estado de stock");

        // 6️ Imprimir resumen en consola para evidencia
        System.out.println(" Catálogo cargado: " + cantidadProductos + " productos encontrados");
        System.out.println(" Primer producto: " + nombreElement.getText());
        System.out.println(" Precio: " + precioElement.getText());
        System.out.println(" " + stockElement.getText());
    }

    // PRUEBA 5: RF06 - Filtrado de Productos
    @Test
    void testFiltradoProductosRF06() {
        // 1️ Navegar al catálogo principal
        driver.get("http://localhost:3000/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("producto-card")));

        // 2️ Contar productos iniciales (sin filtro)
        List<WebElement> productosIniciales = driver.findElements(By.className("producto-card"));
        int totalInicial = productosIniciales.size();

        // 3️ Aplicar filtro (Categoría: Hombre)
        // Ajusta el selector si tu filtro usa dropdown, checkboxes u otra clase
        WebElement filtro = driver.findElement(By.cssSelector("a[href='/hombre'], #cat-hombre, .btn-filtro-hombre"));
        filtro.click();

        // 4️ Esperar a que se aplique el filtro (cambio de URL o actualización del DOM)
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/hombre"),
                ExpectedConditions.stalenessOf(productosIniciales.get(0))
        ));

        // 5️ Contar productos después del filtro
        List<WebElement> productosFiltrados = driver.findElements(By.className("producto-card"));
        int totalFiltrado = productosFiltrados.size();

        // 6️ Validaciones
        assertTrue(totalFiltrado > 0, "El filtro debe mostrar al menos un producto");
        assertTrue(totalFiltrado <= totalInicial, "Los productos filtrados no pueden superar el total inicial");

        // 7️ Registro en consola para evidencia
        System.out.println("Filtro 'Hombre' aplicado correctamente");
        System.out.println(" Productos totales: " + totalInicial + " | Filtrados: " + totalFiltrado);
    }

    //  PRUEBA 6: RF08 - Agregar Producto al Carrito
    @Test
    void testAgregarAlCarritoRF08() {
        // 1 Navegar al catálogo principal
        driver.get("http://localhost:3000/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("producto-card")));

        // 2️ Buscar el primer producto y su botón "Agregar"
        // Usamos el ID que agregamos antes: btn-carrito-${producto.id}
        // Como no sabemos el ID exacto, buscamos por clase 'btn-carrito' dentro del primer producto
        List<WebElement> productos = driver.findElements(By.className("producto-card"));
        WebElement primerProducto = productos.get(0);

        // Buscar el botón dentro del producto
        WebElement btnAgregar = primerProducto.findElement(By.className("btn-carrito"));

        // Obtener el nombre del producto para validar después
        String nombreProducto = primerProducto.findElement(By.className("producto-nombre")).getText();

        // 3️ Hacer clic en Agregar
        btnAgregar.click();

        // 4 Esperar a que aparezca la alerta (tu código usa alert())
        wait.until(ExpectedConditions.alertIsPresent());

        // 5️ Cambiar a la alerta y obtener texto
        Alert alert = driver.switchTo().alert();
        String textoAlerta = alert.getText();

        // 6️ Validaciones
        // Verificamos que la alerta diga que se agregó el producto
        assertTrue(textoAlerta.contains("agregado al carrito"),
                "Debería aparecer una alerta confirmando que se agregó al carrito");

        // Aceptar la alerta para cerrarla
        alert.accept();

        // 7️ Mensaje en consola
        System.out.println("Producto agregado: " + nombreProducto);
        System.out.println("Alerta recibida: " + textoAlerta);
    }

    // PRUEBA 7: Visualización del Carrito
    @Test
    void testVisualizarCarritoRF09() {
        // 1️ Navegar al catálogo y agregar un producto (preparación)
        driver.get("http://localhost:3000/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("producto-card")));

        // Agregar el primer producto
        WebElement btnAgregar = driver.findElements(By.className("btn-carrito")).get(0);
        btnAgregar.click();

        // Aceptar la alerta de confirmación
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        alert.accept();

        // 2️ Navegar a la página del carrito
        // NOTA: Cambia "a[href='/carrito']" por el selector real de tu botón/link del carrito
        // Si tu botón tiene una clase específica, usa: By.className("btn-ver-carrito")
        try {
            WebElement linkCarrito = driver.findElement(By.cssSelector("a[href='/carrito'], .btn-carrito-global, #cart-icon"));
            linkCarrito.click();
        } catch (Exception e) {
            // Si no encuentra el link, navega directo por URL como respaldo
            driver.get("http://localhost:3000/carrito");
        }

        // 3️ Esperar a que cargue la lista del carrito
        // Buscamos la clase que pusimos en el paso 1
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("carrito-item")));

        // 4️ Validar que hay al menos 1 producto en el carrito
        List<WebElement> itemsEnCarrito = driver.findElements(By.className("carrito-item"));
        assertTrue(itemsEnCarrito.size() > 0, "El carrito debería mostrar al menos un producto agregado");

        // 5️ Validar que el nombre del producto agregado está visible
        boolean productoVisible = driver.getPageSource().contains("Nike Air Max"); // O el nombre que use tu BD
        assertTrue(productoVisible, "El nombre del producto agregado debe ser visible en el carrito");

        // 6️ Mensaje en consola
        System.out.println("Carrito abierto correctamente");
        System.out.println("Productos en carrito: " + itemsEnCarrito.size());
    }

    // PRUEBA 8: RF10 - Eliminar Producto del Carrito (Versión con JavaScript)
    @Test
    void testEliminarDelCarritoRF10() throws InterruptedException {
        // 1️ Preparación: Agregar un producto al carrito primero
        driver.get("http://localhost:3000/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("producto-card")));

        // Agregar el primer producto disponible
        WebElement btnAgregar = driver.findElements(By.className("btn-carrito")).get(0);
        btnAgregar.click();

        // Aceptar alerta de confirmación
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        alert.accept();

        // 2️ Navegar al carrito
        try {
            WebElement linkCarrito = driver.findElement(By.cssSelector("a[href='/carrito'], .btn-carrito-global, #cart-icon"));
            linkCarrito.click();
        } catch (Exception e) {
            driver.get("http://localhost:3000/carrito");
        }

        // 3️ Esperar que cargue el carrito y contar items iniciales
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("carrito-item")));
        List<WebElement> itemsAntes = driver.findElements(By.className("carrito-item"));
        int cantidadAntes = itemsAntes.size();

        assertTrue(cantidadAntes > 0, "Debe haber al menos un producto en el carrito para eliminar");

        // 4️ Hacer scroll al botón para que sea visible
        WebElement primerItem = itemsAntes.get(0);
        WebElement btnEliminar = primerItem.findElement(By.className("btn-eliminar-item"));

        // Hacer scroll al elemento
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnEliminar);
        Thread.sleep(500);

        // 5️ Hacer clic usando JavaScript (más confiable que click() normal)
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btnEliminar);

        // 6️ Esperar a que se actualice la lista
        Thread.sleep(1500);

        // 7️ Contar items después de eliminar
        List<WebElement> itemsDespues = driver.findElements(By.className("carrito-item"));
        int cantidadDespues = itemsDespues.size();

        // 8️ Validación: debe haber un producto menos
        assertTrue(cantidadDespues < cantidadAntes,
                "Al eliminar un producto, la cantidad debe reducirse. Antes: " + cantidadAntes + ", Después: " + cantidadDespues);

        // 9️ Mensaje en consola
        System.out.println("Producto eliminado exitosamente");
        System.out.println("Items antes: " + cantidadAntes + " | Items después: " + cantidadDespues);
    }

    //  PRUEBA 9: RF11 - Realizar Pedido (Checkout)
    @Test
    void testRealizarPedidoRF11() throws InterruptedException {
        // 1️ Iniciar sesión primero (requerido por Carrito.js)
        driver.get("http://localhost:3000/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));

        driver.findElement(By.id("email")).sendKeys("admin@demo.com");
        driver.findElement(By.id("password")).sendKeys("admin123");
        driver.findElement(By.id("btn-login")).click();

        // Esperar redirección post-login
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/home"),
                ExpectedConditions.urlContains("/admin")
        ));

        // 2️ Navegar al catálogo y agregar un producto
        driver.get("http://localhost:3000/");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("producto-card")));

        WebElement btnAgregar = driver.findElements(By.className("btn-carrito")).get(0);
        btnAgregar.click();

        // Aceptar alerta de "agregado al carrito"
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        // 3️ Ir al carrito
        try {
            driver.findElement(By.cssSelector("a[href='/carrito']")).click();
        } catch (Exception e) {
            driver.get("http://localhost:3000/carrito");
        }
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("carrito-item")));

        // 4️ Hacer clic en "Realizar Pedido" (usa el ID que agregamos en Carrito.js)
        WebElement btnPedido = driver.findElement(By.id("btn-realizar-pedido"));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btnPedido);

        // 5️ Esperar alerta de éxito y validar
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String mensaje = alert.getText();

        assertTrue(mensaje.contains("Pedido creado exitosamente"),
                "Debería confirmar la creación del pedido");

        alert.accept(); // Cerrar alerta
        System.out.println("Pedido realizado: " + mensaje);
    }

    // PRUEBA 7 (RF12): Vaciar Carrito Completo
    @Test
    void testVaciarCarrito() throws InterruptedException {
        // 1️ Preparación: Agregar un producto para tener algo que vaciar
        driver.get("http://localhost:3000/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("producto-card")));

        WebElement btnAgregar = driver.findElements(By.className("btn-carrito")).get(0);
        btnAgregar.click();

        // Aceptar alerta de agregado
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        // 2️ Navegar al carrito
        driver.get("http://localhost:3000/carrito");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("carrito-item")));

        // Verificar que hay items antes de vaciar
        int itemsAntes = driver.findElements(By.className("carrito-item")).size();
        assertTrue(itemsAntes > 0, "Debe haber items para poder vaciar");

        // 3️ Hacer clic en "Vaciar Carrito"
        WebElement btnVaciar = driver.findElement(By.id("btn-vaciar-carrito"));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btnVaciar);

        // 4️ Manejar la alerta de confirmación (confirm dialog)
        Alert confirm = driver.switchTo().alert();
        String textoConfirmacion = confirm.getText();

        assertTrue(textoConfirmacion.contains("Seguro"), "El sistema debe pedir confirmación antes de vaciar");
        confirm.accept(); // Aceptar "Sí, vaciar"

        // 5️ Validar que el carrito quedó vacío
        Thread.sleep(1000); // Breve espera para renderizado
        List<WebElement> itemsDespues = driver.findElements(By.className("carrito-item"));

        assertTrue(itemsDespues.isEmpty(), "El carrito debería quedar vacío (0 items)");

        // Validar que apareció el mensaje de vacío
        boolean mensajeVacio = driver.findElement(By.id("msg-carrito-vacio")).isDisplayed();
        assertTrue(mensajeVacio, "Debería mostrar el mensaje 'Tu carrito está vacío'");

        // 6️ Mensaje en consola
        System.out.println("Carrito vaciado exitosamente");
        System.out.println("Confirmación aceptada: " + textoConfirmacion);
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}