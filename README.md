# NB!!!!
1. Please create a branch for each feature being worked on!!
2. Please make your branch name accurate to the feature being worked on
3. Please make sure your branch can merge without conflicts or test fails before accepting the pull rewuest


Current suggested work spread according to AI:

# Project Responsibilities

---

# Member 1 — Android UI & Application Structure

## Development

- [ ] Create Android application structure
- [ ] Configure Kotlin/Android project
- [ ] Configure package structure
- [ ] Configure app theme
- [ ] Configure colours/fonts/styles
- [ ] Create reusable UI components
- [ ] Create common loading components
- [ ] Create common error components
- [ ] Implement navigation architecture
- [ ] Create navigation routes
- [ ] Implement Home/Dashboard screen
- [ ] Implement Login screen UI
- [ ] Implement Registration screen UI
- [ ] Implement Settings screen UI
- [ ] Implement custom-feature screen UI
- [ ] Implement loading states
- [ ] Implement empty states
- [ ] Implement UI error states
- [ ] Implement input validation
- [ ] Implement invalid-input messages
- [ ] Implement ViewModels/state management
- [ ] Implement mock repositories/data
- [ ] Connect UI to application state
- [ ] Add lifecycle/state logging

## Testing

> Member 1 tests everything they build.

- [ ] Test navigation to every screen
- [ ] Test back navigation
- [ ] Test UI input validation
- [ ] Test required fields
- [ ] Test invalid inputs
- [ ] Test loading states
- [ ] Test error states
- [ ] Test empty states
- [ ] Test ViewModel state changes
- [ ] Test screen state restoration where applicable
- [ ] Test UI with mock data
- [ ] Test application doesn't crash during invalid UI interactions
- [ ] Test navigation when user is authenticated
- [ ] Test navigation when user is unauthenticated

## Deliverable

**Working Android front-end + tests proving the front-end works.**

---

# Member 2 — Authentication & Settings

## Development

- [ ] Select/configure SSO provider
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
- [ ] Define user preference model
- [ ] Implement preference retrieval
- [ ] Implement preference modification
- [ ] Implement preference saving
- [ ] Associate preferences with authenticated user
- [ ] Restore preferences after login
- [ ] Handle preference errors
- [ ] Add authentication/state logging

## Testing

> Member 2 tests everything they build.

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
- [ ] Test authentication failure doesn't crash the application

## Deliverable

**Working authentication/settings functionality + tests proving authentication and preferences work.**

---

# Member 3 — REST API & Database

## Database

- [ ] Select database technology
- [ ] Create cloud database
- [ ] Design database schema
- [ ] Create tables/collections
- [ ] Define relationships
- [ ] Define user-data ownership
- [ ] Configure database security
- [ ] Create development/test data

## REST API

- [ ] Select backend framework
- [ ] Create backend project
- [ ] Configure backend environment
- [ ] Configure database connection
- [ ] Create API structure
- [ ] Create GET endpoints
- [ ] Create POST endpoints
- [ ] Create PUT/PATCH endpoints
- [ ] Create DELETE endpoints
- [ ] Create custom-feature endpoints where required

## Validation

- [ ] Validate request bodies
- [ ] Validate required fields
- [ ] Validate data types
- [ ] Handle malformed requests
- [ ] Handle missing resources
- [ ] Handle duplicate data
- [ ] Handle database errors
- [ ] Return correct HTTP status codes
- [ ] Return consistent API errors

## Security

- [ ] Validate authenticated API requests
- [ ] Associate requests with users
- [ ] Prevent cross-user data access
- [ ] Handle expired authentication
- [ ] Handle unauthorized requests

## Deployment

- [ ] Deploy API
- [ ] Configure production environment variables
- [ ] Connect production database
- [ ] Verify production API
- [ ] Document API endpoints
- [ ] Add backend logging

## Testing

> Member 3 tests everything they build.

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
- [ ] Test deployed production API
- [ ] Test backend error handling
- [ ] Test API doesn't crash on malformed requests

## Deliverable

**Working cloud REST API/database + tests proving the backend works.**

---

# Member 4 — Custom Features

> Member 4 owns the custom functionality from Part 1, along with all tests for those features.

> The exact development jobs depend on the application's Part 1 design.

---

## Custom Feature #1

### Development

- [ ] Define Feature #1 requirements
- [ ] Create Feature #1 data model
- [ ] Implement Feature #1 business logic
- [ ] Implement Feature #1 UI
- [ ] Implement Feature #1 validation
- [ ] Connect Feature #1 to API
- [ ] Connect Feature #1 to database
- [ ] Implement Feature #1 error handling
- [ ] Add Feature #1 logging

### Testing

