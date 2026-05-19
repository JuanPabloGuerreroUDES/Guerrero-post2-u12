package com.guerrero.miapp.service;

import com.guerrero.miapp.model.Producto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que contiene la lógica de negocio para Productos.
 * Esta clase es testeada con JaCoCo para medir cobertura.
 */
@Service
public class ProductoService {

    // Almacenamiento en memoria (simula una base de datos)
    private final List<Producto> productos = new ArrayList<>();
    private Long contadorId = 1L;

    public ProductoService() {
        // Datos de ejemplo al iniciar
        productos.add(new Producto(contadorId++, "Laptop", 1200.00, 10));
        productos.add(new Producto(contadorId++, "Mouse", 25.99, 50));
        productos.add(new Producto(contadorId++, "Teclado", 45.00, 30));
    }

    /**
     * Retorna todos los productos.
     */
    public List<Producto> listarTodos() {
        return new ArrayList<>(productos);
    }

    /**
     * Busca un producto por su ID.
     */
    public Optional<Producto> buscarPorId(Long id) {
        return productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    /**
     * Agrega un nuevo producto.
     * Lanza excepción si el nombre es nulo o vacío.
     */
    public Producto agregar(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        if (producto.getPrecio() == null || producto.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio debe ser mayor o igual a cero");
        }
        producto.setId(contadorId++);
        productos.add(producto);
        return producto;
    }

    /**
     * Elimina un producto por ID.
     * Retorna true si fue eliminado, false si no existía.
     */
    public boolean eliminar(Long id) {
        return productos.removeIf(p -> p.getId().equals(id));
    }

    /**
     * Calcula el valor total del inventario (precio × stock).
     */
    public Double calcularValorInventario() {
        return productos.stream()
                .mapToDouble(p -> p.getPrecio() * p.getStock())
                .sum();
    }
}
