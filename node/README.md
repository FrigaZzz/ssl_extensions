# API Explorer Extension

## Description
A VS Code extension for making secure HTTPS requests with built-in SSL certificate support. The extension provides an integrated interface for exploring APIs, making requests, and viewing responses directly within VS Code.

## Features
- **Secure HTTPS Requests**: Built-in SSL certificate management for secure API calls
- **Interactive Interface**: 
  - Tree view in the activity bar with a globe icon
  - Dedicated webview panel for response visualization
  - Command palette integration
- **Response Handling**:
  - Automatic content-type detection
  - Formatted JSON responses
  - Plain text support
  - Error handling with visual feedback
- **SSL Security**:
  - Pre-configured SSL certificates
  - Custom certificate support
  - Certificate validation

## Installation
1. Clone the repository
2. Install dependencies:
   ```bash
   npm install
   ```
3. Build the extension:
   ```bash
   npm run build
   ```

## Usage
1. Open VS Code
2. Access the API Explorer from the activity bar (globe icon)
3. Make requests using either:
   - Click "Make HTTPS Request" in the tree view
   - Use Command Palette (`Ctrl+Shift+P` / `Cmd+Shift+P`): "Make HTTPS Request"
4. View responses in the integrated webview panel

## Configuration
The extension supports the following settings in VS Code:

## Repository Structure
``` 
my-extension/
├── src/
│ ├── commands/ # Command implementations
│ ├── constants/ # Command definitions
│ ├── services/
│ │ └── http/ # HTTPS client with SSL support
│ ├── configuration/ # Configuration settings
│ ├── utils/ # Utility functions
│ ├── views/
│ │ ├── treeView/ # Activity bar tree view
│ │ └── webView/ # Response visualization
│ └── extension.ts # Extension entry point
├── resources/
│ └── keystore/ # SSL certificates
└── package.json # Extension manifest
``` 

## Key Files
- `src/extension.ts`: Main extension entry point
- `src/services/http/httpClient.ts`: HTTPS request handling
- `src/views/treeView/apiTreeViewProvider.ts`: API Explorer interface
- `resources/keystore/combined-certificates.pem`: SSL certificates
- `package.json`: Extension manifest and dependencies 

## Development
- **Build**: `npm run build`
- **Watch Mode**: `npm run watch`
- **Lint**: `npm run lint`
- **Test**: `npm run test`
- **Package**: `npm run package`

## SSL Certificates
- **Default Certificate**: Built-in certificate located at `resources/keystore/combined-certificates.pem`
- **Custom Certificates**: Configure through VS Code settings:
  1. Open Settings (`Ctrl+,` / `Cmd+,`)
  2. Search for "My Extension"
  3. Set custom certificate path in `myExtension.certificateFile`

## Requirements
- VS Code 1.85.0 or higher
- Node.js 18.x or higher

## License
This project is licensed under the MIT License. 

