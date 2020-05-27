/*
 * Copyright 2020 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { FunctionComponent } from 'react';
import { Route, Switch } from 'react-router-dom';

import { awsAuthPath, quickstartPath, quickstartStatusPath } from '../../utils/RoutePaths';
import { QuickStartDeploy } from './quickstart/QuickStartDeploy';
import { AuthenticateAWS, CredSubmitFun, QueryRegionFun } from './auth/AuthenticateAWS';
import { callAppRest, RestApiPathConstants } from '../../utils/api';
import { ProvisioningStatusPage } from './quickstart/ProvisioningStatusPage';
import { useEffect } from "react";
import { useLocation } from "react-router-dom";

/*
 * Pages that are navigated to from long content pages 
 * stay scrolled down. This is a component that will 
 * scroll the window up.
 */
const ScrollToTop = (): null => {
    const { pathname } = useLocation();

    useEffect(() => {
        window.scrollTo(0, 0);
    }, [pathname]);

    return null;
};

const getRegions: QueryRegionFun = () => {
    return callAppRest('GET', RestApiPathConstants.awsRegionListPath).then(response =>
        response.json()
    );
};

const saveAWSCredentials: CredSubmitFun = creds => {
    const responsePromise = callAppRest(
        'POST',
        RestApiPathConstants.awsCredentialsStorePath,
        creds
    );
    const apiResponseHandler = async (promise: Promise<Response>): Promise<string> => {
        const response = await promise;
        // https://github.com/whatwg/fetch/issues/113 requires us to handle 204 responses explicitly
        if (response.status === 204) {
            return '';
        }
        const responseData = await response.json();
        throw Error(`Unable to save credentials. API error: ${responseData}`);
    };
    return apiResponseHandler(responsePromise);
};

export const AWSRoutes: FunctionComponent = () => (
    <Switch>
        <Route exact path={quickstartPath}>
            <QuickStartDeploy />
        </Route>
        <Route exact path={quickstartStatusPath}>
            <ScrollToTop />
            <ProvisioningStatusPage />
        </Route>
        <Route exact path={awsAuthPath}>
            <AuthenticateAWS getRegions={getRegions} onSubmitCreds={saveAWSCredentials} />
        </Route>
    </Switch>
);
