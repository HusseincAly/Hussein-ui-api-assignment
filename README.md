# Hussein's UI and API Automation Assignment

This project brings together the UI and API testing covered in Ahmed's sessions. It uses Java, Playwright and TestNG, with Ahmed's capstone application for the API tests and OrangeHRM for the UI test.

The framework follows the examples and design patterns from the course material. The capstone application is included in `capstone/`, with its original business logic unchanged.

## What the tests cover

### OrangeHRM

The UI test logs in as an admin, opens the Admin page, adds a user and searches for that user. It checks the username, role, employee name and status in the search result.

Each run creates a unique username starting with `Hussein` and deletes that account afterwards, so the test can be run again without reusing an existing user.

### Capstone API

The end-to-end test follows the order flow from the capstone business documents:

1. Log in as an admin and a customer.
2. Create two products and check that the customer can see them.
3. Create an order for the customer.
4. Check out as the customer.
5. Verify the total, payment status, order visibility and stock changes.
6. Check that the same order cannot be paid twice.
7. Delete the order, check that stock is restored, and remove the test products.

There are also 22 test cases for `POST /items` and 22 for `POST /orders`. These cover valid requests, boundary values, missing or invalid fields, malformed JSON and access permissions.

That gives **45 API tests and 1 UI test**. Both suites passed separately on my Mac on 9 November 2026.

## Design patterns

| Pattern | Where it is used | Purpose |
|---|---|---|
| Factory | `ApiContextFactory` | Creates the API request contexts. |
| Strategy | `AuthProvider` | Supports requests with and without bearer authentication. |
| Builder | `ItemBuilder` | Builds product data with reusable defaults. |
| Facade | `CheckoutWorkflow` | Groups the steps of the checkout flow. |
| Adapter | `ApiResult` | Makes response status, headers and body available to the tests. |
| Page Object Model | `LoginPage`, `AdminPage`, `AddUserPage` | Keeps browser interactions separate from test assertions. |

API clients handle endpoint calls, while the tests check the responses and saved data. The item and order datasets are JSON files in `src/test/resources/data/`.

## Before running the project

You will need:

- Java 21 or newer.
- Node.js and npm.
- Google Chrome for the UI test.
- The original capstone `users.json` file with an admin and a customer account.

The project includes Maven Wrapper, so Maven does not need to be installed separately. The dependency versions and wrapper configuration come from Ahmed's simplified framework.

The instructions below are for macOS/Linux. On Windows, use `.\mvnw.cmd` instead of `./mvnw` and set the environment variables in PowerShell.

## Start the capstone server

Copy the original `users.json` into `capstone/`. This file is excluded from Git because it contains account passwords.

Open a terminal in the project folder and run these commands one at a time:

```sh
cd capstone
npm ci
npm run build
npm start
```

You should see:

```text
IBM Intern API Server is running on port 3000
```

Leave this terminal running while you run the API tests.

## Set up the test accounts

Open a second terminal in the main project folder, where `pom.xml` is located.

Set the following variables using the accounts in your local `capstone/users.json`. Replace the example values before running the commands. Use an account with `isAdmin: true` for the admin and one with `isAdmin: false` for the customer.

```sh
export CAPSTONE_ADMIN_USERNAME='YOUR_ADMIN_USERNAME'
export CAPSTONE_ADMIN_PASSWORD='YOUR_ADMIN_PASSWORD'
export CAPSTONE_CUSTOMER_USERNAME='YOUR_CUSTOMER_USERNAME'
export CAPSTONE_CUSTOMER_PASSWORD='YOUR_CUSTOMER_PASSWORD'
export CAPSTONE_CUSTOMER_ID='YOUR_CUSTOMER_ID'
export API_BASE_URL='http://localhost:3000'
export BROWSER_CHANNEL='chrome'
```

If the customer has no `id` field, use their username as `CAPSTONE_CUSTOMER_ID`. The customer username, password and ID must all belong to the same account.

These settings apply to the current terminal session. The project reads environment variables directly; it does not load a `.env` file automatically. Keep account passwords out of committed files.

## Run the tests

Run these commands from the main project folder in the same terminal where you set the variables.

API tests:

```sh
./mvnw test
```

UI test:

```sh
./mvnw test -Dgroups=ui
```

Both suites:

```sh
./mvnw test '-Dgroups=api,ui'
```

If the wrapper gives a permission error on macOS/Linux, run `chmod +x mvnw` once.

The UI test can run on its own without the capstone server or API accounts. It uses the OrangeHRM demo login, `Admin` / `admin123`. To use different credentials, set `ORANGEHRM_USERNAME` and `ORANGEHRM_PASSWORD`.

The browser runs in the background by default. To watch the UI test, run this before starting it:

```sh
export HEADLESS='false'
```

The test selects an existing employee from autocomplete. If the demo data changes, set `ORANGEHRM_EMPLOYEE_HINT` to an available employee name. The default search hint is `a`.

## Check the results

The terminal shows the number of tests that passed, failed or were skipped, followed by `BUILD SUCCESS` or `BUILD FAILURE`.

Detailed reports are in `target/surefire-reports/`. The UI test also saves these files in `target/evidence/`:

- `orangehrm-search.png` — the verified search result.
- `orangehrm-failure.png` — a screenshot captured when a test fails.
- `orangehrm-trace.zip` — a browser trace for investigating problems. Keep it private because it may contain session data.

For the full list of scenarios, see [test coverage](docs/TEST_COVERAGE.md).

## Common problems

- **Java release 21 error:** check `java -version` and point `JAVA_HOME` to Java 21 or newer.
- **Connection refused:** make sure the capstone server is still running and `API_BASE_URL` matches its address.
- **Login returns 401:** check the username and password against the `users.json` used by the running server. Enter the values without extra spaces or surrounding JSON quotation marks.

Run the API tests sequentially. The capstone stores data in JSON files, so running multiple suites against the same server at once can cause conflicts.
