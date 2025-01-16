import * as vscode from 'vscode';
import { ResponseWebView } from './ResponseWebView';
import { ResponseHandler } from '../types';

export class ResponsePresenter implements ResponseHandler {
    private view: ResponseWebView;

    constructor() {
        this.view = new ResponseWebView();
    }

    async handleSuccess(data: any): Promise<void> {
        await this.view.show();
        this.view.setContent(data);
    }

    async handleError(error: Error): Promise<void> {
        vscode.window.showErrorMessage('Request failed with error: ' + error.message);
        await this.view.show();
        this.view.setError(error.message);
    }
} 