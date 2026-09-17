# Functional Documentation

## 1. Product Overview

The application helps a user discover LinkedIn jobs that match configured criteria, keep a local record of those jobs, monitor bot activity, and export the records. The application does not automatically apply to jobs; the user reviews each discovered job and applies manually through LinkedIn.

## 2. User Roles and Access

The current application has one implicit user role: an operator who can view the dashboard, configure searches, control the bot, manage job records, and export reports. Authentication and role-based access control are not implemented, so deployment should place the application behind appropriate access controls.

## 3. Main Screens

### Dashboard: `/`

The dashboard provides:

- Bot status: active or idle.
- Target search keywords.
- Controls to start, stop, or trigger one bot cycle.
- Total stored jobs.
- Jobs recorded today.
- Interviews scheduled.
- Offers received.
- Rejected jobs.
- Average match score.
- A table containing all stored job records, ordered with the newest first.
- Live updates while the bot is running.
- Links that open the original LinkedIn job page.
- A CSV report export action.

The job table can be scrolled normally, including with Page Down, because all stored records returned by the dashboard are rendered in one continuously expanding table.

### Configuration: `/config`

The configuration screen allows the operator to set:

- Maximum job age from 1 to 365 days.
- Search keywords, such as job titles or skills separated by commas.
- Country from the dropdown list.
- An optional company name.
- Whether to restrict results to Easy Apply jobs.

Selecting **Save configuration** sends the values to the configuration API. A success or error notification is shown without leaving the page.

## 4. Typical User Workflow

### 4.1 Configure a search

1. Open **Configuration** from the dashboard.
2. Choose a country.
3. Enter one or more job titles or skills.
4. Optionally enter a company.
5. Set the maximum posting age.
6. Enable **Only show jobs with Easy Apply** when required.
7. Select **Save configuration**.

The application validates required values in the browser and again on the server. Saved values are used by the next discovery cycle.

### 4.2 Run one discovery cycle

1. Select **Apply Now (Single Cycle)** on the dashboard.
2. The request returns immediately while the cycle runs asynchronously.
3. The bot reads the saved criteria and searches LinkedIn's public job page.
4. Matching listings are parsed and compared with existing records by job URL.
5. New jobs are stored with status `DISCOVERED`.
6. The dashboard receives live statistics and table updates.
7. Open a job's **View Job** link to review and apply manually on LinkedIn.

The button label says “Apply Now,” but the implementation performs discovery and recording only.

### 4.3 Run scheduled discovery

1. Select **Start Bot**.
2. The application immediately starts one asynchronous cycle.
3. While the bot is active, scheduled cycles run at the configured interval, 60 seconds by default.
4. Select **Stop Bot** to prevent future scheduled cycles.

If a cycle is already running, another trigger is ignored until that cycle finishes. Stopping the bot does not cancel work already in progress.

## 5. Job Record Behavior

Each discovered record contains:

- Database ID.
- Job title.
- Company.
- Location.
- LinkedIn URL.
- Calculated match score.
- Status.
- Timestamp.
- Notes indicating that the job was discovered and should be reviewed manually.

A listing is not saved again when its URL already exists in the database. Records can also be created manually through the REST API and deleted by ID.

Available status values are:

- `DISCOVERED`: found by the bot and awaiting user review.
- `APPLIED`: user has applied or the record is marked as applied.
- `INTERVIEW_SCHEDULED`: an interview is scheduled.
- `REJECTED`: the application was rejected.
- `OFFER_RECEIVED`: an offer was received.
- `FAILED`: an application or processing attempt failed.

## 6. Reporting

Select **Export Report CSV** from the dashboard to download a CSV report of all stored job records. The filename includes the current date.

## 7. Live Notifications

The browser subscribes to the dashboard update channel using STOMP over SockJS. Bot start, stop, search, save, and error events refresh the dashboard. The optional sound control can enable or disable browser notification sounds; the preference is stored in browser local storage.

If the WebSocket connection fails, the page retries the connection after five seconds.

## 8. REST Functional Reference

| Function | Request |
| --- | --- |
| Read bot status | `GET /api/v1/bot/status` |
| Start scheduled bot | `POST /api/v1/bot/start` |
| Stop scheduled bot | `POST /api/v1/bot/stop` |
| Run one cycle | `POST /api/v1/bot/trigger-cycle` |
| Read configuration | `GET /api/v1/config` |
| Save configuration | `PUT /api/v1/config` |
| List jobs | `GET /api/v1/jobs` |
| List jobs by page | `GET /api/v1/jobs/paged?page=0&size=10` |
| Read one job | `GET /api/v1/jobs/{id}` |
| Create a job record | `POST /api/v1/jobs` |
| Delete a job record | `DELETE /api/v1/jobs/{id}` |
| Export CSV | `GET /api/v1/reports/export/csv` |

Swagger UI provides an interactive version of these operations at `/swagger-ui.html`.

## 9. Validation and Error Behavior

- Maximum job age must be a whole number from 1 through 365.
- Country and search keywords are required.
- Company is optional and limited to 150 characters.
- Search keywords are limited to 500 characters.
- Country is selected from the configuration dropdown.
- A missing job ID returns HTTP 404.
- A successful manual job creation returns HTTP 201.
- A successful deletion returns HTTP 204.
- Bot execution errors are logged and published as dashboard notifications; the asynchronous request itself has already returned.

## 10. Data and Environment Notes

The default H2 profile stores data in memory. Restarting the application clears H2 records. PostgreSQL, Oracle, and SQL Server profiles are available for persistent deployments and require database-specific connection settings.

The dashboard and APIs currently expose job records without a login screen. Operators should use network-level or reverse-proxy access control when deploying the application.

## 11. Functional Boundaries

The current release does not provide:

- LinkedIn login or authenticated API access.
- Automatic form filling or application submission.
- User accounts or permissions.
- In-dashboard editing of a job's status.
- Filters or search controls over the stored job table.
- A visual pagination control on the dashboard; the dashboard presents the full list for browser scrolling, while the REST API also provides a paged endpoint.
