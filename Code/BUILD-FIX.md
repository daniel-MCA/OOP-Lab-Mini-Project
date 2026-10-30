# AdaptQ build fix

The previous ZIP had an incorrect MySQL Maven dependency coordinates. That can prevent Maven from resolving the project dependencies, which then causes IntelliJ to report errors such as `package org.springframework.boot does not exist`, `CommandLineRunner cannot be found`, `PasswordEncoder cannot be found`, and similar errors across nearly every Java file.

## IntelliJ setup
1. Open the folder that contains `pom.xml`.
2. In IntelliJ, open the Maven tool window.
3. Click **Reload All Maven Projects**.
4. Ensure the Project SDK is Java 21 or newer.
5. Run **Build > Rebuild Project**.
6. Run `QbankApplication`.

The project uses H2 by default, so MySQL is not required just to start the application.

## If IntelliJ still shows missing Spring packages
Right-click `pom.xml` → **Add as Maven Project** (if shown), then reload Maven. Do not run individual `.java` files as standalone files before Maven finishes importing dependencies.
