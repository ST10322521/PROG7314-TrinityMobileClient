# NB!!!!

1. Please create a branch for each feature being worked on!!
2. Please make your branch name accurate to the feature being worked on
3. Please make sure your branch can merge without conflicts or test fails before accepting the pull request

---

# Part 2 Prototype — Team Responsibilities

# Simon — Android UI & Navigation

> **Simon is responsible ONLY for creating the navigable Android application interface.**

> Simon does not implement authentication, REST API communication, database functionality, settings logic, business logic, ViewModels, repositories, or custom-feature functionality.

> The purpose of Simon's work is to provide a complete, intuitive, navigable application shell that Lyle, James, and Sky can connect their functionality to.

## Development

- [ ] Create Android application structure
- [ ] Configure Kotlin/Android project
- [ ] Configure package structure
- [ ] Configure app theme
- [ ] Configure colours
- [ ] Configure fonts
- [ ] Configure styles
- [ ] Create reusable UI components
- [ ] Create common loading UI components
- [ ] Create common error UI components
- [ ] Implement navigation architecture
- [ ] Create navigation routes
- [ ] Implement Home/Dashboard screen
- [ ] Implement Login screen UI
- [ ] Implement Registration screen UI
- [ ] Implement Settings screen UI
- [ ] Implement custom-feature screen UI
- [ ] Create required forms and input fields
- [ ] Create buttons and user controls
- [ ] Create screen layouts
- [ ] Create loading-state UI
- [ ] Create empty-state UI
- [ ] Create error-state UI
- [ ] Create success-state UI
- [ ] Implement forward navigation
- [ ] Implement back navigation
- [ ] Ensure every required screen can be reached
- [ ] Ensure every required screen can return appropriately
- [ ] Add basic UI-level input feedback where appropriate
- [ ] Add comments to UI/navigation code
- [ ] Add technical references where external UI/code patterns are used

## Navigation Scope

- [ ] Application launches successfully
- [ ] Login screen can be opened
- [ ] Registration screen can be opened
- [ ] Home/Dashboard can be opened
- [ ] Settings screen can be opened
- [ ] Custom-feature screens can be opened
- [ ] Navigation between screens works
- [ ] Back navigation works
- [ ] Appropriate navigation routes exist
- [ ] Navigation does not crash
- [ ] Placeholder UI exists where functionality will later be implemented by another member

## UI Testing

> Simon tests only the UI and navigation he created.

- [ ] Test application launches
- [ ] Test navigation to Login
- [ ] Test navigation to Registration
- [ ] Test navigation to Home/Dashboard
- [ ] Test navigation to Settings
- [ ] Test navigation to custom-feature screens
- [ ] Test forward navigation
- [ ] Test back navigation
- [ ] Test navigation routes
- [ ] Test required UI elements exist
- [ ] Test basic UI input fields
- [ ] Test basic UI validation/display behaviour
- [ ] Test loading UI
- [ ] Test empty-state UI
- [ ] Test error-state UI
- [ ] Test success-state UI
- [ ] Test invalid UI interactions do not crash the application
- [ ] Run all UI/navigation tests locally
- [ ] Fix failures in their own UI/navigation code
- [ ] Verify tests pass through GitHub Actions

## Explicitly NOT Simon's Responsibility

- [ ] SSO implementation
- [ ] Authentication implementation
- [ ] Registration logic
- [ ] Session management
- [ ] Logout logic
- [ ] User identification
- [ ] Settings/preferences logic
- [ ] Application ViewModels
- [ ] Repositories
- [ ] REST API communication
- [ ] API clients
- [ ] Database functionality
- [ ] Backend functionality
- [ ] Business logic
- [ ] Custom-feature functionality
- [ ] Connecting application functionality to the API
- [ ] Authentication tests
- [ ] API tests
- [ ] Database tests
- [ ] Custom-feature tests

## Deliverable

**A complete, visually functional and navigable Android application shell containing all required screens and navigation routes, with UI/navigation tests proving that the application can be navigated successfully.**

---

# Lyle — Authentication & Settings

## Authentication Development

- [ ] Select SSO provider
- [ ] Configure authentication service
- [ ] Configure Android authentication integration
- [ ] Implement authentication client
- [ ] Implement SSO login
- [ ] Implement registration
- [ ] Implement registration validation
- [ ] Implement successful authentication handling
- [ ] Implement authentication failure handling
- [ ] Implement authentication cancellation handling
- [ ] Implement authentication state
- [ ] Implement authenticated state
- [ ] Implement unauthenticated state
- [ ] Implement session restoration
- [ ] Implement session expiry handling
- [ ] Implement logout
- [ ] Implement user identification
- [ ] Connect authentication functionality to Simon's UI
- [ ] Add authentication/state logging using Android `Log`
- [ ] Add clear comments to authentication code
- [ ] Add technical references where external code/patterns are used

