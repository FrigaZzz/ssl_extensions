import * as vscode from 'vscode';
import { HttpClient } from '../services/http/httpClient';
import { ResponseHandler } from '../views/types';
import { Commands } from '../constants/commands';

export class MakeRequestCommand {
    constructor(
        private httpClient: HttpClient,
        private responseHandler: ResponseHandler
    ) {}

    async execute(): Promise<void> {
        try {
            const data = await this.httpClient.get('https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-clean-plugin/2.5/maven-clean-plugin-2.5.pom');
            await this.responseHandler.handleSuccess(data);
        } catch (error: any) {
            await this.responseHandler.handleError(error);
        }
    }

    static register(
        context: vscode.ExtensionContext, 
        httpClient: HttpClient,
        responseHandler: ResponseHandler
    ): vscode.Disposable {
        const command = new MakeRequestCommand(httpClient, responseHandler);
        return vscode.commands.registerCommand(Commands.MAKE_API_REQUEST, () => command.execute());
    }
} 