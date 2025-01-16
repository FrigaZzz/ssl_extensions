# Maven POM Viewer Plugin

A plugin for IntelliJ IDEA that allows viewing Maven POM files with SSL certificate support. This plugin provides a convenient tool window to fetch and view Maven POM files directly within your IDE, making it easier to inspect dependencies and configurations.

## 🚀 Features

- **SSL Certificate Support** - Secure connections with custom certificate handling



## 📋 Prerequisites

- IntelliJ IDEA (2022.2.4 or later)
- JDK 17 or later
- Gradle 8.3 or later

## 🛠️ Installation

### From Source
1. Clone the repository:
```
git clone <your-repository-url>
cd maven-pom-viewer
```

2. Build the plugin:
```
./gradlew clean build
```

The built plugin will be available at `build/distributions/maven-pom-viewer-{version}.zip`

### From IDE
1. Go to `Settings/Preferences → Plugins → ⚙️ → Install Plugin from Disk`
2. Select the built plugin zip file
3. Restart IDE when prompted

<start_code>
./gradlew runIde
</start_code>

## Project Structure

```
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/example/mavenpom/
│ │ │ ├── actions/ # Action handlers
│ │ │ ├── client/ # SSL client implementation
│ │ │ ├── ui/ # UI components
│ │ │ └── util/ # Utility classes
│ │ └── resources/
│ │ ├── META-INF/ # Plugin configuration
│ │ ├── certificates/ # SSL certificates
│ │ └── messages/ # i18n resources
│ └── test/ # Test suite
└── build.gradle.kts # Build configuration
```

## Configuration

The plugin uses Gradle for build configuration. Key configurations can be found in:

- `build.gradle.kts`: Main build configuration
- `gradle.properties`: Gradle and Kotlin settings
- `src/main/resources/META-INF/plugin.xml`: Plugin metadata and extensions

## Testing

Run the test suite using:

```
./gradlew test
```

## Development

### Key Components

1. **Tool Window**
   - `MavenPomToolWindow`: Main UI component
   - `MavenPomToolWindowFactory`: Tool window factory implementation

2. **SSL Client**
   - `SSLClient`: Handles secure connections with certificate support

3. **Actions**
   - `FetchMavenPomAction`: Manages POM file fetching

### Adding Certificates

Place your SSL certificates in:
src/main/resources/certificates/multi-certificate.pem

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Troubleshooting

### Common Issues

1. **Certificate Issues**
   - Ensure certificates are properly formatted
   - Check certificate path in SSLClient configuration

2. **Build Problems**
   - Verify JDK version matches project settings
   - Clear Gradle cache: `./gradlew cleanBuildCache`

### Debug Logging

Enable debug logging by modifying `idea.log.path` property in:
xml:.run/Run Plugin.run.xml

## Contact

For support or questions, please open an issue in the repository.