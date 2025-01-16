import * as vscode from 'vscode';
import { ResponsePresentation } from '../types';

export class ResponseWebView implements ResponsePresentation {
    private panel: vscode.WebviewPanel | undefined;

    async show(): Promise<void> {
        if (!this.panel) {
            this.panel = vscode.window.createWebviewPanel(
                'apiResponse',
                'API Response',
                vscode.ViewColumn.One,
                {
                    enableScripts: true,
                    retainContextWhenHidden: true
                }
            );

            this.panel.onDidDispose(() => {
                this.panel = undefined;
            });
        }

        this.setLoading();
        this.panel.reveal(vscode.ViewColumn.One);
    }

    setContent(data: string): void {
        if (this.panel) {
            this.panel.webview.html = this.generateHtml('Response:', data);
        }
    }

    setError(message: string): void {
        if (this.panel) {
            this.panel.webview.html = this.generateHtml('Error:', message, true);
        }
    }

    private setLoading(): void {
        if (this.panel) {
            this.panel.webview.html = this.generateHtml('Loading...', '');
        }
    }

    private generateHtml(title: string, content: string, isError: boolean = false): string {
        return `
            <html>
                <head>
                    <style>
                        body { 
                            padding: 10px;
                            font-family: var(--vscode-editor-font-family);
                            font-size: var(--vscode-editor-font-size);
                            color: var(--vscode-editor-foreground);
                            background-color: var(--vscode-editor-background);
                        }
                        pre {
                            white-space: pre-wrap;
                            word-wrap: break-word;
                            padding: 10px;
                            background-color: var(--vscode-textCodeBlock-background);
                            border-radius: 3px;
                            ${isError ? 'color: red;' : ''}
                        }
                    </style>
                </head>
                <body>
                    <h3>${title}</h3>
                    ${content ? `<pre>${this.escapeHtml(content)}</pre>` : ''}
                </body>
            </html>`;
    }

    private escapeHtml(unsafe: string): string {
        return unsafe
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }
} 