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

const TerserPlugin = require('terser-webpack-plugin');
const merge = require('webpack-merge');
const webpack = require('webpack');
const path = require('path');
const dotenv = require('dotenv');
const fs = require('fs');

const {
    DEV_SERVER_HOST,
    DEV_SERVER_PORT,
    ENTRY_POINT,
    FRONTEND_OUTPUT_DIR,
    FRONTEND_SRC_DIR,
} = require('./webpack.constants');

const { plugins } = require('./webpack.plugins');
const { loaders } = require('./webpack.loaders');

const watchConfig = {
    devServer: {
        host: DEV_SERVER_HOST,
        port: DEV_SERVER_PORT,
        historyApiFallback: true,
        contentBase: path.join(__dirname, '../public'),
        overlay: true,
        hot: true,
        headers: {
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Headers': '*',
        },
        proxy: {
            '/rest': {
                target: 'http://localhost:2990/jira',
                auth: 'admin:admin',
            },
        },
    },
    plugins: [new webpack.HotModuleReplacementPlugin()],
    devtool: 'inline-source-map',
};

const devConfig = env => {
    return merge([
        {
            optimization: {
                minimize: false,
                runtimeChunk: false,
                splitChunks: false,
            },
            output: {
                publicPath: `http://${DEV_SERVER_HOST}:${DEV_SERVER_PORT}/`,
                filename: '[name].[hash].js',
                chunkFilename: '[name].chunk.js',
            },
        },
        env === 'dev-server' && watchConfig,
    ]);
};

const prodConfig = {
    optimization: {
        minimizer: [
            new TerserPlugin({
                terserOptions: {
                    mangle: {
                        reserved: ['I18n', 'getText'],
                    },
                },
            }),
        ],
    },
    output: {
        filename: '[name].[contenthash].js',
    },
};

module.exports = (env, argv = {}) => {
    const isProductionEnv = (mode = argv.mode) => mode !== 'development';

    const parseEnvVariablesFrom = filePath => {
        const dotEnvOverrides = dotenv.config({ path: filePath }).parsed;
        return Object.keys(dotEnvOverrides).reduce((acc, current) => {
            acc[`process.env.${current}`] = JSON.stringify(dotEnvOverrides[current]);
            return acc;
        }, {});
    };

    const loadEnvVarsFrom = (dotEnvFilePath, acc) => {
        if (fs.existsSync(dotEnvFilePath)) {
            const envVars = parseEnvVariablesFrom(dotEnvFilePath);
            Object.keys(envVars).forEach(key => {
                acc[key] = envVars[key];
            });
        }
        return acc;
    };

    const loadDotEnvVariables = mode => {
        const dotEnvFilePath = path.join(__dirname, '../', '.env');
        const varsFromEnvFile = loadEnvVarsFrom(dotEnvFilePath, {});
        if (!isProductionEnv(mode)) {
            const envScopedOverridesFile = `${dotEnvFilePath}.${mode}`;
            const envVarOverrides = loadEnvVarsFrom(envScopedOverridesFile, varsFromEnvFile);
            console.log(`Loaded env vars with overrides`);
            console.log(envVarOverrides);
            return envVarOverrides;
        }
        console.log(`Loaded env vars`);
        console.log(varsFromEnvFile);
        return varsFromEnvFile;
    };

    const isProduction = isProductionEnv();
    const modeConfig = isProduction ? prodConfig : devConfig(env);
    return merge([
        {
            mode: argv.mode,
            entry: ENTRY_POINT,
            resolve: {
                extensions: ['*', '.ts', '.tsx', '.js', '.jsx'],
            },
            stats: {
                logging: 'info',
            },
            context: FRONTEND_SRC_DIR,
            plugins: [
                ...plugins(!isProduction, env && env.analyze),
                new webpack.DefinePlugin(loadDotEnvVariables(argv.mode)),
            ],
            module: {
                rules: loaders(isProduction),
            },
            output: {
                path: FRONTEND_OUTPUT_DIR,
            },
        },
        modeConfig,
    ]);
};
