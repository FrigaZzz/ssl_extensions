export interface ResponsePresentation {
    show(): Promise<void>;
    setContent(data: string): void;
    setError(message: string): void;
}

export interface ResponseHandler {
    handleSuccess(data: any): Promise<void>;
    handleError(error: Error): Promise<void>;
} 