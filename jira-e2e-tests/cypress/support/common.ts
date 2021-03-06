type Context = {
    base: string;
    context: string;
    baseURL: string;
    welcomeURL: string;
    loginURL: string;
    sudoURL: string;
    upmURL: string;
    pluginPath: string;
    migrationBase: string;
    migrationHome: string;
};

type CloudFormationFormValues = {
    stackName: string;
    dbMasterPassword: string;
    dbPassword: string;
    dbMultiAz?: boolean;
    cidrBlock?: string;
};

type AWSCredentials = {
    keyId: string;
    secretKey: string;
};
