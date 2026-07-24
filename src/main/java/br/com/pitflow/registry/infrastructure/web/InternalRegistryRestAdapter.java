package br.com.pitflow.registry.infrastructure.web;

import br.com.pitflow.registry.controller.CustomerController;
import br.com.pitflow.registry.controller.VehicleController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/registry")
public class InternalRegistryRestAdapter {

    private final CustomerController customerController;
    private final VehicleController vehicleController;

    public InternalRegistryRestAdapter(
            CustomerController customerController,
            VehicleController vehicleController) {
        this.customerController = customerController;
        this.vehicleController = vehicleController;
    }

    @GetMapping("/customers/{id}")
    public InternalCustomerResponse findCustomer(@PathVariable UUID id) {
        var customer = customerController.getById(id);
        return new InternalCustomerResponse(customer.id(), customer.email());
    }

    @GetMapping("/vehicles/{id}")
    public InternalVehicleResponse findVehicle(@PathVariable UUID id) {
        var vehicle = vehicleController.findById(id);
        return new InternalVehicleResponse(vehicle.id(), vehicle.customerId());
    }

    public record InternalCustomerResponse(UUID id, String email) {
    }

    public record InternalVehicleResponse(UUID id, UUID customerId) {
    }
}