## Settings Development

- [ ] Define user preference model
- [ ] Implement preference retrieval
- [ ] Implement preference modification
- [ ] Implement preference saving
- [ ] Associate preferences with authenticated user
- [ ] Restore preferences after login
- [ ] Handle preference errors
- [ ] Connect settings functionality to Simon's Settings UI
- [ ] Add preference state logging

## Unit Testing

> Lyle writes and maintains all tests for the authentication and settings functionality he develops.

- [ ] Set up authentication/settings testing
- [ ] Test successful registration
- [ ] Test invalid registration
- [ ] Test duplicate registration
- [ ] Test successful SSO login
- [ ] Test failed login
- [ ] Test cancelled login
- [ ] Test authentication error handling
- [ ] Test authenticated state
- [ ] Test unauthenticated state
- [ ] Test session restoration
- [ ] Test session expiry
- [ ] Test logout
- [ ] Test preference creation
- [ ] Test preference retrieval
- [ ] Test preference modification
- [ ] Test preference persistence
- [ ] Test user-specific preferences
- [ ] Test invalid preference values
- [ ] Test authentication failures do not crash application logic
- [ ] Run tests locally
- [ ] Fix failures in their own code/tests
- [ ] Verify tests pass through GitHub Actions

## Deliverable

**Working authentication and settings functionality connected to Simon's UI + tests proving the functionality works.**

---

# James — REST API & Database

## Database Development

- [ ] Select database technology
- [ ] Create cloud database
- [ ] Design database schema
- [ ] Create tables/collections
- [ ] Define relationships
- [ ] Define user-data ownership
- [ ] Configure database security
- [ ] Create development/test data
- [ ] Add database logging where appropriate
- [ ] Add clear comments to backend code
- [ ] Add technical references where external code/patterns are used

## REST API Development

- [ ] Select backend framework
- [ ] Create backend project
- [ ] Configure backend environment
- [ ] Configure database connection
- [ ] Create API structure
- [ ] Define request structures
- [ ] Define response structures
- [ ] Create GET endpoints
- [ ] Create POST endpoints
- [ ] Create PUT/PATCH endpoints
- [ ] Create DELETE endpoints
- [ ] Create custom-feature endpoints where required
- [ ] Implement endpoints required by the Android application

## API Validation

- [ ] Validate request bodies
- [ ] Validate required fields
- [ ] Validate data types
- [ ] Handle malformed requests
- [ ] Handle missing resources
- [ ] Handle duplicate data
- [ ] Handle database errors
- [ ] Return correct HTTP status codes
- [ ] Return consistent API errors

## API Security

- [ ] Validate authenticated API requests
- [ ] Associate requests with users
- [ ] Prevent cross-user data access
- [ ] Handle expired authentication
- [ ] Handle unauthorized requests

## Deployment

- [ ] Deploy API to cloud hosting
- [ ] Configure production environment variables
- [ ] Connect production database
- [ ] Verify production API
- [ ] Document API endpoints
- [ ] Add backend logging

## Frontend/API Integration

> James is responsible for the backend side of the frontend/backend connection.

- [ ] Define API contract with frontend developers
- [ ] Define endpoint requirements
- [ ] Define request formats
- [ ] Define response formats
- [ ] Define error formats
- [ ] Ensure API is accessible by Android application
- [ ] Ensure API authentication works with the Android application
- [ ] Ensure API responses contain required data
- [ ] Support Lyle/Sky integration requirements

## Unit/Backend Testing

- [ ] Set up backend testing structure
- [ ] Test GET endpoints
- [ ] Test POST endpoints
- [ ] Test PUT/PATCH endpoints
- [ ] Test DELETE endpoints
- [ ] Test successful database operations
- [ ] Test failed database operations
- [ ] Test invalid request bodies
- [ ] Test missing fields
- [ ] Test invalid data types
- [ ] Test missing resources
- [ ] Test duplicate data
- [ ] Test unauthorized requests
- [ ] Test expired authentication
- [ ] Test cross-user data protection
- [ ] Test API error responses
- [ ] Test HTTP status codes
- [ ] Test API → database communication
- [ ] Test database → API responses
- [ ] Test deployed production API where practical
- [ ] Test backend error handling
- [ ] Test malformed requests
- [ ] Run tests locally
- [ ] Fix failures in their own code/tests
- [ ] Verify tests pass through GitHub Actions

## Deliverable

**Working cloud REST API/database + backend tests proving the backend works and API contract required by the Android application is available.**

---

# Sky — Custom Features

> **Sky owns the custom user-defined functionality selected from the Part 1 design.**

> Each custom feature should be tracked as its own GitHub Issue.

---

# Custom Feature #1

## Development

