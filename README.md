LinkTransformer

LinkTransformer is a RESTful Java API for shortening long URLs into compact, shareable links with built-in visit tracking and secure CRUD operations.

Features
* Shorten URLs into unique, 10-character strings (uppercase & lowercase letters).
* Track visits: Every redirect increments a visit counter stored in the database.
* Password-protected updates/deletions: Only users with the correct password can modify or delete a link.
* Full CRUD support
* In-memory H2 database: Stores links and visit statistics.
* Sample API requests and responses included.

Technologies Used
* Java 21
* Spring Boot
* Spring Web (REST)
* H2 Database
* Gradle

Example API Usage
Create a New Link (with or without password)

POST /api/links
Content-Type: application/json

{
  "name": "PJAIT",
  "targetUrl": "https://pja.edu.pl",
  "password": "abc123"
}


View Link Info

GET /api/links/abCdEFGHiJ

→ 200 OK

{
  "id": "abCdEFGHiJ",
  "name": "PJAIT",
  "targetUrl": "https://pja.edu.pl",
  "redirectUrl": "http://localhost:8080/red/abCdEFGHiJ",
  "visits": 3
}


Update a Link

PATCH /api/links/abCdEFGHiJ
Content-Type: application/json

{
  "name": "PJWSTK",
  "password": "abc123"
}

→ 204 No Content


Delete a Link

DELETE /api/links/abCdEFGHiJ
Header: password=abc123

→ 204 No Content

Error Handling
* 403 Forbidden – Wrong or missing password (with reason: wrong password header)
* 404 Not Found – Link does not exist


Database
All data is stored in an in-memory H2 database. Each link contains:
* id (shortened key)
* name
* targetUrl
* visits
* password (if provided)


Created with Itellij IDEA



