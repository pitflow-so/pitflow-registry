package br.com.pitflow.registry.controller;

import br.com.pitflow.registry.controller.dto.CreateCustomerCommand;
import br.com.pitflow.registry.controller.dto.UpdateCustomerCommand;
import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.core.usecase.customer.inputPort.CreateCustomer;
import br.com.pitflow.registry.core.usecase.customer.inputPort.DeleteCustomer;
import br.com.pitflow.registry.core.usecase.customer.inputPort.FindCustomerByDocument;
import br.com.pitflow.registry.core.usecase.customer.inputPort.FindCustomerById;
import br.com.pitflow.registry.core.usecase.customer.inputPort.ListCustomers;
import br.com.pitflow.registry.core.usecase.customer.inputPort.UpdateCustomer;
import br.com.pitflow.registry.core.valueObject.CpfCnpj;
import br.com.pitflow.registry.core.valueObject.Email;
import br.com.pitflow.registry.infrastructure.web.dto.CreateCustomerRequest;
import br.com.pitflow.registry.infrastructure.web.dto.UpdateCustomerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerControllerTest {

    private CreateCustomer createCustomer;
    private UpdateCustomer updateCustomer;
    private DeleteCustomer deleteCustomer;
    private FindCustomerById findCustomerById;
    private FindCustomerByDocument findCustomerByDocument;
    private ListCustomers listCustomers;
    private CustomerController controller;

    @BeforeEach
    void setUp() {
        createCustomer = mock(CreateCustomer.class);
        updateCustomer = mock(UpdateCustomer.class);
        deleteCustomer = mock(DeleteCustomer.class);
        findCustomerById = mock(FindCustomerById.class);
        findCustomerByDocument = mock(FindCustomerByDocument.class);
        listCustomers = mock(ListCustomers.class);
        controller = new CustomerController(createCustomer, updateCustomer, deleteCustomer,
                findCustomerById, findCustomerByDocument, listCustomers);
    }

    @Test
    void shouldCreateAndPresentCustomer() {
        var customer = customer();
        var request = new CreateCustomerRequest(
                "Cliente Teste", "78177454048", "11999990001", "cliente@pitflow.com");
        when(createCustomer.execute(org.mockito.ArgumentMatchers.any())).thenReturn(customer);

        var response = controller.create(request);

        var command = ArgumentCaptor.forClass(CreateCustomerCommand.class);
        verify(createCustomer).execute(command.capture());
        assertThat(command.getValue().name()).isEqualTo(request.name());
        assertThat(command.getValue().document()).isEqualTo(request.document());
        assertThat(response.id()).isEqualTo(customer.getId());
        assertThat(response.email()).isEqualTo("cliente@pitflow.com");
    }

    @Test
    void shouldUpdateAndReloadCustomer() {
        var id = UUID.randomUUID();
        var updated = customer();
        updated.setId(id);
        var request = new UpdateCustomerRequest(
                "Cliente Atualizado", "78177454048", "11999990002", "atualizado@pitflow.com");
        when(findCustomerById.execute(id)).thenReturn(updated);

        var response = controller.update(id, request);

        var command = ArgumentCaptor.forClass(UpdateCustomerCommand.class);
        verify(updateCustomer).execute(org.mockito.ArgumentMatchers.eq(id), command.capture());
        assertThat(command.getValue().name()).isEqualTo(request.name());
        assertThat(response.id()).isEqualTo(id);
    }

    @Test
    void shouldDeleteCustomer() {
        var id = UUID.randomUUID();

        controller.delete(id);

        verify(deleteCustomer).execute(id);
    }

    @Test
    void shouldFindCustomerByIdAndDocument() {
        var customer = customer();
        when(findCustomerById.execute(customer.getId())).thenReturn(customer);
        when(findCustomerByDocument.execute("78177454048")).thenReturn(customer);

        assertThat(controller.getById(customer.getId()).id()).isEqualTo(customer.getId());
        assertThat(controller.getByDocument("78177454048").document()).isEqualTo("78177454048");
    }

    @Test
    void shouldListCustomers() {
        var first = customer();
        var second = customer();
        when(listCustomers.execute()).thenReturn(List.of(first, second));

        var result = controller.listAll();

        assertThat(result).extracting("id").containsExactly(first.getId(), second.getId());
    }

    private static Customer customer() {
        return new Customer(
                "Cliente Teste",
                "11999990001",
                new Email("cliente@pitflow.com"),
                new CpfCnpj("78177454048"));
    }
}
