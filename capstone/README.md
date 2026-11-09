# API Backend Server

This project is a simple, layered API backend server built with Node.js, Express, and TypeScript. It provides basic CRUD (Create, Read, Update, Delete) functionality for JSON objects, storing them as individual files on the server. It also includes a simple authentication layer with support for JWT Bearer Tokens and Basic Authentication for admin users.

## Architecture

The application follows a layered architecture to separate concerns and improve maintainability:

-   **`src/controllers`**: This layer is responsible for handling incoming HTTP requests. It receives requests, validates parameters, and calls the appropriate service methods. It then formats the response and sends it back to the client.
-   **`src/services`**: This layer contains the core business logic of the application. It orchestrates the flow of data and operations, calling repository methods to interact with the data layer.
-   **`src/repositories`**: This layer is responsible for all data access operations. In this project, it handles reading from and writing to JSON files in the `src/data` directory.
-   **`src/middleware`**: This layer contains middleware functions that can process requests before they reach the route handlers. The `authMiddleware.ts` is used to protect endpoints.
-   **`src/routes.ts`**: This file defines all the API routes and maps them to the corresponding controller methods.

## Getting Started

### Prerequisites

-   [Node.js](https://nodejs.org/) (v14 or later)
-   [npm](https://www.npmjs.com/)

### Installation & Setup

1.  **Clone the repository (or download the files).**

2.  **Install dependencies:**
    ```bash
    npm install
    ```

### Running the Server

You can run the server in two modes:

1.  **Development Mode**: This mode uses `nodemon` and `ts-node` to automatically restart the server whenever you make changes to the source code.
    ```bash
    npm run dev
    ```

2.  **Production Mode**: This mode first builds the TypeScript code into JavaScript and then runs the compiled code.
    ```bash
    # Build the project
    npm run build

    # Start the server
    npm start
    ```

The server will be running at `http://localhost:3000`.

## Authentication

The API provides two methods for authentication: Bearer Token and Basic Authentication.

### 1. Bearer Token (All Users)

To get a Bearer Token, you must send a `POST` request to the `/login` endpoint with a valid username and password from the `users.json` file.

-   **Endpoint**: `POST /login`
-   **Request Body**:
    ```json
    {
      "username": "user",
      "password": "userpassword"
    }
    ```
-   **Success Response**:
    ```json
    {
      "token": "your.generated.jwt.token"
    }
    ```

You can then use this token to access the protected CRUD endpoints by including it in the `Authorization` header:

`Authorization: Bearer your.generated.jwt.token`

### 2. Basic Authentication (Admins Only)

Users marked with `"isAdmin": true` in the `users.json` file can use Basic Authentication to access the protected endpoints. Most API clients (like Postman or your web browser) will prompt for a username and password when you try to access a protected route without a valid Bearer Token.

## API Endpoints

### Authentication

| Method | Endpoint | Description                |
| :----- | :------- | :------------------------- |
| `POST` | `/login` | Generate a JWT Bearer Token. |

### JSON Data (Protected)

All `/json` routes are protected and require authentication.

| Method   | Endpoint     | Description                      | Request Body (Example)             | Success Response (Example)                           |
| :------- | :----------- | :------------------------------- | :--------------------------------- | :--------------------------------------------------- |
| `POST`   | `/json`      | Create a new JSON object.        | `{ "name": "test", "value": 123 }` | `{ "id": "...", "name": "test", "value": 123 }`      |
| `GET`    | `/json/:id`  | Retrieve a JSON object by ID.    | (None)                             | `{ "id": "...", "name": "test", "value": 123 }`      |
| `PUT`    | `/json/:id`  | Replace a JSON object by ID.     | `{ "name": "new name" }`           | `{ "id": "...", "name": "new name", "value": 123 }`  |
| `PATCH`  | `/json/:id`  | Partially update a JSON object.  | `{ "value": 456 }`                 | `{ "id": "...", "name": "new name", "value": 456 }`  |
| `DELETE` | `/json/:id`  | Delete a JSON object by ID.      | (None)                             | `204 No Content`                                     |
