# Task Management CRUD Example

This application demonstrates how to build a Task Management application in Vaadin Flow. It showcases typical 
features in a real business application, including:
- Master-detail layouts
- Handling selections through URL parameters
- Data pagination
- Filtering and sorting
- Forms and validation
- Dialogs
- Custom icons and styles

**Note!** This application uses Testcontainers in development mode. You must have Docker installed and running to
run the application.

To start the application in development mode, import it into your IDE and run the `TestApplication` class. 
You can also start the application from the command line by running: 

```bash
./mvnw
```

To build the application in production mode, run:

```bash
./mvnw -Pproduction package
```
