# LifeLink - Railway Deployment

This project is prepared for Railway deployment.

## Railway services
1. Create a Railway project.
2. Add a MySQL service. Keep its service name simple, for example `MySQL`.
3. Deploy this project from GitHub as a Spring Boot service.
4. In the LifeLink service Variables, add these reference variables (replace `MySQL` if your database service has a different name):

SPRING_DATASOURCE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=${{MySQL.MYSQLUSER}}
SPRING_DATASOURCE_PASSWORD=${{MySQL.MYSQLPASSWORD}}

5. Deploy the service.
6. In the LifeLink service, open Settings/Networking and generate a public domain.

## Database note
`spring.jpa.hibernate.ddl-auto=update` creates/updates the JPA tables when the app connects to a new database. The current `lifelink.sql` in this project is empty, so existing local data is NOT automatically copied to Railway. If you need your current local donor/admin records online, export the local database from XAMPP/phpMyAdmin and import that SQL into the Railway MySQL database before using the live site.

## Local run
No Railway variables are required locally. The defaults still point to:
`jdbc:mysql://127.0.0.1:3306/lifelink`
with username `root` and an empty password.
