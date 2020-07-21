type Context = {
    base: string;
    context: string;
    baseURL: string;
    welcomeURL: string;
    loginURL: string;
    sudoURL: string;
    upmURL: string;
    migrationBase: string;
    migrationHome: string;
};

type CloudFormationFormValues = {
    stackName: string;
};

type AWSCredentials = {
    keyId: string;
    secretKey: string;
};
