# Library Book Management System

A simple full-stack Library Book Management System built with React, Spring Boot, and H2 database. The UI intentionally uses a plain form, search box, and table—no complex dashboard or UI library.

## Features
- Add books with a unique Book ID, title, author, and category
- List all books in a table
- Search by title or author
- Edit book details
- Delete books
- Toggle availability between **Available** and **Issued**
- Server-side validation and duplicate-ID error handling
- H2 file database so data persists between application restarts

## Requirements
- Java 17+
- Maven 3.9+ (or use your IDE's Maven support)
- Node.js 18+ and npm

## Project structure
```text
library-book-management/
├── backend/       Spring Boot REST API
└── frontend/      React + Vite application
```

## 1. Start the backend
Open a terminal in `backend/` and run:

```bash
mvn spring-boot:run
```

API base URL: `http://localhost:8080/api/books`

H2 database files are stored under `backend/data/`. The database is created automatically.

Optional H2 console:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/librarydb`
- Username: `sa`
- Password: leave blank

## 2. Start the frontend
Open another terminal in `frontend/`:

```bash
npm install
npm run dev
```

Open the local URL printed by Vite (usually `http://localhost:5173`).

## REST API
| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/api/books` | List all books; optional `?search=term` searches title or author |
| `GET` | `/api/books/{bookId}` | Get one book |
| `POST` | `/api/books` | Add a book |
| `PUT` | `/api/books/{bookId}` | Update book details |
| `PATCH` | `/api/books/{bookId}/availability` | Toggle availability |
| `DELETE` | `/api/books/{bookId}` | Delete a book |

Example request body:
```json
{
  "bookId": "BK-101",
  "title": "The Hobbit",
  "author": "J. R. R. Tolkien",
  "category": "Fantasy",
  "status": "AVAILABLE"
}
```

Allowed status values are `AVAILABLE` and `ISSUED`. The UI displays these as **Available** and **Issued**.

## Validation and errors
- Book ID, title, author, and category are required.
- Book ID is unique; duplicate IDs return HTTP `409 Conflict`.
- Invalid fields return HTTP `400 Bad Request`.
- A missing book returns HTTP `404 Not Found`.
- The database also enforces uniqueness for the book ID.

## Testing
From `backend/`, run:
```bash
mvn test
```

The backend tests cover the REST API's create/list/search/update/delete flow and duplicate ID validation. For a quick manual check, add a book in the UI, refresh the browser, search by title/author, toggle its status, edit it, and delete it.

## Notes
- Frontend requests use Axios and are configured for `http://localhost:8080/api`.
- This is a learning/demo project. For production use, add authentication, authorization, and deployment configuration.
