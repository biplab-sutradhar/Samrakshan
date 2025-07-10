 list of **all current endpoints** (✅ implemented) **plus optional/future suggestions** (💡 for future enhancement), organized by controller and grouped by functionality.

---

### ✅ **1. TeamFormationController**

| Method | Endpoint                                 | Purpose                                     |
| ------ | ---------------------------------------- | ------------------------------------------- |
| GET    | `/api/team-formations/{id}`              | Get team by ID                              |
| GET    | `/api/team-formations/case/{caseId}`     | Get team by case ID                         |
| POST   | `/api/team-formations`                   | Create new team                             |
| PUT    | `/api/team-formations/{id}/response`     | Team member accepts/rejects case            |
| 💡 GET | `/api/team-formations/pending-responses` | 🔍 View pending responses from team members |

---

### ✅ **2. ReportController**

| Method | Endpoint                              | Purpose                               |
| ------ | ------------------------------------- | ------------------------------------- |
| DELETE | `/api/reports/{id}`                   | Delete a report                       |
| GET    | `/api/reports/{id}`                   | Get report by ID                      |
| GET    | `/api/reports/team-member/{personId}` | Get reports by a team member          |
| GET    | `/api/reports/case/{caseId}`          | Get reports for a case                |
| POST   | `/api/reports`                        | Submit a new report                   |
| PUT    | `/api/reports/{id}`                   | Update a report                       |
| 💡 GET | `/api/cases/{caseId}/final-report`    | 📊 Get final compiled report for case |

---

### ✅ **3. PersonController**

| Method | Endpoint             | Purpose                |
| ------ | -------------------- | ---------------------- |
| DELETE | `/api/persons/{id}`  | Delete person          |
| GET    | `/api/persons/{id}`  | Get person by ID       |
| GET    | `/api/persons`       | List all persons       |
| POST   | `/api/persons`       | Create/register person |
| POST   | `/api/persons/login` | Authenticate (login)   |
| PUT    | `/api/persons/{id}`  | Update person info     |

---

### ✅ **4. CaseController**

| Method  | Endpoint                       | Purpose                        |
| ------- | ------------------------------ | ------------------------------ |
| DELETE  | `/api/cases/{id}`              | Delete a case                  |
| GET     | `/api/cases/{id}`              | Get case by ID                 |
| GET     | `/api/cases`                   | List all cases                 |
| POST    | `/api/cases`                   | Submit new case                |
| POST    | `/api/cases/{caseId}/team`     | Assign team to case            |
| PUT     | `/api/cases/{id}`              | Update case                    |
| 💡 GET  | `/api/cases/{id}/status`       | 🔍 View current case status    |
| 💡 POST | `/api/cases/{caseId}/escalate` | 📌 Manually trigger escalation |

---

### 💡 **5. AdminController** (optional)

| Method | Endpoint           | Purpose       |
| ------ | ------------------ | ------------- |
| POST   | `/api/admin/login` | ✅ Admin login |

---

### 💡 **6. DepartmentController** (optional)

| Method | Endpoint                                   | Purpose                                    |
| ------ | ------------------------------------------ | ------------------------------------------ |
| GET    | `/api/departments/district/{districtName}` | 🗺️ List departments/employees by district |

---