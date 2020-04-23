import { callAppRest } from '../utils/api';

export enum ProvisioningStatus {
    ProvisioningApplicationStack,
    PreProvisionMigrationStack,
    ProvisioningMigrationStack,
    Complete,
}

enum RestApiPathConstants {
    GetDeploymentStatusPath = '/aws/stack/status',
}

type StackStatusResponse = {
    status:
        | 'CREATE_IN_PROGRESS'
        | 'CREATE_COMPLETE'
        | 'CREATE_FAILED'
        | 'PREPARING_MIGRATION_INFRASTRUCTURE_DEPLOYMENT';
    phase?: 'app_infra' | 'migration_infra';
};

type StackStatusErrorResponse = {
    error: string;
};

type GetProvisioningStatusResponse = StackStatusResponse | StackStatusErrorResponse;

export const provisioning = {
    getProvisioningStatus: (): Promise<ProvisioningStatus> => {
        return callAppRest('GET', RestApiPathConstants.GetDeploymentStatusPath)
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status) {
                    // handle success
                    const statusResp = resp as StackStatusResponse;
                    const { phase, status } = statusResp;
                    switch (status) {
                        case 'PREPARING_MIGRATION_INFRASTRUCTURE_DEPLOYMENT':
                            return ProvisioningStatus.PreProvisionMigrationStack;
                        case 'CREATE_IN_PROGRESS':
                            return phase === 'app_infra'
                                ? ProvisioningStatus.ProvisioningApplicationStack
                                : ProvisioningStatus.PreProvisionMigrationStack;
                        case 'CREATE_COMPLETE':
                            return ProvisioningStatus.Complete;
                        case 'CREATE_FAILED':
                            // TODO: internationalise
                            throw new Error('Deployment to AWS failed');
                        default:
                            // TODO: internationalise
                            throw new Error(`Unexpected deployment state: ${phase}`);
                    }
                }
                if (resp.error) {
                    // hanle error
                    const errorResponse = resp as StackStatusErrorResponse;
                    throw new Error(errorResponse.error);
                }
                throw new Error(`Bad response from server ${JSON.stringify(resp)}`);
            });
    },
};
