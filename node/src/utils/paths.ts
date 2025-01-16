import * as path from 'path';
import * as vscode from 'vscode';

export interface ResourcePathConfig {
    resourcesDir: string;
    keystoreDir: string;
    certificateFile: string;
}

export class ResourcePaths {
    constructor(
        private readonly config: ResourcePathConfig,
        private readonly extensionPath: string = vscode.extensions.getExtension('my-extension')?.extensionPath 
            || path.join(__dirname, '..', '..', '..')
    ) {}

    getResourcesPath(): string {
        return path.join(this.extensionPath, this.config.resourcesDir);
    }

    getKeystorePath(): string {
        return path.join(this.getResourcesPath(), this.config.keystoreDir);
    }

    getCertificatePath(): string {
        return path.join(this.getKeystorePath(), this.config.certificateFile);
    }
} 