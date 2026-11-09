# Assignment test coverage

## UI flow
Login with the public admin account, open Admin, select an existing employee, create a unique enabled admin user, search by username, verify username/role/employee/status, capture evidence, and delete that generated account.

## API end-to-end flow
Authenticate admin and customer. Admin creates two products. Customer sees both in the product list. Admin places an order for that customer. Customer checks out. Verify pending-to-paid transition, total, ownership, paid-order visibility, and stock reduction. Verify duplicate checkout rejection. Delete the order and verify stock restoration, then delete products.

## Data-driven endpoint tests
Each successful case checks status, JSON content type, required response field types, values and saved state. Rejected requests check status, an error message where the server returns JSON, and absence of unwanted records. Rejected order cases also check the test item's stock is unchanged.

| Endpoint | Row | Scenario | Expected HTTP |
|---|---:|---|---:|
| POST /items | 1 | I01 valid | 201 |
| POST /items | 2 | I02 min name zero stock | 201 |
| POST /items | 3 | I03 max name | 201 |
| POST /items | 4 | short name | 400 |
| POST /items | 5 | long name | 400 |
| POST /items | 6 | blank name | 400 |
| POST /items | 7 | invalid characters | 400 |
| POST /items | 8 | zero price | 400 |
| POST /items | 9 | negative price | 400 |
| POST /items | 10 | invalid price | 400 |
| POST /items | 11 | negative stock | 400 |
| POST /items | 12 | fractional stock | 400 |
| POST /items | 13 | short description | 400 |
| POST /items | 14 | long description | 400 |
| POST /items | 15 | unknown field | 400 |
| POST /items | 16 | missing name | 400 |
| POST /items | 17 | missing price | 400 |
| POST /items | 18 | missing stock | 400 |
| POST /items | 19 | customer access | 403 |
| POST /items | 20 | none access | 401 |
| POST /items | 21 | invalid access | 401 |
| POST /items | 22 | malformed JSON | 400 |
| POST /orders | 1 | O01 valid | 201 |
| POST /orders | 2 | O02 min quantity | 201 |
| POST /orders | 3 | O03 all stock | 201 |
| POST /orders | 4 | zero quantity | 400 |
| POST /orders | 5 | negative quantity | 400 |
| POST /orders | 6 | fractional quantity | 400 |
| POST /orders | 7 | invalid quantity | 400 |
| POST /orders | 8 | insufficient stock | 400 |
| POST /orders | 9 | unknown item | 404 |
| POST /orders | 10 | invalid item id | 400 |
| POST /orders | 11 | missing itemId | 400 |
| POST /orders | 12 | missing quantity | 400 |
| POST /orders | 13 | missing customerId | 400 |
| POST /orders | 14 | missing items | 400 |
| POST /orders | 15 | empty items | 400 |
| POST /orders | 16 | invalid customer id | 400 |
| POST /orders | 17 | blank customer | 400 |
| POST /orders | 18 | unknown field | 400 |
| POST /orders | 19 | customer access | 403 |
| POST /orders | 20 | none access | 401 |
| POST /orders | 21 | invalid access | 401 |
| POST /orders | 22 | malformed JSON | 400 |

The scenarios are functional and authorization tests based on the supplied application; they do not claim load testing, exhaustive security testing, or every possible combination of malformed inputs. The current order-rejection dataset uses one order line per request.
