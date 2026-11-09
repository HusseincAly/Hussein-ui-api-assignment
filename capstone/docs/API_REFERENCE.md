# API Reference Documentation

This document provides a detailed reference for all available API endpoints, including their methods, paths, authentication requirements, request/response formats, and data validation rules.

---

## Authentication

### `POST /login`

Authenticates a user and returns a JWT token for subsequent protected API calls.

*   **Description:** Allows users to log in with their username and password to obtain an authentication token.
*   **Authentication:** None (public endpoint).
*   **Request Body:**
    *   `username` (string, required): User's unique username.
        *   _Validation:_ Alphanumeric, min 3, max 30 characters.
    *   `password` (string, required): User's password.
        *   _Validation:_ Min 6 characters.
*   **Response (Success - 200 OK):**
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    }
    ```
*   **Response (Error - 400 Bad Request):**
    ```json
    {
      "errors": [
        "Username is required.",
        "Password should have a minimum length of 6."
      ]
    }
    ```
*   **Response (Error - 401 Unauthorized):**
    ```json
    {
      "error": "Invalid credentials"
    }
    ```

---

## Item Management

All item management endpoints (except `GET /items` and `GET /items/:id`) require `admin` role authorization. Authentication is via Bearer Token or Basic Auth.

### `GET /items`

*   **Description:** Retrieves a list of all available items.
*   **Authentication:** Required (Customer or Admin).
*   **Request Body:** None.
*   **Response (Success - 200 OK):**
    ```json
    [
      {
        "id": "1700000000000",
        "name": "Laptop Pro",
        "description": "High-performance laptop.",
        "price": 1200.00,
        "stock": 50
      },
      {
        "id": "1700000000001",
        "name": "Wireless Mouse",
        "description": "Ergonomic wireless mouse.",
        "price": 25.50,
        "stock": 200
      }
    ]
    ```
*   **Response (Error - 401 Unauthorized):**
    ```json
    {
      "error": "Authentication required."
    }
    ```

### `GET /items/:id`

*   **Description:** Retrieves details for a specific item by its ID.
*   **Authentication:** Required (Customer or Admin).
*   **Path Parameters:**
    *   `id` (string, required): The unique identifier of the item.
*   **Response (Success - 200 OK):**
    ```json
    {
      "id": "1700000000000",
      "name": "Laptop Pro",
      "description": "High-performance laptop.",
      "price": 1200.00,
      "stock": 50
    }
    ```
*   **Response (Error - 404 Not Found):**
    ```json
    {
      "error": "Item with ID 1700000000000 not found."
    }
    ```

### `POST /items`

*   **Description:** Creates a new item.
*   **Authentication:** Required (Admin only).
*   **Request Body:**
    *   `name` (string, required): Name of the item.
        *   _Validation:_ Alphanumeric, spaces allowed, min 3, max 100 characters.
    *   `description` (string, optional): Description of the item.
        *   _Validation:_ Alphanumeric, spaces, and common punctuation allowed, min 5, max 500 characters.
    *   `price` (number, required): Price of the item.
        *   _Validation:_ Positive number, 2 decimal places precision.
    *   `stock` (number, required): Current stock quantity.
        *   _Validation:_ Integer, minimum 0.
*   **Response (Success - 201 Created):**
    ```json
    {
      "id": "1700000000002",
      "name": "New Gadget",
      "description": "A shiny new gadget.",
      "price": 99.99,
      "stock": 100
    }
    ```
*   **Response (Error - 400 Bad Request):**
    ```json
    {
      "errors": [
        "Item name is required.",
        "Price must be a positive number."
      ]
    }
    ```
*   **Response (Error - 403 Forbidden):**
    ```json
    {
      "error": "Forbidden: customer does not have access."
    }
    ```

### `PUT /items/:id`

*   **Description:** Updates an existing item's details.
*   **Authentication:** Required (Admin only).
*   **Path Parameters:**
    *   `id` (string, required): The unique identifier of the item to update.
*   **Request Body:** (Same as `POST /items`, all fields are optional for partial updates, but should conform to schema if present).
    *   `name`, `description`, `price`, `stock` (all optional, but validated if provided).
*   **Response (Success - 200 OK):**
    ```json
    {
      "id": "1700000000000",
      "name": "Laptop Pro Max",
      "description": "Updated high-performance laptop.",
      "price": 1250.00,
      "stock": 45
    }
    ```
*   **Response (Error - 400 Bad Request):**
    ```json
    {
      "errors": [
        "Price must be a positive number."
      ]
    }
    ```
*   **Response (Error - 403 Forbidden):**
    ```json
    {
      "error": "Forbidden: customer does not have access."
    }
    ```
*   **Response (Error - 404 Not Found):**
    ```json
    {
      "error": "Item with ID 1700000000000 not found."
    }
    ```

### `DELETE /items/:id`

*   **Description:** Deletes an item by its ID.
*   **Authentication:** Required (Admin only).
*   **Path Parameters:**
    *   `id` (string, required): The unique identifier of the item to delete.
*   **Request Body:** None.
*   **Response (Success - 204 No Content):** Empty response.
*   **Response (Error - 403 Forbidden):**
    ```json
    {
      "error": "Forbidden: customer does not have access."
    }
    ```
*   **Response (Error - 404 Not Found):**
    ```json
    {
      "error": "Item with ID 1700000000000 not found."
    }
    ```

---

## Order Management

All order management endpoints have specific authentication and authorization rules as detailed below.

### `POST /orders`

*   **Description:** Creates a new order for a customer. This operation decrements the stock of the ordered items.
*   **Authentication:** Required (Admin only).
*   **Request Body:**
    *   `customerId` (string, required): The ID of the customer placing the order.
        *   _Validation:_ Alphanumeric.
    *   `items` (array of objects, required): A list of items to include in the order.
        *   _Validation:_ Must contain at least one item. Each item object:
            *   `itemId` (string, required): The ID of the item.
                *   _Validation:_ Alphanumeric.
            *   `quantity` (number, required): The quantity of the item.
                *   _Validation:_ Integer, minimum 1.
*   **Response (Success - 201 Created):**
    ```json
    {
      "id": "1700000000003",
      "customerId": "customer123",
      "items": [
        {
          "itemId": "1700000000000",
          "name": "Laptop Pro",
          "price": 1200.00,
          "quantity": 1
        }
      ],
      "total": 1200.00,
      "status": "pending",
      "createdAt": 1700000000003,
      "paid": false
    }
    ```
*   **Response (Error - 400 Bad Request):**
    ```json
    {
      "errors": [
        "Customer ID is required.",
        "Order must contain at least one item.",
        "Not enough stock for item: Laptop Pro."
      ]
    }
    ```
*   **Response (Error - 403 Forbidden):**
    ```json
    {
      "error": "Forbidden: customer does not have access."
    }
    ```

### `GET /orders`

*   **Description:** Retrieves a list of orders. Admins can view all orders; customers can only view their own orders.
*   **Authentication:** Required (Customer or Admin). The system identifies the user from the token/basic auth.
*   **Request Body:** None.
*   **Response (Success - 200 OK - Admin):**
    ```json
    [
      {
        "id": "1700000000003",
        "customerId": "customer123",
        "items": [...],
        "total": 1200.00,
        "status": "pending",
        "createdAt": 1700000000003,
        "paid": false
      },
      {
        "id": "1700000000004",
        "customerId": "customer456",
        "items": [...],
        "total": 50.00,
        "status": "paid",
        "createdAt": 1700000000004,
        "paid": true
      }
    ]
    ```
*   **Response (Success - 200 OK - Customer):** (Only orders belonging to the authenticated customer)
    ```json
    [
      {
        "id": "1700000000003",
        "customerId": "customer123",
        "items": [...],
        "total": 1200.00,
        "status": "pending",
        "createdAt": 1700000000003,
        "paid": false
      }
    ]
    ```
*   **Response (Error - 401 Unauthorized):**
    ```json
    {
      "error": "Authentication required."
    }
    ```

### `DELETE /orders/:id`

*   **Description:** Deletes an order by its ID. This operation also increments the stock of the items contained in the order.
*   **Authentication:** Required (Admin only).
*   **Path Parameters:**
    *   `id` (string, required): The unique identifier of the order to delete.
*   **Request Body:** None.
*   **Response (Success - 204 No Content):** Empty response.
*   **Response (Error - 403 Forbidden):**
    ```json
    {
      "error": "Forbidden: customer does not have access."
    }
    ```
*   **Response (Error - 404 Not Found):**
    ```json
    {
      "error": "Order with ID 1700000000003 not found."
    }
    ```

### `GET /orders/paid`

*   **Description:** Retrieves a list of all paid orders.
*   **Authentication:** Required (Admin only).
*   **Request Body:** None.
*   **Response (Success - 200 OK):**
    ```json
    [
      {
        "id": "1700000000004",
        "customerId": "customer456",
        "items": [...],
        "total": 50.00,
        "status": "paid",
        "createdAt": 1700000000004,
        "paid": true
      }
    ]
    ```
*   **Response (Error - 403 Forbidden):**
    ```json
    {
      "error": "Forbidden: customer does not have access."
    }
    ```

### `POST /orders/:id/checkout`

*   **Description:** Marks an order as paid.
*   **Authentication:** Required (Customer only). The customer ID from the authenticated user must match the order's customer ID.
*   **Path Parameters:**
    *   `id` (string, required): The unique identifier of the order to checkout.
*   **Request Body:** None.
*   **Response (Success - 200 OK):**
    ```json
    {
      "id": "1700000000003",
      "customerId": "customer123",
      "items": [...],
      "total": 1200.00,
      "status": "paid",
      "createdAt": 1700000000003,
      "paid": true
    }
    ```
*   **Response (Error - 400 Bad Request):**
    ```json
    {
      "error": "Order with ID 1700000000003 has already been paid."
    }
    ```
*   **Response (Error - 403 Forbidden):**
    ```json
    {
      "error": "Forbidden: admin does not have access."
    }
    ```
*   **Response (Error - 404 Not Found):**
    ```json
    {
      "error": "Order with ID 1700000000003 not found."
    }
    ```
*   **Response (Error - 401 Unauthorized):**
    ```json
    {
      "error": "Unauthorized: This order does not belong to the provided customer ID."
    }
    ```
