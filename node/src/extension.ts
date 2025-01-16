import * as vscode from 'vscode';
import { ApiTreeViewProvider } from './views/treeView/apiTreeViewProvider';
import { HttpClientFactory } from './services/http/httpClientFactory';
import { MakeRequestCommand } from './commands/makeRequest';
import { ResponsePresenter } from './views/response/ResponsePresenter';

export function activate(context: vscode.ExtensionContext) {
    const factory = new HttpClientFactory(context.extensionPath);
    const httpClient = factory.createHttpClient();
    const treeDataProvider = new ApiTreeViewProvider(httpClient);
    
    // Initialize views
    const treeView = vscode.window.createTreeView('apiExplorerView', {
        treeDataProvider
    });
    
    // Initialize presenters
    const responsePresenter = new ResponsePresenter();
    
    // Register commands
    const disposable = MakeRequestCommand.register(context, httpClient, responsePresenter);
    
    // Register disposables
    context.subscriptions.push(treeView, disposable);
}

export function deactivate() {}
