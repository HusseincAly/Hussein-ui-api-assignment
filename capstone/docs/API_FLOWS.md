# API Flows Documentation

This document outlines common API interaction flows for both customers and administrators, demonstrating how various endpoints can be chained together to achieve specific tasks.

---

## Customer Flows

### 1. User Registration & Login (Assumption)

*(Note: User registration is assumed to be handled by a separate process or an existing system, as no explicit registration API was requested. The `users.json` file is used to represent existing users.)*

*   **Goal:** Obtain an authentication token to access protected customer features.
*   **Steps:**
    1.  **`POST /login`**: Customer sends their `username` and `password`.
        *   **Request:**
            ```json
            {
              "username": "customer123",
              "password": "customer_pass"
            }
            ```
        *   **Response:** Receives a JWT `token`.
            ```json
            {
              "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            }
            ```
*   **Next Steps:** Use the obtained `token` in the `Authorization: Bearer <token>` header for all subsequent protected requests.

### 2. Browsing Items

*   **Goal:** View available products in the store.
*   **Steps:**
    1.  **`GET /items`**: Customer sends a request to list all items.
        *   **Request Headers:** `Authorization: Bearer <token>`
        *   **Response:** A list of item objects.
            ```json
            [
              { "id": "item1", "name": "Laptop", "price": 1200.00, ... },
              { "id": "item2", "name": "Mouse", "price": 25.00, ... }
            ]
            ```

### 3. Creating and Checking Out an Order

*   **Goal:** Purchase one or more items.
*   **Steps:**
    1.  **(Admin Action - Create Order for Customer)**: An administrator (or an automated system) creates an order on behalf of the customer, specifying the customer's ID and desired items.
        *   **`POST /orders`**: Admin sends a request to create an order.
            *   **Request Headers:** `Authorization: Bearer <admin_token>`
            *   **Request Body:**
                ```json
                {
                  "customerId": "customer123",
                  "items": [
                    { "itemId": "item1", "quantity": 1 },
                    { "itemId": "item2", "quantity": 2 }
                  ]
                }
                ```
            *   **Response:** The newly created order object with `status: "pending"` and `paid: false`.
                ```json
                {
                  "id": "orderABC",
                  "customerId": "customer123",
                  "items": [...],
                  "total": 1250.00,
                  "status": "pending",
                  "createdAt": ...,
                  "paid": false
                }
                ```
    2.  **(Customer Action - Checkout Order)**: The customer initiates the payment process for their pending order.
        *   **`POST /orders/:id/checkout`**: Customer sends a request to checkout their order.
            *   **Path Parameters:** `id` = `orderABC` (the ID of the pending order).
            *   **Request Headers:** `Authorization: Bearer <customer_token>`
            *   **Request Body:** None.
            *   **Response:** The updated order object with `status: "paid"` and `paid: true`.
                ```json
                {
                  "id": "orderABC",
                  "customerId": "customer123",
                  "items": [...],
                  "total": 1250.00,
                  "status": "paid",
                  "createdAt": ...,
                  "paid": true
                }
                ```

### 4. Viewing Own Orders

*   **Goal:** See a list of all orders placed by the customer.
*   **Steps:**
    1.  **`GET /orders`**: Customer sends a request to list orders. The system automatically filters by their `customerId`.
        *   **Request Headers:** `Authorization: Bearer <customer_token>`
        *   **Response:** A list of order objects specific to "customer123".
            ```json
            [
              { "id": "orderABC", "customerId": "customer123", ... }
            ]
            ```

---

## Administrator Flows

### 1. Admin Login

*   **Goal:** Authenticate as an administrator to access all management features.
*   **Steps:**
    1.  **`POST /login`**: Admin sends their `username` and `password`.
        *   **Request:**
            ```json
            {
              "username": "adminUser",
              "password": "admin_pass"
            }
            ```
        *   **Response:** Receives a JWT `token`.
            ```json
            {
              "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            }
            ```
*   **Next Steps:** Use the obtained `token` in the `Authorization: Bearer <token>` header for all subsequent protected requests, or use HTTP Basic Auth (username/password directly).

### 2. Item Management

*   **Goal:** Add, modify, or remove products from the store.
*   **Steps:**
    1.  **`POST /items`**: Admin creates a new product.
        *   **Request Headers:** `Authorization: Bearer <admin_token>`
        *   **Request Body:**
            ```json
            {
              "name": "New Product",
              "description": "Description of new product.",
              "price": 50.00,
              "stock": 100
            }
            ```
        *   **Response:** The newly created item object.
    2.  **`PUT /items/:id`**: Admin updates an existing product (e.g., price, stock).
        *   **Path Parameters:** `id` = `item1`
        *   **Request Headers:** `Authorization: Bearer <admin_token>`
        *   **Request Body:**
            ```json
            {
              "price": 1250.00,
              "stock": 45
            }
            ```
        *   **Response:** The updated item object.
    3.  **`DELETE /items/:id`**: Admin removes a product.
        *   **Path Parameters:** `id` = `item2`
        *   **Request Headers:** `Authorization: Bearer <admin_token>`
        *   **Response:** 204 No Content.

### 3. Order Oversight

*   **Goal:** View and manage all customer orders.
*   **Steps:**
    1.  **`GET /orders`**: Admin views all orders placed by any customer.
        *   **Request Headers:** `Authorization: Bearer <admin_token>`
        *   **Response:** A list of all order objects.
    2.  **`GET /orders/paid`**: Admin views only the orders that have been successfully paid for.
        *   **Request Headers:** `Authorization: Bearer <admin_token>`
        *   **Response:** A list of paid order objects.
    3.  **`DELETE /orders/:id`**: Admin cancels/removes an order. (This action also restocks the items).
        *   **Path Parameters:** `id` = `orderXYZ`
        *   **Request Headers:** `Authorization: Bearer <admin_token>`
        *   **Response:** 204 No Content.

---
