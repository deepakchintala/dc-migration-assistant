import React, { FunctionComponent } from 'react';
import { Routes } from './Routes';
import { MigrationStageRenderer } from './MigrationStageRenderer';

export const MigrationAssistant: FunctionComponent = () => {
    return (
        <>
            <MigrationStageRenderer />
            <Routes />
        </>
    );
};
