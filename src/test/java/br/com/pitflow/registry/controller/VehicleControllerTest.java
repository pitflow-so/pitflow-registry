package br.com.pitflow.registry.controller;

import br.com.pitflow.registry.controller.dto.AddVehicleCommand;
import br.com.pitflow.registry.controller.dto.UpdateVehicleCommand;
import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.AddVehicle;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.DeleteVehicle;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehicleById;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehicleByPlate;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.FindVehiclesByCustomerId;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.ListVehicles;
import br.com.pitflow.registry.core.usecase.vehicle.inputPort.UpdateVehicle;
import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.infrastructure.web.dto.AddVehicleRequest;
import br.com.pitflow.registry.infrastructure.web.dto.UpdateVehicleRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VehicleControllerTest {

    private AddVehicle addVehicle;
    private UpdateVehicle updateVehicle;
    private DeleteVehicle deleteVehicle;
    private FindVehicleById findVehicleById;
    private FindVehicleByPlate findVehicleByPlate;
    private FindVehiclesByCustomerId findVehiclesByCustomerId;
    private ListVehicles listVehicles;
    private VehicleController controller;

    @BeforeEach
    void setUp() {
        addVehicle = mock(AddVehicle.class);
        updateVehicle = mock(UpdateVehicle.class);
        deleteVehicle = mock(DeleteVehicle.class);
        findVehicleById = mock(FindVehicleById.class);
        findVehicleByPlate = mock(FindVehicleByPlate.class);
        findVehiclesByCustomerId = mock(FindVehiclesByCustomerId.class);
        listVehicles = mock(ListVehicles.class);
        controller = new VehicleController(addVehicle, updateVehicle, deleteVehicle,
                findVehicleById, findVehicleByPlate, findVehiclesByCustomerId, listVehicles);
    }

    @Test
    void shouldCreateAndPresentVehicle() {
        var customerId = UUID.randomUUID();
        var vehicle = vehicle(customerId, "BDD1A23");
        var request = new AddVehicleRequest(customerId, "BDD1A23", "PitFlow", "Teste", 2026);
        when(addVehicle.execute(any())).thenReturn(vehicle);

        var response = controller.create(request);

        var command = ArgumentCaptor.forClass(AddVehicleCommand.class);
        verify(addVehicle).execute(command.capture());
        assertThat(command.getValue().customerId()).isEqualTo(customerId);
        assertThat(command.getValue().licensePlate()).isEqualTo("BDD1A23");
        assertThat(response.licensePlate()).isEqualTo("BDD1A23");
    }

    @Test
    void shouldUpdateAndReloadVehicle() {
        var id = UUID.randomUUID();
        var vehicle = vehicle(UUID.randomUUID(), "BDD1A23");
        vehicle.setId(id);
        var request = new UpdateVehicleRequest("PitFlow", "Atualizado", 2026, "BDD1A23");
        when(findVehicleById.execute(id)).thenReturn(vehicle);

        var response = controller.update(id, request);

        var command = ArgumentCaptor.forClass(UpdateVehicleCommand.class);
        verify(updateVehicle).execute(eq(id), command.capture());
        assertThat(command.getValue().model()).isEqualTo("Atualizado");
        assertThat(response.id()).isEqualTo(id);
    }

    @Test
    void shouldDeleteVehicle() {
        var id = UUID.randomUUID();

        controller.delete(id);

        verify(deleteVehicle).execute(id);
    }

    @Test
    void shouldFindVehicleByIdAndPlate() {
        var vehicle = vehicle(UUID.randomUUID(), "BDD1A23");
        when(findVehicleById.execute(vehicle.getId())).thenReturn(vehicle);
        when(findVehicleByPlate.execute("BDD1A23")).thenReturn(vehicle);

        assertThat(controller.findById(vehicle.getId()).id()).isEqualTo(vehicle.getId());
        assertThat(controller.findByPlate("BDD1A23").licensePlate()).isEqualTo("BDD1A23");
    }

    @Test
    void shouldListVehiclesByCustomerAndGlobally() {
        var customerId = UUID.randomUUID();
        var first = vehicle(customerId, "BDD1A23");
        var second = vehicle(customerId, "ABC1D23");
        when(findVehiclesByCustomerId.execute(customerId)).thenReturn(List.of(first, second));
        when(listVehicles.execute()).thenReturn(List.of(second, first));

        assertThat(controller.findByCustomerId(customerId))
                .extracting("id").containsExactly(first.getId(), second.getId());
        assertThat(controller.findAll())
                .extracting("id").containsExactly(second.getId(), first.getId());
    }

    private static Vehicle vehicle(UUID customerId, String plate) {
        return new Vehicle(customerId, new LicensePlate(plate), "PitFlow", "Teste", 2026);
    }
}
