import * as https from 'https';
import * as fs from 'fs';
import { HttpClient } from './httpClient';
import { getConfiguration } from '../../configuration/settings';

export class HttpClientFactory {
    constructor(private readonly extensionPath: string) {}

    createHttpClient(): HttpClient {
        const httpsAgent = this.createHttpsAgent();
        return new HttpClient(httpsAgent);
    }

    private createHttpsAgent(): https.Agent {
        try {
            const config = getConfiguration(this.extensionPath);
            const certPath = config.certificateFile;
            
            if (!fs.existsSync(certPath)) {
                throw new Error(`Certificate file not found at: ${certPath}`);
            }

            const cert = fs.readFileSync(certPath);
            return new https.Agent({
                ca: cert,
                rejectUnauthorized: true,
                keepAlive: true,
                secureOptions: require('constants').SSL_OP_NO_TLSv1_3
            });
        } catch (error: unknown) {
            console.error('Error loading certificates:', error);
            const errorMessage = error instanceof Error ? error.message : 'Unknown error';
            throw new Error(`Failed to initialize HTTPS agent with certificates: ${errorMessage}`);
        }
    }
} 