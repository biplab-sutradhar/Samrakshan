

### All Endpoints

#### Case Management Endpoints

##### 1. **POST /api/cases**
- **Description**: Submits a new child marriage case, triggering team formation.
- **Request (req)**:
  ```json
  {
    "complainantName": "John Doe",
    "complainantPhone": "1234567890",
    "caseAddress": "123 Main St",
    "district": "Unknown District",
    "state": "Unknown State",
    "description": "Child marriage reported",
    "reportedAt": "2025-07-07T18:00:00",
    "createdBy": "550e8400-e29b-41d4-a716-44665544000a",
    "status": "OPEN",
    "caseDetails": {
      "notes": "Initial report details",
      "evidencePath": "path/to/evidence"
    }
  }
  ```
    - `complainantName`: String (required).
    - `complainantPhone`: String (required).
    - `caseAddress`: String (required).
    - `district`: String (required).
    - `state`: String (required).
    - `description`: String (optional).
    - `reportedAt`: ISO 8601 timestamp (optional).
    - `createdBy`: UUID of the person submitting the case (required).
    - `status`: String (optional, defaults to "OPEN").
    - `caseDetails`: Object with `notes` and `evidencePath` (optional).

- **Response (res)**:
  ```json
  {
    "id": "550e8400-e29b-41d4-a716-44665544000b",
    "complainantName": "John Doe",
    "complainantPhone": "1234567890",
    "caseAddress": "123 Main St",
    "district": "Unknown District",
    "state": "Unknown State",
    "description": "Child marriage reported",
    "reportedAt": "2025-07-07T18:00:00",
    "createdBy": "550e8400-e29b-41d4-a716-44665544000a",
    "status": "OPEN",
    "createdAt": "2025-07-07T18:51:00",
    "updatedAt": "2025-07-07T18:51:00",
    "caseDetails": [
      {
        "id": "550e8400-e29b-41d4-a716-44665544000c",
        "caseId": "550e8400-e29b-41d4-a716-44665544000b",
        "notes": "Initial report details",
        "evidencePath": "path/to/evidence",
        "createdAt": "2025-07-07T18:51:00",
        "updatedAt": "2025-07-07T18:51:00"
      }
    ]
  }
  ```
    - **HTTP Status**: `200 OK`

##### 2. **GET /api/cases/{id}**
- **Description**: Retrieves a specific case by its ID.
- **Request (req)**: (URL parameter) `http://localhost:8080/api/cases/550e8400-e29b-41d4-a716-44665544000b`
    - `id`: UUID of the case to retrieve.

- **Response (res)**:
  ```json
  {
    "id": "550e8400-e29b-41d4-a716-44665544000b",
    "complainantName": "John Doe",
    "complainantPhone": "1234567890",
    "caseAddress": "123 Main St",
    "district": "Unknown District",
    "state": "Unknown State",
    "description": "Child marriage reported",
    "reportedAt": "2025-07-07T18:00:00",
    "createdBy": "550e8400-e29b-41d4-a716-44665544000a",
    "status": "OPEN",
    "createdAt": "2025-07-07T18:51:00",
    "updatedAt": "2025-07-07T18:51:00",
    "caseDetails": [
      {
        "id": "550e8400-e29b-41d4-a716-44665544000c",
        "caseId": "550e8400-e29b-41d4-a716-44665544000b",
        "notes": "Initial report details",
        "evidencePath": "path/to/evidence",
        "createdAt": "2025-07-07T18:51:00",
        "updatedAt": "2025-07-07T18:51:00"
      }
    ]
  }
  ```
    - **HTTP Status**: `200 OK`
    - **Error Response (404)**:
      ```json
      {
        "error": "Case not found with ID: 550e8400-e29b-41d4-a716-44665544000b"
      }
      ```
        - **HTTP Status**: `404 Not Found`

##### 3. **GET /api/cases**
- **Description**: Retrieves all cases.
- **Request (req)**: (No body) `http://localhost:8080/api/cases`

