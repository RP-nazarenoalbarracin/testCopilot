package com.recarga.demo.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag; // <-- IMPORT NECESARIO
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de PRUEBA — incluye endpoints correctos e incorrectos
 * para disparar validaciones de Copilot según las guidelines.
 */
@Tag(name = "APIDoc2.0/users") // controller con tag → corre STRICT checks
@RestController
@RequestMapping("/v2")
public class DemoController {

  // ==============================
  // ✅ CORRECTO: colección paginada, plural, kebab-case, camelCase,
  //    códigos permitidos, ejemplos y schemas.
  // ==============================
  @Operation(
          operationId = "listUsers",
          summary = "List users (paginated)",
          description = "Returns a paginated slice of users.",
          responses = {
                  @ApiResponse(responseCode = "200", description = "OK",
                          content = @Content(schema = @Schema(implementation = UsersPageResponse.class))),
                  @ApiResponse(responseCode = "400", description = "Bad request")
          }
  )
  @GetMapping("/users")
  public ResponseEntity<UsersPageResponse> listUsers(
          @Parameter(description = "Page index (0-based)") @RequestParam(defaultValue = "0") int page,
          @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size
  ) {
    UsersPageResponse resp = new UsersPageResponse(
            List.of(new UserDto("670b9562-b30d-52d5-b827-655787665500", "John", "Doe")),
            true, true, 1, 0
    );
    return ResponseEntity.ok(resp);
  }

  // ==============================
  // ❌ VIOLACIÓN: singular en URL (/user), header custom sin X-RP-*,
  //    (la ausencia de tag en método NO es violación; el tag es a nivel clase).
  // ==============================
  @Operation(
          operationId = "getUserSingular_BAD",
          summary = "[BAD] Uses singular noun and non-standard header",
          responses = {
                  @ApiResponse(responseCode = "200", description = "OK",
                          content = @Content(schema = @Schema(implementation = UserDto.class)))
          }
  )
  @GetMapping("/user")
  public ResponseEntity<UserDto> getUserSingularBad(
          // Debería ser X-RP-USERID
          @RequestHeader(name = "X-UserId", required = false) String userId
  ) {
    return ResponseEntity.ok(new UserDto("id", "Jane", "Doe"));
  }

  // ==============================
  // ❌ VIOLACIÓN: query param en snake_case y sin paginación en colección.
  // ==============================
  @Operation(
          operationId = "listTransactionsWithoutPagination_BAD",
          summary = "[BAD] Missing pagination; snake_case query param",
          responses = {
                  @ApiResponse(responseCode = "200", description = "OK",
                          content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransactionDto.class))))
          }
  )
  @GetMapping("/transactions")
  public ResponseEntity<List<TransactionDto>> listTransactionsBad(
          // Debe ser camelCase (statusFilter) y este endpoint debería soportar paginación
          @RequestParam(name = "status_filter", required = false) String status_filter
  ) {
    return ResponseEntity.ok(List.of(
            new TransactionDto("tx-1", new Money("BRL", new BigDecimal("12.84")))
    ));
  }

  // ==============================
  // ❌ VIOLACIÓN: 408 declarado (no permitido por la guía).
  // ==============================
  @Operation(
          operationId = "getSomethingWith408_BAD",
          summary = "[BAD] Uses 408 in OpenAPI responses",
          responses = {
                  @ApiResponse(responseCode = "200", description = "OK"),
                  @ApiResponse(responseCode = "408", description = "Request Timeout (should not be used)")
          }
  )
  @GetMapping("/bad-timeout")
  public ResponseEntity<Void> badTimeoutExample() {
    return ResponseEntity.ok().build();
  }

  // ==============================
  // ✅ CORRECTO: creación con idempotency key y códigos permitidos.
  // ==============================
  @Operation(
          operationId = "createUser",
          summary = "Create user (idempotent)",
          description = "Creates a user. Requires idempotency key.",
          responses = {
                  @ApiResponse(responseCode = "201", description = "Created",
                          content = @Content(schema = @Schema(implementation = UserDto.class))),
                  @ApiResponse(responseCode = "400", description = "Bad request"),
                  @ApiResponse(responseCode = "409", description = "Conflict")
          }
  )
  @PostMapping("/users")
  public ResponseEntity<UserDto> createUser(
          @RequestBody CreateUserRequest body,
          @RequestHeader(name = "X-RP-IDEMPOTENCY-KEY", required = true) String idempotencyKey
  ) {
    UserDto created = new UserDto("new-id", body.firstName, body.lastName);
    return ResponseEntity.status(201)
            .header("Location", "/v2/users/new-id")
            .body(created);
  }

  // ======= DTOs =======

  public static class CreateUserRequest {
    @Schema(example = "John")
    public String firstName;
    @Schema(example = "Doe")
    public String lastName;
  }

  public static class UserDto {
    @Schema(example = "670b9562-b30d-52d5-b827-655787665500")
    public String id;
    @Schema(example = "John")
    public String firstName;
    @Schema(example = "Doe")
    public String lastName;

    public UserDto(String id, String firstName, String lastName) {
      this.id = id;
      this.firstName = firstName;
      this.lastName = lastName;
    }
  }

  public static class UsersPageResponse {
    public List<UserDto> content;
    public boolean last;
    public boolean first;
    public int numberOfElements;
    public int number;

    public UsersPageResponse(List<UserDto> content, boolean last, boolean first,
                             int numberOfElements, int number) {
      this.content = content;
      this.last = last;
      this.first = first;
      this.numberOfElements = numberOfElements;
      this.number = number;
    }
  }

  public static class Money {
    @Schema(example = "BRL")
    public String currency;
    @Schema(example = "12.84")
    public BigDecimal amount;

    public Money(String currency, BigDecimal amount) {
      this.currency = currency;
      this.amount = amount;
    }
  }

  public static class TransactionDto {
    @Schema(example = "tx-1")
    public String id;
    public Money total;

    public TransactionDto(String id, Money total) {
      this.id = id;
      this.total = total;
    }
  }

  // BAD formatos (para que Copilot los señale si quisieras)
  public static class ProfileDtoBad {
    public String id;
    @Schema(
            description = "Registration date (should be yyyy-MM-dd'T'HH:mm:ss.SSS'Z')",
            example = "2020-07-27 11:12:43" // formato incorrecto adrede
    )
    public OffsetDateTime registrationDate;
    @Schema(
            description = "Account status (should be UPPER-KEBAB-CASE string)",
            allowableValues = {"Active", "Suspended"} // casing incorrecto adrede
    )
    public String status;
  }
}