- [ ] Test Feature #1 normal operation
- [ ] Test Feature #1 invalid input
- [ ] Test Feature #1 empty input
- [ ] Test Feature #1 API communication
- [ ] Test Feature #1 data persistence
- [ ] Test Feature #1 data retrieval
- [ ] Test Feature #1 error handling
- [ ] Test Feature #1 edge cases

---

## Custom Feature #2

### Development

- [ ] Define Feature #2 requirements
- [ ] Create Feature #2 data model
- [ ] Implement Feature #2 business logic
- [ ] Implement Feature #2 UI
- [ ] Implement Feature #2 validation
- [ ] Connect Feature #2 to API
- [ ] Connect Feature #2 to database
- [ ] Implement Feature #2 error handling
- [ ] Add Feature #2 logging

### Testing

- [ ] Test Feature #2 normal operation
- [ ] Test Feature #2 invalid input
- [ ] Test Feature #2 empty input
- [ ] Test Feature #2 API communication
- [ ] Test Feature #2 data persistence
- [ ] Test Feature #2 data retrieval
- [ ] Test Feature #2 error handling
- [ ] Test Feature #2 edge cases

> Repeat this structure for every custom feature specified in the Part 1 design.

## Deliverable

**Working custom functionality + tests proving each custom feature works.**

---

# Member 5 — Team Lead

> The Team Lead does not own application feature development or automated test creation.

> Their role is:
>
> **Set up → Coordinate → Integrate → Verify → Submit**

---

## GitHub Setup

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
- [ ] Assign jobs to members

---

## Project Setup

- [ ] Create/initialise Android project
- [ ] Configure Gradle
- [ ] Configure `.gitignore`
- [ ] Create initial project structure
- [ ] Verify clean project builds
- [ ] Push initial project
- [ ] Make sure all members can clone/build the project

---

## GitHub Actions

> The Team Lead sets up the automation but does not write the tests.

- [ ] Create GitHub Actions workflow
- [ ] Configure JDK
- [ ] Configure Gradle
- [ ] Configure Android build
- [ ] Configure test execution
- [ ] Configure workflow on push
- [ ] Configure workflow on pull request
- [ ] Verify CI works
- [ ] Fix CI configuration problems

### Tests executed by GitHub Actions

- Member 1 tests
- Member 2 tests
- Member 3 tests
- Member 4 tests

---

# Team Lead — Integration

- [ ] Review Member 1 work
- [ ] Review Member 2 work
- [ ] Review Member 3 work
- [ ] Review Member 4 work
- [ ] Merge branches
- [ ] Resolve merge conflicts
- [ ] Verify dependencies
- [ ] Verify API configuration
- [ ] Verify authentication configuration
- [ ] Verify production configuration
- [ ] Verify final `main` branch builds
- [ ] Ensure all developer tests pass

---

# Team Lead — Final System Testing

> This is not test development.
>
> The other members prove that their individual components work.
>
> The Team Lead proves that the entire application works together.

## Full Application Test

- [ ] Install application on physical device
- [ ] Launch application
- [ ] Register user
- [ ] Complete SSO authentication
- [ ] Log in
- [ ] Navigate through application
- [ ] Change settings
- [ ] Save settings
- [ ] Restart application
- [ ] Verify settings persisted
- [ ] Use custom feature #1
- [ ] Use custom feature #2
- [ ] Create data
- [ ] Verify data reaches API
- [ ] Verify data reaches database
- [ ] Modify data
- [ ] Verify database modification
- [ ] Retrieve modified data
- [ ] Verify modified data appears in application
- [ ] Log out
- [ ] Verify authenticated areas are protected
- [ ] Test major error scenarios
- [ ] Verify application doesn't crash

---

# Team Lead — README & Submission

- [ ] Create README structure
- [ ] Add project overview
- [ ] Add project purpose
- [ ] Add feature list
- [ ] Add technology stack
- [ ] Add architecture diagram
- [ ] Add setup instructions
- [ ] Add authentication information
- [ ] Add API information supplied by Member 3
- [ ] Add database information supplied by Member 3
- [ ] Add testing information from Members 1–4
- [ ] Add screenshots
- [ ] Add GitHub Actions information
- [ ] Add demonstration video
- [ ] Add AI Sub-Report
- [ ] Check README formatting
- [ ] Check all links

---

# Team Lead — Demonstration & Submission

- [ ] Create demonstration checklist
- [ ] Prepare physical device
- [ ] Prepare test account
- [ ] Prepare demonstration data
- [ ] Record authentication demonstration
- [ ] Record settings demonstration
- [ ] Record REST API demonstration
- [ ] Record database verification
- [ ] Record custom feature demonstration
- [ ] Upload demonstration video
- [ ] Add video to README
- [ ] Verify GitHub repository accessibility
- [ ] Verify final CI run passes
- [ ] Verify final build
- [ ] Verify physical device version
- [ ] Complete submission
