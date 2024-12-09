# Capstone First Stage: Users API

## Initial Setup

### Credentials and URLs

In order to keep developer credentials secret and also centralizing the definition of some URLs, we added a config file for storing those.

Before you run the app you need to add an `application-secrets.properties` file with the following variables:

- `db.url`: Database URL (IP, port and schema)
- `db.user`: Database username
- `db.password`: Database password
- `email.username` = Email direction(me@example.com) to be used for sending emails
- `email.password` = App password for having access to the email provided
- `email.host` = Host of the email provided (Example: smtp.gmail.com)
- `email.port` = Port used by the email host

Format: `variable.name = value`

Example: `db.user = myUser`

# API usage
### 1. Get User - @GetMapping
Purpose:
Fetches the authenticated user's details based on the user ID extracted from the JWT token.

How it works:
Extracts the JWT from the Authorization header.
Retrieves the user ID from the token.
Fetches the user details from the database using the userService.
If the user is found, returns a 200 OK response with the UserDto. Otherwise, it returns 404 Not Found.

### 2. Create User - @PostMapping("/create")
Purpose:
Creates a new user in the system.

How it works:
Receives a UserDto in the request body, containing the user's details (e.g., email, password).
Validates the email and password formats.
If the input is valid, creates the user and returns a 201 Created response with the User object. If the input is invalid, returns a 400 Bad Request.

### 3. Update User - @PatchMapping
Purpose:
Updates the authenticated user's information.
How it works:
Extracts the JWT from the Authorization header to identify the user.
Receives a UserDto containing the updated user details in the request body.
The userService updates the user's information if valid.
Returns a 200 OK response with the updated UserDto. If the user is not found, returns 404 Not Found. If the email or password is invalid, it returns 400 Bad Request.

### 4. Delete User - @DeleteMapping
Purpose:
Deletes the authenticated user's account.
How it works:
Extracts the JWT from the Authorization header to get the user ID.
Calls the userService to delete the user from the database.
Returns a 200 OK response with a success message.

### 5. Login User - @PostMapping("/login")
Purpose:
Authenticates the user and returns a JWT token for subsequent requests.
How it works:
Receives a UserDto in the request body containing the user's email and password.
Authenticates the user through the authService.
If successful, generates a JWT token using the JwtTokenProvider and returns it in a 200 OK response. If the credentials are invalid, returns a 403 Forbidden response with an error message.