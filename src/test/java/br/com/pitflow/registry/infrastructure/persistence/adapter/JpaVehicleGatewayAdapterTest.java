package br.com.pitflow.registry.infrastructure.persistence.adapter;

import br.com.pitflow.registry.core.entity.Vehicle;
import br.com.pitflow.registry.core.valueObject.LicensePlate;
import br.com.pitflow.registry.infrastructure.persistence.entity.VehicleJpa;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringVehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaVehicleGatewayAdapterTest {

    private SpringVehicleRepository repository;
    private JpaVehicleGatewayAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(SpringVehicleRepository.class);
        adapter = new JpaVehicleGatewayAdapter(repository);
    }

    @Test
    void shouldSaveAndDeleteVehicle() {
        var vehicle = vehicle();

        adapter.save(vehicle);
        adapter.deleteById(vehicle.getId());

        var entity = ArgumentCaptor.forClass(VehicleJpa.class);
        verify(repository).save(entity.capture());
        verify(repository).deleteById(vehicle.getId());
        assertThat(entity.getValue().getLicensePlate()).isEqualTo("BDD1A23");
    }

    @Test
    void shouldMapRepositoryQueriesToDomain() {
        var customerId = UUID.randomUUID();
        var id = UUID.randomUUID();
        var entity = new VehicleJpa(id, customerId, "BDD1A23", "PitFlow", "Teste", 2026);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.findByLicensePlate("BDD1A23")).thenReturn(Optional.of(entity));
        when(repository.findByCustomerId(customerId)).thenReturn(List.of(entity));
        when(repository.findAll()).thenReturn(List.of(entity));

        assertThat(adapter.findById(id)).get().extracting(Vehicle::getId).isEqualTo(id);
        assertThat(adapter.findByLicensePlate(new LicensePlate("BDD1A23"))).isPresent();
        assertThat(adapter.findByCustomerId(customerId)).extracting(Vehicle::getId).containsExactly(id);
        assertThat(adapter.findAll()).extracting(Vehicle::getId).containsExactly(id);
    }

    @Test
    void shouldReturnEmptyWhenVehicleDoesNotExist() {
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThat(adapter.findById(id)).isEmpty();
    }

    private static Vehicle vehicle() {
        return new Vehicle(
                UUID.randomUUID(), new LicensePlate("BDD1A23"), "PitFlow", "Teste", 2026);
    }
}
