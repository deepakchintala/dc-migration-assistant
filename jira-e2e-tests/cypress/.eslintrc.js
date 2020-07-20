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

module.exports = {
    root: true,
    extends: ['airbnb', 'plugin:prettier/recommended', 'plugin:@typescript-eslint/recommended'],
    env: {
        es6: true,
        node: true,
    },
    parser: '@typescript-eslint/parser',
    parserOptions: {
        ecmaVersion: 7,
        ecmaFeatures: {
            jsx: true,
        },
        sourceType: 'module',
    },
    rules: {
        '@typescript-eslint/no-explicit-any': 'off',
        'import/no-webpack-loader-syntax': 'off',
        'import/prefer-default-export': 'off',
        'import/extensions': ['error', 'never'],
        'prettier/prettier': 'error',
        'no-shadow': 'warn',
        'no-undef': 'warn',
        'no-param-reassign': 'warn',
        'no-nested-ternary': 'warn',
        'import/no-extraneous-dependencies': [
            'error',
            { devDependencies: ['**/*.test.tsx', '**/*.test.ts'] },
        ],
        'jsx-a11y/label-has-associated-control': 'warn',
        'no-return-assign': 'warn',
        'react/static-property-placement': 'warn',
        'react/sort-comp': 'warn',
        'class-methods-use-this': 'warn',
        'react/state-in-constructor': 'warn',
        'consistent-return': 'warn',
        'react/no-access-state-in-setstate': 'warn',
        radix: 'warn',
        'react/no-did-update-set-state': 'warn',
        'import/no-cycle': 'warn',
        'import/named': 'warn',
        'no-prototype-builtins': 'warn',
        'import/order': 'warn',
        'no-unused-expressions': 'warn',
        'import/no-unresolved': 'warn',
        'react/forbid-component-props': 'warn',
        'object-shorthand': 'warn',
    },
    overrides: [
        {
            files: ['*test.js*'],
            env: {
                jest: true,
            },
        },
    ],
};