- [ ] Define Feature #1 requirements
- [ ] Create Feature #1 data model
- [ ] Implement Feature #1 business logic
- [ ] Implement Feature #1 UI functionality using Simon's UI shell
- [ ] Implement Feature #1 validation
- [ ] Implement Feature #1 frontend API communication
- [ ] Connect Feature #1 to required REST API endpoints
- [ ] Connect Feature #1 to required database functionality through the API
- [ ] Implement Feature #1 error handling
- [ ] Add Feature #1 logging
- [ ] Add comments to Feature #1 code
- [ ] Add technical references where external code/patterns are used

## Unit Testing

- [ ] Test Feature #1 normal operation
- [ ] Test Feature #1 invalid input
- [ ] Test Feature #1 empty input
- [ ] Test Feature #1 business logic
- [ ] Test Feature #1 API communication logic
- [ ] Test Feature #1 data persistence
- [ ] Test Feature #1 data retrieval
- [ ] Test Feature #1 error handling
- [ ] Test Feature #1 edge cases
- [ ] Run tests locally
- [ ] Fix failures in their own code/tests
- [ ] Verify tests pass through GitHub Actions

---

# Custom Feature #2

## Development

- [ ] Define Feature #2 requirements
- [ ] Create Feature #2 data model
- [ ] Implement Feature #2 business logic
- [ ] Implement Feature #2 UI functionality using Simon's UI shell
- [ ] Implement Feature #2 validation
- [ ] Implement Feature #2 frontend API communication
- [ ] Connect Feature #2 to required REST API endpoints
- [ ] Connect Feature #2 to required database functionality through the API
- [ ] Implement Feature #2 error handling
- [ ] Add Feature #2 logging
- [ ] Add comments to Feature #2 code
- [ ] Add technical references where external code/patterns are used

## Unit Testing

- [ ] Test Feature #2 normal operation
- [ ] Test Feature #2 invalid input
- [ ] Test Feature #2 empty input
- [ ] Test Feature #2 business logic
- [ ] Test Feature #2 API communication logic
- [ ] Test Feature #2 data persistence
- [ ] Test Feature #2 data retrieval
- [ ] Test Feature #2 error handling
- [ ] Test Feature #2 edge cases
- [ ] Run tests locally
- [ ] Fix failures in their own code/tests
- [ ] Verify tests pass through GitHub Actions

> Repeat this structure for every custom feature specified in the Part 1 design.

## Deliverable

**Working custom functionality + tests proving each custom feature works.**

---

# Member 5 — Team Lead

> **Member 5 does not develop application features and does not write application unit tests.**

> Member 5's responsibility is:
>
> **Set Up → Coordinate → Integrate → Compile → Verify → Submit**

---

# Team Lead — GitHub Setup

- [ ] Create GitHub repository
- [ ] Add team members
- [ ] Configure repository permissions
- [ ] Create `main` branch
- [ ] Establish branch naming convention
- [ ] Establish commit convention
- [ ] Configure branch protection
- [ ] Create GitHub Issues
- [ ] Create GitHub Project board
- [ ] Create milestones
- [ ] Assign responsibilities
- [ ] Establish pull request workflow
- [ ] Ensure members commit regularly
- [ ] Ensure members push work regularly

---

# Team Lead — Initial Project Setup

> The Team Lead creates the starting environment so Simon, Lyle, James, and Sky can work independently.

- [ ] Create/initialise Android project
- [ ] Configure initial Gradle project
- [ ] Configure `.gitignore`
- [ ] Create initial package/project structure
- [ ] Push initial project to GitHub
- [ ] Verify clean project builds
- [ ] Verify all members can clone repository
- [ ] Verify all members can build project
- [ ] Establish development branches
- [ ] Ensure project supports unit testing
- [ ] Ensure project is ready for development

> **After initial setup, application development belongs to Simon, Lyle, James, and Sky.**

---

# Team Lead — GitHub Actions / CI

> **The Team Lead sets up and configures GitHub Actions. They do not write the tests that GitHub Actions executes.**

## Workflow Setup

- [ ] Create `.github/workflows/` directory
- [ ] Create GitHub Actions workflow file
- [ ] Configure JDK
- [ ] Configure Gradle
- [ ] Configure Android build environment
- [ ] Configure application compilation
- [ ] Configure unit-test execution
- [ ] Configure workflow to run on every push
- [ ] Configure workflow to run on pull requests
- [ ] Configure appropriate build/test commands
- [ ] Commit workflow configuration
- [ ] Push workflow configuration
- [ ] Verify GitHub Actions starts correctly
- [ ] Verify application build runs
- [ ] Verify unit tests run
- [ ] Verify failed tests cause workflow failure
- [ ] Verify successful tests produce a passing workflow
- [ ] Maintain CI configuration throughout development

## Team Lead Does NOT Write

- [ ] Simon's UI tests
- [ ] Lyle's authentication/settings tests
- [ ] James's API/database tests
- [ ] Sky's custom-feature tests
