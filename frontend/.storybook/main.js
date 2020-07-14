const webpackConfigGenerator = require('../config/webpack.config');
const webpackConstants = require('../config/webpack.constants');

module.exports = {
    stories: ['../src/**/*.stories.[jt]sx'],
    addons: [
        '@storybook/preset-create-react-app',
        '@storybook/addon-actions',
        '@storybook/addon-links',
    ],
    webpackFinal: async config => {
        const webpackConfig = webpackConfigGenerator('production', { mode: 'development' });
        config.plugins.push(...webpackConfig.plugins);
        config.module.rules.push({
            test: /\.(tsx|ts)?$/,
            exclude: /node_modules/,
            use: [
                {
                    loader: '@atlassian/i18n-properties-loader',
                    options: {
                        i18nFiles: webpackConstants.MY_I18N_FILES,
                        disabled: false,
                    },
                },
            ],
        });
        return config;
    },
};
