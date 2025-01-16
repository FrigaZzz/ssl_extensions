import * as https from 'https';

export class HttpClient {
    constructor(private readonly httpsAgent: https.Agent) {
        if (!httpsAgent) {
            throw new Error('HTTPS Agent is required');
        }
    }

    async get(url: string): Promise<string> {
        return this.sendRequest(url, 'GET');
    }

    async post(url: string, body: any): Promise<string> {
        return this.sendRequest(url, 'POST', body);
    }

    private async sendRequest(url: string, method: string, body?: any): Promise<string> {
        return new Promise((resolve, reject) => {
            const parsedUrl = new URL(url);
            const options: https.RequestOptions = {
                method,
                hostname: parsedUrl.hostname,
                port: parsedUrl.port || 443,
                path: parsedUrl.pathname + parsedUrl.search,
                headers: {
                    'Content-Type': 'application/json',
                    'User-Agent': 'VSCode-Extension',
                    'Accept': 'application/json, text/plain, */*'
                },
                agent: this.httpsAgent,
                servername: parsedUrl.hostname
            };

            const req = https.request(options, (res) => {
                const chunks: Buffer[] = [];

                res.on('data', (chunk) => {
                    chunks.push(Buffer.from(chunk));
                });

                res.on('end', () => {
                    const responseData = Buffer.concat(chunks);
                    const contentType = res.headers['content-type'] || '';

                    if (res.statusCode && res.statusCode >= 200 && res.statusCode < 300) {
                        try {
                            // Handle different content types
                            if (contentType.includes('application/json')) {
                                const jsonData = JSON.parse(responseData.toString());
                                resolve(JSON.stringify(jsonData, null, 2)); // Pretty print JSON
                            } else if (contentType.includes('text/html')) {
                                resolve(responseData.toString());
                            } else {
                                // Default to string representation
                                resolve(responseData.toString());
                            }
                        } catch (error) {
                            console.error('Error processing response:', error);
                            reject(new Error('Failed to process response data'));
                        }
                    } else {
                        reject(new Error(`Request failed with status code ${res.statusCode} - ${res.statusMessage}`));
                    }
                });
            });

            req.on('error', (error) => {
                console.error('Request error:', error);
                reject(error);
            });

            if (body) {
                req.write(JSON.stringify(body));
            }

            req.end();
        });
    }
}