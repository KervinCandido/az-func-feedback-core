---
name: escrita-de-testes
description: user para escrita, alteração e revisão de testes unitário, integração, E2E
---

# Escrita de testes
Define the step-by-step process or specific rules the agent must follow.

## Regras & Restrições
- Monte sempre focando no BDD
- Utilize sempre os exemplos com base
- Crie testes para até ter 100% de cobertura do código
- Utilize assertJ para criar os testes
- Os nomes dos metodos devem ser em português
- Sempre adicione @DisplayName com textos em português e intuitivos nos métodos e classes
- Sempre for usar mockito sempre use @ExtendWith(MockitoExtension.class)

## Exemplos
Exemplo com assertJ
```java
@DisplayName("Testes para a classe OrderItem")
class OrderItemTest {

    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        menuItem = new MenuItem(1L, "Cheeseburger", new BigDecimal("25.50"), false, 1L);
    }

    @Nested
    @DisplayName("Construtores")
    class Constructors {

        @Test
        @DisplayName("Deve criar um item de pedido com sucesso usando o construtor principal")
        void deveCriarItemDePedidoComConstrutorPrincipal() {
            // Arrange
            var id = 1L;
            var quantity = new BigDecimal("2");
            var unitPrice = new BigDecimal("25.50");

            // Act
            var orderItem = new OrderItem(id, menuItem, quantity, unitPrice);

            // Assert
            assertThat(orderItem.getId()).isEqualTo(id);
            assertThat(orderItem.getMenuItem()).isEqualTo(menuItem);
            assertThat(orderItem.getQuantity()).isEqualTo(quantity);
            assertThat(orderItem.getUnitPrice()).isEqualTo(unitPrice);
        }
    }

    @Nested
    @DisplayName("Cálculos")
    class Calculations {

        @Test
        @DisplayName("Deve calcular o total corretamente")
        void deveCalcularTotalCorretamente() {
            // Arrange
            var quantity = new BigDecimal("2");
            var unitPrice = new BigDecimal("10.50");
            var orderItem = new OrderItem(1L, menuItem, quantity, unitPrice);
            var expectedTotal = new BigDecimal("21.00");

            // Act
            var total = orderItem.getTotal();

            // Assert
            assertThat(total).isEqualTo(expectedTotal);
        }
    }
}
```
Exemplo com mockito
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o caso de uso SaveAllMenuItemsUsecase")
class SaveAllMenuItemsUsecaseTest {
    @Mock
    private MenuItemGateway menuItemGateway;
    private SaveAllMenuItemsUsecase saveAllMenuItemsUsecase;
    @Captor
    private ArgumentCaptor<List<MenuItem>> menuItemListCaptor;

    @BeforeEach
    void setUp() {
        saveAllMenuItemsUsecase = new SaveAllMenuItemsUsecase(menuItemGateway);
    }

    @Nested
    @DisplayName("Cenários de execução")
    class ExecutionScenarios {

        @Test
        @DisplayName("Deve salvar uma lista de itens de menu com sucesso")
        void deveSalvarListaDeItensComSucesso() {
            // Given
            var itemInput1 = new MenuItemInput(1L, "Item 1", BigDecimal.TEN, false, 1L);
            var itemInput2 = new MenuItemInput(2L, "Item 2", BigDecimal.ONE, true, 1L);
            var itemsInput = List.of(itemInput1, itemInput2);

            // When
            saveAllMenuItemsUsecase.save(itemsInput);

            // Then
            then(menuItemGateway).should().saveAll(menuItemListCaptor.capture());
            List<MenuItem> capturedItems = menuItemListCaptor.getValue();

            assertThat(capturedItems).hasSize(2);
            assertThat(capturedItems.get(0).getId()).isEqualTo(itemInput1.id());
            assertThat(capturedItems.get(0).getName()).isEqualTo(itemInput1.name());
            assertThat(capturedItems.get(1).getId()).isEqualTo(itemInput2.id());
            assertThat(capturedItems.get(1).getName()).isEqualTo(itemInput2.name());
        }
    }

    @Nested
    @DisplayName("Validações de entrada e construtor")
    class ValidationScenarios {

        @Test
        @DisplayName("Deve lançar NullPointerException quando a lista de input for nula")
        void deveLancarExcecaoQuandoListaDeInputForNula() {
            // When & Then
            assertThatThrownBy(() -> saveAllMenuItemsUsecase.save(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("itemsInput cannot be null");

            then(menuItemGateway).shouldHaveNoInteractions();
        }
    }
}
```
Exemplo com Restassured
```java


import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestHTTPEndpoint(GreetingResource.class)
public class GreetingResourceTest {

    @Test
    public void testHelloEndpoint() {
        when().get()
          .then()
             .statusCode(200)
             .body(is("hello"));
    }
}
```
