package com.guerrero.miapp.service;

import com.guerrero.miapp.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ProductoService.
 * JaCoCo medirá la cobertura de estas pruebas sobre el servicio.
 */
@DisplayName("ProductoService - Pruebas Unitarias")
class ProductoServiceTest {

    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        // Se crea una instancia nueva antes de cada prueba
        productoService = new ProductoService();
    }

    // ─── listarTodos ─────────────────────────────────────────────

    @Test
    @DisplayName("listarTodos() retorna los productos iniciales")
    void listarTodos_retornaProductosIniciales() {
        List<Producto> productos = productoService.listarTodos();
        assertFalse(productos.isEmpty(), "La lista no debe estar vacía al iniciar");
        assertEquals(3, productos.size(), "Deben existir 3 productos de ejemplo");
    }

    // ─── buscarPorId ──────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId() retorna producto cuando el ID existe")
    void buscarPorId_idExistente_retornaProducto() {
        Optional<Producto> resultado = productoService.buscarPorId(1L);
        assertTrue(resultado.isPresent(), "Debe encontrar el producto con ID 1");
        assertEquals("Laptop", resultado.get().getNombre());
    }

    @Test
    @DisplayName("buscarPorId() retorna vacío cuando el ID no existe")
    void buscarPorId_idInexistente_retornaVacio() {
        Optional<Producto> resultado = productoService.buscarPorId(999L);
        assertTrue(resultado.isEmpty(), "No debe encontrar producto con ID 999");
    }

    // ─── agregar ──────────────────────────────────────────────────

    @Test
    @DisplayName("agregar() añade el producto y le asigna ID")
    void agregar_productoValido_retornaConId() {
        Producto nuevo = new Producto(null, "Monitor", 350.00, 5);
        Producto guardado = productoService.agregar(nuevo);

        assertNotNull(guardado.getId(), "El producto guardado debe tener ID asignado");
        assertEquals("Monitor", guardado.getNombre());
        assertEquals(4, productoService.listarTodos().size());
    }

    @Test
    @DisplayName("agregar() lanza excepción si el nombre está vacío")
    void agregar_nombreVacio_lanzaExcepcion() {
        Producto invalido = new Producto(null, "", 10.0, 1);
        assertThrows(IllegalArgumentException.class,
                () -> productoService.agregar(invalido),
                "Debe lanzar excepción con nombre vacío");
    }

    @Test
    @DisplayName("agregar() lanza excepción si el nombre es nulo")
    void agregar_nombreNulo_lanzaExcepcion() {
        Producto invalido = new Producto(null, null, 10.0, 1);
        assertThrows(IllegalArgumentException.class,
                () -> productoService.agregar(invalido),
                "Debe lanzar excepción con nombre nulo");
    }

    @Test
    @DisplayName("agregar() lanza excepción si el precio es negativo")
    void agregar_precioNegativo_lanzaExcepcion() {
        Producto invalido = new Producto(null, "Auriculares", -5.0, 1);
        assertThrows(IllegalArgumentException.class,
                () -> productoService.agregar(invalido),
                "Debe lanzar excepción con precio negativo");
    }

    // ─── eliminar ─────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar() retorna true cuando el producto existe")
    void eliminar_idExistente_retornaTrue() {
        boolean resultado = productoService.eliminar(1L);
        assertTrue(resultado, "Debe retornar true al eliminar un ID existente");
        assertEquals(2, productoService.listarTodos().size());
    }

    @Test
    @DisplayName("eliminar() retorna false cuando el producto no existe")
    void eliminar_idInexistente_retornaFalse() {
        boolean resultado = productoService.eliminar(999L);
        assertFalse(resultado, "Debe retornar false al intentar eliminar ID inexistente");
    }

    // ─── calcularValorInventario ──────────────────────────────────

    @Test
    @DisplayName("calcularValorInventario() retorna suma correcta")
    void calcularValorInventario_retornaSumacorrecta() {
        // Laptop: 1200 * 10 = 12000 | Mouse: 25.99 * 50 = 1299.5 | Teclado: 45 * 30 = 1350
        double esperado = (1200.00 * 10) + (25.99 * 50) + (45.00 * 30);
        double resultado = productoService.calcularValorInventario();
        assertEquals(esperado, resultado, 0.01,
                "El valor total del inventario debe ser " + esperado);
    }

    @Test
    @DisplayName("calcularValorInventario() retorna 0 cuando no hay productos")
    void calcularValorInventario_sinProductos_retornaCero() {
        // Eliminar todos los productos de ejemplo
        productoService.eliminar(1L);
        productoService.eliminar(2L);
        productoService.eliminar(3L);

        double resultado = productoService.calcularValorInventario();
        assertEquals(0.0, resultado, "El valor debe ser 0 sin productos");
    }
}
