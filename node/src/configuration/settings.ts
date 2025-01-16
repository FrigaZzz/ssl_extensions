import * as vscode from 'vscode';
import * as path from 'path';
import { ResourcePathConfig } from '../utils/paths';

const DEFAULT_PATHS = {
    resourcesDir: 'resources',
    keystoreDir: 'keystore',
    certificateFile: 'combined-certificates.pem'
} as const;

export function getConfiguration(extensionPath: string): ResourcePathConfig {
    const config = vscode.workspace.getConfiguration('myExtension');
    const customCertPath = config.get<string>('certificateFile');
    
    // If custom cert path is provided, use it directly
    if (customCertPath) {
        return {
            resourcesDir: DEFAULT_PATHS.resourcesDir,
            keystoreDir: DEFAULT_PATHS.keystoreDir,
            certificateFile: customCertPath
        };
    }

    // Otherwise use the bundled certificate
    return {
        resourcesDir: DEFAULT_PATHS.resourcesDir,
        keystoreDir: DEFAULT_PATHS.keystoreDir,
        certificateFile: path.join(extensionPath, DEFAULT_PATHS.resourcesDir, DEFAULT_PATHS.keystoreDir, DEFAULT_PATHS.certificateFile)
    };
} 