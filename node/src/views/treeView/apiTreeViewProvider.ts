import * as vscode from 'vscode';
import { HttpClient } from '../../services/http/httpClient';
import { Commands } from '../../constants/commands';

export class ApiTreeViewProvider implements vscode.TreeDataProvider<vscode.TreeItem> {
    private _onDidChangeTreeData: vscode.EventEmitter<vscode.TreeItem | undefined | null | void> = new vscode.EventEmitter<vscode.TreeItem | undefined | null | void>();
    readonly onDidChangeTreeData: vscode.Event<vscode.TreeItem | undefined | null | void> = this._onDidChangeTreeData.event;

    constructor(private httpClient: HttpClient) {}

    getTreeItem(element: vscode.TreeItem): vscode.TreeItem {
        return element;
    }

    getChildren(element?: vscode.TreeItem): Thenable<vscode.TreeItem[]> {
        if (element) {
            return Promise.resolve([]);
        }
        
        const makeRequestButton = new vscode.TreeItem(
            'Make HTTPS Request',
            vscode.TreeItemCollapsibleState.None
        );
        
        makeRequestButton.command = {
            command: Commands.MAKE_API_REQUEST,
            title: 'Make HTTPS Request'
        };
        
        makeRequestButton.iconPath = new vscode.ThemeIcon('globe');
        
        return Promise.resolve([makeRequestButton]);
    }

    refresh(): void {
        this._onDidChangeTreeData.fire();
    }
} 