- **Response (res)**:
  ```json
  [
    {
      "id": "550e8400-e29b-41d4-a716-44665544000b",
      "complainantName": "John Doe",
      "complainantPhone": "1234567890",
      "caseAddress": "123 Main St",
      "district": "Unknown District",
      "state": "Unknown State",
      "description": "Child marriage reported",
      "reportedAt": "2025-07-07T18:00:00",
      "createdBy": "550e8400-e29b-41d4-a716-44665544000a",
      "status": "OPEN",
      "createdAt": "2025-07-07T18:51:00",
      "updatedAt": "2025-07-07T18:51:00",
      "caseDetails": [
        {
          "id": "550e8400-e29b-41d4-a716-44665544000c",
          "caseId": "550e8400-e29b-41d4-a716-44665544000b",
          "notes": "Initial report details",
          "evidencePath": "path/to/evidence",
          "createdAt": "2025-07-07T18:51:00",
          "updatedAt": "2025-07-07T18:51:00"
        }
      ]
    }
  ]
  ```
    - **HTTP Status**: `200 OK`

##### 4. **PUT /api/cases/{id}**
- **Description**: Updates an existing case.
- **Request (req)**: (URL parameter) `http://localhost:8080/api/cases/550e8400-e29b-41d4-a716-44665544000b`
  ```json
  {
    "complainantName": "John Doe Updated",
    "complainantPhone": "0987654321",
    "caseAddress": "456 Updated St",
    "district": "Updated District",
    "state": "Updated State",
    "description": "Updated child marriage report",
    "reportedAt": "2025-07-07T18:30:00",
    "createdBy": "550e8400-e29b-41d4-a716-44665544000a",
    "status": "IN_PROGRESS",
    "caseDetails": {
      "notes": "Updated report details",
      "evidencePath": "path/to/updated/evidence"
    }
  }
  ```
    - **HTTP Status**: `200 OK`
    - **Error Response (404)**:
      ```json
      {
        "error": "Case not found with ID: 550e8400-e29b-41d4-a716-44665544000b"
      }
      ```
        - **HTTP Status**: `404 Not Found`

##### 5. **DELETE /api/cases/{id}**
- **Description**: Deletes a specific case by its ID.
- **Request (req)**: (URL parameter) `http://localhost:8080/api/cases/550e8400-e29b-41d4-a716-44665544000b`
    - `id`: UUID of the case to delete.

- **Response (res)**:
  ```json
  {}
  ```
    - **HTTP Status**: `204 No Content`
    - **Error Response (404)**:
      ```json
      {
        "error": "Case not found with ID: 550e8400-e29b-41d4-a716-44665544000b"
      }
      ```
        - **HTTP Status**: `404 Not Found`

#### Team Formation Endpoints

##### 6. **POST /api/team-formations**
- **Description**: Creates a new team formation for a specific case by assigning members from Police, DICE, and Administration departments.
- **Request (req)**:
  ```json
  {
    "caseId": "550e8400-e29b-41d4-a716-44665544000a",
    "policePersonId": "550e8400-e29b-41d4-a716-44665544000e",
    "dicePersonId": "550e8400-e29b-41d4-a716-44665544000f",
    "adminPersonId": "550e8400-e29b-41d4-a716-446655440010"
  }
  ```
    - `caseId`: UUID of the associated case (required).
    - `policePersonId`: UUID of the police department member (required).
    - `dicePersonId`: UUID of the DICE department member (required).
    - `adminPersonId`: UUID of the administration department member (required).

- **Response (res)**:
  ```json
  {
    "caseId": "550e8400-e29b-41d4-a716-44665544000a",
    "policePersonId": "550e8400-e29b-41d4-a716-44665544000e",
    "dicePersonId": "550e8400-e29b-41d4-a716-44665544000f",
    "adminPersonId": "550e8400-e29b-41d4-a716-446655440010",
    "formedAt": "2025-07-07T18:51:00",
    "policeStatus": "PENDING",
    "diceStatus": "PENDING",
    "adminStatus": "PENDING"
  }
  ```
    - **HTTP Status**: `200 OK`

