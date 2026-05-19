package com.guerrero.miapp.controller;

import com.guerrero.miapp.model.Producto;
import com.guerrero.miapp.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de productos.
 * Expone endpoints en /api/productos.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /** GET /api/productos → lista todos los productos */
    @GetMapping
    public List<Producto> listar() {
        return productoService.listarTodos();
    }

    /** GET /api/productos/{id} → busca por ID */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscar(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/productos → crea un nuevo producto */
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        try {
            return ResponseEntity.ok(productoService.agregar(producto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** DELETE /api/productos/{id} → elimina un producto */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (productoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /** GET /api/productos/inventario/valor → valor total del inventario */
    @GetMapping("/inventario/valor")
    public ResponseEntity<Double> valorInventario() {
        return ResponseEntity.ok(productoService.calcularValorInventario());
    }
}
