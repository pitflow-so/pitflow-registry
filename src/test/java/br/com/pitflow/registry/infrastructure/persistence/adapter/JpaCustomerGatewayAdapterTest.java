package br.com.pitflow.registry.infrastructure.persistence.adapter;

import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.core.valueObject.Email;
import br.com.pitflow.registry.infrastructure.persistence.entity.CustomerJpa;
import br.com.pitflow.registry.infrastructure.persistence.repository.SpringCustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaCustomerGatewayAdapterTest {

    private SpringCustomerRepository repository;
    private JpaCustomerGatewayAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(SpringCustomerRepository.class);
        adapter = new JpaCustomerGatewayAdapter(repository);
    }

    @Test
    void shouldSaveAndDeleteCustomer() {
        var customer = customer();

        adapter.save(customer);
        adapter.delete(customer.getId());

        var entity = ArgumentCaptor.forClass(CustomerJpa.class);
        verify(repository).save(entity.capture());
        verify(repository).deleteById(customer.getId());
        assertThat(entity.getValue().getDocument()).isEqualTo("78177454048");
    }

    @Test
    void shouldMapRepositoryQueriesToDomain() {
        var id = UUID.randomUUID();
        var entity = entity(id);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.findByDocument("78177454048")).thenReturn(Optional.of(entity));
        when(repository.findAll()).thenReturn(List.of(entity));

        assertThat(adapter.findById(id)).get().extracting(Customer::getId).isEqualTo(id);
        assertThat(adapter.findByDocument(new CpfCnpj("78177454048"))).isPresent();
        assertThat(adapter.findAll()).extracting(Customer::getId).containsExactly(id);
    }

    @Test
    void shouldReturnEmptyWhenCustomerDoesNotExist() {
        var id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThat(adapter.findById(id)).isEmpty();
    }

    private static Customer customer() {
        return new Customer(
                "Cliente Teste",
                "11999990001",
                new Email("cliente@pitflow.com"),
                new CpfCnpj("78177454048"));
    }

    private static CustomerJpa entity(UUID id) {
        return new CustomerJpa(
                id, "Cliente Teste", "78177454048", "11999990001",
                "cliente@pitflow.com", "ACTIVE", LocalDateTime.now());
    }
}