##### 7. **GET /api/team-formations/{id}**
- **Description**: Retrieves details of a specific team formation by its ID.
- **Request (req)**: (URL parameter) `http://localhost:8080/api/team-formations/550e8400-e29b-41d4-a716-44665544000e`
    - `id`: UUID of the team formation to retrieve.

- **Response (res)**:
  ```json
  {
    "caseId": "550e8400-e29b-41d4-a716-44665544000a",
    "policePersonId": "550e8400-e29b-41d4-a716-44665544000e",
    "dicePersonId": "550e8400-e29b-41d4-a716-44665544000f",
    "adminPersonId": "550e8400-e29b-41d4-a716-446655440010",
    "formedAt": "2025-07-07T18:51:00",
    "policeStatus": "PENDING",
    "diceStatus": "ACCEPTED",
    "adminStatus": "REJECTED"
  }
  ```
    - **HTTP Status**: `200 OK`
    - **Error Response (404)**:
      ```json
      {
        "error": "Team formation not found with ID: 550e8400-e29b-41d4-a716-44665544000e"
      }
      ```
        - **HTTP Status**: `404 Not Found`

##### 8. **PUT /api/team-formations/{id}/response**
- **Description**: Handles the response (accept/reject) from a team member based on their department.
- **Request (req)**: (URL parameter and query parameters) `http://localhost:8080/api/team-formations/550e8400-e29b-41d4-a716-44665544000e?department=POLICE&status=ACCEPTED`
    - `id`: UUID of the team formation.
    - `department`: String indicating the department ("POLICE", "DICE", "ADMINISTRATION") (required).
    - `status`: String indicating the response ("ACCEPTED", "REJECTED") (required).

- **Response (res)**:
  ```json
  {}
  ```
    - **HTTP Status**: `200 OK`
    - **Error Response (400)**:
      ```json
      {
        "error": "Invalid department"
      }
      ```
        - **HTTP Status**: `400 Bad Request` (if department is invalid)
    - **Error Response (404)**:
      ```json
      {
        "error": "Team formation not found with ID: 550e8400-e29b-41d4-a716-44665544000e"
      }
      ```
        - **HTTP Status**: `404 Not Found`

#### Additional Notes (POST /api/cases/{caseId}/team)
- **Description**: This endpoint was included in the `CaseController` to manually form a team for a case, but it’s not part of the automatic team formation triggered by `POST /api/cases`. It’s retained for manual intervention.
- **Request (req)**: (URL parameter) `http://localhost:8080/api/cases/550e8400-e29b-41d4-a716-44665544000a/team`
  ```json
  {
    "policePersonId": "550e8400-e29b-41d4-a716-44665544000e",
    "dicePersonId": "550e8400-e29b-41d4-a716-44665544000f",
    "adminPersonId": "550e8400-e29b-41d4-a716-446655440010"
  }
  ```
    - `caseId`: UUID from the URL path (required).
    - `policePersonId`: UUID of the police department member (required).
    - `dicePersonId`: UUID of the DICE department member (required).
    - `adminPersonId`: UUID of the administration department member (required).

- **Response (res)**:
  ```json
  {
    "caseId": "550e8400-e29b-41d4-a716-44665544000a",
    "policePersonId": "550e8400-e29b-41d4-a716-44665544000e",
    "dicePersonId": "550e8400-e29b-41d4-a716-44665544000f",
    "adminPersonId": "550e8400-e29b-41d4-a716-446655440010",
    "formedAt": "2025-07-07T18:51:00",
    "policeStatus": "PENDING",
    "diceStatus": "PENDING",
    "adminStatus": "PENDING"
  }
  ```
    - **HTTP Status**: `200 OK`
    - **Error Response (404)**:
      ```json
      {
        "error": "Case not found with ID: 550e8400-e29b-41d4-a716-44665544000a"
      }
      ```
        - **HTTP Status**: `404 Not Found`
