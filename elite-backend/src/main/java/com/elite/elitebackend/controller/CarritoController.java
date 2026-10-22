package com.elite.elitebackend.controller;

import com.elite.elitebackend.model.Carrito;
import com.elite.elitebackend.model.CarritoDetalle;
import com.elite.elitebackend.model.Producto;
import com.elite.elitebackend.repository.CarritoDetalleRepository;
import com.elite.elitebackend.repository.CarritoRepository;
import com.elite.elitebackend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.elite.elitebackend.dto.CarritoDetalleRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/carritos")
@CrossOrigin(origins = "*")
public class CarritoController {
    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoDetalleRepository carritoDetalleRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public List<Carrito> listar() {
        return carritoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Carrito buscarPorId(@PathVariable Integer id) {
        return carritoRepository.findById(id).orElse(null);
    }

    // Obtener los detalles del carrito de un cliente específico
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<?> obtenerCarritoPorCliente(@PathVariable Integer idCliente) {
        Optional<Carrito> carritoOpt = carritoRepository.findByIdCliente(idCliente);

        // Si no tiene carrito, lo creamos automáticamente
        if (carritoOpt.isEmpty()) {
            Carrito nuevoCarrito = new Carrito();
            nuevoCarrito.setIdCliente(idCliente);
            nuevoCarrito.setFechaCreacion(LocalDate.now());
            carritoOpt = Optional.of(carritoRepository.save(nuevoCarrito));
        }

        Carrito carrito = carritoOpt.get();
        List<CarritoDetalle> detalles = carritoDetalleRepository.findByCarritoId(carrito.getId());

        return ResponseEntity.ok(detalles);
    }

    @PostMapping("/agregar")
    public ResponseEntity<?> agregarAlCarrito(@RequestBody CarritoDetalleRequest request) {
        Optional<Carrito> carritoOpt = carritoRepository.findByIdCliente(request.getIdCliente());
        Carrito carrito;

        if (carritoOpt.isEmpty()) {
            carrito = new Carrito();
            carrito.setIdCliente(request.getIdCliente());
            carrito.setFechaCreacion(LocalDate.now());
            carrito = carritoRepository.save(carrito);
        } else {
            carrito = carritoOpt.get();
        }

        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        CarritoDetalle detalle = new CarritoDetalle();
        detalle.setCarrito(carrito);
        detalle.setProducto(producto);
        detalle.setCantidad(request.getCantidad());
        detalle.setSubtotal(producto.getPrecio() * request.getCantidad());

        CarritoDetalle guardado = carritoDetalleRepository.save(detalle);
        return ResponseEntity.ok(guardado);
    }



    @PostMapping
    public Carrito crear(@RequestBody Carrito carrito) {
        return carritoRepository.save(carrito);
    }

    @PutMapping("/{id}")
    public Carrito actualizar(@PathVariable Integer id, @RequestBody Carrito carrito) {
        carrito.setId(id);
        return carritoRepository.save(carrito);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        carritoRepository.deleteById(id);
    }

}
